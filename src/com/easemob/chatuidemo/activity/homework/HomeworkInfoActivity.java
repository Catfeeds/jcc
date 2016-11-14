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
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
import com.easemob.chat.core.r;
import com.easemob.chatuidemo.DemoApplication;
import com.easemob.chatuidemo.DemoHXSDKModel;
import com.wenpy.jcc.R;
import com.easemob.chatuidemo.activity.BaseActivity;
import com.easemob.chatuidemo.activity.group.GroupSelectActivity;
import com.easemob.chatuidemo.activity.main.MainActivity;
import com.easemob.chatuidemo.adapter.ImageAdapter;
import com.easemob.chatuidemo.adapter.TalentAdapter;
import com.easemob.chatuidemo.domain.Info;
import com.easemob.chatuidemo.utils.ConnServer;
import com.easemob.chatuidemo.utils.DialogDemo;
import com.easemob.exceptions.EaseMobException;
import com.google.android.gms.internal.bt;
import com.google.android.gms.internal.da;

/**
 * 
 * 
 */
public class HomeworkInfoActivity extends BaseActivity implements OnClickListener {
    private ListView lvInformationList;
    private TextView tvSeeQuestion;
    private TextView tvFromTH;
    private EditText etContent;
    private LinearLayout llMainST;
    private LinearLayout llMainTH;
    private ImageView ivFile;
    private TextView tvFile;
    private RelativeLayout rlPolitical;
    private RelativeLayout rlChinese;
    private RelativeLayout rlMath;
    private RelativeLayout rlEnglish;
    private RelativeLayout rlPhysics;
    private RelativeLayout rlChemistry;
    private RelativeLayout rlGeography;
    private RelativeLayout rlHistory;
    private RelativeLayout rlOther;
    private TextView tvMsgPolitical;
    private TextView tvMsgChinese;
    private TextView tvMsgMath;
    private TextView tvMsgEnglish;
    private TextView tvMsgPhysics;
    private TextView tvMsgChemistry;
    private TextView tvMsgGeography;
    private TextView tvMsgHistory;
    private TextView tvMsgOther;
    private Button btSubmit;
    private File cache;
    private List<Map<String, Object>> mListData;
    private int mIdentity;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_homework_info);
		
		tvSeeQuestion = (TextView)findViewById(R.id.see_question);
		tvFromTH = (TextView)findViewById(R.id.text);
		llMainST = (LinearLayout)findViewById(R.id.main_st);
        llMainTH = (LinearLayout)findViewById(R.id.main_th);
        etContent = (EditText)findViewById(R.id.et_content);
        ivFile =  (ImageView)findViewById(R.id.iv_file);
        tvFile = (TextView)findViewById(R.id.tv_file);
		
		rlPolitical = (RelativeLayout)findViewById(R.id.rl_political);
		rlChinese = (RelativeLayout)findViewById(R.id.rl_chinese);
		rlMath = (RelativeLayout)findViewById(R.id.rl_math);
		rlEnglish = (RelativeLayout)findViewById(R.id.rl_english);
		rlPhysics = (RelativeLayout)findViewById(R.id.rl_physics);
		rlChemistry = (RelativeLayout)findViewById(R.id.rl_chemistry);
		rlGeography = (RelativeLayout)findViewById(R.id.rl_geography);
		rlHistory = (RelativeLayout)findViewById(R.id.rl_history);
		rlOther = (RelativeLayout)findViewById(R.id.rl_other);

        tvMsgPolitical = (TextView)findViewById(R.id.unread_msg_number_political);
        tvMsgChinese = (TextView)findViewById(R.id.unread_msg_number_chinese);
        tvMsgMath = (TextView)findViewById(R.id.unread_msg_number_math);
        tvMsgEnglish = (TextView)findViewById(R.id.unread_msg_number_english);
        tvMsgPhysics = (TextView)findViewById(R.id.unread_msg_number_physics);
        tvMsgChemistry = (TextView)findViewById(R.id.unread_msg_number_chemistry);
        tvMsgGeography = (TextView)findViewById(R.id.unread_msg_number_geography);
        tvMsgHistory = (TextView)findViewById(R.id.unread_msg_number_history);
        tvMsgOther = (TextView)findViewById(R.id.unread_msg_number_other);
        btSubmit = (Button)findViewById(R.id.submit);
        
        tvSeeQuestion.setOnClickListener(this);
		rlPolitical.setOnClickListener(this);
		rlChinese.setOnClickListener(this);
		rlMath.setOnClickListener(this);
		rlEnglish.setOnClickListener(this);
        rlPhysics.setOnClickListener(this);
        rlChemistry.setOnClickListener(this);
        rlGeography.setOnClickListener(this);
        rlHistory.setOnClickListener(this);
        rlOther.setOnClickListener(this);
        btSubmit.setOnClickListener(this);
        ivFile.setOnClickListener(this);

        initView();
	}

	private void initView(){
        if(DemoApplication.getInstance().getIdentity() == 3){
            tvFromTH.setVisibility(View.GONE);
            llMainST.setVisibility(View.GONE);
            llMainTH.setVisibility(View.VISIBLE);
            btSubmit.setVisibility(View.VISIBLE);
            tvSeeQuestion.setVisibility(View.VISIBLE);
        }else{
            tvSeeQuestion.setVisibility(View.GONE);
            tvFromTH.setVisibility(View.VISIBLE);
            llMainST.setVisibility(View.VISIBLE);
            llMainTH.setVisibility(View.GONE);
            btSubmit.setVisibility(View.GONE);
            
            DialogDemo.show(this,"正在加载，请稍后...");
            new GetDataThread(handler).start();
        }
	}
	
    private class GetDataThread extends Thread { 
         
        private Handler handler; 
 
        public GetDataThread(Handler handler) { 
            this.handler = handler; 
        } 
 
        @Override 
        public void run() { 
            String edit = "inform_count_get";
            Map<String, String> params = new HashMap<String, String>();
            params.put("method", edit);
            params.put("user", EMChatManager.getInstance().getCurrentUser());
            
            ConnServer server = new ConnServer();
            String path = "http://"+getResources().getString(R.string.server)+"/api.php";
            mListData = server.GetData(path,params,edit,handler);
        }
    }   

    void Show(){
        List<Info> Infos = new ArrayList<Info>();
        for(int i=0;i<mListData.size();i++){
           String subject = mListData.get(i).get("subject").toString();
           int count = Integer.valueOf(mListData.get(i).get("count").toString());
           if(count > 0){
               if(subject.equals("数学")){
                   tvMsgMath.setText(count+"");
                   tvMsgMath.setVisibility(View.VISIBLE);
               }else  if(subject.equals("语文")){
                   tvMsgChinese.setText(count+"");    
                   tvMsgChinese.setVisibility(View.VISIBLE);               
               }else  if(subject.equals("英语")){
                   tvMsgEnglish.setText(count+"");       
                   tvMsgEnglish.setVisibility(View.VISIBLE);            
               }else  if(subject.equals("物理")){
                   tvMsgPhysics.setText(count+"");    
                   tvMsgPhysics.setVisibility(View.VISIBLE);               
               }else  if(subject.equals("化学")){
                   tvMsgChemistry.setText(count+""); 
                   tvMsgChemistry.setVisibility(View.VISIBLE);        
               }else  if(subject.equals("政治")){
                   tvMsgPolitical.setText(count+""); 
                   tvMsgPolitical.setVisibility(View.VISIBLE);
               }else  if(subject.equals("历史")){
                   tvMsgHistory.setText(count+"");  
                   tvMsgHistory.setVisibility(View.VISIBLE);
               }else  if(subject.equals("地理")){
                   tvMsgGeography.setText(count+"");    
                   tvMsgGeography.setVisibility(View.VISIBLE);               
               }else{
                   tvMsgOther.setText(count+"");  
                   tvMsgOther.setVisibility(View.VISIBLE);                         
               }
           }else{
               Handler handler = DemoApplication.getInstance().getHandler();               
               Message msg = handler.obtainMessage(); 
               msg.what = STATE_HOMEWORK_READ;
               handler.sendMessage(msg);
           }
               
        }        
    }
    
	public void back(View view) {
		finish();
	}

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.rl_political:
        case R.id.rl_chinese:
        case R.id.rl_math:
        case R.id.rl_english:
        case R.id.rl_physics:
        case R.id.rl_chemistry:
        case R.id.rl_geography:
        case R.id.rl_history:
        case R.id.rl_other:
                Intent intent = new Intent(getApplicationContext(),
                        HomeworkInfoDetailActivity.class);
                Bundle extras = new Bundle();
                extras.putInt("id", v.getId());
                intent.putExtras(extras);
                startActivity(intent);
            break;
        case R.id.submit:
            if(etContent.getText().toString().equals("")){
                Toast.makeText(HomeworkInfoActivity.this, "内容不能为空！", 0).show();
                return;
            }
            intent = new Intent(getApplicationContext(),GroupSelectActivity.class);
            intent.putExtra("content", etContent.getText().toString());
            intent.putExtra("file", tvFile.getText().toString());
            startActivityForResult(intent, 100);
            break;
        case R.id.see_question:
            intent = new Intent(getApplicationContext(),HomeworkListTeacherActivity.class);
            startActivity(intent);
            break;
        default:
            break;
        }
    }

    private final Handler handler = new Handler(Looper.getMainLooper()) {
         
        public void handleMessage(Message msg) { // 处理Message，更新ListView
            DialogDemo.dismiss();
            System.out.println("msg.obj:"+msg.obj);
            switch(msg.what){ 
                case STATE_FINISH: 
                {
                    Show();
                }
                    break;
                case STATE_FAIL:
                {
                    String info = "Fail!";
                    if(msg.obj != null){
                        info = info+"\n"+msg.obj;
                    }
                    Toast.makeText(HomeworkInfoActivity.this, info, 1).show();
                    break;
                }
                case STATE_NULL:
                    //Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.datanull),Toast.LENGTH_SHORT).show(); 
                    break;
                case STATE_LINKERR:
                {
                    Toast.makeText(HomeworkInfoActivity.this, "网络连接失败",Toast.LENGTH_SHORT) .show();  
                    break;
                }
                case STATE_ERROR: 
                    Toast.makeText(HomeworkInfoActivity.this, "网络连接错误",Toast.LENGTH_SHORT) .show();  
                    break;
               default:  
            } 
        } 
    }; 
    
    @Override
    protected void onActivityResult(int requestCode , int resultCode, Intent data) {
        if(resultCode == RESULT_OK){
            etContent.setText("");
            tvFile.setText("");
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

}
