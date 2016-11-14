package com.easemob.chatuidemo.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.easemob.applib.controller.HXSDKHelper;
import com.easemob.chatuidemo.DemoHXSDKModel;
import com.easemob.chatuidemo.activity.question.QuestionListFragment;
import com.easemob.chatuidemo.activity.tutorial.TutorialQuestionAddFragment;
import com.wenpy.jcc.R;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class PublicForFragmentActivity extends BaseActivity {
    private String mType;
    private String mFrom;
    private String mTitle;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_public_for_fragment);

        Intent intent = getIntent();
        mFrom = intent.getStringExtra("from");
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        if(mFrom.equals("question_search")){
            List<Map<String, Object>> mListData = (List<Map<String,Object>>)intent.getSerializableExtra("list");
            QuestionListFragment questionListFragment = new QuestionListFragment();
            questionListFragment.setListData(mListData);
            ft.add(R.id.fragment_container, questionListFragment).commit();
        }else if(mFrom.equals("question_add")){
            TutorialQuestionAddFragment tutorialQuestionAddFragment = new TutorialQuestionAddFragment();
            ft.add(R.id.fragment_container, tutorialQuestionAddFragment).commit();
        }else if(mFrom.equals("chat_msg")){
            ChatAllHistoryFragment chatAllHistoryFragment = new ChatAllHistoryFragment();
            ft.add(R.id.fragment_container, chatAllHistoryFragment).commit();
        }
    }
}
