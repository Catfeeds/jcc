package com.easemob.chatuidemo.activity.main;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.easemob.EMCallBack;
import com.easemob.chat.EMChatManager;
import com.easemob.chatuidemo.DemoApplication;
import com.easemob.chatuidemo.DemoHXSDKHelper;
import com.easemob.chatuidemo.activity.WebBrowerActivity;
import com.easemob.chatuidemo.activity.group.ParentGroupActivity;
import com.easemob.chatuidemo.activity.group.StudentGroupActivity;
import com.easemob.chatuidemo.activity.group.TeacherGroupActivity;
import com.easemob.chatuidemo.activity.homework.HomeworkInfoActivity;
import com.easemob.chatuidemo.activity.info.InformationActivity;
import com.easemob.chatuidemo.activity.question.QuestionSearchActivity;
import com.easemob.chatuidemo.activity.talent.TalentsActivity;
import com.easemob.chatuidemo.activity.tutorial.TutorialStudentOpenActivity;
import com.easemob.chatuidemo.activity.video.VideoActivity;
import com.easemob.chatuidemo.utils.DialogDemo;
import com.wenpy.jcc.R;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.PopupMenu;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.PopupMenu.OnMenuItemClickListener;

public class SchoolyardFragment extends Fragment {
    private ImageView ivStudentGroup;
    private ImageView ivTeacherGroup;
    private ImageView ivParentGroup;
    private ImageView ivInformation;
    private ImageView ivTalents;
    private ImageView ivHomework;
    private ImageView ivVideo;
    private ImageView ivEnrollment;
    private ImageView ivSQuestion;
    private ImageView ivPSchool;
    private ImageView ivJSchool;
    public ImageView ivHSchool;
    private RelativeLayout rlQuestionAnswer;
    public TextView unreadHomework;
    private int identity = 0;
    private Map<String, Boolean> permission;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        permission = new HashMap<String, Boolean>();
        permission.put("student_group", false);
        permission.put("teacher_group", false);
        permission.put("parent_group", false);
        permission.put("inform_group", true);
        permission.put("talent_group", true);
        permission.put("homework_group", true);
        permission.put("video_group", true);
        permission.put("enrollment_group", true);
        permission.put("question_group", true);
        
