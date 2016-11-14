package com.easemob.chatuidemo.activity.tutorial;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.easemob.applib.controller.HXSDKHelper;
import com.easemob.chat.EMChatManager;
import com.easemob.chatuidemo.Constant;
import com.easemob.chatuidemo.DemoApplication;
import com.easemob.chatuidemo.DemoHXSDKHelper;
import com.easemob.chatuidemo.DemoHXSDKModel;
import com.easemob.chatuidemo.adapter.ContactAdapter;
import com.easemob.chatuidemo.adapter.ImageAdapter;
import com.easemob.chatuidemo.domain.Info;
import com.easemob.chatuidemo.domain.User;
import com.easemob.chatuidemo.utils.ConnServer;
import com.easemob.chatuidemo.utils.DialogDemo;
import com.wenpy.jcc.R;

import android.support.v4.app.FragmentManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class TutorailSubjectFragment extends Fragment {
    private static final int STATE_ERROR = -1; 
    private static final int STATE_FAIL = 0; 
    private static final int STATE_FINISH = 1; 
    private static final int STATE_LINKERR = 2; 
    private static final int STATE_TIMEOUT = 3; 
    private static final int STATE_NULL = 4; 
    private TextView tvTitle;
    private CheckBox cbPolitical;
    private CheckBox cbChinese;
    private CheckBox cbMath;
    private CheckBox cbEnglish;
    private CheckBox cbPhysics;
    private CheckBox cbChemistry;
    private CheckBox cbGeography;
    private CheckBox cbHistory;
    private Button btSend;
    private List<Map<String, Object>> mSubjectData;
    private File cache;
    private String mID;
    private Map<String, String> mSubject= new HashMap<String, String>();
    private List<Map<String, Object>> mListData;
    private Map<String, Object> OSubject  = new HashMap<String, Object>();
    private String mFrom="";
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        return inflater.inflate(R.layout.fragment_tutorail_subject, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);        
        tvTitle = (TextView)getActivity().findViewById(R.id.message_title);
        if(mFrom.equals("adjust_subject")){
            tvTitle.setText("调整课程");
        }
        cbChinese = (CheckBox)getActivity().findViewById(R.id.cb_chinese);
        cbMath = (CheckBox)getActivity().findViewById(R.id.cb_math);
        cbPolitical = (CheckBox)getActivity().findViewById(R.id.cb_political);
        cbEnglish = (CheckBox)getActivity().findViewById(R.id.cb_english);
        cbPhysics = (CheckBox)getActivity().findViewById(R.id.cb_physics);
        cbChemistry = (CheckBox)getActivity().findViewById(R.id.cb_chemistry);
        cbGeography = (CheckBox)getActivity().findViewById(R.id.cb_geography);
        cbHistory = (CheckBox)getActivity().findViewById(R.id.cb_history);
        OSubject.put("语文", cbChinese);
        OSubject.put("数学", cbMath);
        OSubject.put("政治", cbPolitical);
        OSubject.put("英语", cbEnglish);
        OSubject.put("物理", cbPhysics);
        OSubject.put("化学", cbChemistry);
        OSubject.put("地理", cbGeography);
        OSubject.put("历史", cbHistory);
        btSend = (Button)getActivity().findViewById(R.id.send);
        
        DialogDemo.show(getActivity(),"正在加载，请稍后...");
        new GetDataThread(mHandler).start();
        init();
    }
    
    private void init(){
        cbChinese.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    mSubject.put("chinese", "语文");
                }else{
                    mSubject.remove("chinese");
                }
            }
        });
        cbMath.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    mSubject.put("math", "数学");
                }else{
                    mSubject.remove("math");
                }
            }
        });
        cbPolitical.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    mSubject.put("political", "政治");
                }else{
                    mSubject.remove("political");
                }
            }
        });
        cbEnglish.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    mSubject.put("english", "英语");
                }else{
                    mSubject.remove("english");
                }
            }
        });
        cbPhysics.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    mSubject.put("physics", "物理");
                }else{
                    mSubject.remove("physics");
                }
            }
        });
        cbChemistry.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    mSubject.put("chemistry", "化学");
                }else{
                    mSubject.remove("chemistry");
                }
            }
        });
        cbGeography.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    mSubject.put("geography", "地理");
                }else{
                    mSubject.remove("geography");
                }
            }
        });
        cbHistory.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked){
                    mSubject.put("history", "历史");
                }else{
                    mSubject.remove("history");
                }
            }
        });
        btSend.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                DialogDemo.show(getActivity(),"正在提交，请稍后...");
                new SaveDataThread(mHandler).start();
            }
        });
    }
    
    public void show(){
        System.out.println("CheckBox="+OSubject);
        for(int i=0;i<mSubjectData.size();i++){
            CheckBox checkBox = (CheckBox)OSubject.get(mSubjectData.get(i).get("subject").toString());
            if(checkBox != null)
                checkBox.setChecked(true);
        }
    }

    private class GetDataThread extends Thread { 
         
        private Handler handler; 
 
        public GetDataThread(Handler handler) { 
            this.handler = handler; 
        }
 
        @Override 
        public void run() {
            String edit = "tutorial_get";
            Map<String, String> params = new HashMap<String, String>();
            params.put("method", edit);
            params.put("user", EMChatManager.getInstance().getCurrentUser());
            String path = "http://"+getResources().getString(R.string.server)+"/api.php"; 
            ConnServer server = new ConnServer();
            mSubjectData = server.GetData(path,params,edit,handler);
        }
    }
    
    private class SaveDataThread extends Thread { 
         
        private Handler handler; 
 
        public SaveDataThread(Handler handler) { 
            this.handler = handler; 
        }
 
        @Override 
        public void run() {
            String edit = "tutorial_add";
            StringBuffer subjects = new StringBuffer();
            subjects.append("[");
            for (String v : mSubject.values()) {  
                subjects.append("{\"subject\":\"").append(v).append("\"},");
            } 
//            for(int i=0;i<mSubject.size();i++){
//                subjects.append("{\"subject\":\"").append(mSubject[i]).append("\"},");
//            }
            if(subjects.length() > 1)
                subjects.deleteCharAt(subjects.length()-1);
            subjects.append("]");
            
            Map<String, String> params = new HashMap<String, String>();
            params.put("method", edit);
            params.put("user", EMChatManager.getInstance().getCurrentUser());
            params.put("subjects", subjects.toString());
            String path = "http://"+getResources().getString(R.string.server)+"/api.php"; 
            ConnServer server = new ConnServer();
            mListData = server.GetData(path,params,edit,handler);
        }
    }
    
    public void setFrom(String from){
        mFrom = from;
    }
    
    private Handler mHandler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            DialogDemo.dismiss(); 
            switch(msg.what){
                case STATE_FINISH:
                    if(msg.obj.toString().equals("tutorial_get")){
                        show();
                    }else{
                        DemoApplication.getInstance().setTutorial(1);
    //                    FragmentManager manager = getActivity().getSupportFragmentManager();
    //                    QuestionAddFragment questionAddFragment  = new QuestionAddFragment();
    //                    manager.beginTransaction().replace(R.id.fragment_container, questionAddFragment).commit();
                        getActivity().finish();
                        if(mFrom.equals("adjust_subject"))
                            Toast.makeText(getActivity(), "调整课程成功", 1).show();
                        else
                            Toast.makeText(getActivity(), "已成功开通，点击问题随手拍可调整课程", 1).show();
                    }
                    break;
                case STATE_ERROR:
                    Toast.makeText(getActivity(), "network error", 0).show();
                    getActivity().finish();
                    break;
                case STATE_FAIL:
                    Toast.makeText(getActivity(), msg.obj+"", 0).show();
                    break;
            }
        };
    };    
}
