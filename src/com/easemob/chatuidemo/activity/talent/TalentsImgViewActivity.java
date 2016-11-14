package com.easemob.chatuidemo.activity.talent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.easemob.applib.controller.HXSDKHelper;
import com.easemob.chatuidemo.DemoHXSDKModel;
import com.easemob.chatuidemo.activity.BaseActivity;
import com.easemob.chatuidemo.activity.ImageViewFragment;
import com.wenpy.jcc.R;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class TalentsImgViewActivity extends BaseActivity {
    private List<Map<String, Object>> mFriendListData;
    private ImageViewFragment imageViewFragment;
    private TextView tvTitle;
    private String mType;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_talents_detail);
        tvTitle = (TextView)findViewById(R.id.message_title);

        
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        imageViewFragment = new ImageViewFragment();

        Intent intent = getIntent();
        Bundle bundlbe = intent.getExtras();
        String id = bundlbe.getString("id");
        mType = bundlbe.getString("type");

        if(mType.equals("seikatsu")){
            tvTitle.setText("生活秀");
        }else{
            tvTitle.setText("师生风采");
        }
        imageViewFragment.SetId(id);
        imageViewFragment.setType(mType);
        ft.add(R.id.fragment_container, imageViewFragment);
        ft.commit();
    }
}
