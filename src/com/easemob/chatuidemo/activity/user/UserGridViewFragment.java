package com.easemob.chatuidemo.activity.user;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.baidu.mapapi.map.Text;
import com.easemob.applib.controller.HXSDKHelper;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMChatOptions;
import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupManager;
import com.easemob.chatuidemo.Constant;
import com.easemob.chatuidemo.DemoHXSDKModel;
import com.easemob.chatuidemo.domain.Info;
import com.easemob.chatuidemo.domain.User;
import com.easemob.chatuidemo.utils.ConnServer;
import com.easemob.chatuidemo.utils.DialogDemo;
import com.easemob.chatuidemo.utils.UserUtils;
import com.easemob.exceptions.EaseMobException;
import com.easemob.util.EMLog;
import com.squareup.picasso.Picasso;
import com.wenpy.jcc.R;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SectionIndexer;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class UserGridViewFragment extends Fragment  {
    static final int STATE_ERROR = -1; 
    static final int STATE_FAIL = 0; 
    static final int STATE_FINISH = 1; 
    static final int STATE_LINKERR = 2; 
    static final int STATE_TIMEOUT = 3; 
    static final int STATE_NULL = 4; 
    private RelativeLayout rlTitle;
    private TextView tvTitle;
    private GridView gvUserList;
    private List<Map<String, Object>> mReturnData = new ArrayList<Map<String,Object>>();
    private GroupAdapter adapter;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_friend_view, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        initView();
    }

    private void initView() {
        gvUserList = (GridView)getView().findViewById(R.id.gv_friend_list);
        rlTitle = (RelativeLayout)getActivity().findViewById(R.id.main_title);
        tvTitle = (TextView)getActivity().findViewById(R.id.message_title);
        
        rlTitle.setVisibility(View.VISIBLE);
        tvTitle.setText("找ta");
        
        adapter = new GroupAdapter(getActivity(), mReturnData);
        gvUserList.setAdapter(adapter);

        gvUserList.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String username = mReturnData.get(position).get("name").toString();
              
                startActivity(new Intent(getActivity(), UserProfileActivity.class).putExtra("username", username));
            }
        });
    }
    
    public void setData(List<Map<String, Object>> data){
        mReturnData = data;
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
            if(data.size()>0){
                if(!data.get(position).get("avatar").toString().equals(""))
                    Picasso.with(context).load(data.get(position).get("avatar").toString()).placeholder(R.drawable.default_avatar).into(holder.avatar);
                holder.name.setText(data.get(position).get("nick").toString());
            }
            
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
}
