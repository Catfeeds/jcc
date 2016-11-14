package com.easemob.chatuidemo.activity.homework;

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
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
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
import com.squareup.picasso.Picasso;
import com.wenpy.jcc.R;
import com.easemob.chatuidemo.activity.BaseActivity;
import com.easemob.chatuidemo.adapter.ImageAdapter;
import com.easemob.chatuidemo.adapter.TalentAdapter;
import com.easemob.chatuidemo.domain.Info;
import com.easemob.chatuidemo.task.CommonDownloadTask;
import com.easemob.chatuidemo.task.DownloadImageTask;
import com.easemob.chatuidemo.task.DownloadImageTask.DownloadFileCallback;
import com.easemob.chatuidemo.utils.ConnServer;
import com.easemob.chatuidemo.utils.DialogDemo;
import com.easemob.chatuidemo.utils.MD5;
import com.easemob.exceptions.EaseMobException;
import com.google.android.gms.internal.da;

/**
 * 作业信息通知内容
 * 
 */
public class HomeworkInfoContentActivity extends BaseActivity {

    private TextView tvDate;
    private TextView tvContent;
    private TextView tvTitle;
    private List<Map<String, Object>> mListData;
    private ImageView ivFile;
    private String mId;
    private String strFileurl = "";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_homework_info_content);
		Intent intent = getIntent();
        mId = intent.getStringExtra("id");
        String strNick = intent.getStringExtra("nick");
        
        tvTitle = (TextView)findViewById(R.id.message_title);
        tvDate = (TextView)findViewById(R.id.date);
        tvContent = (TextView)findViewById(R.id.content);
        ivFile = (ImageView)findViewById(R.id.iv_file);
        
        if(DemoApplication.getInstance().getIdentity() == 1)
            tvTitle.setText(strNick+"老师");
        else
            tvTitle.setText("查看作业信息通知");
        
        ivFile.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                DialogDemo.show(HomeworkInfoContentActivity.this,"正在加载，请稍后...");
                String fileName = MD5.getMD5(strFileurl) + strFileurl.substring(strFileurl.lastIndexOf("."));  
                CommonDownloadTask task = new CommonDownloadTask(handler,  strFileurl, STATE_FINISH,  "filedownload");
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
                    task.execute(fileName);
                } else {
                    task.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, fileName);
                }
            }
        });
		mListData = new ArrayList<Map<String,Object>>();
		DialogDemo.show(this,"正在加载，请稍后...");
        new GetDataThread(handler).start();
	}
	
	void Show(){
	    strFileurl = mListData.get(0).get("file").toString();
        tvDate.setText(mListData.get(0).get("date").toString());
        tvContent.setText(mListData.get(0).get("content").toString());
	}

    private class GetDataThread extends Thread { 
         
        private Handler handler; 
 
        public GetDataThread(Handler handler) { 
            this.handler = handler; 
        } 
 
        @Override 
        public void run() {
            ConnServer server = new ConnServer();
            String path = "http://"+getResources().getString(R.string.server)+"/api.php";
            String edit = "inform_get";
            Map<String, String> params = new HashMap<String, String>();
            params.put("method", "inform_get");
            params.put("id", mId);
            mListData = server.GetData(path, params, edit, handler);
        }
    }

    private void OpenFile(String response) {
        String type = response.substring(response.lastIndexOf("."));
        Intent intent = new Intent(Intent.ACTION_VIEW);    
        if(type.equals(".jpg")||type.equals(".jpeg")||type.equals(".png")||type.equals(".bmp")||type.equals(".gif"))
            intent.setDataAndType(Uri.parse("file://"+response), "image/*"); 
        else if(type.equals(".doc"))
            intent.setDataAndType(Uri.parse("file://"+response), "application/msword"); 
        startActivity(intent);
    } 

    private final Handler handler = new Handler(Looper.getMainLooper()) {
         
        public void handleMessage(Message msg) { // 处理Message，更新ListView
            DialogDemo.dismiss();
            switch(msg.what){ 
                case STATE_FINISH: 
                {
                    if(msg.obj.toString().equals("filedownload")){
                        Bundle bundle = msg.getData();
                        String response = bundle.getString("response");
                        OpenFile(response);
                    }else
                        Show();
                }
                    break;
                case STATE_FAIL:
                {
                    String info = "Error!";
                    if(msg.obj != null){
                        info = info+"\n"+msg.obj;
                    }
                    Toast.makeText(HomeworkInfoContentActivity.this, info, 1).show();
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
