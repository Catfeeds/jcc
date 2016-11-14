package com.easemob.chatuidemo.activity.info;

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
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
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
import com.easemob.chatuidemo.activity.BaseActivity;
import com.easemob.chatuidemo.adapter.ImageAdapter;
import com.easemob.chatuidemo.adapter.TalentAdapter;
import com.easemob.chatuidemo.domain.Info;
import com.easemob.chatuidemo.utils.ConnServer;
import com.easemob.chatuidemo.utils.DialogDemo;
import com.easemob.exceptions.EaseMobException;
import com.google.android.gms.internal.da;

/**
 * 
 * 
 */
public class InformationActivity extends BaseActivity {
    private TextView tvTitle;
    private ListView lvInformationList;
    private List<Map<String, Object>> mListData;
    private File cache;
    private String mType;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_information);

        Intent intent = getIntent();
        mType = intent.getStringExtra("type");

        tvTitle = (TextView)findViewById(R.id.message_title);
		lvInformationList = (ListView)this.findViewById(R.id.lv_information_list);
		lvInformationList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                    long arg3) {
                Intent intent = new Intent(InformationActivity.this,
                        InformationDetailActivity.class);
                Bundle extras = new Bundle();
                extras.putString("id", mListData.get(arg2).get("id").toString());
                extras.putString("title", mListData.get(arg2).get("title").toString());
                intent.putExtra("type", mType);
                intent.putExtras(extras);
                startActivity(intent);
                
            }
        });
        
		mListData = new ArrayList<Map<String,Object>>();
		DialogDemo.show(this,"正在加载，请稍后...");
        new GetDataThread(handler).start();
	}
	
	void Show(){
	    SimpleAdapter adapter = new SimpleAdapter(getApplicationContext(), mListData, R.layout.listview_text_item, 
	            new String[]{"title"}, new int[]{R.id.tv_title});
        lvInformationList.setAdapter(adapter);
	}

    private class GetDataThread extends Thread { 
         
        private Handler handler; 
 
        public GetDataThread(Handler handler) { 
            this.handler = handler; 
        } 
 
        @Override 
        public void run() { 
            String edit = "GET";
            Map<String, String> params = new HashMap<String, String>();
            params.put("method", "school_message_get");
            GetData(edit,params);
        }
    }
    
	public List<Map<String, Object>> GetData(String edit,Map<String, String> params){
        String path = "http://"+getResources().getString(R.string.server)+"/api.php";       
        
        String[] list = new String[]{};
        
        ConnServer server = new ConnServer();
        mListData = new ArrayList<Map<String,Object>>();
        Message msg = handler.obtainMessage(); 
        Bundle b = new Bundle();
        try {
            mListData = server.sendPOSTRequire(path,params,"UTF-8",list);
            b.putString("type", edit);
            if(Boolean.valueOf(mListData.get(0).get("status").toString())){
                msg.what = STATE_FINISH;
                mListData = server.Json2List(mListData.get(0).get("data").toString());
                System.out.println("mData:"+mListData.size());
            }else{
                msg.what = STATE_FAIL;
                msg.obj = mListData.get(0).get("msg");
            }
            msg.setData(b); 
            handler.sendMessage(msg);
        } catch (Exception e) {
            e.printStackTrace();
            b.putInt("state", STATE_ERROR);
            msg.setData(b); 
            handler.sendMessage(msg);
            return null;
        }
        return mListData;
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
                    Toast.makeText(InformationActivity.this, info, 1).show();
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
