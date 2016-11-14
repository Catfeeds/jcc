package com.easemob.chatuidemo.activity.job;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

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
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
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
import com.easemob.chatuidemo.adapter.ImageAdapter;
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
 * 就业
 * 
 */
public class JobActivity extends BaseActivity implements OnWheelChangedListener, OnWheelScrollListener {
    private TextView tvTitle;
    private TextView tvArea;
    private TextView tvSearch;
    private TextView tvMyResume;
    private ListView lvJobList;
    private List<Map<String, Object>> mReturnData;
    private List<Map<String, Object>> mFilterData;

    private JSONObject mJsonObj;
    private WheelView mProvince;
    private WheelView mCity;
    private WheelView mArea;
    private String[] mProvinceDatas;
    private int iProvSelect = 0;
    private int iCitySelect = 0;
    private int iAreaSelect = 0;
    private Map<String, String[]> mCitisDatasMap = new HashMap<String, String[]>();  
    private Map<String, String[]> mAreaDatasMap = new HashMap<String, String[]>();  
    private String mCurrentProviceName = "";  
    private String mCurrentCityName = "";  
    private String mCurrentAreaName ="";
    private LayoutInflater inflater = null;
    private JobAdapter adapter;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_job);
        
		initView();
		initListener();
		initData();
	}

    private void initView() {
        tvTitle = (TextView)findViewById(R.id.message_title);
        tvArea = (TextView)findViewById(R.id.tv_area);
        tvSearch = (TextView)findViewById(R.id.tv_search);
        tvMyResume = (TextView)findViewById(R.id.my_resume);
        lvJobList = (ListView)this.findViewById(R.id.lv_job_list);
    }

    private void initListener() {
        tvArea.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View arg0) {
                View outerView = getSanJi();
                AlertDialog.Builder builder = new AlertDialog.Builder(JobActivity.this);       
                AlertDialog myDialog = builder.create();
                myDialog.setView(outerView);
                myDialog.show();
                Window window = myDialog.getWindow();
                window.setGravity(Gravity.BOTTOM);
                window.setLayout(LayoutParams.MATCH_PARENT,LayoutParams.WRAP_CONTENT);
            }
        });
        tvSearch.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View arg0) {
                mFilterData.clear();
                for(int i=0;i<mReturnData.size();i++){
                    Map<String, Object> map = new HashMap<String, Object>();
                    map = mReturnData.get(i);
                    if(map.containsValue(mCurrentProviceName))
                        mFilterData.add(map);
                }
                adapter.notifyDataSetChanged();
            }
        });
        lvJobList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                    long arg3) {
                Intent intent = new Intent(JobActivity.this,
                        JobDetailActivity.class);
                Bundle extras = new Bundle();
                extras.putString("id", mReturnData.get(arg2).get("id").toString());
                intent.putExtras(extras);
                startActivity(intent); 
            }
        });
        tvMyResume.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(JobActivity.this, ResumeActivity.class);
                intent.putExtra("from", "my");
                startActivity(intent);                
            }
        });
    }
    
    private void initData() {
        inflater = (LayoutInflater) JobActivity.this.getSystemService(LAYOUT_INFLATER_SERVICE);
        mReturnData = new ArrayList<Map<String,Object>>();
        mFilterData = new ArrayList<Map<String,Object>>();
        DialogDemo.show(this,"正在加载，请稍后...");
        new GetDataThread(handler).start();
    }

    void Show(){
        mFilterData.addAll(mReturnData);
        adapter = new JobAdapter(this,mFilterData);
        lvJobList.setAdapter(adapter);
	}

    private class GetDataThread extends Thread { 
         
        private Handler handler; 
 
        public GetDataThread(Handler handler) { 
            this.handler = handler; 
        } 
 
        @Override 
        public void run() {
            Map<String, String> params = new HashMap<String, String>();
            String edit = "job_get";
            params.put("method", edit);
            String url = "http://"+getResources().getString(R.string.server)+"/api.php";      
            ConnServer server = new ConnServer();
            mReturnData = server.GetData(url, params, edit, handler);
        }
    }
    
    public class JobAdapter extends BaseAdapter{
        private Context context;  
        private List<Map<String, Object>> list;
        private LayoutInflater mInflater;  

        private class ViewHolder {
            TextView company;
            TextView position;
        }
        
        // 自己定义的构造函数  
        public JobAdapter(Context context, List<Map<String,Object>> list) {  
            this.context = context;  
            this.list = list;  
      
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
        }  
        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return list.size();
        }

        @Override
        public Map<String, Object> getItem(int arg0) {
            // TODO Auto-generated method stub
            return list.get(arg0);
        }

        @Override
        public long getItemId(int arg0) {
            // TODO Auto-generated method stub
            return arg0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if(convertView == null){
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.row_job, null);
                holder.company = (TextView) convertView.findViewById(R.id.company);
                holder.position = (TextView) convertView.findViewById(R.id.position);
                convertView.setTag(holder);
            }else{
                holder = (ViewHolder) convertView.getTag();
            }
            holder.company.setText(list.get(position).get("company").toString());
            holder.position.setText(list.get(position).get("position").toString());
            
            return convertView;
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
                    Toast.makeText(JobActivity.this, info, 1).show();
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

    @Override
    protected void onActivityResult(int arg0, int arg1, Intent arg2) {
        if(arg1 == 100){
            DialogDemo.show(this,"正在加载，请稍后...");
            new GetDataThread(handler).start();
        }
        super.onActivityResult(arg0, arg1, arg2);
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
        mArea.setVisibility(View.GONE);
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

        mProvince.setCurrentItem(iProvSelect);
        mCity.setCurrentItem(iCitySelect);  
        mArea.setCurrentItem(iAreaSelect);
        
        mProvince.setVisibleItems(5);
        mCity.setVisibleItems(5);  
        mArea.setVisibleItems(5);  
        updateCities();  
        updateAreas(); 
        
        return view;
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
        tvArea.setText(mCurrentProviceName + "|"+ mCurrentCityName);
    }
}
