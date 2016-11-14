/**
 * Copyright (C) 2013-2014 EaseMob Technologies. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *     http://www.apache.org/licenses/LICENSE-2.0
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.easemob.chatuidemo.activity.group;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.map.Text;
import com.easemob.applib.controller.HXSDKHelper;
import com.easemob.applib.utils.HXPreferenceUtils;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMCursorResult;
import com.easemob.chat.EMGroup;
import com.easemob.chat.EMGroupInfo;
import com.easemob.chat.EMGroupManager;
import com.wenpy.jcc.R;
import com.easemob.chatuidemo.activity.BaseActivity;
import com.easemob.chatuidemo.activity.ChatActivity;
import com.easemob.chatuidemo.adapter.GroupAdapter;
import com.easemob.chatuidemo.adapter.MyGroupAdapter;
import com.easemob.chatuidemo.utils.CommonUtils;
import com.easemob.exceptions.EaseMobException;
import com.easemob.util.EMLog;

public class GroupsListActivity extends BaseActivity {
	public static final String TAG = "GroupsActivity";
	private GridView gvGroupList;
	protected List<EMGroup> grouplist;
    protected List<EMGroup> mcGrouplist;
    private GroupAdapter mcGroupAdapter;
	private InputMethodManager inputMethodManager;
	public static GroupsListActivity instance;
	private SyncListener syncListener;
	private RelativeLayout progressBar;
	Handler handler = new Handler();
    private boolean isLoading;

	class SyncListener implements HXSDKHelper.HXSyncListener {
		@Override
		public void onSyncSucess(final boolean success) {
			EMLog.d(TAG, "onSyncGroupsFinish success:" + success);
			runOnUiThread(new Runnable() {
				public void run() {
				    Log.e("state","state="+success);
					if (success) {
						handler.postDelayed(new Runnable() {
							@Override
							public void run() {
								refresh();
								progressBar.setVisibility(View.GONE);
							}
						}, 1000);
					} else {
						if (!GroupsListActivity.this.isFinishing()) {
							String s1 = getResources()
									.getString(
											R.string.Failed_to_get_group_chat_information);
							Toast.makeText(GroupsListActivity.this, s1, Toast.LENGTH_LONG).show();
							progressBar.setVisibility(View.GONE);
						}
					}
				}
			});
		}
	}
		
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_groups_gvlist);

		mcGrouplist = new ArrayList<EMGroup>();
		instance = this;
		inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		grouplist = EMGroupManager.getInstance().getAllGroups();
				
		gvGroupList = (GridView) findViewById(R.id.gv_groups);
		
        mcGroupAdapter = new GroupAdapter(this,grouplist);
        gvGroupList.setAdapter(mcGroupAdapter);
        
		
        gvGroupList.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
				Intent intent = new Intent(GroupsListActivity.this, ChatActivity.class);
				// it is group chat
				intent.putExtra("chatType", ChatActivity.CHATTYPE_GROUP);
				intent.putExtra("groupId", ((EMGroup) mcGroupAdapter.getItem(position)).getGroupId());
				startActivityForResult(intent, 0);
			}

		});
        		
		progressBar = (RelativeLayout)findViewById(R.id.progress_bar);
		
		syncListener = new SyncListener();
		HXSDKHelper.getInstance().addSyncGroupListener(syncListener);

		if (!HXSDKHelper.getInstance().isGroupsSyncedWithServer()) {
			progressBar.setVisibility(View.VISIBLE);
		} else {
			progressBar.setVisibility(View.GONE);
		}
		
		refresh();
	}


    public class GroupAdapter extends BaseAdapter{
        private List<EMGroup> data = new ArrayList<EMGroup>();
        private Context context;
        private LayoutInflater mInflater;  

        public GroupAdapter(Context context, List<EMGroup> data) {  
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
            int height = CommonUtils.getWidthHeight(context,"h");
            final float scale = context.getResources().getDisplayMetrics().density;
            height = height - (int) (200 * scale + 0.5f); 
            int pad = (int) (10 * scale + 0.5f); 
            holder.avatar.setLayoutParams(new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT,
                    height/3));
            holder.avatar.setScaleType(ImageView.ScaleType.CENTER_CROP);
            holder.avatar.setPadding(pad, pad, pad, pad);  
            holder.avatar.setImageResource(R.drawable.group_icon);
            holder.name.setText(data.get(position).getGroupName());
            
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
	 * 进入公开群聊列表
	 */
	public void onPublicGroups(View view) {
		startActivity(new Intent(this, PublicGroupsActivity.class));
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}

	@Override
	public void onResume() {
		super.onResume();
		grouplist = EMGroupManager.getInstance().getAllGroups();
//		mcGroupAdapter = new MyGroupAdapter(this, 1, mcGrouplist);
//		gvGroupList.setAdapter(mcGroupAdapter);
		mcGroupAdapter.notifyDataSetChanged();
	}

	@Override
	protected void onDestroy() {
		if (syncListener != null) {
			HXSDKHelper.getInstance().removeSyncGroupListener(syncListener);
			syncListener = null;
		}
		super.onDestroy();
		instance = null;
	}
	
	public void refresh() {
		if (gvGroupList != null && mcGroupAdapter != null) {
			grouplist = EMGroupManager.getInstance().getAllGroups();
//			mcGroupAdapter = new GroupAdapter(GroupsListActivity.this, grouplist);
			gvGroupList.setAdapter(mcGroupAdapter);
			mcGroupAdapter.notifyDataSetChanged();
		}
	}

	/**
	 * 返回
	 * 
	 * @param view
	 */
	public void back(View view) {
		finish();
	}
}
