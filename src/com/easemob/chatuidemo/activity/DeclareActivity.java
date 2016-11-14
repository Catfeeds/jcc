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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.DialogInterface.OnCancelListener;
import android.content.Intent;
import android.os.Bundle;
import android.sax.TextElementListener;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.EMCallBack;
import com.easemob.applib.controller.HXSDKHelper;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMGroupManager;
import com.easemob.chatuidemo.Constant;
import com.easemob.chatuidemo.DemoApplication;
import com.easemob.chatuidemo.DemoHXSDKHelper;
import com.wenpy.jcc.R;
import com.easemob.chatuidemo.activity.main.LoginActivity;
import com.easemob.chatuidemo.activity.main.MainActivity;
import com.easemob.chatuidemo.db.UserDao;
import com.easemob.chatuidemo.domain.User;
import com.easemob.chatuidemo.utils.CommonUtils;

/**
 * 声明页面
 * 
 */
public class DeclareActivity extends BaseActivity {
	private static final String TAG = "DeclareActivity";
	public static final int REQUEST_CODE_SETNICK = 1;
	private TextView contentTextView;
    private static final int sleepTime = 2000;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// 如果用户名密码都有，直接进入主页面
//		if (DemoHXSDKHelper.getInstance().isLogined()) {
//			autoLogin = true;
//			startActivity(new Intent(DeclareActivity.this, MainActivity.class));
//
//			return;
//		}
		setContentView(R.layout.activity_declaration);
		contentTextView = (TextView)findViewById(R.id.content);
		setContent();
	}

	public void setContent(){
	    String text = "1、用户可以不使用真实姓名注册，但身份属性必须按真实情况填写，否则会影响用户使用各模块的功能。\n"
	            + "2、 教师、学生按要求注册信息后，自动生成群组；家长的帐号为其小孩帐号后加字母J，如学生账户为123456789，"
	            + "则家长帐号为123456789J，家长帐号随学生所处年级、班级、学校的变化而自动组合成家长群。\n"
	            + "3、 大学生和中小学生两大群体用户不能使用对方模块功能。\n"
	            + "4、 所有用户必须遵守国家法律法规，不得发布虚假信息和诋毁损害他人利益的言论，"
	            + "违者本站有权查封其帐号或删帖，造成严重后果的，本站有权追究其发法律责任。";
	    contentTextView.setText(text);
	}
	
	/**
	 * 同意
	 * 
	 * @param view
	 */
	public void agree(View view) {
        // 进入下一个页面
	    if (DemoHXSDKHelper.getInstance().isLogined()) {
            // ** 免登陆情况 加载所有本地群和会话
            //不是必须的，不加sdk也会自动异步去加载(不会重复加载)；
            //加上的话保证进了主页面会话和群组都已经load完毕
            long start = System.currentTimeMillis();
            EMGroupManager.getInstance().loadAllGroups();
            EMChatManager.getInstance().loadAllConversations();
            long costTime = System.currentTimeMillis() - start;
            //等待sleeptime时长
            if (sleepTime - costTime > 0) {
                try {
                    Thread.sleep(sleepTime - costTime);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            //进入主页面
            startActivity(new Intent(DeclareActivity.this, MainActivity.class));
            finish();
        }else {
            try {
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                
            }
            startActivity(new Intent(DeclareActivity.this, LoginActivity.class));
            finish();
        }
    }
}
