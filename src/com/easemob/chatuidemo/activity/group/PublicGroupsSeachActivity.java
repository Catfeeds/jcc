package com.easemob.chatuidemo.activity.group;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.EMError;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupInfo;
import com.easemob.chat.EMGroupManager;
import com.easemob.chatuidemo.activity.BaseActivity;
import com.easemob.chatuidemo.utils.ConnServer;
import com.easemob.chatuidemo.utils.DialogDemo;
import com.squareup.picasso.Picasso;
import com.wenpy.jcc.R;
import com.easemob.exceptions.EaseMobException;

public class PublicGroupsSeachActivity extends BaseActivity{
    private GridView gvGroups;
    private EditText idET;
    private TextView nameText;
    private List<Map<String, Object>> mReturnData;
    private GroupAdapter adapter;

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_public_groups_search);
        
        mReturnData = new ArrayList<Map<String,Object>>();
        
        gvGroups = (GridView) findViewById(R.id.gv_groups);
        idET = (EditText) findViewById(R.id.et_search_id);
        nameText = (TextView) findViewById(R.id.name);        

        adapter = new GroupAdapter(PublicGroupsSeachActivity.this, mReturnData);
        gvGroups.setAdapter(adapter);
        
        gvGroups.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                    long arg3) {
                EMGroupInfo groupInfo = new EMGroupInfo(mReturnData.get(arg2).get("groupid").toString(),
                        mReturnData.get(arg2).get("groupname").toString());
                Intent intent = new Intent(PublicGroupsSeachActivity.this, GroupSimpleDetailActivity.class);
                intent.putExtra("groupinfo", groupInfo);
                intent.putExtra("created", mReturnData.get(arg2).get("created").toString());
                startActivity(intent);
            }
        });
    }

    public class GroupAdapter extends BaseAdapter{
        private List<Map<String, Object>> data = new ArrayList<Map<String,Object>>();
        private Context context;
        private LayoutInflater mInflater;  

        public GroupAdapter(Context context, List<Map<String, Object>> data) {  
            this.context = context;  
            this.data = data;  
      
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
        }  
        
        private class ViewHolder {
            ImageView avatar;
            TextView name;
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if(convertView == null){
                holder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.gridview_group_list, null);
                holder.avatar = (ImageView) convertView.findViewById(R.id.avatar);
                holder.name = (TextView) convertView.findViewById(R.id.name);
                convertView.setTag(holder);
            }else{
                holder = (ViewHolder) convertView.getTag();
            }
            holder.avatar.setImageResource(R.drawable.group_icon);
            holder.name.setText(data.get(position).get("groupname").toString());
            
            return convertView;
        }
        @Override
        public int getCount() {
            // TODO Auto-generated method stub
            return data.size();
        }
        @Override
        public Object getItem(int position) {
            // TODO Auto-generated method stub
            return data.get(position);
        }
        @Override
        public long getItemId(int position) {
            // TODO Auto-generated method stub
            return position;
        }
    }
    
    /**
     * 搜索
     * @param v
     */
    public void searchGroup(View v){
        if(TextUtils.isEmpty(idET.getText())){
            return;
        }
        DialogDemo.show(PublicGroupsSeachActivity.this,"正在搜索，请稍后...");
        new GetDataThread(handler).start();        
    }
    
    
    /**
     * 点击搜索到的群组进入群组信息页面
     * @param view
     */
    public void enterToDetails(View view){
      
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
            params.put("method", "group_get");
            params.put("search", idET.getText().toString());
            String path = "http://"+getResources().getString(R.string.server)+"/api.php"; 
            ConnServer server = new ConnServer();
            List<Map<String, Object>> Data = new ArrayList<Map<String,Object>>();
            mReturnData = server.GetData(path,params,edit,handler);
        }
    }
    
    private final Handler handler = new Handler(Looper.getMainLooper()) {
         
        public void handleMessage(Message msg) { // 处理Message，更新ListView
            DialogDemo.dismiss();
            switch(msg.what){ 
                case STATE_FINISH: 
                {
                    adapter = new GroupAdapter(PublicGroupsSeachActivity.this, mReturnData);
                    gvGroups.setAdapter(adapter);
                    adapter.notifyDataSetChanged();
                }
                    break;
                case STATE_FAIL:
                {
                    String info = "Error!";
                    if(msg.obj != null){
                        info = info+"\n"+msg.obj;
                    }
                    Toast.makeText(getApplicationContext(), info, 1).show();
                    break;
                }
                case STATE_NULL:
                    Toast.makeText(PublicGroupsSeachActivity.this, getResources().getString(R.string.group_not_existed), 0).show();
                    break;
                case STATE_LINKERR:
                {
                    String type = msg.getData().getString("type");
                    Toast.makeText(PublicGroupsSeachActivity.this, "网络连接失败",Toast.LENGTH_SHORT) .show();  
                    break;
                }
                case STATE_ERROR: 
                    Toast.makeText(PublicGroupsSeachActivity.this, "网络连接错误",Toast.LENGTH_SHORT) .show();  
                    break;
               default:  
            } 
        } 
    }; 
}
