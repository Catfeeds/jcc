package com.easemob.chatuidemo.activity.ta;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.support.v4.app.Fragment;
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
import android.widget.RadioButton;
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
import com.easemob.chatuidemo.activity.user.UserGridViewFragment;
import com.easemob.chatuidemo.adapter.ArrayWheelAdapter;
import com.easemob.chatuidemo.adapter.GridImageAdapter;
import com.easemob.chatuidemo.adapter.ImageAdapter;
import com.easemob.chatuidemo.adapter.NumericWheelAdapter;
import com.easemob.chatuidemo.adapter.TalentAdapter;
import com.easemob.chatuidemo.domain.Info;
import com.easemob.chatuidemo.utils.ConnServer;
import com.easemob.chatuidemo.utils.DialogDemo;
import com.easemob.chatuidemo.widget.OnWheelChangedListener;
import com.easemob.chatuidemo.widget.OnWheelScrollListener;
import com.easemob.chatuidemo.widget.WheelView;
import com.easemob.exceptions.EaseMobException;
import com.google.android.gms.internal.da;

/**
 * 找ta搜索条件
 * 
 */
public class TaSearchFilterFragment extends Fragment implements  OnWheelChangedListener, OnWheelScrollListener {
    static final int STATE_ERROR = -1; 
    static final int STATE_FAIL = 0; 
    static final int STATE_FINISH = 1; 
    static final int STATE_LINKERR = 2; 
    static final int STATE_TIMEOUT = 3; 
    static final int STATE_NULL = 4; 
    private EditText etPlace;
    private EditText etSchool;
    private EditText etName;
    private RadioGroup rgGender;
    private RadioButton rbMale;
    private RadioButton rbFemale;
    private Button btSearch;
    private LinearLayout llMsg;

    private JSONObject mJsonObj;  
    private WheelView mProvince;  
    private WheelView mCity;  
    private WheelView mArea;  
    private String[] mProvinceDatas;  
    private Map<String, String[]> mCitisDatasMap = new HashMap<String, String[]>();  
    private Map<String, String[]> mAreaDatasMap = new HashMap<String, String[]>();  
    private String mCurrentProviceName = "";  
    private String mCurrentCityName = "";  
    private String mCurrentAreaName ="";
    private String mGender = "";
    private LayoutInflater inflater = null;
    private List<Map<String, Object>> mReturnData;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        return inflater.inflate(R.layout.fragment_user_search, container, false);
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);

        etPlace = (EditText)getActivity().findViewById(R.id.et_place);
        etSchool = (EditText)getActivity().findViewById(R.id.et_school);
        rgGender = (RadioGroup)getActivity().findViewById(R.id.gender);
        rbFemale  = (RadioButton)getActivity().findViewById(R.id.female);
        rbMale  = (RadioButton)getActivity().findViewById(R.id.male);
        etName = (EditText)getActivity().findViewById(R.id.et_name);
		btSearch = (Button)getActivity().findViewById(R.id.bt_search);
		llMsg = (LinearLayout)getActivity().findViewById(R.id.main_msg);

		rgGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            
            @Override
            public void onCheckedChanged(RadioGroup arg0, int arg1) {
                switch(arg1){
                    case R.id.male:
                        mGender = "男";
                        break;
                    case R.id.female:
                        mGender = "女";
                        break;
                    default:
                        mGender = "";
                        break;
                }
            }
        });
		etPlace.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                View outerView = getSanJi();
                AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());       
                AlertDialog myDialog = builder.create();
                myDialog.setView(outerView);
                myDialog.show();
                Window window = myDialog.getWindow();
                window.setGravity(Gravity.BOTTOM);
                window.setLayout(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
            }
        });
	    btSearch.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                llMsg.setVisibility(View.GONE);
                DialogDemo.show(getActivity(),"正在搜索，请稍后...");
                new SearchThread(handler).start();
            }
        });
        inflater = (LayoutInflater) getActivity().getSystemService("layout_inflater");
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
            InputStream is = getActivity().getAssets().open("city.json");  
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
        mArea.setViewAdapter(new ArrayWheelAdapter<String>(getActivity(), areas));  
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
        mCity.setViewAdapter(new ArrayWheelAdapter<String>(getActivity(), cities));  
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
        
        mProvince.setViewAdapter(new ArrayWheelAdapter<String>(getActivity(), mProvinceDatas));  
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
    
	void Show(){
	    if(mReturnData.size()>0){
	        FragmentManager manager = getActivity().getSupportFragmentManager();
	        UserGridViewFragment userGridViewFragment = new UserGridViewFragment();
	        userGridViewFragment.setData(mReturnData);
	        manager.beginTransaction().replace(R.id.fragment_container, userGridViewFragment).addToBackStack(null).commit();
        }else
            llMsg.setVisibility(View.VISIBLE);
	}

    private class SearchThread extends Thread { 

        private Handler handler; 
 
        public SearchThread(Handler handler) { 
            this.handler = handler; 
        } 
 
        @Override 
        public void run() { 
            String edit = "college_info_get";
            Map<String, String> params = new HashMap<String, String>();
            params.put("method", edit);
            params.put("school", etSchool.getText().toString());
            params.put("province", mCurrentProviceName);
            params.put("city", mCurrentCityName);
            params.put("county", mCurrentAreaName);
            params.put("name", etName.getText().toString());
            params.put("gender", mGender);
            
            ConnServer server = new ConnServer();
            String path = "http://"+getActivity().getResources().getString(R.string.server)+"/api.php";
            mReturnData = server.GetData(path,params,edit,handler);
        }
    }

    private final Handler handler = new Handler(Looper.getMainLooper()) {
         
        public void handleMessage(Message msg) { // 处理Message，更新ListView
            DialogDemo.dismiss();
            switch(msg.what){ 
                case STATE_FINISH: 
                {
                    if(msg.obj.toString().equals("college_info_get")){                       
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

    @Override
    public void onScrollingStarted(WheelView wheel) {
        // TODO Auto-generated method stub
        
    }

    @Override
    public void onScrollingFinished(WheelView wheel) {

        etPlace.setText(mCurrentProviceName + mCurrentCityName + mCurrentAreaName);
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

}
