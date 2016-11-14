package com.easemob.chatuidemo.activity.question;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.support.v4.app.Fragment;
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
import com.easemob.chatuidemo.activity.tutorial.TutorialQuestionAddFragment;
import com.easemob.chatuidemo.activity.tutorial.TutorialQuestionContentFragment;
import com.easemob.chatuidemo.adapter.ImageAdapter;
import com.easemob.chatuidemo.adapter.QuestionListAdapter;
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
public class QuestionListFragment extends Fragment {
    static final int STATE_ERROR = -1; 
    static final int STATE_FAIL = 0; 
    static final int STATE_FINISH = 1; 
    static final int STATE_LINKERR = 2; 
    static final int STATE_TIMEOUT = 3; 
    static final int STATE_NULL = 4; 
    private TextView tvQuestions;
    private TextView tvTitle;
    private ListView lvInformationList;
    private List<Map<String, Object>> mListData;
    private String mTitle = "";
    private  List<Info> mInfo;
    private File cache;
    private TutorialQuestionAddFragment tutorialQuestionAddFragment;
    private TutorialQuestionContentFragment tutorialQuestionContentFragment;
    private FragmentManager manager; 
    QuestionListAdapter adapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        return inflater.inflate(R.layout.activity_information, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mInfo = new ArrayList<Info>();
        manager = getActivity().getSupportFragmentManager();
        tutorialQuestionAddFragment = new TutorialQuestionAddFragment();
        tutorialQuestionContentFragment = new TutorialQuestionContentFragment();
        tvQuestions = (TextView)getActivity().findViewById(R.id.questions);
        tvQuestions.setVisibility(View.INVISIBLE);
        tvQuestions.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                manager.beginTransaction().replace(R.id.fragment_container, tutorialQuestionAddFragment).addToBackStack(null).commit();
            }
        });
		tvTitle = (TextView)getActivity().findViewById(R.id.message_title);
		tvTitle.setText("搜题");
		lvInformationList = (ListView)getActivity().findViewById(R.id.lv_information_list);
		lvInformationList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                    long arg3) {
                
                tutorialQuestionContentFragment.setID(mListData.get(arg2).get("id").toString());
                manager.beginTransaction().replace(R.id.fragment_container, tutorialQuestionContentFragment).addToBackStack(null).commit();
            }
        });
		if(mListData!=null){
		    Show();
		}
	}
	
	void Show(){
	    mInfo.clear();
	    for(int i=0;i<mListData.size();i++){
	        Info info = new Info();
	        info.setTitle(mListData.get(i).get("question").toString());
            info.setDate(mListData.get(i).get("question_time").toString());
            info.setImage("1");
            info.setObj(mListData.get(i).get("subject").toString());
            mInfo.add(info);
	    }
	    adapter = new QuestionListAdapter(getActivity(),mInfo);
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
            params.put("method", "tutorial_question_get");
            params.put("user", EMChatManager.getInstance().getCurrentUser());
            String path = "http://"+getResources().getString(R.string.server)+"/api.php"; 
            ConnServer server = new ConnServer();
            List<Map<String, Object>> Data = new ArrayList<Map<String,Object>>();
            Data = server.GetData(path,params,edit,handler);
            if(Data.size()>0)
                mListData = Data;
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
                    Toast.makeText(getActivity(), info, 1).show();
                    break;
                }
                case STATE_NULL:
                    //Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.datanull),Toast.LENGTH_SHORT).show(); 
                    break;
                case STATE_LINKERR:
                {
                    String type = msg.getData().getString("type");
                    Toast.makeText(getActivity(), "网络连接失败",Toast.LENGTH_SHORT) .show();  
                    break;
                }
                case STATE_ERROR: 
                    Toast.makeText(getActivity(), "网络连接错误",Toast.LENGTH_SHORT) .show();  
                    break;
               default:  
            } 
        } 
    };
    
    public void setListData(List<Map<String, Object>> list){
        mListData = list;
    }

    @Override
    public void onResume() {
        if(adapter!=null){
            lvInformationList.setAdapter(adapter);
            adapter.notifyDataSetChanged();
        }
        super.onResume();
    }
}
