package com.easemob.chatuidemo.activity;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentManager;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.baidu.mapapi.cloud.BoundSearchInfo;
import com.easemob.EMError;
import com.easemob.applib.controller.HXSDKHelper;
import com.easemob.chat.EMChatManager;
import com.easemob.chatuidemo.DemoApplication;
import com.easemob.chatuidemo.DemoHXSDKModel;
import com.wenpy.jcc.R;
import com.easemob.chatuidemo.adapter.ImageAdapter;
import com.easemob.chatuidemo.adapter.TalentAdapter;
import com.easemob.chatuidemo.domain.Info;
import com.easemob.chatuidemo.utils.ConnServer;
import com.easemob.chatuidemo.utils.DialogDemo;
import com.easemob.exceptions.EaseMobException;
import com.google.android.gms.internal.da;

/**
 * 情感辅导
 * 
 */
public class EmotionActivity extends BaseActivity {
    private TextView tvTitle;
    private TextView tvContent;
    private EditText etUser;
    private EditText etNick;
    private Button btOrder;
    private Button btCancel;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_emotion);
		initView();
		initListener();
		initData();
	}

    private void initView() {
        tvTitle = (TextView)findViewById(R.id.message_title);
        tvContent = (TextView)findViewById(R.id.tv_content);
        etUser = (EditText)findViewById(R.id.user);
        etNick = (EditText)findViewById(R.id.nick);
        btOrder = (Button)findViewById(R.id.order);
        btCancel = (Button)findViewById(R.id.cancel);        
    }	

    private void initListener() {
       btOrder.setOnClickListener(new OnClickListener() {
        
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(EmotionActivity.this, 
                        WebBrowerActivity.class);
                intent.putExtra("title", "情感辅导");
                intent.putExtra("user", etUser.getText().toString());
                intent.putExtra("nick", etNick.getText().toString());
                intent.putExtra("url", "http://"+getResources().getString(R.string.server)+"/mobile/emotion.php?user="+EMChatManager.getInstance().getCurrentUser());
                startActivity(intent);
            }
       });
       btCancel.setOnClickListener(new OnClickListener() {
           
           @Override
           public void onClick(View arg0) {
               finish();
           }
      });
    }
    
    private void initData() {
        String content = "    本模块主要为大学生在感情等方面遇到的疑难问题，提供鼓励、沟通、疏导和咨询服务。" +
        		"用户注册满30天，如有需求请网上预约，一天限约一次，辅导师确认机主身份与预约相符，即可通话。";
        tvContent.setText(content);
    }
}
