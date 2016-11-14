package com.easemob.chatuidemo.activity.talent;

import java.io.File;
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
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
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
public class TalentsSearchActivity extends BaseActivity {

    private GridView gvResult;
    private Button btSearch;
    private EditText etTitle;
    private EditText etSchool;
    private EditText etStDate;
    private EditText etEdDate;
    private TextView tvTitle;
    private List<Map<String, Object>> mListData;
    private File cache;
    private int mYear=1996;
    private int mMonth=0;
    private int mDay=1;
    View view=null;
    private LayoutInflater inflater = null;
    boolean isMonthSetted=false,isDaySetted=false;
    private WheelView year;
    private WheelView month;
    private WheelView day;
    private String mType;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_talents_search);
        mListData = new ArrayList<Map<String,Object>>();

        Intent intent = getIntent();
        mType = intent.getStringExtra("type");
        
		gvResult = (GridView)this.findViewById(R.id.gv_result);
		btSearch = (Button)findViewById(R.id.bt_search);
        etTitle = (EditText)findViewById(R.id.search_title);
        etSchool = (EditText)findViewById(R.id.search_school);
        etStDate = (EditText)findViewById(R.id.search_stdate);
        etEdDate = (EditText)findViewById(R.id.search_eddata);
        tvTitle = (TextView)findViewById(R.id.message_title); 

        if(mType.equals("seikatsu")){
            tvTitle.setText("生活秀");
        }else{
            tvTitle.setText("师生风采");            
        }
        btSearch.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                DialogDemo.show(TalentsSearchActivity.this,"正在加载，请稍后...");
                new GetDataThread(handler).start();
            }
        });
        gvResult.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                    long arg3) {
                Intent intent = new Intent(TalentsSearchActivity.this,
                        TalentsImgViewActivity.class);
                Bundle extras = new Bundle();
                extras.putString("id", mListData.get(arg2).get("id").toString());
                intent.putExtras(extras);
                startActivity(intent); 
            }
        });
        etStDate.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                View outerView = getDataPick();
                AlertDialog.Builder builder = new AlertDialog.Builder(TalentsSearchActivity.this);       
                AlertDialog myDialog = builder.create();
                myDialog.setView(outerView);
                myDialog.show();
                Window window = myDialog.getWindow();
                window.setGravity(Gravity.BOTTOM);
                window.setLayout(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
            }
        });
        etEdDate.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                    View outerView = getDataPick1();
                    AlertDialog.Builder builder = new AlertDialog.Builder(TalentsSearchActivity.this);       
                    AlertDialog myDialog = builder.create();
                    myDialog.setView(outerView);
                    myDialog.show();
                    Window window = myDialog.getWindow();
                    window.setGravity(Gravity.BOTTOM);
                    window.setLayout(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
                }
        });
        inflater = (LayoutInflater) TalentsSearchActivity.this.getSystemService(LAYOUT_INFLATER_SERVICE);
        //创建缓存目录，系统一运行就得创建缓存目录的，
        cache = new File(Environment.getExternalStorageDirectory(), "cache");
        
        if(!cache.exists()){
            cache.mkdirs();
        }        
	}
	
    private View getDataPick() {
        Calendar c = Calendar.getInstance();
        int norYear = c.get(Calendar.YEAR);
//      int curMonth = c.get(Calendar.MONTH) + 1;//通过Calendar算出的月数要+1
//      int curDate = c.get(Calendar.DATE);
        
        int curYear = mYear;
        int curMonth =mMonth+1;
        int curDate = mDay;
        
        view = inflater.inflate(R.layout.wheel_view, null);
        
        year = (WheelView) view.findViewById(R.id.wheel_view_wv1);
        NumericWheelAdapter numericWheelAdapter1=new NumericWheelAdapter(this,1950, norYear); 
        numericWheelAdapter1.setLabel("年");
        year.setViewAdapter(numericWheelAdapter1);
        year.setCyclic(false);//是否可循环滑动
        year.addScrollingListener(scrollListener);
        
        month = (WheelView) view.findViewById(R.id.wheel_view_wv2);
        NumericWheelAdapter numericWheelAdapter2=new NumericWheelAdapter(this,1, 12, "%02d"); 
        numericWheelAdapter2.setLabel("月");
        month.setViewAdapter(numericWheelAdapter2);
        month.setCyclic(false);
        month.addScrollingListener(scrollListener);
        
        day = (WheelView) view.findViewById(R.id.wheel_view_wv3);
        initDay(curYear,curMonth);
        day.setCyclic(false);
        day.addScrollingListener(scrollListener);
        
        year.setVisibleItems(7);//设置显示行数
        month.setVisibleItems(7);
        day.setVisibleItems(7);

        year.setCurrentItem(curYear - 1950);
        month.setCurrentItem(curMonth - 1);
        day.setCurrentItem(curDate - 1);
        return view;
    }
    
    OnWheelScrollListener scrollListener = new OnWheelScrollListener() {
        @Override
        public void onScrollingStarted(WheelView wheel) {

        }

        @Override
        public void onScrollingFinished(WheelView wheel) {
            int n_year = year.getCurrentItem() + 1950;//年
            int n_month = month.getCurrentItem() + 1;//月
            
            initDay(n_year,n_month);
            
            String birthday=new StringBuilder().append((year.getCurrentItem()+1950)).append("-").append((month.getCurrentItem() + 1) < 10 ? "0" + (month.getCurrentItem() + 1) : (month.getCurrentItem() + 1)).append("-").append(((day.getCurrentItem()+1) < 10) ? "0" + (day.getCurrentItem()+1) : (day.getCurrentItem()+1)).toString();
            Log.d("server", birthday);
            etStDate.setText(birthday);
        }
    };

    private View getDataPick1() {
        Calendar c = Calendar.getInstance();
        int norYear = c.get(Calendar.YEAR);
//      int curMonth = c.get(Calendar.MONTH) + 1;//通过Calendar算出的月数要+1
//      int curDate = c.get(Calendar.DATE);
        
        int curYear = mYear;
        int curMonth =mMonth+1;
        int curDate = mDay;
        
        view = inflater.inflate(R.layout.wheel_view, null);
        
        year = (WheelView) view.findViewById(R.id.wheel_view_wv1);
        NumericWheelAdapter numericWheelAdapter1=new NumericWheelAdapter(this,1950, norYear); 
        numericWheelAdapter1.setLabel("年");
        year.setViewAdapter(numericWheelAdapter1);
        year.setCyclic(false);//是否可循环滑动
        year.addScrollingListener(scrollListener1);
        
        month = (WheelView) view.findViewById(R.id.wheel_view_wv2);
        NumericWheelAdapter numericWheelAdapter2=new NumericWheelAdapter(this,1, 12, "%02d"); 
        numericWheelAdapter2.setLabel("月");
        month.setViewAdapter(numericWheelAdapter2);
        month.setCyclic(false);
        month.addScrollingListener(scrollListener1);
        
        day = (WheelView) view.findViewById(R.id.wheel_view_wv3);
        initDay(curYear,curMonth);
        day.setCyclic(false);
        day.addScrollingListener(scrollListener1);
        
        year.setVisibleItems(7);//设置显示行数
        month.setVisibleItems(7);
        day.setVisibleItems(7);

        year.setCurrentItem(curYear - 1950);
        month.setCurrentItem(curMonth - 1);
        day.setCurrentItem(curDate - 1);
        return view;
    }
    
    OnWheelScrollListener scrollListener1 = new OnWheelScrollListener() {
        @Override
        public void onScrollingStarted(WheelView wheel) {

        }

        @Override
        public void onScrollingFinished(WheelView wheel) {
            int n_year = year.getCurrentItem() + 1950;//年
            int n_month = month.getCurrentItem() + 1;//月
            
            initDay(n_year,n_month);
            
            String birthday=new StringBuilder().append((year.getCurrentItem()+1950)).append("-").append((month.getCurrentItem() + 1) < 10 ? "0" + (month.getCurrentItem() + 1) : (month.getCurrentItem() + 1)).append("-").append(((day.getCurrentItem()+1) < 10) ? "0" + (day.getCurrentItem()+1) : (day.getCurrentItem()+1)).toString();
            Log.d("server", birthday);
            etEdDate.setText(birthday);
        }
    };
    
    private void initDay(int arg1, int arg2) {
        NumericWheelAdapter numericWheelAdapter=new NumericWheelAdapter(this,1, getDay(arg1, arg2), "%02d");
        numericWheelAdapter.setLabel("日");
        day.setViewAdapter(numericWheelAdapter);
    }

    private int getDay(int year, int month) {
        int day = 30;
        boolean flag = false;
        switch (year % 4) {
        case 0:
            flag = true;
            break;
        default:
            flag = false;
            break;
        }
        switch (month) {
        case 1:
        case 3:
        case 5:
        case 7:
        case 8:
        case 10:
        case 12:
            day = 31;
            break;
        case 2:
            day = flag ? 29 : 28;
            break;
        default:
            day = 30;
            break;
        }
        return day;
    }
	
	void Show(){
	    List<Info> Infos = new ArrayList<Info>();
        for(int i=0;i<mListData.size();i++){
            Info info = new Info();
            info.setImage(mListData.get(i).get("skin").toString());
            info.setTitle(mListData.get(i).get("title").toString());
            Infos.add(info);
        }
//             Log.d("server", "size:"+contacts.size()+"=>"+contacts.get(0).getImage());
        GridImageAdapter mAdapter = new GridImageAdapter(this,Infos,cache);
        gvResult.setAdapter(mAdapter);
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
            params.put("method", "elegant_demeanour_get");
            params.put("title", etTitle.getText().toString());
            params.put("start_date", etStDate.getText().toString());
            params.put("end_date", etEdDate.getText().toString());
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
                if(mListData.get(0).get("data").toString().equals("null")){
                    msg.what = STATE_NULL;                    
                }else{
                    msg.what = STATE_FINISH;
                    mListData = server.Json2List(mListData.get(0).get("data").toString());
                    System.out.println("mData:"+mListData.size());
                }
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
                    Toast.makeText(TalentsSearchActivity.this, info, 1).show();
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
