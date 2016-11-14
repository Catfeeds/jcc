package com.easemob.chatuidemo.activity.job;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentManager;
import android.app.AlertDialog;
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
import android.view.Window;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

import com.baidu.android.bbalbs.common.a.b;
import com.baidu.mapapi.cloud.BoundSearchInfo;
import com.baidu.mapapi.map.Text;
import com.easemob.EMError;
import com.easemob.applib.controller.HXSDKHelper;
import com.easemob.chat.EMChatManager;
import com.easemob.chatuidemo.DemoApplication;
import com.easemob.chatuidemo.DemoHXSDKModel;
import com.wenpy.jcc.R;
import com.easemob.chatuidemo.activity.BaseActivity;
import com.easemob.chatuidemo.activity.tutorial.TutorailAgreementFragment;
import com.easemob.chatuidemo.activity.tutorial.TutorailSubjectFragment;
import com.easemob.chatuidemo.activity.tutorial.TutorialQuestionListFragment;
import com.easemob.chatuidemo.activity.tutorial.TutorialTeacherOpenFragment;
import com.easemob.chatuidemo.adapter.ImageAdapter;
import com.easemob.chatuidemo.adapter.TalentAdapter;
import com.easemob.chatuidemo.domain.Info;
import com.easemob.chatuidemo.utils.ConnServer;
import com.easemob.chatuidemo.utils.DialogDemo;
import com.easemob.exceptions.EaseMobException;
import com.google.android.gms.internal.da;

/**
 * 职位内容
 * 
 */
