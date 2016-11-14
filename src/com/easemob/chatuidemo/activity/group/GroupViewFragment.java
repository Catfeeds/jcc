package com.easemob.chatuidemo.activity.group;

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
import com.easemob.chatuidemo.activity.AlertDialog;
import com.easemob.chatuidemo.activity.ChatActivity;
import com.easemob.chatuidemo.activity.user.UserProfileActivity;
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

public class GroupViewFragment extends Fragment implements OnClickListener  {
    private static final int REQUEST_CODE_ADD_USER = 0;
    private static final int REQUEST_CODE_EXIT = 1;
    private static final int REQUEST_CODE_EXIT_DELETE = 2;
    private static final int REQUEST_CODE_CLEAR_ALL_HISTORY = 3;
    private static final int REQUEST_CODE_ADD_TO_BALCKLIST = 4;
    private static final int REQUEST_CODE_EDIT_GROUPNAME = 5;
    static final int STATE_ERROR = -1; 
    static final int STATE_FAIL = 0; 
    static final int STATE_FINISH = 1; 
    static final int STATE_LINKERR = 2; 
    static final int STATE_TIMEOUT = 3; 
    static final int STATE_NULL = 4; 
    /**
     * 设置新消息通知布局
     */
    private RelativeLayout rl_switch_notification;
    /**
     * 设置声音布局
     */
    private RelativeLayout rl_switch_sound;
    /**
     * 打开新消息通知imageView
     */
    private ImageView iv_switch_open_notification;
    /**
     * 关闭新消息通知imageview
     */
    private ImageView iv_switch_close_notification;
    /**
     * 打开声音提示imageview
     */
    private ImageView iv_switch_open_sound;
    /**
     * 关闭声音提示imageview
     */
    private ImageView iv_switch_close_sound;
    // 清空所有聊天记录
    private LinearLayout clearAllHistory;
    DemoHXSDKModel model;
    private GridView gvGroupList;
    private TextView tvGroupName;
    private EMGroup group;
    private String mGroupId = "";
    private List<Map<String, Object>> mGroupListData;
    private List<Map<String, Object>> mListData;
    private Map<String, String> mAvatarData;
    private GroupAdapter adapter;
    private ProgressDialog progressDialog;
    private String mType;
    private EMChatOptions chatOptions;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        Bundle bundle = getArguments();
        mGroupId = bundle.getString("groupId");
        mType = bundle.getString("type");
        
        group = EMGroupManager.getInstance().getGroup(mGroupId);
        System.out.println("groupId:"+mGroupId);
        mListData = new ArrayList<Map<String,Object>>();
        mAvatarData = new HashMap<String, String>();
        return inflater.inflate(R.layout.fragment_group_view, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if(mGroupId.equals("")){
            Toast.makeText(getActivity(), "该用户没有加入群组，请联系管理员添加", 0).show();
            getActivity().finish();
            return;
        }
        initView();
    }

