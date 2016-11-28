package com.easemob.chatuidemo.activity;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.easemob.applib.controller.HXSDKHelper;
import com.easemob.applib.controller.HXSDKHelper.HXSyncListener;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMContactManager;
import com.easemob.chatuidemo.Constant;
import com.easemob.chatuidemo.DemoHXSDKHelper;
import com.easemob.chatuidemo.DemoHXSDKModel;
import com.easemob.chatuidemo.activity.ContactlistFragment.HXBlackListSyncListener;
import com.easemob.chatuidemo.activity.ContactlistFragment.HXContactInfoSyncListener;
import com.easemob.chatuidemo.activity.ContactlistFragment.HXContactSyncListener;
import com.easemob.chatuidemo.activity.group.GroupViewFragment.GroupAdapter;
import com.easemob.chatuidemo.activity.user.UserProfileActivity;
import com.easemob.chatuidemo.adapter.ContactAdapter;
import com.easemob.chatuidemo.adapter.ContactGridAdapter;
import com.easemob.chatuidemo.domain.User;
import com.easemob.chatuidemo.utils.ConnServer;
import com.easemob.chatuidemo.utils.DialogDemo;
import com.easemob.chatuidemo.widget.Sidebar;
import com.easemob.util.EMLog;
import com.squareup.picasso.Picasso;
import com.wenpy.jcc.R;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.View.OnClickListener;
import android.view.View.OnTouchListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;

public class FriendViewFragment extends Fragment {
    static final int STATE_ERROR = -1; 
    static final int STATE_FAIL = 0; 
    static final int STATE_FINISH = 1; 
    static final int STATE_LINKERR = 2; 
    static final int STATE_TIMEOUT = 3; 
    static final int STATE_NULL = 4; 
    private GridView gvFriendList;
    private List<Map<String, Object>> mFriendListData;
    private List<Map<String, Object>> mListData;
    private Map<String, String> mAvatarData;
    public static final String TAG = "ContactlistFragment";
    FriendAdapter adapter;
    private List<User> contactList;
    private boolean hidden;
    private InputMethodManager inputMethodManager;
    private List<String> blackList;
    ImageButton clearSearch;
    EditText query;
    HXContactSyncListener contactSyncListener;
    HXBlackListSyncListener blackListSyncListener;
    HXContactInfoSyncListener contactInfoSyncListener;
    View progressBar;
    private User toBeProcessUser;
    private String toBeProcessUsername;    

