package com.easemob.chatuidemo.activity.group;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TabHost;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.easemob.EMError;
import com.easemob.applib.controller.HXSDKHelper;
import com.easemob.chat.EMChatManager;
import com.easemob.chatuidemo.DemoApplication;
import com.easemob.chatuidemo.DemoHXSDKModel;
import com.easemob.chatuidemo.activity.BaseActivity;
import com.easemob.chatuidemo.activity.MyFriendActivity;
import com.wenpy.jcc.R;
import com.easemob.exceptions.EaseMobException;

/**
 * 教师群
 * 
 */
public class TeacherGroupActivity extends BaseActivity {
   
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_teacher_group);
	}
	
	public void MyFriendClick(View v){
        startActivityForResult(new Intent(this, MyFriendActivity.class), 0);	    
	}
    
    public void MyGroupClick(View v){
        startActivityForResult(new Intent(this, GroupsListActivity.class), 0);
    }
	
	public void back(View view) {
		finish();
	}

}
