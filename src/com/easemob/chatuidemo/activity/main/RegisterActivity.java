package com.easemob.chatuidemo.activity.main;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.EMError;
import com.easemob.chat.EMChatManager;
import com.easemob.chatuidemo.DemoApplication;
import com.easemob.chatuidemo.activity.BaseActivity;
import com.easemob.chatuidemo.adapter.ArrayWheelAdapter;
import com.easemob.chatuidemo.adapter.ImageAdapter;
import com.easemob.chatuidemo.adapter.NumericWheelAdapter;
import com.easemob.chatuidemo.domain.Info;
import com.easemob.chatuidemo.utils.ConnServer;
import com.easemob.chatuidemo.utils.DialogDemo;
import com.easemob.chatuidemo.utils.ImageUtils;
import com.easemob.chatuidemo.widget.OnWheelChangedListener;
import com.easemob.chatuidemo.widget.OnWheelScrollListener;
import com.easemob.chatuidemo.widget.WheelView;
import com.easemob.exceptions.EaseMobException;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.wenpy.jcc.R;

/**
 * 注册页
 * 
 */
public class RegisterActivity extends BaseActivity implements OnClickListener, OnWheelChangedListener, OnWheelScrollListener {
    public static final int NONE = 0;   
    public static final int THCREDENTIAL = 1; 
    public static final int STCREDENTIAL = 2; 
    public static final String IMAGE_UNSPECIFIED = "image/*";   
	private EditText etUserName;
	private EditText etPassword;
	private EditText etNick;
    private EditText etMail;
    private EditText etClass;
    private EditText etThCredential;
    private EditText etStCredential;
    private EditText etThSF;
    private EditText etStSF;
    private EditText etColSF;
    private EditText etThSchool;
    private EditText etStSchool;
    private EditText etColSchool;
    private EditText chool;
    private TextView tvBrith;
    private Spinner spSubject;
    private Spinner spThGrade;
    private Spinner spStGrade;
    private RadioGroup rgGender;
    private RadioGroup rgIdentity;
    private RadioButton rbTeacher;
    private RadioButton rbCollega;
    private RadioButton rbStudent;    
    private TextView etStatement;
    private LinearLayout llThAttr;
    private RelativeLayout rlStAttr;
    private RelativeLayout rlColAttr;
    private int mYear=1996;
    private int mMonth=0;
    private int mDay=1;
    private LayoutInflater inflater = null;
    boolean isMonthSetted=false,isDaySetted=false;
    private WheelView year;
    private WheelView month;
    private WheelView day;
    LinearLayout ll;
    private List<Map<String, Object>> mReturnData;
    String strThCredential = "";
    String strStCredential = "";
    String strUPCredential = "";
    private int mPicCount = 0;
    private int mCurSclool = -1;
    private String mIdentity = "3";
    private String mSchool = "";
    private String mGrade = "";
    
    /** 
     * 把全国的省市区的信息以json的格式保存，解析完成后赋值为null 
     */  
    private JSONObject mJsonObj;  
    /** 
     * 省的WheelView控件 
     */  
    private WheelView mProvince;  
    /** 
     * 市的WheelView控件 
     */  
    private WheelView mCity;  
    /** 
     * 区的WheelView控件 
     */  
    private WheelView mArea;  
  
    /** 
     * 所有省 
     */  
    private String[] mProvinceDatas;  
    /** 
     * key - 省 value - 市s 
     */  
    private Map<String, String[]> mCitisDatasMap = new HashMap<String, String[]>();  
    /** 
     * key - 市 values - 区s 
     */  
    private Map<String, String[]> mAreaDatasMap = new HashMap<String, String[]>();  
  
