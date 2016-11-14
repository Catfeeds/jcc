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
import com.easemob.chatuidemo.activity.BaseActivity;
import com.easemob.chatuidemo.adapter.ImageAdapter;
import com.easemob.chatuidemo.adapter.TalentAdapter;
import com.easemob.chatuidemo.domain.Info;
import com.easemob.chatuidemo.utils.ConnServer;
import com.easemob.chatuidemo.utils.DialogDemo;
import com.easemob.exceptions.EaseMobException;
import com.google.android.gms.internal.da;

/**
 * 作业信息通知详情
 * 
 */
public class HomeworkInfoDetailActivity extends BaseActivity {

    private ListView lvList;
    private List<Map<String, Object>> mListData;
    private String mSubject;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_homework_info_detail);
		Intent intent = getIntent();
        int id = intent.getIntExtra("id", 0);
        switch(id){
            case R.id.rl_political:
                mSubject = "政治";
                break;
            case R.id.rl_chinese:
                mSubject = "语文";
                break;
            case R.id.rl_math:
                mSubject = "数学";
                break;
            case R.id.rl_english:
                mSubject = "英语";
                break;
            case R.id.rl_physics:
                mSubject = "物理";
                break;
            case R.id.rl_chemistry:
                mSubject = "化学";
                break;
            case R.id.rl_geography:
                mSubject = "地理";
                break;
            case R.id.rl_history:
                mSubject = "历史";
                break;
            case R.id.rl_other:
                mSubject = "其他";
            default:
                break;
        }
		lvList = (ListView)this.findViewById(R.id.lv_list);
		lvList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                    long arg3) {
                Intent intent = new Intent(HomeworkInfoDetailActivity.this,
                        HomeworkInfoContentActivity.class);
                Bundle extras = new Bundle();
                extras.putString("id", mListData.get(arg2).get("id").toString());
                extras.putString("nick", mListData.get(arg2).get("nick").toString());
                intent.putExtras(extras);
                startActivity(intent);
                
            }
        });
		mListData = new ArrayList<Map<String,Object>>();
		DialogDemo.show(this,"正在加载，请稍后...");
        new GetDataThread(handler).start();
	}
	
	void Show(){
        SimpleAdapter adapter = new SimpleAdapter(getApplicationContext(), mListData, R.layout.row_homework_info,
                new String[]{"content","date"}, new int[]{R.id.title, R.id.date});
        lvList.setAdapter(adapter);
	}

    private class GetDataThread extends Thread { 
         
        private Handler handler; 
 
        public GetDataThread(Handler handler) { 
            this.handler = handler; 
        } 
 
        @Override 
        public void run() { 
            String edit = "inform_get";
            Map<String, String> params = new HashMap<String, String>();
            params.put("method", edit);
            params.put("accepter", EMChatManager.getInstance().getCurrentUser());
            params.put("subject", mSubject);
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
                    Toast.makeText(HomeworkInfoDetailActivity.this, info, 1).show();
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
