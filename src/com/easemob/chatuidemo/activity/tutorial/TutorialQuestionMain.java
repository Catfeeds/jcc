package com.easemob.chatuidemo.activity.tutorial;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.support.v4.app.Fragment;
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
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ListView;
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
import com.easemob.chat.core.r;
import com.easemob.chatuidemo.DemoApplication;
import com.easemob.chatuidemo.DemoHXSDKModel;
import com.wenpy.jcc.R;
import com.easemob.chatuidemo.activity.homework.HomeworkInfoDetailActivity;
import com.easemob.chatuidemo.adapter.ImageAdapter;
import com.easemob.chatuidemo.adapter.TalentAdapter;
import com.easemob.chatuidemo.domain.Info;
import com.easemob.chatuidemo.utils.ConnServer;
import com.easemob.chatuidemo.utils.DialogDemo;
import com.easemob.exceptions.EaseMobException;
import com.google.android.gms.internal.da;

/**
 * 
 * 
 */
public class TutorialQuestionMain extends Fragment implements OnClickListener {
    private TextView tvQuestions;
    private ListView lvInformationList;
    private RelativeLayout rlPolitical;
    private RelativeLayout rlChinese;
    private RelativeLayout rlMath;
    private RelativeLayout rlEnglish;
    private RelativeLayout rlPhysics;
    private RelativeLayout rlChemistry;
    private RelativeLayout rlGeography;
    private RelativeLayout rlHistory;
    private TextView tvMsgPolitical;
    private TextView tvMsgChinese;
    private TextView tvMsgMath;
    private TextView tvMsgEnglish;
    private TextView tvMsgPhysics;
    private TextView tvMsgChemistry;
    private TextView tvMsgGeography;
    private TextView tvMsgHistory;
    private TutorialQuestionAddFragment questionAddFragment;
    private File cache;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        return inflater.inflate(R.layout.fragment_question_main, container, false);
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
		
        questionAddFragment = new TutorialQuestionAddFragment();
        tvQuestions = (TextView)getActivity().findViewById(R.id.questions);
		rlPolitical = (RelativeLayout)getActivity().findViewById(R.id.rl_political);
		rlChinese = (RelativeLayout)getActivity().findViewById(R.id.rl_chinese);
		rlMath = (RelativeLayout)getActivity().findViewById(R.id.rl_math);
		rlEnglish = (RelativeLayout)getActivity().findViewById(R.id.rl_english);
		rlPhysics = (RelativeLayout)getActivity().findViewById(R.id.rl_physics);
		rlChemistry = (RelativeLayout)getActivity().findViewById(R.id.rl_chemistry);
		rlGeography = (RelativeLayout)getActivity().findViewById(R.id.rl_geography);
		rlHistory = (RelativeLayout)getActivity().findViewById(R.id.rl_history);

        tvMsgPolitical = (TextView)getActivity().findViewById(R.id.unread_msg_number_political);
        tvMsgChinese = (TextView)getActivity().findViewById(R.id.unread_msg_number_chinese);
        tvMsgMath = (TextView)getActivity().findViewById(R.id.unread_msg_number_math);
        tvMsgEnglish = (TextView)getActivity().findViewById(R.id.unread_msg_number_english);
        tvMsgPhysics = (TextView)getActivity().findViewById(R.id.unread_msg_number_physics);
        tvMsgChemistry = (TextView)getActivity().findViewById(R.id.unread_msg_number_chemistry);
        tvMsgGeography = (TextView)getActivity().findViewById(R.id.unread_msg_number_geography);
        tvMsgHistory = (TextView)getActivity().findViewById(R.id.unread_msg_number_history);
        
        tvQuestions.setOnClickListener(this);
		rlPolitical.setOnClickListener(this);
		rlChinese.setOnClickListener(this);
		rlMath.setOnClickListener(this);
		rlEnglish.setOnClickListener(this);
        rlPhysics.setOnClickListener(this);
        rlChemistry.setOnClickListener(this);
        rlGeography.setOnClickListener(this);
        rlHistory.setOnClickListener(this);
	}

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
        case R.id.questions:
            FragmentManager manager = getActivity().getSupportFragmentManager();
            manager.beginTransaction().replace(R.id.fragment_container, questionAddFragment).addToBackStack(null).commit();
            break;
        case R.id.rl_political:
            if(tvMsgPolitical.getVisibility() != View.INVISIBLE){
                Intent intent = new Intent(getActivity(),
                        HomeworkInfoDetailActivity.class);
                Bundle extras = new Bundle();
                extras.putInt("id", v.getId());
                intent.putExtras(extras);
                startActivity(intent);
            }
            break;
        case R.id.rl_chinese:
            break;
        case R.id.rl_math:
            break;
        case R.id.rl_english:
            break;
        case R.id.rl_physics:
            break;
        case R.id.rl_chemistry:
            break;
        case R.id.rl_geography:
            break;
        case R.id.rl_history:
        default:
            break;
        }
    }

}
