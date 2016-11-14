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
package com.easemob.chatuidemo.activity.homework;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
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
import com.easemob.chatuidemo.activity.group.NewGroupActivity;
import com.easemob.chatuidemo.activity.group.PublicGroupsActivity;
import com.easemob.chatuidemo.activity.group.PublicGroupsSeachActivity;
import com.easemob.chatuidemo.activity.main.MainActivity;
import com.easemob.chatuidemo.adapter.GroupAdapter;
import com.easemob.chatuidemo.adapter.HomeworkAdapter;
import com.easemob.chatuidemo.adapter.MyGroupAdapter;
import com.easemob.chatuidemo.domain.Info;
import com.easemob.chatuidemo.utils.ConnServer;
import com.easemob.chatuidemo.utils.DialogDemo;
import com.easemob.exceptions.EaseMobException;
import com.easemob.util.EMLog;

public class HomeworkListTeacherActivity extends BaseActivity {
	public static final String TAG = "GroupsActivity";
	private ListView HomeworkListView;
    private HomeworkAdapter mAdapter;
	private InputMethodManager inputMethodManager;
	public static HomeworkListTeacherActivity instance;
	private View progressBar;
	private SwipeRefreshLayout swipeRefreshLayout;
	private String cursor;
	private final int pagesize = 20;
    private boolean isLoading;
    public List<Map<String, Object>> mReturnData;
    private List<Info> infos = new ArrayList<Info>();
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_homeworklist_teacher);

		instance = this;
		
		HomeworkListView = (ListView) findViewById(R.id.lv_homework_list);
			
		swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_layout);
		swipeRefreshLayout.setColorSchemeResources(android.R.color.holo_blue_bright, android.R.color.holo_green_light,
		                android.R.color.holo_orange_light, android.R.color.holo_red_light);
		swipeRefreshLayout.setOnRefreshListener(new OnRefreshListener() {

			@Override
			public void onRefresh() {
			    new GetDataThread(handler).start();
			}
		});

        mAdapter = new HomeworkAdapter(this, infos);
        HomeworkListView.setAdapter(mAdapter);
       
		
		HomeworkListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
			    Intent intent = new Intent(HomeworkListTeacherActivity.this,
                        HomeworkInfoContentActivity.class);
                Bundle extras = new Bundle();
                extras.putString("id", mReturnData.get(position).get("id").toString());
                extras.putString("nick", EMChatManager.getInstance().getCurrentUser());
                intent.putExtras(extras);
                startActivity(intent);
			}

		});
		
        DialogDemo.show(this,"正在加载，请稍后...");
        new GetDataThread(handler).start();
	}

    private class GetDataThread extends Thread { 
         
        private Handler handler; 
 
        public GetDataThread(Handler handler) { 
            this.handler = handler; 
        } 
 
        @Override 
        public void run() { 
            String edit = "inform_get";
            Map<String, String> params = new HashMap<String, String>();
            params.put("method", edit);
            params.put("user", EMChatManager.getInstance().getCurrentUser());
            
            ConnServer server = new ConnServer();
            String path = "http://"+getResources().getString(R.string.server)+"/api.php";
            mReturnData = server.GetData(path,params,edit,handler);
        }
    }
    
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		super.onActivityResult(requestCode, resultCode, data);
	}
		
	public void refresh() {
		if (HomeworkListView != null && mAdapter != null) {
			mAdapter.notifyDataSetChanged();
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
	
    private final Handler handler = new Handler(Looper.getMainLooper()) {
         
        public void handleMessage(Message msg) { // 处理Message，更新ListView
            DialogDemo.dismiss();
            String op  = msg.obj.toString();
            switch(msg.what){ 
                case STATE_FINISH: 
                {
                    if(op.equals("inform_get")){
                        infos.clear();
                        for(int i=0;i<mReturnData.size();i++){
                            Info info = new Info();
                            info.setTitle(mReturnData.get(i).get("accepter").toString());
                            info.setDate(mReturnData.get(i).get("date").toString());
                            infos.add(info);
                        }
                    }
                    mAdapter.notifyDataSetChanged();
                    swipeRefreshLayout.setRefreshing(false);
                }
                    break;
                case STATE_FAIL:
                {
                    String info = "Fail!";
                    if(msg.obj != null){
                        info = info+"\n"+msg.obj;
                    }
                    Toast.makeText(HomeworkListTeacherActivity.this, info, 1).show();
                    break;
                }
                case STATE_NULL:
                    //Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.datanull),Toast.LENGTH_SHORT).show(); 
                    break;
                case STATE_LINKERR:
                {
                    Toast.makeText(HomeworkListTeacherActivity.this, "网络连接失败",Toast.LENGTH_SHORT) .show();  
                    break;
                }
                case STATE_ERROR: 
                    Toast.makeText(HomeworkListTeacherActivity.this, "网络连接错误",Toast.LENGTH_SHORT) .show();  
                    break;
               default:  
            } 
        } 
    }; 
}
