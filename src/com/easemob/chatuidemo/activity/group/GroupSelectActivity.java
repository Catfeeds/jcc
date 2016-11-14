package com.easemob.chatuidemo.activity.group;

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
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
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

import com.baidu.android.bbalbs.common.a.b;
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
import com.easemob.chatuidemo.adapter.ImageAdapter;
import com.easemob.chatuidemo.adapter.TalentAdapter;
import com.easemob.chatuidemo.domain.Info;
import com.easemob.chatuidemo.utils.ConnServer;
import com.easemob.chatuidemo.utils.DialogDemo;
import com.easemob.exceptions.EaseMobException;
import com.google.android.gms.internal.da;

/**
 * 
 * 
 */
public class GroupSelectActivity extends BaseActivity {
    private ProgressBar pb;
    private TextView tvTitle;
    private ListView ListView;
    private Button btOK;
    private Button btCancel;
    private List<Map<String, Object>> mListData;
    private File cache;
    public  TalentAdapter mAdapter;
    private String mType;
    private GroupsAdapter adapter;
    private List<EMGroupInfo> groupsList;
    private boolean isLoading;
    private boolean isFirstLoading = true;
    private boolean hasMoreData = true;
    private final int pagesize = 5;
    private String cursor;
    private LinearLayout footLoadingLayout;
    private ProgressBar footLoadingPB;
    private TextView footLoadingText;
    private List<String> mSelectGroup;
    private String mContent;
    private String mFile;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_group_select);

		mContent = getIntent().getStringExtra("content");
        mFile = getIntent().getStringExtra("file");
		
        mListData = new ArrayList<Map<String,Object>>();
        groupsList = new ArrayList<EMGroupInfo>();
        mSelectGroup = new ArrayList<String>();
        
        pb = (ProgressBar) findViewById(R.id.progressBar);
		tvTitle = (TextView)findViewById(R.id.message_title);
		ListView = (ListView)findViewById(R.id.list);
		btOK = (Button)findViewById(R.id.bt_ok);
        btCancel = (Button)findViewById(R.id.bt_cancel);
        
        btOK.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View arg0) {
                if(mSelectGroup.size() == 0){
                    Toast.makeText(GroupSelectActivity.this, "请选择发送对象！", 0).show();
                    return;
                }
                DialogDemo.show(GroupSelectActivity.this, "正在提交，请稍后...");
                new SaveDataThread(handler).start();
            }
        });
        btCancel.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View arg0) {
                finish();
            }
        });        
		ListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                    long arg3) {
//                Intent intent = new Intent(GroupSelectActivity.this,
//                        TalentsImgViewActivity.class);
//                Bundle extras = new Bundle();
//                extras.putString("id", mListData.get(arg2).get("id").toString());
//                intent.putExtra("type", mType);
//                intent.putExtras(extras);
//                startActivity(intent); 
            }
        });
		
		View footView = getLayoutInflater().inflate(R.layout.listview_footer_view, null);
        footLoadingLayout = (LinearLayout) footView.findViewById(R.id.loading_layout);
        footLoadingPB = (ProgressBar)footView.findViewById(R.id.loading_bar);
        footLoadingText = (TextView) footView.findViewById(R.id.loading_text);
        ListView.addFooterView(footView, null, false);
        footLoadingLayout.setVisibility(View.GONE);
             

        //获取及显示数据
        loadAndShowData();
        
        ListView.setOnScrollListener(new OnScrollListener() {
            
            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {
                if(scrollState == OnScrollListener.SCROLL_STATE_IDLE){
//                    if(!hasMoreData){
//                        footLoadingLayout.setVisibility(View.VISIBLE);
//                        footLoadingPB.setVisibility(View.GONE);
//                        footLoadingText.setText("No more data");
//                        adapter.notifyDataSetChanged();
//                    }
                    if(ListView.getCount() != 0){
                        int lasPos = view.getLastVisiblePosition();
                        if(hasMoreData && !isLoading && lasPos == ListView.getCount()-1){
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
	}
	
	void Show(){
	    List<Info> Infos = new ArrayList<Info>();
        for(int i=0;i<mListData.size();i++){
            Info info = new Info();
            info.setImage(mListData.get(i).get("skin").toString());
            info.setTitle(mListData.get(i).get("title").toString());
            info.setDate(mListData.get(i).get("date").toString());
            Infos.add(info);
        }
//             Log.d("server", "size:"+contacts.size()+"=>"+contacts.get(0).getImage());
        adapter = new GroupsAdapter(GroupSelectActivity.this, 1, groupsList);
        mAdapter = new TalentAdapter(this,Infos,cache);
        ListView.setAdapter(mAdapter);
	}


    private void loadAndShowData(){
        new Thread(new Runnable() {

            public void run() {
                try {
                    isLoading = true;
                    final EMCursorResult<EMGroupInfo> result = EMGroupManager.getInstance().getPublicGroupsFromServer(pagesize, cursor);
                    //获取group list
                    final List<EMGroupInfo> returnGroups = result.getData();
                    runOnUiThread(new Runnable() {

                        public void run() {
                            Log.e("returnGroups", "returnGroups.size() = "+ returnGroups.size());
                            footLoadingLayout.setVisibility(View.INVISIBLE);
                            groupsList.addAll(returnGroups);
                            if(returnGroups.size() != 0){
                                //获取cursor
                                cursor = result.getCursor();
//                                if(returnGroups.size() == pagesize)
//                                    footLoadingLayout.setVisibility(View.VISIBLE);
                            }
                            if(isFirstLoading){
                                pb.setVisibility(View.INVISIBLE);
                                isFirstLoading = false;
                                //设置adapter
                                if(returnGroups.size() < pagesize)
                                    hasMoreData = false;
                                adapter = new GroupsAdapter(GroupSelectActivity.this, 1, groupsList);
                                ListView.setAdapter(adapter);
                            }else{
                                Log.e("returnGroups", "returnGroups.size() = "+ returnGroups.size());
                                Log.e("pagesize", "pagesize = "+ pagesize);
                                if(returnGroups.size() < pagesize){
                                    hasMoreData = false;
                                    footLoadingLayout.setVisibility(View.VISIBLE);
                                    footLoadingPB.setVisibility(View.GONE);
                                    footLoadingText.setText("No more data");
                                }
                                adapter.notifyDataSetChanged();
                            }
                            isLoading = false;
                        }
                    });
                } catch (EaseMobException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        public void run() {
                            isLoading = false;
                            pb.setVisibility(View.INVISIBLE);
                            footLoadingLayout.setVisibility(View.GONE);
                            Toast.makeText(GroupSelectActivity.this, "加载数据失败，请检查网络或稍后重试", 0).show();
                        }
                    });
                }
            }
        }).start();
    }
    
    /**
     * adapter
     *
     */
    private class GroupsAdapter extends ArrayAdapter<EMGroupInfo> {

        private LayoutInflater inflater;

        public GroupsAdapter(Context context, int res, List<EMGroupInfo> groups) {
            super(context, res, groups);
            this.inflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final int intCheck = position;
            CheckBox cbSelect = null;
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.row_group_select, null);
            }

            cbSelect = (CheckBox)convertView.findViewById(R.id.select);
            ((TextView) convertView.findViewById(R.id.name)).setText(getItem(position).getGroupName());
            
            cbSelect.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                
                @Override
                public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
                    if(arg1)
                        mSelectGroup.add(groupsList.get(intCheck).getGroupId());
                    else{
                        for(int i=0;i<mSelectGroup.size();i++){
                            if(groupsList.get(intCheck).getGroupId().equals(mSelectGroup.get(i))){
                                mSelectGroup.remove(i);
                                break;
                            }
                        }
                    }
                }
            });
            
            return convertView;
        }
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
             GetData(edit,params);
        }
    }
    
    private class SaveDataThread extends Thread { 
         
        private Handler handler; 
 
        public SaveDataThread(Handler handler) { 
            this.handler = handler; 
        } 
 
        @Override 
        public void run() {
            String accepter = "";
            for(int i=0;i<mSelectGroup.size();i++){
                accepter += mSelectGroup.get(i) + ",";
            }
            accepter = accepter.substring(0, accepter.length()-1);
            
            String edit = "inform_add";
            Map<String, String> params = new HashMap<String, String>();
            params.put("method", edit);
            params.put("user", EMChatManager.getInstance().getCurrentUser());
            params.put("accepter", accepter);
            params.put("content", mContent);
            params.put("file", mFile);
            
            ConnServer server = new ConnServer();
            String path = "http://"+getResources().getString(R.string.server)+"/api.php";
            mListData = server.GetData(path,params,edit,handler);
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
                msg.what = STATE_FINISH;
                mListData = server.Json2List(mListData.get(0).get("data").toString());
                System.out.println("mData:"+mListData.size());
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
                    if(msg.obj.toString().equals("inform_add")){
                        Toast.makeText(GroupSelectActivity.this, "发送成功", 0).show();
                        Intent intent = new Intent();
                        intent.putExtra("second", "I am second!");
                        setResult(RESULT_OK, intent);
                        GroupSelectActivity.this.finish();
                    }
                }
                    break;
                case STATE_FAIL:
                {
                    String info = "Error!";
                    if(msg.obj != null){
                        info = info+"\n"+msg.obj;
                    }
                    Toast.makeText(GroupSelectActivity.this, info, 1).show();
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
