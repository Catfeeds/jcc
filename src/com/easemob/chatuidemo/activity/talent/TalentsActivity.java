package com.easemob.chatuidemo.activity.talent;

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
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.AbsListView.OnScrollListener;
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
import com.easemob.chat.EMCursorResult;
import com.easemob.chat.EMGroupInfo;
import com.easemob.chat.EMGroupManager;
import com.easemob.chatuidemo.DemoApplication;
import com.easemob.chatuidemo.DemoHXSDKModel;
import com.wenpy.jcc.R;
import com.easemob.chatuidemo.activity.BaseActivity;
import com.easemob.chatuidemo.activity.group.GroupSelectActivity;
import com.easemob.chatuidemo.adapter.ImageAdapter;
import com.easemob.chatuidemo.adapter.TalentAdapter;
import com.easemob.chatuidemo.domain.Info;
import com.easemob.chatuidemo.utils.ConnServer;
import com.easemob.chatuidemo.utils.DialogDemo;
import com.easemob.exceptions.EaseMobException;
import com.google.android.gms.internal.da;

/**
 * 风采
 * 
 */
public class TalentsActivity extends BaseActivity {
    private TextView tvTitle;
    private ListView lvTalentsList;
    private List<Map<String, Object>> mListData = new ArrayList<Map<String,Object>>();
    private File cache;
    private ImageView ivMenu;
    private PopupMenu popupMenu;  
    private Menu menu;  
    public  TalentAdapter mAdapter;
    private String mType;
    private String mEdit;
    private boolean isFirstLoading = true;
    private boolean hasMoreData = true;
    private boolean isLoading;
    private LinearLayout footLoadingLayout;
    private ProgressBar footLoadingPB;
    private TextView footLoadingText;
    private int start = 0;
    private int pagesize = 15;
    private List<Info> Infos = new ArrayList<Info>();
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_talents);

        Intent intent = getIntent();
        mType = intent.getStringExtra("type");
        
		tvTitle = (TextView)findViewById(R.id.message_title);
		lvTalentsList = (ListView)this.findViewById(R.id.lv_talents_list);
		ivMenu = (ImageView)findViewById(R.id.menu);

        View footView = getLayoutInflater().inflate(R.layout.listview_footer_view, null);
        footLoadingLayout = (LinearLayout) footView.findViewById(R.id.loading_layout);
        footLoadingPB = (ProgressBar)footView.findViewById(R.id.loading_bar);
        footLoadingText = (TextView) footView.findViewById(R.id.loading_text);
        lvTalentsList.addFooterView(footView, null, false);
        footLoadingLayout.setVisibility(View.GONE);
        
		lvTalentsList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                    long arg3) {
                Intent intent = new Intent(TalentsActivity.this,
                        TalentsImgViewActivity.class);
                Bundle extras = new Bundle();
                extras.putString("id", mListData.get(arg2).get("id").toString());
                intent.putExtra("type", mType);
                intent.putExtras(extras);
                startActivity(intent); 
            }
        });
		lvTalentsList.setOnScrollListener(new OnScrollListener() {
            
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if(scrollState == OnScrollListener.SCROLL_STATE_IDLE){
//                    if(!hasMoreData){
//                        footLoadingLayout.setVisibility(View.VISIBLE);
//                        footLoadingPB.setVisibility(View.GONE);
//                        footLoadingText.setText("No more data");
//                        adapter.notifyDataSetChanged();
//                    }
                    if(lvTalentsList.getCount() != 0){
                        int lasPos = view.getLastVisiblePosition();
                        if(hasMoreData && !isLoading && lasPos == lvTalentsList.getCount()-1){
                            loadAndShowData();
                            footLoadingLayout.setVisibility(View.VISIBLE);
                        }
                    }
                }
            }
            
            @Override
            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                
            }
        });
		
		ivMenu.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                popupMenu.show();
            }
        });
		popupMenu = new PopupMenu(this, ivMenu);  
        menu = popupMenu.getMenu();  

        
        if(mType.equals("seikatsu")){
            tvTitle.setText("生活秀");
            menu.add(Menu.NONE, Menu.FIRST + 0, 0, "秀一下");  
            mEdit = "life_show_get";
        }else{
            tvTitle.setText("师生风采");            
            menu.add(Menu.NONE, Menu.FIRST + 0, 0, "上传我们的风采");  
            mEdit = "elegant_demeanour_get";
        }
        
        // 通过代码添加菜单项  
        menu.add(Menu.NONE, Menu.FIRST + 1, 1, "刷新");  
        menu.add(Menu.NONE, Menu.FIRST + 2, 2, "查询");  
  
        // 通过XML文件添加菜单项  
        MenuInflater menuInflater = getMenuInflater();  
        menuInflater.inflate(R.menu.popupmenu, menu);  
  
        // 监听事件  
        popupMenu.setOnMenuItemClickListener(new OnMenuItemClickListener() {  
  
            @Override  
            public boolean onMenuItemClick(MenuItem item) {  
                switch (item.getItemId()) { 
                case Menu.FIRST + 0: 
                    {
                        Intent intent = new Intent(TalentsActivity.this,
                                TalentsAddActivity.class);
                        intent.putExtra("type", mType);
                        startActivityForResult(intent, 100);      
                    }
                    break;
                case Menu.FIRST + 1:  
                    DialogDemo.show(TalentsActivity.this,"正在加载，请稍后...");
                    new GetDataThread(handler).start();
                    break;  
                case Menu.FIRST + 2:  
                    {
                        Intent intent = new Intent(TalentsActivity.this,
                                TalentsSearchActivity.class);
                        intent.putExtra("type", mType);
                        startActivity(intent);
                    }
                    break;  
                default:  
                    break;  
                }  
                return false;  
            }  
        });  
        //创建缓存目录，系统一运行就得创建缓存目录的，
        cache = new File(Environment.getExternalStorageDirectory(), "cache");
        
        if(!cache.exists()){
            cache.mkdirs();
        }        
		mListData = new ArrayList<Map<String,Object>>();
		

        mAdapter = new TalentAdapter(this,Infos ,cache);
        lvTalentsList.setAdapter(mAdapter);
        
		DialogDemo.show(this,"正在加载，请稍后...");
        //new GetDataThread(handler).start();
		loadAndShowData();
	}
	
	void Show(){
	    Infos.clear();
        for(int i=0;i<mListData.size();i++){
            Info info = new Info();
            info.setImage(mListData.get(i).get("skin").toString());
            info.setTitle(mListData.get(i).get("title").toString());
            info.setDate(mListData.get(i).get("date").toString());
            Infos.add(info);
        }
//             Log.d("server", "size:"+contacts.size()+"=>"+contacts.get(0).getImage());
        mAdapter.notifyDataSetChanged();
	}

	private void loadAndShowData(){
        new Thread(new Runnable() {

            public void run() {
                try {
                    isLoading = true;
                    
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("method", mEdit);
                    params.put("limit", pagesize+"");
                    params.put("offset", start+"");
                    String url = "http://"+getResources().getString(R.string.server)+"/api.php";
                    ConnServer server = new ConnServer();
                    List<Map<String,Object>> tmp = server.GetData(url, params, mEdit, handler);
                    mListData.clear();
                    if(tmp != null)
                    mListData.addAll(tmp);
                    
                    runOnUiThread(new Runnable() {

                        public void run() {
                            Log.e("returnData", "datasize = "+ mListData.size());
                            footLoadingLayout.setVisibility(View.INVISIBLE);
                            for(int i=0;i<mListData.size();i++){
                                Info info = new Info();
                                info.setImage(mListData.get(i).get("skin").toString());
                                info.setTitle(mListData.get(i).get("title").toString());
                                info.setDate(mListData.get(i).get("date").toString());
                                Infos.add(info);
                            }
                            if(mListData.size() != 0){
                                //获取cursor
                                start += pagesize;
//                                if(returnGroups.size() == pagesize)
//                                    footLoadingLayout.setVisibility(View.VISIBLE);
                            }
                            if(isFirstLoading){
                                isFirstLoading = false;
                                //设置adapter
                                if(mListData.size() < pagesize)
                                    hasMoreData = false;
                                mAdapter.notifyDataSetChanged();
                            }else{
                                Log.e("returnData", "datasize = "+ mListData.size());
                                Log.e("pagesize", "pagesize = "+ pagesize);
                                if(mListData.size() < pagesize){
                                    hasMoreData = false;
                                    footLoadingLayout.setVisibility(View.VISIBLE);
                                    footLoadingPB.setVisibility(View.GONE);
                                    footLoadingText.setText("No more data");
                                }
                                mAdapter.notifyDataSetChanged();
                            }
                            isLoading = false;
                        }
                    });
                } catch (Exception e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        public void run() {
                            isLoading = false;
                            footLoadingLayout.setVisibility(View.GONE);
                            Toast.makeText(TalentsActivity.this, "加载数据失败，请检查网络或稍后重试", 0).show();
                        }
                    });
                }
            }
        }).start();
    }
	
    private class GetDataThread extends Thread { 
         
        private Handler handler; 
 
        public GetDataThread(Handler handler) { 
            this.handler = handler; 
        } 
 
        @Override 
        public void run() {
            Map<String, String> params = new HashMap<String, String>();
            params.put("method", mEdit);
            params.put("limit", pagesize+"");
            params.put("offset", start+"");
            String url = "http://"+getResources().getString(R.string.server)+"/api.php";
            ConnServer server = new ConnServer();
            mListData = server.GetData(url, params, mEdit, handler);
        }
    }
    
    public class Adapter extends BaseAdapter {
        private List<Map<String, Object>> data;//在绑定的数据
        private int resource;//绑定的条目界面
        private LayoutInflater inflater;
        private Context context;
        
        public Adapter(Context context, List<Map<String, Object>> data, int resource) {
            this.data = data;
            this.resource = resource;
            this.context = context;
            inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            return data.size();//数据总数
        }

        @Override
        public Object getItem(int position) {
            return data.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ImageView ivPhoto = null;
            TextView tvTitle = null;
            TextView tvDate = null;
            final int index = position;
            
            if(convertView == null){
                convertView = inflater.inflate(resource, null);//生成条目界面对象
            }
            ivPhoto = (ImageView) convertView.findViewById(R.id.photo);
            tvTitle = (TextView) convertView.findViewById(R.id.title);
            tvDate = (TextView) convertView.findViewById(R.id.date);

            tvTitle.setText(data.get(position).get("title").toString());
            tvDate.setText(data.get(position).get("date").toString());

            return convertView;
        } 
    }

    private final Handler handler = new Handler(Looper.getMainLooper()) {
         
        public void handleMessage(Message msg) { // 处理Message，更新ListView
            DialogDemo.dismiss();
            switch(msg.what){ 
                case STATE_FINISH: 
                {
                    //Show();
                }
                    break;
                case STATE_FAIL:
                {
                    String info = "Error!";
                    if(msg.obj != null){
                        info = info+"\n"+msg.obj;
                    }
                    Toast.makeText(TalentsActivity.this, info, 1).show();
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
}
