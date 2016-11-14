package com.easemob.chatuidemo.activity.main;

import java.util.HashMap;
import java.util.Map;

import com.easemob.chat.EMChatManager;
import com.easemob.chatuidemo.DemoApplication;
import com.easemob.chatuidemo.activity.EmotionActivity;
import com.easemob.chatuidemo.activity.MyFriendActivity;
import com.easemob.chatuidemo.activity.WebBrowerActivity;
import com.easemob.chatuidemo.activity.game.GameActivity;
import com.easemob.chatuidemo.activity.group.GroupsActivity;
import com.easemob.chatuidemo.activity.info.InformationActivity;
import com.easemob.chatuidemo.activity.job.JobActivity;
import com.easemob.chatuidemo.activity.ta.TaSearchActivity;
import com.easemob.chatuidemo.activity.talent.TalentsActivity;
import com.easemob.chatuidemo.activity.video.VideoActivity;
import com.easemob.chatuidemo.adapter.GroupAdapter;
import com.wenpy.jcc.R;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

public class SeikatsuFragment extends Fragment {

    private ImageView ivVideo;
    private ImageView ivGame;
    private ImageView ivTalents;
    private ImageView ivInformation;
    private ImageView ivStartup;
    private ImageView ivMyFriend;
    private ImageView ivFindFriend;
    private ImageView ivGroup;
    private ImageView ivEmotion;
    private ImageView ivJob;
    private int identity = 0;
    private Map<String, Boolean> permission;
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        permission = new HashMap<String, Boolean>();
        permission.put("video_group", true);
        permission.put("game_group", true);
        permission.put("info_group", true);
        permission.put("startup_group", true);
        permission.put("talent_group", true);
        permission.put("emotion_group", true);
        permission.put("job_group", true);
        permission.put("myfriend_group", true);
        permission.put("findfriend_group", true);
        permission.put("group_group", true);
        
        identity = DemoApplication.getInstance().getIdentity();
        switch (identity) {
        case 1:
        case 3:
        case 4:
            permission.put("video_group", false);
            permission.put("game_group", false);
            permission.put("info_group", false);
            permission.put("startup_group", false);
            permission.put("talent_group", false);
            permission.put("emotion_group", false);
            permission.put("job_group", false);
            permission.put("myfriend_group", false);
            permission.put("findfriend_group", false);
            permission.put("group_group", false);
            break;

        default:
            break;
        }
        return inflater.inflate(R.layout.fragment_seikatsu, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
        ivVideo = (ImageView)getView().findViewById(R.id.iv_spxx);
        ivVideo.setEnabled(permission.get("video_group"));
        ivVideo.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),
                       VideoActivity.class);
                startActivity(intent);
            }
        });
        ivGame = (ImageView)getView().findViewById(R.id.iv_wy);
        ivGame.setEnabled(permission.get("game_group"));
        ivGame.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),
                       GameActivity.class);
                startActivity(intent);
            }
        });
        ivTalents = (ImageView)getView().findViewById(R.id.iv_shx);
        ivTalents.setEnabled(permission.get("talent_group"));
        ivTalents.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),
                        TalentsActivity.class);
                intent.putExtra("type", "seikatsu");
                startActivity(intent);
            }
       });
        ivInformation = (ImageView)getView().findViewById(R.id.iv_zx);
        ivInformation.setEnabled(permission.get("info_group"));
        ivInformation.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),
                        InformationActivity.class);
                intent.putExtra("type", "seikatsu");
                startActivity(intent);
            }
        });
        ivStartup = (ImageView)getView().findViewById(R.id.iv_cy);
        ivStartup.setEnabled(permission.get("startup_group"));
        ivStartup.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),
                        WebBrowerActivity.class);
                intent.putExtra("title", "创业");
                intent.putExtra("url", "http://"+getResources().getString(R.string.server)+"/mobile/startup.php");
                startActivity(intent);
            }
        });
        ivEmotion = (ImageView)getView().findViewById(R.id.iv_qgfd);
        ivEmotion.setEnabled(permission.get("emotion_group"));
        ivEmotion.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),
                        EmotionActivity.class);
                startActivity(intent);
            }
        });
        ivJob = (ImageView)getView().findViewById(R.id.iv_jy);
        ivJob.setEnabled(permission.get("job_group"));
        ivJob.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),
                        JobActivity.class);
                startActivity(intent);
            }
        });
        ivMyFriend = (ImageView)getView().findViewById(R.id.iv_hy);
        ivMyFriend.setEnabled(permission.get("myfriend_group"));
        ivMyFriend.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),
                        MyFriendActivity.class);
                startActivity(intent);
            }
       });
        ivFindFriend = (ImageView)getView().findViewById(R.id.iv_zta);
        ivFindFriend.setEnabled(permission.get("findfriend_group"));
        ivFindFriend.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),
                        TaSearchActivity.class);
                startActivity(intent);
            }
       });
        ivGroup = (ImageView)getView().findViewById(R.id.iv_qz);
        ivGroup.setEnabled(permission.get("group_group"));
        ivGroup.setOnClickListener(new View.OnClickListener() {
            
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),
                        GroupsActivity.class);
                startActivity(intent);
            }
       });
    }

}