    /** 
     * 当前省的名称 
     */  
    private String mCurrentProviceName = "";  
    /** 
     * 当前市的名称 
     */  
    private String mCurrentCityName = "";  
    /** 
     * 当前区的名称 
     */  
    private String mCurrentAreaName ="";
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_register);
		etUserName = (EditText) findViewById(R.id.username);
		etPassword = (EditText) findViewById(R.id.password);
		etNick = (EditText) findViewById(R.id.nick);
        etMail = (EditText) findViewById(R.id.mail);
        etClass = (EditText) findViewById(R.id.et_st_class);
        etThCredential = (EditText) findViewById(R.id.et_th_credential);
        etStCredential = (EditText) findViewById(R.id.et_st_credential);
        tvBrith = (TextView) findViewById(R.id.brith);
        rgGender = (RadioGroup) findViewById(R.id.gender);
        rgIdentity = (RadioGroup) findViewById(R.id.rg_identity);
        etStatement = (TextView) findViewById(R.id.statement);
        etThSF = (EditText) findViewById(R.id.et_th_sf);
        etStSF= (EditText) findViewById(R.id.et_st_sf);
        etColSF = (EditText) findViewById(R.id.et_col_sf);
        etThSchool = (EditText) findViewById(R.id.et_th_school);
        etStSchool= (EditText) findViewById(R.id.et_st_school);
        etColSchool = (EditText) findViewById(R.id.et_col_school);
        spSubject = (Spinner) findViewById(R.id.sp_th_subject);
        spThGrade = (Spinner) findViewById(R.id.sp_th_grade);
        spStGrade = (Spinner) findViewById(R.id.sp_st_grade);
        llThAttr = (LinearLayout) findViewById(R.id.ll_th_attr);
        rlStAttr = (RelativeLayout) findViewById(R.id.rl_st_attr);
        rlColAttr = (RelativeLayout) findViewById(R.id.rl_col_attr);
        rbTeacher = (RadioButton)findViewById(R.id.rb_teacher);
        rbCollega = (RadioButton)findViewById(R.id.rb_college);
        rbStudent = (RadioButton)findViewById(R.id.rb_student);
        
        etThCredential.setOnClickListener(this);
        etStCredential.setOnClickListener(this);
        etThSF.setOnClickListener(this);
        etStSF.setOnClickListener(this);
        etColSF.setOnClickListener(this);
        etThSchool.addTextChangedListener(new TextWatcher() {
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                    int after) {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public void afterTextChanged(Editable s) {
                mSchool = etThSchool.getText().toString();
            }
        });
        etStSchool.addTextChangedListener(new TextWatcher() {
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                    int after) {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public void afterTextChanged(Editable s) {
                mSchool = etStSchool.getText().toString();
            }
        });
        etColSchool.addTextChangedListener(new TextWatcher() {
            
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                    int after) {
                // TODO Auto-generated method stub
                
            }
            
            @Override
            public void afterTextChanged(Editable s) {
                mSchool = etColSchool.getText().toString();
            }
        });
        spThGrade.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
                    long arg3) {
                mGrade = arg2 + "";
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
                
            }
        }) ;
        spStGrade.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

         @Override
         public void onItemSelected(AdapterView<?> arg0, View arg1, int arg2,
                 long arg3) {
             mGrade = arg2 + "";
         }

         @Override
         public void onNothingSelected(AdapterView<?> arg0) {
             // TODO Auto-generated method stub
             
         }
        }) ;
        
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this, 
                R.array.subject,R.layout.spinnermin);
        adapter.setDropDownViewResource(R.layout.spinnermin);
        spSubject.setAdapter(adapter); 

        ArrayAdapter<CharSequence> gradeAdapter = ArrayAdapter.createFromResource(this, 
                R.array.grade,R.layout.spinnermin);
        gradeAdapter.setDropDownViewResource(R.layout.spinnermin);
       spThGrade.setAdapter(gradeAdapter);
       spStGrade.setAdapter(gradeAdapter);
       
       
        String statement = " 1、用户可以不使用真实姓名注册，但身份属性必须按真实情况填写，否则会影响用户使用各模块的功能。\r\n" +
        		"2、教师、学生按要求注册信息后，自动生成群组；家长的帐号为其小孩帐号后加字母J，如学生账户为123456789，" +
        		"则家长帐号为123456789J，家长帐号随学生所处年级、班级、学校的变化而自动组合成家长群。\r\n" +
        		"3、大学生和中小学生两大群体用户不能使用对方模块功能。\r\n" +
        		"4、所有用户必须遵守国家法律法规，不得发布虚假信息和诋毁损害他人利益的言论，违者本站有权查封其帐号或删帖，" +
        		"造成严重后果的，本站有权追究其发法律责任。";
        etStatement.setText(statement);
        tvBrith.setOnClickListener(new onClick());
        
        rgIdentity.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                LinearLayout.LayoutParams stparams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                LinearLayout.LayoutParams colparams = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                
                switch(checkedId){
                    case R.id.rb_teacher:
                    mIdentity = "3";
                    llThAttr.setVisibility(View.VISIBLE);
                    rlStAttr.setVisibility(View.GONE);
                    rlColAttr.setVisibility(View.GONE);
                    etThCredential.setVisibility(View.VISIBLE);
                    etStCredential.setVisibility(View.GONE);
                    stparams.setMargins(0, dip(RegisterActivity.this,140), 0, 0);
                    colparams.setMargins(0, dip(RegisterActivity.this,20), 0, 0);
                    break;
                    case R.id.rb_college:
                    mIdentity = "2";
                    etThCredential.setVisibility(View.GONE);
                    llThAttr.setVisibility(View.GONE);
                    rlStAttr.setVisibility(View.GONE);
                    rlColAttr.setVisibility(View.VISIBLE);
                    etThCredential.setVisibility(View.GONE);
                    etStCredential.setVisibility(View.VISIBLE);
                    stparams.setMargins(0, dip(RegisterActivity.this,20), 0, 0);
                    colparams.setMargins(0, dip(RegisterActivity.this,20), 0, 0);
                    break;
                    case R.id.rb_student:
                    mIdentity = "1";
                    llThAttr.setVisibility(View.GONE);
                    rlStAttr.setVisibility(View.VISIBLE);
                    rlColAttr.setVisibility(View.GONE);
                    etThCredential.setVisibility(View.GONE);
                    etStCredential.setVisibility(View.GONE);
                    stparams.setMargins(0, dip(RegisterActivity.this,20), 0, 0);
                    colparams.setMargins(0, dip(RegisterActivity.this,100), 0, 0);
                    break;
                }

                rbStudent.setLayoutParams(stparams);
                rbCollega.setLayoutParams(colparams);
            }
        });
        inflater = (LayoutInflater) RegisterActivity.this.getSystemService(LAYOUT_INFLATER_SERVICE);
	}
	public static int dip(Context context,int pxValue) {  
	    return (int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP,pxValue, context.getResources().getDisplayMetrics());  
	 }
    public class onClick implements View.OnClickListener {     

        @Override
        public void onClick(View v) {
            View outerView = getDataPick();
            AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);       
            AlertDialog myDialog = builder.create();
            myDialog.setView(outerView);
            myDialog.show();
            Window window = myDialog.getWindow();
            window.setGravity(Gravity.BOTTOM);
            window.setLayout(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
        }
    }
    
    private View getArray(){
        
        Calendar c = Calendar.getInstance();
        int norYear = c.get(Calendar.YEAR);
//      int curMonth = c.get(Calendar.MONTH) + 1;//通过Calendar算出的月数要+1
//      int curDate = c.get(Calendar.DATE);
        
        int curYear = mYear;
        int curMonth =mMonth+1;
        int curDate = mDay;
        
        View view = inflater.inflate(R.layout.wheel_view, null);
        
        year = (WheelView) view.findViewById(R.id.wheel_view_wv1);
        String[] years = getResources().getStringArray(R.array.baudrates_value);       
        ArrayWheelAdapter<String> arrayWheelAdapter = new ArrayWheelAdapter<String>(this, years);
        year.setViewAdapter(arrayWheelAdapter);
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


    /** 
     * 解析整个Json对象，完成后释放Json对象的内存 
     */  
    private void initDatas()  
    {  
        try  
        {  
            JSONArray jsonArray = mJsonObj.getJSONArray("citylist");  
            mProvinceDatas = new String[jsonArray.length()];  
            for (int i = 0; i < jsonArray.length(); i++)  
            {  
                JSONObject jsonP = jsonArray.getJSONObject(i);// 每个省的json对象  
                String province = jsonP.getString("p");// 省名字  
  
                mProvinceDatas[i] = province;  
  
                JSONArray jsonCs = null;  
                try  
                {  
                    /** 
                     * Throws JSONException if the mapping doesn't exist or is 
                     * not a JSONArray. 
                     */  
                    jsonCs = jsonP.getJSONArray("c");  
                } catch (Exception e1)  
                {  
                    continue;  
                }  
                String[] mCitiesDatas = new String[jsonCs.length()];  
                for (int j = 0; j < jsonCs.length(); j++)  
                {  
                    JSONObject jsonCity = jsonCs.getJSONObject(j);  
                    String city = jsonCity.getString("n");// 市名字  
                    mCitiesDatas[j] = city;  
                    JSONArray jsonAreas = null;  
                    try  
                    {  
                        /** 
                         * Throws JSONException if the mapping doesn't exist or 
                         * is not a JSONArray. 
                         */  
                        jsonAreas = jsonCity.getJSONArray("a");  
                    } catch (Exception e)  
                    {  
                        continue;  
                    }  
  
                    String[] mAreasDatas = new String[jsonAreas.length()];// 当前市的所有区  
                    for (int k = 0; k < jsonAreas.length(); k++)  
                    {  
                        String area = jsonAreas.getJSONObject(k).getString("s");// 区域的名称  
                        mAreasDatas[k] = area;  
                    }  
                    mAreaDatasMap.put(city, mAreasDatas);  
                }  
  
                mCitisDatasMap.put(province, mCitiesDatas);  
            }  
  
        } catch (JSONException e)  
        {  
            e.printStackTrace();  
        }  
        mJsonObj = null;  
    }  
  
    /** 
     * 从assert文件夹中读取省市区的json文件，然后转化为json对象 
     */  
    private void initJsonData()  
    {  
        try
        {  
            StringBuffer sb = new StringBuffer();  
            InputStream is = getAssets().open("city.json");  
            int len = -1;  
            byte[] buf = new byte[1024];  
            while ((len = is.read(buf)) != -1)  
            {  
                sb.append(new String(buf, 0, len, "gbk"));  
            }  
            is.close();  
            mJsonObj = new JSONObject(sb.toString());  
        } catch (IOException e)  
        {  
            e.printStackTrace();  
        } catch (JSONException e)  
        {  
            e.printStackTrace();  
        }  
    }  
    

    /** 
     * 根据当前的市，更新区WheelView的信息 
     */  
    private void updateAreas()  
    {  
        int pCurrent = mCity.getCurrentItem();  
        try {
            mCurrentCityName = mCitisDatasMap.get(mCurrentProviceName)[pCurrent];
        } catch (Exception e) {
            mCurrentCityName = "";
            mCurrentAreaName = "";
            e.printStackTrace();
            return;
        }  
        String[] areas = mAreaDatasMap.get(mCurrentCityName); 
        
        if (areas == null)  
        {  
            areas = new String[] { "" };  
            mCurrentAreaName = "";
        }else
            mCurrentAreaName = areas[0];
        mArea.setViewAdapter(new ArrayWheelAdapter<String>(this, areas));  
        mArea.setCurrentItem(0);  
    }  
  
    /** 
     * 根据当前的省，更新市WheelView的信息 
     */  
    private void updateCities()  
    {  
        int pCurrent = mProvince.getCurrentItem();  
        mCurrentProviceName = mProvinceDatas[pCurrent];  
        String[] cities = mCitisDatasMap.get(mCurrentProviceName);  
        if (cities == null)  
        {  
            cities = new String[] { "" };  
        }  
        mCity.setViewAdapter(new ArrayWheelAdapter<String>(this, cities));  
        mCity.setCurrentItem(0);  
        updateAreas();  
    }  
    
    private View getSanJi() {
        initJsonData(); 
        
        View view = inflater.inflate(R.layout.wheel_view, null);
        
        mProvince = (WheelView) view.findViewById(R.id.wheel_view_wv1);
        mCity = (WheelView) view.findViewById(R.id.wheel_view_wv2);
        mArea = (WheelView) view.findViewById(R.id.wheel_view_wv3);
        initDatas();  
        
        mProvince.setViewAdapter(new ArrayWheelAdapter<String>(this, mProvinceDatas));  
        // 添加change事件  
        mProvince.addChangingListener(this);  
        // 添加change事件  
        mCity.addChangingListener(this);  
        // 添加change事件  
        mArea.addChangingListener(this);          

        mProvince.addScrollingListener(this); 
        mCity.addScrollingListener(this); 
        mArea.addScrollingListener(this);  
  
        mProvince.setVisibleItems(5);  
        mCity.setVisibleItems(5);  
        mArea.setVisibleItems(5);  
        updateCities();  
        updateAreas(); 
        
        return view;
    }
    
    private View getDataPick() {
        Calendar c = Calendar.getInstance();
        int norYear = c.get(Calendar.YEAR);
//      int curMonth = c.get(Calendar.MONTH) + 1;//通过Calendar算出的月数要+1
//      int curDate = c.get(Calendar.DATE);
        
        int curYear = mYear;
        int curMonth =mMonth+1;
        int curDate = mDay;
        
        View view = inflater.inflate(R.layout.wheel_view, null);
        
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
            tvBrith.setText(birthday);
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
	/**
	 * 注册
	 * 
	 * @param view
	 */
	public void register(View view) {
        final String username = etUserName.getText().toString().trim();
        final String pwd = etPassword.getText().toString().trim();
        final String nick = etNick.getText().toString().trim();
        final String mail = etMail.getText().toString().trim();
        final String sclass = etClass.getText().toString().trim();
        final String subject = spSubject.getSelectedItem().toString();
        
        if (TextUtils.isEmpty(username)) {
            Toast.makeText(this, getResources().getString(R.string.User_name_cannot_be_empty), Toast.LENGTH_SHORT).show();
            etUserName.requestFocus();
            return;
        } else if (TextUtils.isEmpty(pwd)) {
            Toast.makeText(this, getResources().getString(R.string.Password_cannot_be_empty), Toast.LENGTH_SHORT).show();
            etPassword.requestFocus();
            return;
        } else if (TextUtils.isEmpty(nick)) {
            Toast.makeText(this, "昵称不能为空", Toast.LENGTH_SHORT).show();
            etNick.requestFocus();
            return;
        } else if (TextUtils.isEmpty(mail)) {
            Toast.makeText(this, "邮箱不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(mCurrentProviceName)) {
            Toast.makeText(this, "省份不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(mSchool)) {
            Toast.makeText(this, "学校名称不能为空", Toast.LENGTH_SHORT).show();
            return;
        }
        switch(Integer.valueOf(mIdentity)){
            case 1:
                if (TextUtils.isEmpty(mGrade)) {
                    Toast.makeText(this, "年级不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(sclass)) {
                    Toast.makeText(this, "班级不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (!TextUtils.isDigitsOnly(sclass)) {
                    Toast.makeText(this, "班级必须为数字", Toast.LENGTH_SHORT).show();
                    return;
                }
                break;
            case 2:
                break;
            case 3:
                if (TextUtils.isEmpty(mGrade)) {
                    Toast.makeText(this, "年级不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                if (TextUtils.isEmpty(subject)) {
                    Toast.makeText(this, "学科不能为空", Toast.LENGTH_SHORT).show();
                    return;
                }
                break;
        }

        DialogDemo.show(this,"正在注册，请稍后...");
        if (!TextUtils.isEmpty(username) && !TextUtils.isEmpty(pwd)) {
            String url = "http://"+getResources().getString(R.string.server)+"/api.php";
            try {
                if(!etThCredential.getText().toString().equals("")){
                    mPicCount ++;
                    uploadFile(etStCredential.getText().toString(), url);
                }else if(!etStCredential.getText().toString().equals("")){
                    mPicCount ++;
                    uploadFile(etStCredential.getText().toString(), url);
                }else{
                    new RegisterThread(mHandler).start();
                }
                    
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
	}	

    /** 
    * @param path 
    *            要上传的文件路径 
    * @param url 
    *            服务端接收URL 
    * @throws Exception 
    */  
    public void uploadFile(String path, String url) throws Exception {
        File file = new File(path);  
        if (file.exists() && file.length() > 0) {  
            AsyncHttpClient client = new AsyncHttpClient();  
            RequestParams params = new RequestParams();  
            params.put("method", "upload_pic"); 
            params.put("uploadedfile", file);
            // 上传文件  
            client.post(url, params, new AsyncHttpResponseHandler() {  
                @Override
                public void onFinish() {
                    //progressBar.setVisibility(View.GONE);
                    mPicCount--;
                    if(mPicCount == 0){                       
                        new RegisterThread(mHandler).start();
                    }
                    super.onFinish();
                }
    
                @Override  
                public void onSuccess(int statusCode, Header[] headers,  
                        byte[] responseBody) {
                    // 上传成功后要做的工作  
                    //Toast.makeText(TalentsAddActivity.this, "上传成功", Toast.LENGTH_LONG).show();  
                    //progress.setProgress(0);
                    ConnServer server = new ConnServer();
                    List<Map<String,Object>> Data = new ArrayList<Map<String,Object>>();

                    try {
                        Data = server.parseJSON(responseBody);
                        //System.out.println("responseBody:"+new String(responseBody)+";data:"+Data);
                        if(Boolean.valueOf(Data.get(0).get("status").toString())){
                            strUPCredential = Data.get(0).get("msg").toString();
                        }
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }  
          
                @Override  
                public void onFailure(int statusCode, Header[] headers,  
                        byte[] responseBody, Throwable error) {  
                    // 上传失败后要做到工作  
                    //Toast.makeText(TalentsAddActivity.this, "上传失败", Toast.LENGTH_LONG).show();  
                }  
          
                @Override  
                public void onProgress(int bytesWritten, int totalSize) {  
                    // TODO Auto-generated method stub  
                    super.onProgress(bytesWritten, totalSize);  
                    int count = (int) ((bytesWritten * 1.0 / totalSize) * 100);  
                    // 上传进度显示  
                    //progress.setProgress(count);  
                    //Log.e("上传 Progress>>>>>", bytesWritten + " / " + totalSize);  
                }  
          
                @Override  
                public void onRetry(int retryNo) {  
                    // TODO Auto-generated method stub  
                    super.onRetry(retryNo);  
                    // 返回重试次数  
                }  
          
            }); 
        }
    }
    
    private class RegisterThread extends Thread { 
         
        private Handler handler; 
 
        public RegisterThread(Handler handler) { 
            this.handler = handler; 
        } 
 
        @Override 
        public void run() { 
            final String username = etUserName.getText().toString().trim();
            final String pwd = etPassword.getText().toString().trim();
            final String nick = etNick.getText().toString().trim();
            final String mail = etMail.getText().toString().trim();
            final String brither = tvBrith.getText().toString().trim();
            final String sClass = etClass.getText().toString().trim();
            String tmp = "";
            if(rgGender.getCheckedRadioButtonId() == R.id.female){
                tmp = "女";
            }else{
                tmp = "男";
            }
            final String gender = tmp;
            final String subject = spSubject.getSelectedItem().toString();
            
            String url = "http://"+getResources().getString(R.string.server)+"/api.php";
            ConnServer server = new ConnServer();
            String edit = "register";
            Map<String, String> params = new HashMap<String, String>();
            params.put("method", edit);
            params.put("name", username);
            params.put("password", pwd);
            params.put("nick", nick);
            params.put("mail", mail);
            params.put("gender", gender);
            params.put("brith", brither);
            params.put("identity", mIdentity);
            params.put("province", mCurrentProviceName);
            params.put("city", mCurrentCityName);
            params.put("county", mCurrentAreaName);
            params.put("school", mSchool);
            params.put("grade", mGrade);
            params.put("class", sClass);
            params.put("subject", subject);
            server.GetData(url,params, edit, mHandler);
        }
    }
    
	private Handler mHandler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            DialogDemo.dismiss(); 
            switch(msg.what){
                case STATE_FINISH:
                    Intent mIntent = new Intent();  
                    mIntent.putExtra("username", etUserName.getText().toString());
                    RegisterActivity.this.setResult(2, mIntent);
                    finish();
                    Toast.makeText(getApplicationContext(), "注册成功", 0).show();
                    break;
                case STATE_ERROR:
                    Toast.makeText(getApplicationContext(), "network error", 1).show();                    
                    break;
                case STATE_FAIL:
                    Toast.makeText(getApplicationContext(), msg.obj+"", 1).show();
                    break;
            }
        };
    };
    
	public void back(View view) {
		finish();
	}

    @Override
    public void onClick(View arg0) {
        switch (arg0.getId()) {
        case R.id.et_th_credential:
        {
            Intent intent = new Intent(Intent.ACTION_PICK, null);   
            intent.setDataAndType(   
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,   
                    IMAGE_UNSPECIFIED);   
            startActivityForResult(intent, THCREDENTIAL);
            break;
        }
        case R.id.et_st_credential:
        {
            Intent intent = new Intent(Intent.ACTION_PICK, null);   
            intent.setDataAndType(   
                    MediaStore.Images.Media.EXTERNAL_CONTENT_URI,   
                    IMAGE_UNSPECIFIED);   
            startActivityForResult(intent, STCREDENTIAL);
            break;
        }
        case R.id.et_th_sf:
        {
            mCurSclool = 0;
            View outerView = getSanJi();
            AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);       
            AlertDialog myDialog = builder.create();
            myDialog.setView(outerView);
            myDialog.show();
            Window window = myDialog.getWindow();
            window.setGravity(Gravity.BOTTOM);
            window.setLayout(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
            break;
        }
        case R.id.et_st_sf:
        {
            mCurSclool = 1;
            View outerView = getSanJi();
            AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);       
            AlertDialog myDialog = builder.create();
            myDialog.setView(outerView);
            myDialog.show();
            Window window = myDialog.getWindow();
            window.setGravity(Gravity.BOTTOM);
            window.setLayout(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
            break;
        }
        case R.id.et_col_sf:
        {
            mCurSclool = 2;
            View outerView = getSanJi();
            AlertDialog.Builder builder = new AlertDialog.Builder(RegisterActivity.this);       
            AlertDialog myDialog = builder.create();
            myDialog.setView(outerView);
            myDialog.show();
            Window window = myDialog.getWindow();
            window.setGravity(Gravity.BOTTOM);
            window.setLayout(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
            break;
        }
        default:
            break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == NONE)   
            return;   

        Uri uri = data.getData();
        Log.e("requestCode","requestCode = " + requestCode);
        if (requestCode == THCREDENTIAL) {
            try {
                Bitmap bitmap = ImageUtils.compressImage(
                        MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri),
                        500);
                saveMyBitmap(bitmap, "thcredential");
                strThCredential = Environment.getExternalStorageDirectory()+
                        "/cache/thcredential.jpg";
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            etThCredential.setText(strThCredential);            
        }

        if (requestCode == STCREDENTIAL) {   
            try {
                Bitmap bitmap = ImageUtils.compressImage(
                        MediaStore.Images.Media.getBitmap(this.getContentResolver(), uri),
                        500);
                saveMyBitmap(bitmap, "stcredential");
                strStCredential = Environment.getExternalStorageDirectory()+
                        "/cache/stcredential.jpg";
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            etStCredential.setText(strStCredential);            
        }  
    }
    
    public void saveMyBitmap(Bitmap mBitmap,String bitName)  {
        File f = new File( Environment.getExternalStorageDirectory()+
                "/cache/" + bitName + ".jpg");
        Log.e("File","File = " + f);
        FileOutputStream fOut = null;
        try {
           fOut = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
                e.printStackTrace();
        }
        mBitmap.compress(Bitmap.CompressFormat.JPEG, 50, fOut);
        try {
            fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    @Override
    public void onChanged(WheelView wheel, int oldValue, int newValue) {
        if (wheel == mProvince)  
        {  
            updateCities();  
        } else if (wheel == mCity)  
        {  
            updateAreas();  
        } else if (wheel == mArea)  
        {  
            mCurrentAreaName = mAreaDatasMap.get(mCurrentCityName)[newValue];
        } 
    }


    @Override
    public void onScrollingStarted(WheelView wheel) {
        // TODO Auto-generated method stub
        
    }


    @Override
    public void onScrollingFinished(WheelView wheel) {
        switch(mCurSclool){
            case 0:
                etThSF.setText(mCurrentProviceName + mCurrentCityName + mCurrentAreaName);
                break;
            case 1:
                etStSF.setText(mCurrentProviceName + mCurrentCityName + mCurrentAreaName);
                break;
            case 2:
                etColSF.setText(mCurrentProviceName + mCurrentCityName + mCurrentAreaName);
                break;
        }
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