    class HXContactSyncListener implements HXSDKHelper.HXSyncListener {
        @Override
        public void onSyncSucess(final boolean success) {
            EMLog.d(TAG, "on contact list sync success:" + success);
            FriendViewFragment.this.getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    getActivity().runOnUiThread(new Runnable(){

                        @Override
                        public void run() {
                            if(success){
                                progressBar.setVisibility(View.GONE);
                                refresh();
                            }else{
                                String s1 = getResources().getString(R.string.get_failed_please_check);
                                Toast.makeText(getActivity(), s1, 1).show();
                                progressBar.setVisibility(View.GONE);
                            }
                        }
                        
                    });
                }
            });
        }
    }
    
    class HXBlackListSyncListener implements HXSyncListener{

        @Override
        public void onSyncSucess(boolean success) {
            getActivity().runOnUiThread(new Runnable(){

                @Override
                public void run() {
                    blackList = EMContactManager.getInstance().getBlackListUsernames();
                    refresh();
                }
                
            });
        }
        
    };
    
    class HXContactInfoSyncListener implements HXSDKHelper.HXSyncListener{

        @Override
        public void onSyncSucess(final boolean success) {
            EMLog.d(TAG, "on contactinfo list sync success:" + success);
            getActivity().runOnUiThread(new Runnable() {
                
                @Override
                public void run() {
                    progressBar.setVisibility(View.GONE);
                    if(success){
                        refresh();
                    }
                }
            });
        }
        
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        mListData = new ArrayList<Map<String,Object>>();
        mAvatarData = new HashMap<String, String>();
        return inflater.inflate(R.layout.fragment_friend_view, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
      //防止被T后，没点确定按钮然后按了home键，长期在后台又进app导致的crash
        if(savedInstanceState != null && savedInstanceState.getBoolean("isConflict", false))
            return;
        inputMethodManager = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        gvFriendList = (GridView) getView().findViewById(R.id.gv_friend_list);
        
        //黑名单列表
        blackList = EMContactManager.getInstance().getBlackListUsernames();
        contactList = new ArrayList<User>();
        // 获取设置contactlist
        getContactList();        

//        User addUser = new User();
//        addUser.setUsername("item_add_user");
//        contactList.add(addUser);
//        // 设置adapter
//        System.out.println("user0:"+contactList);
//        adapter = new ContactGridAdapter(getActivity(), R.layout.gridview_group_list, contactList);
//        gvFriendList.setAdapter(adapter);
        
        mFriendListData = new ArrayList<Map<String,Object>>();
        for(int i=0;i<contactList.size();i++){
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("avatar", R.drawable.default_avatar);
            map.put("name", contactList.get(i).getUsername());
            mFriendListData.add(map);
        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("avatar", R.drawable.user_add);
        map.put("name", "add");
        mFriendListData.add(map);

        adapter = new FriendAdapter(getActivity(), mFriendListData);
        gvFriendList.setAdapter(adapter);        
        
        gvFriendList.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //String username = adapter.getItem(position).getUsername();
                String username = mFriendListData.get(position).get("name").toString();
                if (position == mFriendListData.size()-1) {
                    startActivity(new Intent(getActivity(), AddContactActivity.class));
                } else {
                    Intent intent = new Intent(getActivity(), UserProfileActivity.class);
                    intent.putExtra("username", username);
                    if(username.equals(EMChatManager.getInstance().getCurrentUser()))
                        intent.putExtra("setting", true);
                    startActivity(intent);
                }
            }
        });
        gvFriendList.setOnTouchListener(new OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                // 隐藏软键盘
                if (getActivity().getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
                    if (getActivity().getCurrentFocus() != null)
                        inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                                InputMethodManager.HIDE_NOT_ALWAYS);
                }
                return false;
            }
        });
        
        progressBar = (View) getView().findViewById(R.id.progress_bar);

        contactSyncListener = new HXContactSyncListener();
        HXSDKHelper.getInstance().addSyncContactListener(contactSyncListener);
        
        blackListSyncListener = new HXBlackListSyncListener();
        HXSDKHelper.getInstance().addSyncBlackListListener(blackListSyncListener);
        
        contactInfoSyncListener = new HXContactInfoSyncListener();
        ((DemoHXSDKHelper)HXSDKHelper.getInstance()).getUserProfileManager().addSyncContactInfoListener(contactInfoSyncListener);
        
        if (!HXSDKHelper.getInstance().isContactsSyncedWithServer()) {
            progressBar.setVisibility(View.VISIBLE);
        } else {
            progressBar.setVisibility(View.GONE);
        }
        if(mListData.size()==0)
            new GetDataThread(handler).start();
    }

    private class GetDataThread extends Thread { 
         
        private Handler handler; 
 
        public GetDataThread(Handler handler) { 
            this.handler = handler; 
        } 
 
        @Override 
        public void run() {
            ConnServer server = new ConnServer();
            String path = "http://"+getResources().getString(R.string.server)+"/api.php";
            String edit = "user_avatar_get";
            Map<String, String> params = new HashMap<String, String>();
            params.put("method", edit);
            params.put("user", EMChatManager.getInstance().getCurrentUser());
            params.put("type", "friend");
            mListData = server.GetData(path, params, edit, handler);
        }
    }

    private final Handler handler = new Handler(Looper.getMainLooper()) {
        
        public void handleMessage(Message msg) { // 处理Message，更新ListView
            DialogDemo.dismiss();
            switch(msg.what){ 
                case STATE_FINISH: 
                        ShowUserAvatar();
                    break;
               default:  
            } 
        }
    };
    
    /**
     * 获取联系人列表，并过滤掉黑名单和排序
     */
    private void getContactList() {
        contactList.clear();
        //获取本地好友列表
        Map<String, User> users = ((DemoHXSDKHelper)HXSDKHelper.getInstance()).getContactList();
        System.out.println("user:"+users);
        Iterator<Entry<String, User>> iterator = users.entrySet().iterator();
        while (iterator.hasNext()) {
            Entry<String, User> entry = iterator.next();
            if (!entry.getKey().equals(Constant.NEW_FRIENDS_USERNAME)
                    && !entry.getKey().equals(Constant.GROUP_USERNAME)
                    && !entry.getKey().equals(Constant.CHAT_ROOM)
                    && !entry.getKey().equals(Constant.CHAT_ROBOT)
                    && !blackList.contains(entry.getKey()))
                contactList.add(entry.getValue());
        }
        // 排序
        Collections.sort(contactList, new Comparator<User>() {

            @Override
            public int compare(User lhs, User rhs) {
                return lhs.getUsername().compareTo(rhs.getUsername());
            }
        });        
    }
    
    public void showProgressBar(boolean show) {
        if (progressBar != null) {
            if (show) {
                progressBar.setVisibility(View.VISIBLE);
            } else {
                progressBar.setVisibility(View.GONE);
            }
        }
    }
    void hideSoftKeyboard() {
        if (getActivity().getWindow().getAttributes().softInputMode != WindowManager.LayoutParams.SOFT_INPUT_STATE_HIDDEN) {
            if (getActivity().getCurrentFocus() != null)
                inputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(),
                        InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }
    
    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
//        if(((MainActivity)getActivity()).isConflict){
//            outState.putBoolean("isConflict", true);
//        }else if(((MainActivity)getActivity()).getCurrentAccountRemoved()){
//            outState.putBoolean(Constant.ACCOUNT_REMOVED, true);
//        }        
    }

    // 刷新ui
    public void refresh() {
        try {
            // 可能会在子线程中调到这方法
            getActivity().runOnUiThread(new Runnable() {
                public void run() {
                    getContactList();
                    adapter.notifyDataSetChanged();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public class FriendAdapter extends BaseAdapter{
        private List<Map<String, Object>> data = new ArrayList<Map<String,Object>>();
        private Context context;
        private LayoutInflater mInflater;  

        public FriendAdapter(Context context, List<Map<String, Object>> data) {  
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
            if(data.size()>1){
                if(data.get(position).get("name").toString().equals("add")){
                    Picasso.with(context).load(R.drawable.user_add).into(holder.avatar);
                    holder.name.setText("");
                }
                else{
                    if(mAvatarData.size()>0){
                        String avatar = mAvatarData.get(data.get(position).get("name").toString());
                        if(avatar !=null && !avatar.equals(""))
                            Picasso.with(context).load(avatar).placeholder(R.drawable.default_avatar).into(holder.avatar);
                    }
                    holder.name.setText(data.get(position).get("name").toString());
                }
            }else{
                Picasso.with(context).load(R.drawable.user_add).into(holder.avatar);
                holder.name.setText("");
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
    
    protected void ShowUserAvatar() {
        for(int i=0;i<mListData.size();i++){
            mAvatarData.put(mListData.get(i).get("name").toString(), mListData.get(i).get("avatar").toString());
        }
        adapter.notifyDataSetChanged();
    } 
    
    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        this.hidden = hidden;
        if (!hidden) {
            refresh();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (!hidden) {
            refresh();
        }
    }

    @Override
    public void onDestroy() {
        if (contactSyncListener != null) {
            HXSDKHelper.getInstance().removeSyncContactListener(contactSyncListener);
            contactSyncListener = null;
        }
        
        if(blackListSyncListener != null){
            HXSDKHelper.getInstance().removeSyncBlackListListener(blackListSyncListener);
        }
        
        if(contactInfoSyncListener != null){
            ((DemoHXSDKHelper)HXSDKHelper.getInstance()).getUserProfileManager().removeSyncContactInfoListener(contactInfoSyncListener);
        }
        super.onDestroy();
    }

}
