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
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnTouchListener;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
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
import com.easemob.chatuidemo.activity.main.MainActivity;
import com.easemob.chatuidemo.adapter.GroupAdapter;
import com.easemob.chatuidemo.adapter.MyGroupAdapter;
import com.easemob.exceptions.EaseMobException;
import com.easemob.util.EMLog;

public class GroupsActivity extends BaseActivity {
	public static final String TAG = "GroupsActivity";
	private ListView mcGroupListView;
    private ListView mjGroupListView;
    private ListView otGroupListView;
	private TextView tvCreateGroups;
	private TextView tvSearch;
	protected List<EMGroup> grouplist;
    protected List<EMGroup> mcGrouplist;
    protected List<EMGroup> mjGrouplist;
    protected List<EMGroupInfo> otGrouplist;
	private GroupsAdapter otGroupAdapter;
    private MyGroupAdapter mjGroupAdapter;
    private MyGroupAdapter mcGroupAdapter;
	private InputMethodManager inputMethodManager;
	public static GroupsActivity instance;
	private SyncListener syncListener;
	private View progressBar;
	private SwipeRefreshLayout swipeRefreshLayout;
	private String cursor;
	private final int pagesize = 20;
	Handler handler = new Handler();
    private boolean isLoading;

	class SyncListener implements HXSDKHelper.HXSyncListener {
		@Override
		public void onSyncSucess(final boolean success) {
			EMLog.d(TAG, "onSyncGroupsFinish success:" + success);
			runOnUiThread(new Runnable() {
				public void run() {
					swipeRefreshLayout.setRefreshing(false);
					if (success) {
						handler.postDelayed(new Runnable() {
							@Override
							public void run() {
								refresh();
								progressBar.setVisibility(View.GONE);
							}
						}, 1000);
					} else {
						if (!GroupsActivity.this.isFinishing()) {
							String s1 = getResources()
									.getString(
											R.string.Failed_to_get_group_chat_information);
							Toast.makeText(GroupsActivity.this, s1, Toast.LENGTH_LONG).show();
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
		setContentView(R.layout.fragment_groups_zj);

		instance = this;
		inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		mcGrouplist = new ArrayList<EMGroup>();
        mjGrouplist = new ArrayList<EMGroup>();
        otGrouplist = new ArrayList<EMGroupInfo>();
		grouplist = EMGroupManager.getInstance().getAllGroups();
		
		loadOtherGroups();
        
		group_classification(grouplist);
		
		mcGroupListView = (ListView) findViewById(R.id.mc_groups_list);
        mjGroupListView = (ListView) findViewById(R.id.mj_groups_list);
        otGroupListView = (ListView) findViewById(R.id.other_groups_list);
		
		tvCreateGroups = (TextView) findViewById(R.id.create_groups);
		tvCreateGroups.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View arg0) {
                startActivityForResult(new Intent(GroupsActivity.this, NewGroupActivity.class), 0);
                
            }
        });
		tvSearch = (TextView) findViewById(R.id.search_groups);
		tvSearch.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View arg0) {
                startActivity(new Intent(GroupsActivity.this, PublicGroupsSeachActivity.class));
            }
        });
		
		swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_layout);
		swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light,
		                android.R.color.holo_orange_light, android.R.color.holo_red_light);
		swipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
			    MainActivity.asyncFetchGroupsFromServer();
			}
		});

        mcGroupAdapter = new MyGroupAdapter(this, 1, mcGrouplist);
        mcGroupListView.setAdapter(mcGroupAdapter);
        
        mjGroupAdapter = new MyGroupAdapter(this, 1, mjGrouplist);
        mjGroupListView.setAdapter(mjGroupAdapter);
		
		mcGroupListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
//				if (position == 1) {
//					// 新建群聊
//					startActivityForResult(new Intent(GroupsActivity.this, NewGroupActivity.class), 0);
//				} else if (position == 2) {
//					// 添加公开群
//					startActivityForResult(new Intent(GroupsActivity.this, PublicGroupsActivity.class), 0);
//				} else 
				{
					// 进入群聊
					Intent intent = new Intent(GroupsActivity.this, ChatActivity.class);
					// it is group chat
					intent.putExtra("chatType", ChatActivity.CHATTYPE_GROUP);
					intent.putExtra("groupId", mcGroupAdapter.getItem(position).getGroupId());
					startActivityForResult(intent, 0);
				}
			}

		});
        mjGroupListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(GroupsActivity.this, ChatActivity.class);
                // it is group chat
                intent.putExtra("chatType", ChatActivity.CHATTYPE_GROUP);
                intent.putExtra("groupId", mjGroupAdapter.getItem(position).getGroupId());
                startActivityForResult(intent, 0);
            }

        });
		
		progressBar = (View)findViewById(R.id.progress_bar);
		
		syncListener = new SyncListener();
		HXSDKHelper.getInstance().addSyncGroupListener(syncListener);

		if (!HXSDKHelper.getInstance().isGroupsSyncedWithServer()) {
			progressBar.setVisibility(View.VISIBLE);
		} else {
			progressBar.setVisibility(View.GONE);
		}
		
		refresh();
	}


    private class GroupsAdapter extends ArrayAdapter<EMGroupInfo> {

        private LayoutInflater inflater;

        public GroupsAdapter(Context context, int res, List<EMGroupInfo> groups) {
            super(context, res, groups);
            this.inflater = LayoutInflater.from(context);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.row_group, null);
            }

            ((TextView) convertView.findViewById(R.id.name)).setText(getItem(position).getGroupName());

            return convertView;
        }
    }
    
    private void loadOtherGroups(){
        new Thread(new Runnable() {

            public void run() {
                try {
                    isLoading = true;
                    final EMCursorResult<EMGroupInfo> result = EMGroupManager.getInstance().getPublicGroupsFromServer(pagesize, cursor);
                    //获取group list
                    final List<EMGroupInfo> returnGroups = result.getData();
                    runOnUiThread(new Runnable() {

                        public void run() {
                            otGrouplist.addAll(returnGroups);
                            otGroupAdapter = new GroupsAdapter(GroupsActivity.this, 1, otGrouplist);
                            otGroupListView.setAdapter(otGroupAdapter);
                        }
                    });
                }catch (EaseMobException e) {
                    e.printStackTrace();
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(GroupsActivity.this, "加载数据失败，请检查网络或稍后重试", 0).show();
                        }
                    });
                }
            }

        }).start();
    }
    
	private void group_classification(List<EMGroup> groups) {
	    for(int i=0;i<groups.size();i++){
	        EMGroup group = groups.get(i);
	        if(group.getOwner().equals(EMChatManager.getInstance().getCurrentUser())){
	            mcGrouplist.add(group);
	        }else{
	            mjGrouplist.add(group);
	        }
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
		mcGroupAdapter = new MyGroupAdapter(this, 1, mcGrouplist);
		mcGroupListView.setAdapter(mcGroupAdapter);
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
		if (mcGroupListView != null && mcGroupAdapter != null) {
			grouplist = EMGroupManager.getInstance().getAllGroups();
			mcGroupAdapter = new MyGroupAdapter(GroupsActivity.this, 1,
			        mcGrouplist);
			mcGroupListView.setAdapter(mcGroupAdapter);
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
