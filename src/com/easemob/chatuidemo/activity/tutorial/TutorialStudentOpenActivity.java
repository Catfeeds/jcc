package com.easemob.chatuidemo.activity.tutorial;

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
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.PopupMenu.OnMenuItemClickListener;
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
 * 学生开通辅导
 * 
 */
public class TutorialStudentOpenActivity extends BaseActivity {
    private FragmentManager manager;
    private TutorailAgreementFragment tutorailAgreementFragment;
    private TutorialQuestionListFragment tutorialQuestionListFragment;
    private TutorialTeacherOpenFragment teacherOpenFragment;
    private List<Map<String, Object>> mListData;
    private String from;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_tutorial_student_open);
		
		mListData = new ArrayList<Map<String,Object>>();
	    manager = getSupportFragmentManager();
	    tutorailAgreementFragment = new TutorailAgreementFragment();
	    tutorialQuestionListFragment = new TutorialQuestionListFragment();
	    teacherOpenFragment = new TutorialTeacherOpenFragment();
	    TutorailSubjectFragment tutorailSubjectFragment = new TutorailSubjectFragment();
	    
	    Intent intent = getIntent();
	    from = intent.getStringExtra("from");
	    System.out.println(from);
	    int hasTut = DemoApplication.getInstance().getTutorial();
	    int identity = DemoApplication.getInstance().getIdentity();
	    if(from.equals("adjust_subject")){
	        tutorailSubjectFragment.setFrom(from);
	        manager.beginTransaction().replace(R.id.fragment_container, tutorailSubjectFragment).commit();
	    }else  if(DemoApplication.getInstance().getTutorial() == -1){
	        new GetDataThread(mHandler).start();
	    }else if(from.equals("student")){
            manager.beginTransaction().replace(R.id.fragment_container, tutorailAgreementFragment).commit();	        
	    }else if(from.equals("other")){
            manager.beginTransaction().replace(R.id.fragment_container, teacherOpenFragment).commit();	        
		}else{
		    if(hasTut == 1){
		        manager.beginTransaction().replace(R.id.fragment_container, tutorialQuestionListFragment).commit();
		    }else{
		        if(identity == 1)
		            manager.beginTransaction().replace(R.id.fragment_container, tutorailAgreementFragment).commit();
		        else
		            manager.beginTransaction().replace(R.id.fragment_container, teacherOpenFragment).commit();
		    }
		}		    
	}
	
	public void Show(){
	    if(mListData.size()>0){
	        manager.beginTransaction().replace(R.id.fragment_container, tutorialQuestionListFragment).commit();
	    }else
            manager.beginTransaction().replace(R.id.fragment_container, tutorailAgreementFragment).commit();
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
            mListData = server.GetData(path,params,edit,handler);
        }
    }
    
    private Handler mHandler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            DialogDemo.dismiss(); 
            switch(msg.what){
                case STATE_FINISH:
                    Show();
                    break;
                case STATE_ERROR:
                    Toast.makeText(getApplicationContext(), "network error", 0).show();                    
                    break;
                case STATE_FAIL:
                    Toast.makeText(getApplicationContext(), msg.obj+"", 0).show();
                    break;
            }
        };
    };
    /**
     * 返回
     * 
     * @param view
     */
    public void back(View view) {
        int count = manager.getBackStackEntryCount();
        System.out.println("cu:"+count);
        if(count == 0)
            finish();
        else
            manager.popBackStack();
    }
    

    private void deleteAllFiles(File root){
        File files[] = root.listFiles();  
        if (files != null)  
            for (File f : files) {  
                if (f.isDirectory()) { // 判断是否为文件夹  
                    deleteAllFiles(f);  
                    try {  
                        f.delete();  
                    } catch (Exception e) {  
                    }  
                } else {  
                    if (f.exists()) { // 判断是否存在  
                        deleteAllFiles(f);  
                        try {  
                            f.delete();  
                        } catch (Exception e) {  
                        }  
                    }  
                }  
            }
    }
    
    @Override
    protected void onDestroy() {
        deleteAllFiles(new File(Environment.getExternalStorageDirectory()+ "/cache/"));
        super.onDestroy();
    }
}