public class JobDetailActivity extends BaseActivity {
    private TextView tvCompanyIntro;
    private TextView tvPosition;
    private TextView tvJobIntro;
    private Button btApply;
    private LinearLayout llAddResume;
    private LinearLayout llSubmitResume;
    private LinearLayout llProgressBar;
    private Button btAddResume;
    private TextView tvEdit;
    private EditText etResumeName;
    private ProgressBar bar;
    private List<Map<String, Object>> mJobData;
    private List<Map<String, Object>> mRetuenData;
    private String id;
    public List<Map<String, Object>> mResumeData;
    private boolean isLoad = true;
    public String resume_id = "";
    private AlertDialog alertDialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_job_detail);
		
		mJobData = new ArrayList<Map<String,Object>>();
		mResumeData = new ArrayList<Map<String,Object>>();
		
	    Intent intent = getIntent();
	    id = intent.getStringExtra("id");
	    System.out.println(id);	   

	    tvCompanyIntro = (TextView)findViewById(R.id.company_intro);
	    tvPosition = (TextView)findViewById(R.id.position);
	    tvJobIntro = (TextView)findViewById(R.id.job_intro);
	    btApply = (Button)findViewById(R.id.apply);
	    
	    btApply.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View arg0) {
                new GetResumeThread(handler).start();
                alertDialog = new AlertDialog.Builder(JobDetailActivity.this).create();  
                View alertDialogView = View.inflate(JobDetailActivity.this, R.layout.activity_resume_diag, null);
                alertDialog.setView(alertDialogView);
                alertDialog.show();
                Window window = alertDialog.getWindow();  
                window.setContentView(R.layout.activity_resume_diag);  
                llAddResume = (LinearLayout) window.findViewById(R.id.ll_add_resume);
                llProgressBar = (LinearLayout) window.findViewById(R.id.ll_progressBar);
                llSubmitResume = (LinearLayout) window.findViewById(R.id.ll_submit_resume);
                etResumeName = (EditText) window.findViewById(R.id.default_resume);  
                tvEdit = (TextView) window.findViewById(R.id.edit_name);  
                btApply = (Button) window.findViewById(R.id.bt_apply); 
                btAddResume = (Button) window.findViewById(R.id.add_resume); 
                bar = (ProgressBar) window.findViewById(R.id.progressBar); 
                if(isLoad){
                    llAddResume.setVisibility(View.GONE);
                    llSubmitResume.setVisibility(View.GONE);
                    llProgressBar.setVisibility(View.VISIBLE);
                }else{
                    llProgressBar.setVisibility(View.GONE);
                    if(mResumeData.size()>0){
                        etResumeName.setText(mResumeData.get(0).get("resume_name").toString());  
                        llAddResume.setVisibility(View.GONE);
                    }else
                        llSubmitResume.setVisibility(View.GONE);
                }
                    
                btApply.setOnClickListener(new OnClickListener() {
                    
                    @Override
                    public void onClick(View arg0) {
                        DialogDemo.show(JobDetailActivity.this,2);
                        new SendResumeThread(handler).start();
                    }
                });
                btAddResume.setOnClickListener(new OnClickListener() {
                    
                    @Override
                    public void onClick(View arg0) {
                        alertDialog.dismiss();
                        Intent intent = new Intent(JobDetailActivity.this, ResumeActivity.class);
                        intent.putExtra("from", "add");
                        intent.putExtra("job_id", id);
                        startActivity(intent);
                    }
                });
            }
        });
	    
        DialogDemo.show(this,2);
        new GetDataThread(handler).start();
	}
	
	public void Show(){
	   tvCompanyIntro.setText(mJobData.get(0).get("company_intro").toString());
	   tvPosition.setText(mJobData.get(0).get("position").toString());
	   tvJobIntro.setText(mJobData.get(0).get("job_intro").toString());
	}

    private class GetDataThread extends Thread { 
         
        private Handler handler; 
 
        public GetDataThread(Handler handler) { 
            this.handler = handler; 
        }
 
        @Override 
        public void run() {
            String edit = "job_get";
            Map<String, String> params = new HashMap<String, String>();
            params.put("method", edit);
            params.put("id", id);
            String path = "http://"+getResources().getString(R.string.server)+"/api.php"; 
            ConnServer server = new ConnServer();
            mJobData = server.GetData(path,params,edit,handler);
        }
    }

    private class GetResumeThread extends Thread { 
         
        private Handler handler; 
 
        public GetResumeThread(Handler handler) { 
            this.handler = handler; 
        }
 
        @Override 
        public void run() {
            String edit = "resume_get";
            Map<String, String> params = new HashMap<String, String>();
            params.put("method", edit);
            params.put("user", EMChatManager.getInstance().getCurrentUser());
            String path = "http://"+getResources().getString(R.string.server)+"/api.php"; 
            ConnServer server = new ConnServer();
            mResumeData = server.GetData(path,params,edit,handler);
        }
    }

    private class SendResumeThread extends Thread { 
         
        private Handler handler; 
 
        public SendResumeThread(Handler handler) { 
            this.handler = handler; 
        }
 
        @Override 
        public void run() {
            String edit = "job_resume_add";
            Map<String, String> params = new HashMap<String, String>();
            params.put("method", edit);
            params.put("job_id", id);
            params.put("resume_id", resume_id );
            String path = "http://"+getResources().getString(R.string.server)+"/api.php"; 
            ConnServer server = new ConnServer();
            mRetuenData = server.GetData(path,params,edit,handler);
        }
    }
    
    private Handler handler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            DialogDemo.dismiss(); 
            switch(msg.what){
                case STATE_FINISH:
                    String op = msg.obj.toString();
                    if(mJobData.size()>0 && op.equals("job_get"))
                        Show();
                    if(op.equals("resume_get")){
                        isLoad = false;
                        if(mResumeData.size()>0)
                            resume_id = mResumeData.get(0).get("id").toString();
                        if(llProgressBar != null){
                            llProgressBar.setVisibility(View.GONE);
                            if(mResumeData.size()>0){
                                llAddResume.setVisibility(View.GONE);
                                llSubmitResume.setVisibility(View.VISIBLE);
                            }else{
                                llAddResume.setVisibility(View.VISIBLE);
                                llSubmitResume.setVisibility(View.GONE);
                            }
                        }
                    }
                    if(op.equals("job_resume_add")){
                        Toast.makeText(getApplicationContext(), "提交成功！", 0).show();
                        if(alertDialog != null)
                            alertDialog.dismiss();
                    }
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
}