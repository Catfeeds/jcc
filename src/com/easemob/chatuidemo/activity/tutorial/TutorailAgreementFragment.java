package com.easemob.chatuidemo.activity.tutorial;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.easemob.applib.controller.HXSDKHelper;
import com.easemob.chatuidemo.Constant;
import com.easemob.chatuidemo.DemoHXSDKHelper;
import com.easemob.chatuidemo.DemoHXSDKModel;
import com.easemob.chatuidemo.adapter.ContactAdapter;
import com.easemob.chatuidemo.adapter.ImageAdapter;
import com.easemob.chatuidemo.domain.Info;
import com.easemob.chatuidemo.domain.User;
import com.easemob.chatuidemo.utils.ConnServer;
import com.easemob.chatuidemo.utils.DialogDemo;
import com.wenpy.jcc.R;

import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class TutorailAgreementFragment extends Fragment {
    private FragmentManager manager;
    private Button btAgree;
    private TutorailSubjectFragment tutorailSubjectFragment;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        return inflater.inflate(R.layout.fragment_tutorail_agreement_student, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        btAgree = (Button)getActivity().findViewById(R.id.agree);
        tutorailSubjectFragment = new TutorailSubjectFragment();
        manager = getActivity().getSupportFragmentManager();
        btAgree.setOnClickListener(new OnClickListener() {
 
            @Override
            public void onClick(View v) {
                manager.beginTransaction().replace(R.id.fragment_container, tutorailSubjectFragment).addToBackStack(null).commit();
            }
        });
    }    
}
