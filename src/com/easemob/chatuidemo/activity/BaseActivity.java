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

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;

import com.easemob.applib.controller.HXSDKHelper;
import com.easemob.applib.model.HXNotifier;
import com.testin.agent.TestinAgent;
import com.umeng.analytics.MobclickAgent;

public class BaseActivity extends FragmentActivity {
    public static final int STATE_ERROR = -1; 
    public static final int STATE_FAIL = 0; 
    public static final int STATE_FINISH = 1; 
    public static final int STATE_LINKERR = 2; 
    public static final int STATE_TIMEOUT = 3; 
    public static final int STATE_NULL = 4; 
    public static final int STATE_HOMEWORK_READ = 5; 
    
    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        TestinAgent.init(this, "536c275972bc23e960ddcb68452c6eb6", "wenpy");
    }

    @Override
    protected void onResume() {
        super.onResume();
        // onresume时，取消notification显示
        HXSDKHelper.getInstance().getNotifier().reset();
        
        // umeng
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        // umeng
        MobclickAgent.onPause(this);
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
