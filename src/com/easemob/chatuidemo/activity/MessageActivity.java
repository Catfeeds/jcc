package com.easemob.chatuidemo.activity;

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
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.easemob.EMError;
import com.easemob.applib.controller.HXSDKHelper;
import com.easemob.chat.EMChatManager;
import com.easemob.chatuidemo.Constant;
import com.easemob.chatuidemo.DemoApplication;
import com.easemob.chatuidemo.DemoHXSDKHelper;
import com.easemob.chatuidemo.DemoHXSDKModel;
import com.easemob.chatuidemo.activity.main.MainActivity;
import com.easemob.chatuidemo.domain.User;
import com.wenpy.jcc.R;
import com.easemob.exceptions.EaseMobException;

/**
 * 
 * 
 */
public class MessageActivity extends BaseActivity {
    private TextView tvUnreadInvite;
    private TextView tvUnreadChat;
    private MainActivity mainActivity;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_message_main);
		
		Intent intent = getIntent();
		int invitenum = intent.getIntExtra("invitenum", 0);
        int chatnum = intent.getIntExtra("chatnum", 0);
        
        tvUnreadInvite = (TextView)findViewById(R.id.unread_msg_invite);
        tvUnreadChat = (TextView)findViewById(R.id.unread_msg_chat);
        
        if(invitenum > 0)
            tvUnreadInvite.setVisibility(View.VISIBLE);
        if(chatnum > 0)
            tvUnreadChat.setVisibility(View.VISIBLE);
	}
	
	public void InviteClick(View v){
	    User user = ((DemoHXSDKHelper)HXSDKHelper.getInstance()).getContactList().get(Constant.NEW_FRIENDS_USERNAME);
        user.setUnreadMsgCount(0);
        tvUnreadInvite.setVisibility(View.INVISIBLE);
        startActivity(new Intent(MessageActivity.this, NewFriendsMsgActivity.class));
	}
    
    public void ChatClick(View v){
        tvUnreadChat.setVisibility(View.INVISIBLE);
        Intent intent = new Intent(this, PublicForFragmentActivity.class);
        intent.putExtra("from", "chat_msg");
        startActivity(intent);
    }
	
	public void back(View view) {
		finish();
	}

}