    private void initView() {
        tvGroupName = (TextView)getView().findViewById(R.id.tv_group_name);
        clearAllHistory = (LinearLayout) getView().findViewById(R.id.clear_all_history);
        rl_switch_notification = (RelativeLayout) getView().findViewById(R.id.rl_switch_notification);
        rl_switch_sound = (RelativeLayout) getView().findViewById(R.id.rl_switch_sound);
        iv_switch_open_notification = (ImageView) getView().findViewById(R.id.iv_switch_open_notification);
        iv_switch_close_notification = (ImageView) getView().findViewById(R.id.iv_switch_close_notification);
        iv_switch_open_sound = (ImageView) getView().findViewById(R.id.iv_switch_open_sound);
        iv_switch_close_sound = (ImageView) getView().findViewById(R.id.iv_switch_close_sound);

        gvGroupList = (GridView)getView().findViewById(R.id.gv_stugroup_list);
        tvGroupName.setText(group.getGroupName());
        
        List<String> members = new ArrayList<String>();
        members.addAll(group.getMembers());

        mGroupListData = new ArrayList<Map<String,Object>>();
        for(int i=0;i<members.size();i++){
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("avatar", R.drawable.default_avatar);
            map.put("name", members.get(i));
            mGroupListData.add(map);
        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("avatar", R.drawable.user_add);
        map.put("name", "add");
        mGroupListData.add(map);

//        adapter = new SimpleAdapter(getActivity(),mGroupListData,R.layout.gridview_group_list,
//                new String[]{"image","text"},new int[]{R.id.avatar, R.id.name});
        
        adapter = new GroupAdapter(getActivity(), mGroupListData);
        gvGroupList.setAdapter(adapter);

        gvGroupList.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                String username = mGroupListData.get(position).get("name").toString();
              
               if(position == mGroupListData.size()-1){
                   startActivityForResult(
                           (new Intent(getActivity(), GroupPickContactsActivity.class).putExtra("groupId", mGroupId)),
                           REQUEST_CODE_ADD_USER);
               }else                  
                   startActivity(new Intent(getActivity(), UserProfileActivity.class).putExtra("username", username));
            }
        });
        
        // 保证每次进详情看到的都是最新的group
        updateGroup();
        
        clearAllHistory.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                String st9 = getResources().getString(R.string.sure_to_empty_this);
                Intent intent = new Intent(getActivity(), AlertDialog.class);
                intent.putExtra("cancel", true);
                intent.putExtra("titleIsCancel", true);
                intent.putExtra("msg", st9);
                startActivityForResult(intent, REQUEST_CODE_CLEAR_ALL_HISTORY);
            }
        });
        
        
        model = (DemoHXSDKModel) HXSDKHelper.getInstance().getModel();
        
        // 震动和声音总开关，来消息时，是否允许此开关打开
        // the vibrate and sound notification are allowed or not?
        if (model.getSettingMsgNotification()) {
            iv_switch_open_notification.setVisibility(View.VISIBLE);
            iv_switch_close_notification.setVisibility(View.INVISIBLE);
        } else {
            iv_switch_open_notification.setVisibility(View.INVISIBLE);
            iv_switch_close_notification.setVisibility(View.VISIBLE);
        }
        
        // 是否打开声音
        // sound notification is switched on or not?
        if (model.getSettingMsgSound()) {
            iv_switch_open_sound.setVisibility(View.VISIBLE);
            iv_switch_close_sound.setVisibility(View.INVISIBLE);
        } else {
            iv_switch_open_sound.setVisibility(View.INVISIBLE);
            iv_switch_close_sound.setVisibility(View.VISIBLE);
        }
        chatOptions = EMChatManager.getInstance().getChatOptions();
        rl_switch_notification.setOnClickListener(this);
        rl_switch_sound.setOnClickListener(this);
        
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
            String edit = "user_get";
            Map<String, String> params = new HashMap<String, String>();
            params.put("method", edit);
            params.put("group_id", mGroupId);
            mListData = server.GetData(path, params, edit, handler);
        }
    }

    protected void updateGroup() {
        new Thread(new Runnable() {
            public void run() {
                try {
                    final EMGroup returnGroup = EMGroupManager.getInstance().getGroupFromServer(mGroupId);
                    // 更新本地数据
                    EMGroupManager.getInstance().createOrUpdateLocalGroup(returnGroup);

                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            tvGroupName.setText(group.getGroupName());
//                            if(mType.equals("student_group")){
//                                ((StudentGroupActivity) getActivity()).rbClassGroup.setText(group.getGroupName());
//                                ((StudentGroupActivity) getActivity()).loadingPB.setVisibility(View.INVISIBLE);
//                            }else {
//                                ((ParentGroupActivity) getActivity()).rbClassGroup.setText(group.getGroupName());
//                                ((ParentGroupActivity) getActivity()).loadingPB.setVisibility(View.INVISIBLE);      
//                            }
                            refreshMembers();
//                            if (EMChatManager.getInstance().getCurrentUser().equals(group.getOwner())) {
//                                // 显示解散按钮
//                                exitBtn.setVisibility(View.GONE);
//                                deleteBtn.setVisibility(View.VISIBLE);
//                            } else
//                            {
//                                // 显示退出按钮
//                                exitBtn.setVisibility(View.VISIBLE);
//                                deleteBtn.setVisibility(View.GONE);
//                            }

                            // update block群消息屏蔽按钮
//                            EMLog.d("server", "group msg is blocked:" + group.getMsgBlocked());
//                            if (group.isMsgBlocked()) {
//                                iv_switch_block_groupmsg.setVisibility(View.VISIBLE);
//                                iv_switch_unblock_groupmsg.setVisibility(View.INVISIBLE);
//                            } else {
//                                iv_switch_block_groupmsg.setVisibility(View.INVISIBLE);
//                                iv_switch_unblock_groupmsg.setVisibility(View.VISIBLE);
//                            }
                        }
                    });

                } catch (Exception e) {
//                    if(mType.equals("student_group")){
//                        ((StudentGroupActivity) getActivity()).loadingPB.setVisibility(View.INVISIBLE);
//                    }else {
//                        ((ParentGroupActivity) getActivity()).loadingPB.setVisibility(View.INVISIBLE); 
//                    }
                }
            }
        }).start();
    }
    
    private void refreshMembers(){
        group = EMGroupManager.getInstance().getGroup(mGroupId);
        List<String> members = new ArrayList<String>();
        members.addAll(group.getMembers());
        
        mGroupListData = new ArrayList<Map<String,Object>>();
        for(int i=0;i<members.size();i++){
            Map<String, Object> map = new HashMap<String, Object>();
            map.put("avatar", R.drawable.default_avatar);
            map.put("name", members.get(i));
            mGroupListData.add(map);
        }
        Map<String, Object> map = new HashMap<String, Object>();
        map.put("avatar", "");
        map.put("name", "add");
        mGroupListData.add(map);

//        adapter = new SimpleAdapter(getActivity(),mGroupListData,R.layout.gridview_group_list,
//                new String[]{"image","text"},new int[]{R.id.avatar, R.id.name});
        adapter = new GroupAdapter(getActivity(), mGroupListData);
        gvGroupList.setAdapter(adapter);
    }
    /**
     * 增加群成员
     * 
     * @param newmembers
     */
    private void addMembersToGroup(final String[] newmembers) {
        final String st6 = getResources().getString(R.string.Add_group_members_fail);
        new Thread(new Runnable() {
            
            public void run() {
                try {
                    // 创建者调用add方法
                    if (EMChatManager.getInstance().getCurrentUser().equals(group.getOwner())) {
                        EMGroupManager.getInstance().addUsersToGroup(mGroupId, newmembers);
                    } else {
                        // 一般成员调用invite方法
                        EMGroupManager.getInstance().inviteUser(mGroupId, newmembers, null);
                    }
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            refreshMembers();
                            tvGroupName.setText(group.getGroupName());
//                            if(mType.equals("student_group")){
//                                ((StudentGroupActivity) getActivity()).rbClassGroup.setText(group.getGroupName());
//                            }else {
//                                ((ParentGroupActivity) getActivity()).rbClassGroup.setText(group.getGroupName());
//                            }
                            progressDialog.dismiss();
                        }
                    });
                } catch (final Exception e) {
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(), st6 + e.getMessage(), 1).show();
                        }
                    });
                }
            }
        }).start();
    }

    /**
     * 退出群组
     * 
     * @param groupId
     */
    private void exitGrop() {
        String st1 = getResources().getString(R.string.Exit_the_group_chat_failure);
        new Thread(new Runnable() {
            public void run() {
                try {
                    EMGroupManager.getInstance().exitFromGroup(mGroupId);
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            progressDialog.dismiss();
                            getActivity().setResult(getActivity().RESULT_OK);
                            getActivity().finish();
                            if(ChatActivity.activityInstance != null)
                                ChatActivity.activityInstance.finish();
                        }
                    });
                } catch (final Exception e) {
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(), getResources().getString(R.string.Exit_the_group_chat_failure) + " " + e.getMessage(), 1).show();
                        }
                    });
                }
            }
        }).start();
    }

    /**
     * 解散群组
     * 
     * @param groupId
     */
    private void deleteGrop() {
        final String st5 = getResources().getString(R.string.Dissolve_group_chat_tofail);
        new Thread(new Runnable() {
            public void run() {
                try {
                    EMGroupManager.getInstance().exitAndDeleteGroup(mGroupId);
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            progressDialog.dismiss();
                            getActivity().setResult(getActivity().RESULT_OK);
                            getActivity().finish();
                            if(ChatActivity.activityInstance != null)
                                ChatActivity.activityInstance.finish();
                        }
                    });
                } catch (final Exception e) {
                    getActivity().runOnUiThread(new Runnable() {
                        public void run() {
                            progressDialog.dismiss();
                            Toast.makeText(getActivity(), st5 + e.getMessage(), 1).show();
                        }
                    });
                }
            }
        }).start();
    }
    

    /**
     * 清空群聊天记录
     */
    public void clearGroupHistory() {

        EMChatManager.getInstance().clearConversation(group.getGroupId());
        progressDialog.dismiss();
        // adapter.refresh(EMChatManager.getInstance().getConversation(toChatUsername));

    }
    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        String st1 = getResources().getString(R.string.being_added);
        String st2 = getResources().getString(R.string.is_quit_the_group_chat);
        String st3 = getResources().getString(R.string.chatting_is_dissolution);
        String st4 = getResources().getString(R.string.are_empty_group_of_news);
        String st5 = getResources().getString(R.string.is_modify_the_group_name);
        final String st6 = getResources().getString(R.string.Modify_the_group_name_successful);
        final String st7 = getResources().getString(R.string.change_the_group_name_failed_please);
        String st8 = getResources().getString(R.string.Are_moving_to_blacklist);
        final String st9 = getResources().getString(R.string.failed_to_move_into);
        
        final String stsuccess = getResources().getString(R.string.Move_into_blacklist_success);
        if (resultCode == getActivity().RESULT_OK) {
            if (progressDialog == null) {
                progressDialog = new ProgressDialog(getActivity());
                progressDialog.setMessage(st1);
                progressDialog.setCanceledOnTouchOutside(false);
            }
            switch (requestCode) {
            case REQUEST_CODE_ADD_USER:// 添加群成员
                final String[] newmembers = data.getStringArrayExtra("newmembers");
                progressDialog.setMessage(st1);
                progressDialog.show();
                addMembersToGroup(newmembers);
                break;
            case REQUEST_CODE_EXIT: // 退出群
                progressDialog.setMessage(st2);
                progressDialog.show();
                exitGrop();
                break;
            case REQUEST_CODE_EXIT_DELETE: // 解散群
                progressDialog.setMessage(st3);
                progressDialog.show();
                deleteGrop();
                break;
            case REQUEST_CODE_CLEAR_ALL_HISTORY:
                // 清空此群聊的聊天记录
                progressDialog.setMessage(st4);
                progressDialog.show();
                clearGroupHistory();
                break;

            case REQUEST_CODE_EDIT_GROUPNAME: //修改群名称
                final String returnData = data.getStringExtra("data");
                if(!TextUtils.isEmpty(returnData)){
                    progressDialog.setMessage(st5);
                    progressDialog.show();
                    
                    new Thread(new Runnable() {
                        public void run() {
                            try {
                                EMGroupManager.getInstance().changeGroupName(mGroupId, returnData);
                                getActivity().runOnUiThread(new Runnable() {
                                    public void run() {
                                        tvGroupName.setText(group.getGroupName());
//                                        if(mType.equals("student_group")){
//                                            ((StudentGroupActivity) getActivity()).rbClassGroup.setText(group.getGroupName());
//                                        }else {
//                                            ((ParentGroupActivity) getActivity()).rbClassGroup.setText(group.getGroupName());
//                                        }
                                        progressDialog.dismiss();
                                        Toast.makeText(getActivity(), st6, 0).show();
                                    }
                                });
                                
                            } catch (EaseMobException e) {
                                e.printStackTrace();
                                getActivity().runOnUiThread(new Runnable() {
                                    public void run() {
                                        progressDialog.dismiss();
                                        Toast.makeText(getActivity(), st7, 0).show();
                                    }
                                });
                            }
                        }
                    }).start();
                }
                break;
            default:
                break;
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.rl_switch_notification:
            if (iv_switch_open_notification.getVisibility() == View.VISIBLE) {
                iv_switch_open_notification.setVisibility(View.INVISIBLE);
                iv_switch_close_notification.setVisibility(View.VISIBLE);
                rl_switch_sound.setVisibility(View.GONE);
                chatOptions.setNotificationEnable(false);
                EMChatManager.getInstance().setChatOptions(chatOptions);

                HXSDKHelper.getInstance().getModel().setSettingMsgNotification(false);
            } else {
                iv_switch_open_notification.setVisibility(View.VISIBLE);
                iv_switch_close_notification.setVisibility(View.INVISIBLE);
                rl_switch_sound.setVisibility(View.VISIBLE);
                chatOptions.setNotificationEnable(true);
                EMChatManager.getInstance().setChatOptions(chatOptions);
                HXSDKHelper.getInstance().getModel().setSettingMsgNotification(true);
            }
            break;
        case R.id.rl_switch_sound:
            if (iv_switch_open_sound.getVisibility() == View.VISIBLE) {
                iv_switch_open_sound.setVisibility(View.INVISIBLE);
                iv_switch_close_sound.setVisibility(View.VISIBLE);
                chatOptions.setNoticeBySound(false);
                EMChatManager.getInstance().setChatOptions(chatOptions);
                HXSDKHelper.getInstance().getModel().setSettingMsgSound(false);
            } else {
                iv_switch_open_sound.setVisibility(View.VISIBLE);
                iv_switch_close_sound.setVisibility(View.INVISIBLE);
                chatOptions.setNoticeBySound(true);
                EMChatManager.getInstance().setChatOptions(chatOptions);
                HXSDKHelper.getInstance().getModel().setSettingMsgSound(true);
            }
            break;
        }
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
            if(data.size()>1){
                if(data.get(position).get("name").toString().equals("add")){
                    Picasso.with(context).load(R.drawable.user_add).into(holder.avatar);
                    holder.name.setText("");
                }
                else{
                    if(mAvatarData.size()>0){
                        String avatar = mAvatarData.get(data.get(position).get("name").toString());
                        if(!TextUtils.isEmpty(avatar))
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
//       for(int i=0;i<mGroupListData.size()-1;i++){
//           Map<String, Object> map = new HashMap<String, Object>();
//           map = mGroupListData.get(i);
//           if(!mListData.get(i).get("avatar").toString().equals("")){
//               map.remove("image");
//               map.put("avatar",mListData.get(i).get("avatar").toString());
//               mGroupListData.remove(i);
//               mGroupListData.add(i, map);
//           }
//       }
        for(int i=0;i<mListData.size();i++){
            mAvatarData.put(mListData.get(i).get("name").toString(), mListData.get(i).get("avatar").toString());
        }
        adapter.notifyDataSetChanged();
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
}
