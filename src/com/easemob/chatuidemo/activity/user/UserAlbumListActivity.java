package com.easemob.chatuidemo.activity.user;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.baidu.mapapi.cloud.BoundSearchInfo;
import com.easemob.EMError;
import com.easemob.applib.controller.HXSDKHelper;
import com.easemob.chat.EMChatManager;
import com.easemob.chatuidemo.DemoApplication;
import com.easemob.chatuidemo.DemoHXSDKModel;
import com.wenpy.jcc.R;
import com.easemob.chatuidemo.activity.AlbumCommentActivity;
import com.easemob.chatuidemo.activity.BaseActivity;
import com.easemob.chatuidemo.activity.PicUploadActivity;
import com.easemob.chatuidemo.adapter.ImageAdapter;
import com.easemob.chatuidemo.adapter.TalentAdapter;
import com.easemob.chatuidemo.adapter.UserAblumAdapter;
import com.easemob.chatuidemo.domain.Info;
import com.easemob.chatuidemo.utils.ConnServer;
import com.easemob.chatuidemo.utils.DialogDemo;
import com.easemob.exceptions.EaseMobException;
import com.google.android.gms.internal.da;

/**
 * 用户相册列表
 * 
 */
public class UserAlbumListActivity extends BaseActivity {
    private TextView tvTitle;
    private ListView lvList;
    private List<Map<String, Object>> mListData;
    private String mSubject;
    private File cache;
    private String username;
    private TextView tvUpload;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_user_album_list);
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        tvTitle = (TextView)findViewById(R.id.message_title);
		lvList = (ListView)this.findViewById(R.id.lv_list);
		tvUpload = (TextView)findViewById(R.id.upload);
		
		tvTitle.setText(username);
		lvList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                    long arg3) {
                Intent intent = new Intent(UserAlbumListActivity.this,
                        AlbumCommentActivity.class);
                Bundle extras = new Bundle();
                intent.putExtra("id", mListData.get(arg2).get("id").toString());
                intent.putExtras(extras);
                startActivityForResult(intent, 101);
                
            }
        });
		tvUpload.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(UserAlbumListActivity.this, PicUploadActivity.class);
                startActivityForResult(intent, 120);
            }
        });
		
		cache = new File(Environment.getExternalStorageDirectory(), "cache");
        
        if(!cache.exists()){
            cache.mkdirs();
        } 
		mListData = new ArrayList<Map<String,Object>>();
		DialogDemo.show(this,"正在加载，请稍后...");
        new GetDataThread(handler).start();
	}
	
	void Show(){
        UserAblumAdapter adapter = new UserAblumAdapter(UserAlbumListActivity.this, mListData, cache);
        lvList.setAdapter(adapter);
	}

    private class GetDataThread extends Thread { 
         
        private Handler handler; 
 
        public GetDataThread(Handler handler) { 
            this.handler = handler; 
        } 
 
        @Override 
        public void run() { 
            String edit = "album_get";
            Map<String, String> params = new HashMap<String, String>();
            params.put("method", edit);
            params.put("user", username);
            String url = "http://"+getResources().getString(R.string.server)+"/api.php"; 
            ConnServer server = new ConnServer();
            mListData = server.GetData(url, params, edit, handler);
        }
    }

    private final Handler handler = new Handler(Looper.getMainLooper()) {
         
        public void handleMessage(Message msg) { // 处理Message，更新ListView
            DialogDemo.dismiss();
            switch(msg.what){ 
                case STATE_FINISH: 
                {
                    Show();
                }
                    break;
                case STATE_FAIL:
                {
                    String info = "Error!";
                    if(msg.obj != null){
                        info = info+"\n"+msg.obj;
                    }
                    Toast.makeText(UserAlbumListActivity.this, info, 1).show();
                    break;
                }
                case STATE_NULL:
                    //Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.datanull),Toast.LENGTH_SHORT).show(); 
                    break;
                case STATE_LINKERR:
                {
                    String type = msg.getData().getString("type");
                    Toast.makeText(getApplicationContext(), "网络连接失败",Toast.LENGTH_SHORT) .show();  
                    break;
                }
                case STATE_ERROR: 
                    Toast.makeText(getApplicationContext(), "网络连接错误",Toast.LENGTH_SHORT) .show();  
                    break;
               default:  
            } 
        } 
    }; 
	public void back(View view) {
		finish();
	}

}
