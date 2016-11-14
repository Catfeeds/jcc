package com.easemob.chatuidemo.activity.question;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Spinner;
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
import com.easemob.chatuidemo.activity.PublicForFragmentActivity;
import com.easemob.chatuidemo.adapter.ArrayWheelAdapter;
import com.easemob.chatuidemo.adapter.GridImageAdapter;
import com.easemob.chatuidemo.adapter.ImageAdapter;
import com.easemob.chatuidemo.adapter.NumericWheelAdapter;
import com.easemob.chatuidemo.adapter.TalentAdapter;
import com.easemob.chatuidemo.domain.Info;
import com.easemob.chatuidemo.utils.ConnServer;
import com.easemob.chatuidemo.utils.DialogDemo;
import com.easemob.chatuidemo.widget.OnWheelScrollListener;
import com.easemob.chatuidemo.widget.WheelView;
import com.easemob.exceptions.EaseMobException;
import com.google.android.gms.internal.da;

/**
 * 师生风采查询
 * 
 */
public class QuestionSearchActivity extends BaseActivity {
    private RadioGroup rgLevel;
    private Button btSearch;
    private EditText etKeyword;
    private Spinner spGrade;
    private Spinner spSubject;
    private Spinner spType;
    private TextView tvTitle;
    private ImageButton ibGoto;
    private LinearLayout llMsg;
    private List<Map<String, Object>> mListData;
    private List<Map<String, Object>> mTypeData;
    private ArrayAdapter<String> typeAdapter;
    private String[] arrType = new String[]{};
    private LayoutInflater inflater = null;
    boolean isMonthSetted=false,isDaySetted=false;
    private WheelView year;
    private WheelView month;
    private WheelView day;
    private String mType;
    private String mLevel = "小学";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_question_search);
        mListData = new ArrayList<Map<String,Object>>();
        mTypeData = new ArrayList<Map<String,Object>>();

        Intent intent = getIntent();
        mType = intent.getStringExtra("type");

        rgLevel = (RadioGroup)findViewById(R.id.rg_level);
        spGrade = (Spinner)findViewById(R.id.grade);
        spSubject = (Spinner)findViewById(R.id.subject);
        spType = (Spinner)findViewById(R.id.type);
		btSearch = (Button)findViewById(R.id.bt_search);
		etKeyword = (EditText)findViewById(R.id.search_keyword);
		llMsg = (LinearLayout)findViewById(R.id.main_msg);
        ibGoto = (ImageButton)findViewById(R.id.ibgoto);

        rgLevel.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            
            @Override
            public void onCheckedChanged(RadioGroup arg0, int arg1) {
                switch(arg1){
                    case R.id.rbXiaoxue:
                        mLevel = "小学";
                        break;
                    case R.id.rbChuzhong:
                        mLevel = "初中";
                        break;
                    case R.id.rbGaozhong:
                        mLevel = "高中";
                        break;                    
                }
            }
        });
	    ArrayAdapter<CharSequence> gradeAdapter = ArrayAdapter.createFromResource(this, 
	            R.array.grade,android.R.layout.simple_spinner_item);
	    gradeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    spGrade.setAdapter(gradeAdapter); 
	       
		typeAdapter= new ArrayAdapter<String>(this, 
                android.R.layout.simple_spinner_item,arrType);
	    typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    spType.setAdapter(typeAdapter);
       
	    ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, 
               R.array.subject,android.R.layout.simple_spinner_item);
	    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
	    spSubject.setAdapter(adapter); 
       
	    spSubject.setOnItemSelectedListener(new OnItemSelectedListener() {

           @Override
           public void onItemSelected(AdapterView<?> arg0, View arg1,
                   int arg2, long arg3) {
              String strSubject = spSubject.getSelectedItem().toString();
              List tmp = new ArrayList<String>();
              tmp.add("");
              for(int i=0;i<mTypeData.size();i++){
                  if(mTypeData.get(i).get("parent").toString().equals(strSubject)){
                      tmp.add(mTypeData.get(i).get("name").toString());
                  }
              }
              arrType = new String[tmp.size()];
              arrType = (String[])tmp.toArray(new String[tmp.size()]);
              typeAdapter= new ArrayAdapter<String>(QuestionSearchActivity.this,
                      android.R.layout.simple_spinner_item,arrType);
              typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
              spType.setAdapter(typeAdapter);
           }

           @Override
           public void onNothingSelected(AdapterView<?> arg0) {
               // TODO Auto-generated method stub
               
           }
       });
	    btSearch.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                llMsg.setVisibility(View.INVISIBLE);
                DialogDemo.show(QuestionSearchActivity.this,"正在搜索，请稍后...");
                new SearchThread(handler).start();
            }
        });
        
        ibGoto.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(QuestionSearchActivity.this, PublicForFragmentActivity.class);
                intent.putExtra("from", "question_add");
                startActivity(intent);
            }
        });
        DialogDemo.show(this,"正在加载，请稍后...");
        new GetTypeThread(handler).start();
	}
	
	void Show(){
	    if(mListData.size()>0){
    	    Intent intent = new Intent(QuestionSearchActivity.this, PublicForFragmentActivity.class);
    	    intent.putExtra("from", "question_search");
            intent.putExtra("list", (Serializable)mListData);
            startActivity(intent);
	    }else
            llMsg.setVisibility(View.VISIBLE);
	}
	
    private class GetTypeThread extends Thread { 

        private Handler handler; 
 
        public GetTypeThread(Handler handler) { 
            this.handler = handler; 
        } 
 
        @Override 
        public void run() { 
            String edit = "question_type_get";
            Map<String, String> params = new HashMap<String, String>();
            params.put("method", edit);
            
            ConnServer server = new ConnServer();
            String path = "http://"+getResources().getString(R.string.server)+"/api.php";
            mTypeData = server.GetData(path,params,edit,handler);
        }
    }
    

    private class SearchThread extends Thread { 

        private Handler handler; 
 
        public SearchThread(Handler handler) { 
            this.handler = handler; 
        } 
 
        @Override 
        public void run() { 
            String edit = "question_search";
            Map<String, String> params = new HashMap<String, String>();
            params.put("method", edit);
            params.put("level", mLevel);
            params.put("grade", spGrade.getSelectedItemPosition()==0?"":spGrade.getSelectedItemPosition()+"");
            params.put("subject", spSubject.getSelectedItem().toString());
            params.put("type", spType.getCount()>0?spType.getSelectedItem().toString():"");
            params.put("keyword", etKeyword.getText().toString());
            
            ConnServer server = new ConnServer();
            String path = "http://"+getResources().getString(R.string.server)+"/api.php";
            mListData = server.GetData(path,params,edit,handler);
        }
    }

    private final Handler handler = new Handler(Looper.getMainLooper()) {
         
        public void handleMessage(Message msg) { // 处理Message，更新ListView
            DialogDemo.dismiss();
            switch(msg.what){ 
                case STATE_FINISH: 
                {
                    if(msg.obj.toString().equals("question_type_get")){
                        if(mTypeData.size()>0){
                            List tmp = new ArrayList<String>();
                            for(int i=0;i<mTypeData.size();i++){
                                if(mTypeData.get(i).get("name").toString().equals(spSubject.getSelectedItem().toString()))
                                    tmp.add(mTypeData.get(i).get("name").toString());
                            }
                            arrType = (String[])tmp.toArray(new String[tmp.size()]);
                            typeAdapter= new ArrayAdapter<String>(QuestionSearchActivity.this, 
                                    android.R.layout.simple_spinner_item,arrType);
                            typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spType.setAdapter(typeAdapter);
                        }
                    }else if(msg.obj.toString().equals("question_search")){
                        Show();
                    }
                }
                    break;
                case STATE_FAIL:
                {
                    String info = "Error!";
                    if(msg.obj != null){
                        info = info+"\n"+msg.obj;
                    }
                    Toast.makeText(QuestionSearchActivity.this, info, 1).show();
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
