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
package com.easemob.chatuidemo.activity;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.applib.controller.HXSDKHelper;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMContactManager;
import com.easemob.chatuidemo.DemoApplication;
import com.easemob.chatuidemo.DemoHXSDKHelper;
import com.easemob.chatuidemo.utils.ConnServer;
import com.easemob.chatuidemo.utils.DialogDemo;
import com.wenpy.jcc.R;

public class AddContactActivity extends BaseActivity{
	private EditText editText;
	private LinearLayout searchedUserLayout;
	private TextView nameText,mTextView;
	private Button searchBtn;
	private ImageView avatar;
	private InputMethodManager inputMethodManager;
	private String toAddUsername;
	private ProgressDialog progressDialog;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_add_contact);
		mTextView = (TextView) findViewById(R.id.add_list_friends);
		
		editText = (EditText) findViewById(R.id.edit_note);
		String strAdd = getResources().getString(R.string.add_friend);
		mTextView.setText(strAdd);
		String strUserName = getResources().getString(R.string.user_name);
		editText.setHint(strUserName);
		searchedUserLayout = (LinearLayout) findViewById(R.id.ll_user);
		nameText = (TextView) findViewById(R.id.name);
		searchBtn = (Button) findViewById(R.id.search);
		avatar = (ImageView) findViewById(R.id.avatar);
		inputMethodManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
	}
	
	
	/**
	 * 查找contact
	 * @param v
	 */
	public void searchContact(View v) {
		final String name = editText.getText().toString();
		String saveText = searchBtn.getText().toString();
		
		if (getString(R.string.button_search).equals(saveText)) {
			toAddUsername = name;
			if(TextUtils.isEmpty(name)) {
				String st = getResources().getString(R.string.Please_enter_a_username);
				startActivity(new Intent(this, AlertDialog.class).putExtra("msg", st));
				return;
			}
			
			// TODO 从服务器获取此contact,如果不存在提示不存在此用户
			DialogDemo.show(AddContactActivity.this,"正在查找...");
            
            new Thread(new Runnable() {
                public void run() {
                    
                    try {
                        String edit = "user_get";
                        Map<String, String> params = new HashMap<String, String>();
                        params.put("method", edit);
                        params.put("name", nameText.getText().toString());
                        String path = "http://"+getResources().getString(R.string.server)+"/api.php"; 
                        ConnServer server = new ConnServer();
                        List<Map<String, Object>> ListData = server.GetData(path,params,edit,mHandler);
                    } catch (final Exception e) {
                        
                    }
                }
            }).start();
            
			//服务器存在此用户，显示此用户和添加按钮
//			searchedUserLayout.setVisibility(View.VISIBLE);
//			nameText.setText(toAddUsername);
			
		} 
	}	
	
	/**
	 *  添加contact
	 * @param view
	 */
	public void addContact(View view){
		if(DemoApplication.getInstance().getUserName().equals(nameText.getText().toString())){
			String str = getString(R.string.not_add_myself);
			startActivity(new Intent(this, AlertDialog.class).putExtra("msg", str));
			return;
		}
		
		if(((DemoHXSDKHelper)HXSDKHelper.getInstance()).getContactList().containsKey(nameText.getText().toString())){
		    //提示已在好友列表中，无需添加
		    if(EMContactManager.getInstance().getBlackListUsernames().contains(nameText.getText().toString())){
		        startActivity(new Intent(this, AlertDialog.class).putExtra("msg", "此用户已是你好友(被拉黑状态)，从黑名单列表中移出即可"));
		        return;
		    }
			String strin = getString(R.string.This_user_is_already_your_friend);
			startActivity(new Intent(this, AlertDialog.class).putExtra("msg", strin));
			return;
		}
		final EditText resson = new EditText(this);
		resson.setHint("添加留言");
		
		new android.app.AlertDialog.Builder(AddContactActivity.this)
		    .setTitle("添加好友")
		    .setView(resson)
		    .setNegativeButton("取消", null)
		    .setPositiveButton("发送", new DialogInterface.OnClickListener() {
                
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    progressDialog = new ProgressDialog(AddContactActivity.this);
                    String stri = getResources().getString(R.string.Is_sending_a_request);
                    progressDialog.setMessage(stri);
                    progressDialog.setCanceledOnTouchOutside(false);
                    progressDialog.show();
                    
                    new Thread(new Runnable() {
                        public void run() {
                            
                            try {
                                //demo写死了个reason，实际应该让用户手动填入
                                String s = resson.getText().toString();
                                EMContactManager.getInstance().addContact(toAddUsername, s);
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        progressDialog.dismiss();
                                        String s1 = getResources().getString(R.string.send_successful);
                                        Toast.makeText(getApplicationContext(), s1, 1).show();
                                    }
                                });
                            } catch (final Exception e) {
                                runOnUiThread(new Runnable() {
                                    public void run() {
                                        progressDialog.dismiss();
                                        String s2 = getResources().getString(R.string.Request_add_buddy_failure);
                                        Toast.makeText(getApplicationContext(), s2 + e.getMessage(), 1).show();
                                    }
                                });
                            }
                        }
                    }).start();
                }
            })
            .create()
            .show();
	}
	

    private Handler mHandler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            DialogDemo.dismiss(); 
            switch(msg.what){
                case STATE_FINISH:
                  searchedUserLayout.setVisibility(View.VISIBLE);
                  nameText.setText(toAddUsername);
                    break;
                case STATE_ERROR:
                    Toast.makeText(getApplicationContext(), "network error", 0).show();
                    break;
                case STATE_FAIL:
                    break;
                case STATE_NULL:
                    Toast.makeText(getApplicationContext(), "no user", 0).show();                    
                    break;
            }
        };
    };    
    
	public void back(View v) {
		finish();
	}
}