        identity = DemoApplication.getInstance().getIdentity();
        switch (identity) {
        case 1:
            permission.put("student_group", true);
            break;
        case 2:
            permission.put("inform_group", false);
            permission.put("talent_group", false);
            permission.put("homework_group", false);
            permission.put("video_group", false);
            permission.put("enrollment_group", false);
            permission.put("question_group", false);
            break;
        case 3:
            permission.put("teacher_group", true);
            break;
        case 4:
            permission.put("parent_group", true);
            break;

        default:
            break;
        }
        return inflater.inflate(R.layout.fragment_shoolyard, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
       unreadHomework = (TextView)getView().findViewById(R.id.unread_msg_number_homework);
       ivStudentGroup = (ImageView)getView().findViewById(R.id.iv_xsq);
       ivStudentGroup.setEnabled(permission.get("student_group"));
       ivStudentGroup.setOnClickListener(new View.OnClickListener() {
        
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),
                        StudentGroupActivity.class);
                startActivity(intent);
            }
       });
       ivTeacherGroup = (ImageView)getView().findViewById(R.id.iv_jsq);
       ivTeacherGroup.setEnabled(permission.get("teacher_group"));
       ivTeacherGroup.setOnClickListener(new View.OnClickListener() {
        
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),
                        TeacherGroupActivity.class);
                startActivity(intent);
            }
       });
       
       ivParentGroup = (ImageView)getView().findViewById(R.id.iv_jzq);
       ivParentGroup.setEnabled(permission.get("parent_group"));
       ivParentGroup.setOnClickListener(new View.OnClickListener() {
        
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(),
                        ParentGroupActivity.class);
                startActivity(intent);
            }
       });
       ivTalents = (ImageView)getView().findViewById(R.id.iv_fc);
       ivTalents.setEnabled(permission.get("talent_group"));
       ivTalents.setOnClickListener(new View.OnClickListener() {
           
           @Override
           public void onClick(View v) {
               Intent intent = new Intent(getActivity(),
                       TalentsActivity.class);
               intent.putExtra("type", "schoolyard");
               startActivity(intent);
           }
      });
       ivInformation = (ImageView)getView().findViewById(R.id.iv_zx);
       ivInformation.setEnabled(permission.get("inform_group"));
       ivInformation.setOnClickListener(new View.OnClickListener() {
           
           @Override
           public void onClick(View v) {
               Intent intent = new Intent(getActivity(),
                       InformationActivity.class);
               intent.putExtra("type", "schoolyard");
               startActivity(intent);
           }
      });
       ivHomework = (ImageView)getView().findViewById(R.id.iv_xxzy);
       ivHomework.setEnabled(permission.get("homework_group"));
       ivHomework.setOnClickListener(new View.OnClickListener() {
           
           @Override
           public void onClick(View v) {
               Intent intent = new Intent(getActivity(),
                       HomeworkInfoActivity.class);
               startActivity(intent);
           }
      });
       ivVideo = (ImageView)getView().findViewById(R.id.iv_sp);
       ivVideo.setEnabled(permission.get("video_group"));
       ivVideo.setOnClickListener(new View.OnClickListener() {
           
           @Override
           public void onClick(View v) {
               Intent intent = new Intent(getActivity(),
                      VideoActivity.class);
               startActivity(intent);
           }
       });
       ivEnrollment = (ImageView)getView().findViewById(R.id.iv_zs);
       ivEnrollment.setEnabled(permission.get("enrollment_group"));
       ivEnrollment.setOnClickListener(new View.OnClickListener() {
           
           @Override
           public void onClick(View v) {
               Intent intent = new Intent(getActivity(),
                       WebBrowerActivity.class);
               String url = "http://"+getResources().getString(R.string.server)+"/mobile/admissions.php";    
               intent.putExtra("url", url);
               intent.putExtra("title", "校外与招生");
               startActivity(intent);
           }
       });
       ivSQuestion = (ImageView)getView().findViewById(R.id.iv_st);
       ivSQuestion.setEnabled(permission.get("question_group"));
       ivSQuestion.setOnClickListener(new View.OnClickListener() {
           
           @Override
           public void onClick(View v) {
               Intent intent = new Intent(getActivity(),
                       QuestionSearchActivity.class);
                startActivity(intent);
           }
       });
       rlQuestionAnswer = (RelativeLayout)getView().findViewById(R.id.rl_question_answer);
       rlQuestionAnswer.setOnClickListener(new View.OnClickListener() {
           
           @Override
           public void onClick(View v) {
               Intent intent = new Intent(getActivity(),
                       TutorialStudentOpenActivity.class);
               if(DemoApplication.getInstance().getIdentity() == 1){
                   if(DemoApplication.getInstance().getTutorial()==1){
                       intent.putExtra("from", "adjust_subject");
                   }else{
                       intent.putExtra("from", "student");
                   }
               }else{
                   intent.putExtra("from", "other");                   
               }
               startActivity(intent);
           }
       });
       ivJSchool = (ImageView)getView().findViewById(R.id.iv_xx);
       ivJSchool.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               Intent intent = new Intent(getActivity(),
                       TutorialStudentOpenActivity.class);
               intent.putExtra("from", "jschool");
               startActivity(intent);
           }
       });
       ivPSchool = (ImageView)getView().findViewById(R.id.iv_cz);
       ivPSchool.setOnClickListener(new View.OnClickListener() {
           
           @Override
           public void onClick(View v) {
               Intent intent = new Intent(getActivity(),
                       TutorialStudentOpenActivity.class);
               intent.putExtra("from", "pschool");
               startActivity(intent);
           }
       });
       ivHSchool = (ImageView)getView().findViewById(R.id.iv_gz);
       ivHSchool.setOnClickListener(new View.OnClickListener() {
           
           @Override
           public void onClick(View v) {
               Intent intent = new Intent(getActivity(),
                       TutorialStudentOpenActivity.class);
               intent.putExtra("from", "hschool");
               startActivity(intent);
           }
       });
        super.onActivityCreated(savedInstanceState);
    }
    
    public void setMsg(boolean isSee){
        if(isSee)
            unreadHomework.setVisibility(View.VISIBLE);
        else
            unreadHomework.setVisibility(View.GONE);
    }
}
