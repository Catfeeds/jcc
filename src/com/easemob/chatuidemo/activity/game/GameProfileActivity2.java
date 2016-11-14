package com.easemob.chatuidemo.activity.game;

import java.io.File;
import java.io.IOException;
import java.lang.ref.SoftReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.CoreConnectionPNames;

import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
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
import com.easemob.chatuidemo.adapter.ViewPagerAdapter;
import com.easemob.chatuidemo.domain.Info;
import com.easemob.chatuidemo.utils.ConnServer;
import com.easemob.chatuidemo.utils.DialogDemo;
import com.easemob.exceptions.EaseMobException;
import com.google.android.gms.internal.da;

/**
 * 网游详情
 * 
 */
public class GameProfileActivity2 extends BaseActivity {
    
    private ViewPager view_pager;    
    private LayoutInflater inflater;
    private ImageView ivIcon;
    private TextView tvContent;
    private TextView tvSize;
    private TextView tvDeveloper;
    private TextView tvOperator;
    private TextView tvVersion;
    private Button btDownload;

    // 图片的地址，这里可以从服务器获取
    private String[] urls;
        
    private ImageView image;
    private View item ;
    private ViewPagerAdapter adapter ;
    private List<Map<String, Object>> mListData;
    private String mId;
    private String mUrl;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_game_profile);
		view_pager = (ViewPager) findViewById(R.id.view_pager);
		ivIcon = (ImageView)findViewById(R.id.game_icon);
        tvContent = (TextView)findViewById(R.id.content);
		tvSize = (TextView)findViewById(R.id.size);
        tvDeveloper = (TextView)findViewById(R.id.developer);
        tvOperator = (TextView)findViewById(R.id.operator);
        tvVersion = (TextView)findViewById(R.id.version);
        btDownload = (Button)findViewById(R.id.download);
        inflater = LayoutInflater.from(this);

        Intent intent = getIntent();
        Bundle bundlbe = intent.getExtras();
        mId = bundlbe.getString("id");
        String content = bundlbe.getString("content");
        mUrl = bundlbe.getString("url");
        String size = bundlbe.getString("size");
        String developers = bundlbe.getString("developers");
        String operator = bundlbe.getString("operator");
        String version = bundlbe.getString("version");
        
        tvContent.setText(content);
        tvSize.setText(size);
        tvDeveloper.setText(developers);
        tvOperator.setText(operator);
        tvVersion.setText(version);
        
        btDownload.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                Uri uri = Uri.parse(mUrl);
                Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                startActivity(intent);
            }
        });
        
        mListData = new ArrayList<Map<String,Object>>();
 //       DialogDemo.show(this,"正在加载，请稍后...");
        new GetDataThread(handler).start();
        
    }
    
    void Show(){
        urls = new String[mListData.size()];
        for(int i=0;i<mListData.size();i++){
            urls[i] = mListData.get(i).get("photo").toString();
        }

        List<View> list = new ArrayList<View>();
        for (int i = 0; i < urls.length; i++) {
            item = inflater.inflate(R.layout.viewpager_image, null);
            list.add(item);
        }
        
        view_pager.setOffscreenPageLimit(3);        
        adapter = new ViewPagerAdapter(list, urls);
        view_pager.setAdapter(adapter);
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
            params.put("method", "game_photo_get");
            params.put("id", mId);
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
            //DialogDemo.dismiss();
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
                    Toast.makeText(GameProfileActivity2.this, info, 1).show();
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
