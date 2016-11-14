package com.easemob.chatuidemo.activity.tutorial;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentManager;
import android.app.AlertDialog;
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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Spinner;
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
import com.easemob.chatuidemo.adapter.ArrayWheelAdapter;
import com.easemob.chatuidemo.adapter.GridImageAdapter;
import com.easemob.chatuidemo.adapter.ImageAdapter;
import com.easemob.chatuidemo.adapter.NumericWheelAdapter;
import com.easemob.chatuidemo.adapter.TalentAdapter;
import com.easemob.chatuidemo.domain.Info;
import com.easemob.chatuidemo.utils.ConnServer;
import com.easemob.chatuidemo.utils.DialogDemo;
import com.easemob.chatuidemo.widget.OnWheelScrollListener;
import com.easemob.chatuidemo.widget.WheelView;
import com.easemob.exceptions.EaseMobException;
import com.google.android.gms.internal.da;

/**
 * 师生风采查询
 * 
 */
public class TutorialResignFragment extends Fragment {
    static final int STATE_ERROR = -1; 
    static final int STATE_FAIL = 0; 
    static final int STATE_FINISH = 1; 
    static final int STATE_LINKERR = 2; 
    static final int STATE_TIMEOUT = 3; 
    static final int STATE_NULL = 4; 
    private TextView tvContent;
    private Button btOK;
    private Button btCancel;
    private List<Map<String, Object>> mReturnData;
    private Context context;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        return inflater.inflate(R.layout.fragment_resign_tutorial, container, false);
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
        context = getActivity();
        mReturnData = new ArrayList<Map<String,Object>>();
        
        tvContent = (TextView)getActivity().findViewById(R.id.content);
        btOK = (Button)getActivity().findViewById(R.id.bt_ok);
        btCancel = (Button)getActivity().findViewById(R.id.bt_cancel);

        String text = "提示：辞职辅导老师须提前5个工作日提出，以便本站安排新教师接任。" +
        		"在此期间请继续履行辅导职责。如提出后即刻辞任，不履行辅导职责，造成学生问题无人解答，本站可扣除未发辅导费收入。";
        tvContent.setText(text);
        
	    btOK.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                DialogDemo.show(getActivity(),"正在提交，请稍后...");
                new ResignThread(handler).start();
            }
        });
        
        btCancel.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
               getActivity().getSupportFragmentManager().popBackStack();
            }
        });
	}
		
    private class ResignThread extends Thread { 

        private Handler handler; 
 
        public ResignThread(Handler handler) { 
            this.handler = handler; 
        } 
 
        @Override 
        public void run() { 
            String edit = "tutorial_resign";
            Map<String, String> params = new HashMap<String, String>();
            params.put("method", edit);
            params.put("user", EMChatManager.getInstance().getCurrentUser());
            
            ConnServer server = new ConnServer();
            String path = "http://"+getResources().getString(R.string.server)+"/api.php";
            mReturnData = server.GetData(path,params,edit,handler);
        }
    }
    
    private final Handler handler = new Handler(Looper.getMainLooper()) {
         
        public void handleMessage(Message msg) {
            DialogDemo.dismiss();
            switch(msg.what){ 
                case STATE_FINISH: 
                {
                    if(msg.obj.toString().equals("tutorial_resign")){
                        DemoApplication.getInstance().setTutorial(0);
                        getActivity().getSupportFragmentManager().popBackStack();
                        Toast.makeText(context, "提交成功", 0).show();
                    }
                }
                    break;
                case STATE_FAIL:
                {
                    String info = "Error!";
                    if(msg.obj != null){
                        info = info+"\n"+msg.obj;
                    }
                    Toast.makeText(context, info, 1).show();
                    break;
                }
                case STATE_NULL:
                    //Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.datanull),Toast.LENGTH_SHORT).show(); 
                    break;
                case STATE_LINKERR:
                {
                    String type = msg.getData().getString("type");
                    Toast.makeText(context, "网络连接失败",Toast.LENGTH_SHORT) .show();  
                    break;
                }
                case STATE_ERROR: 
                    Toast.makeText(context, "网络连接错误",Toast.LENGTH_SHORT) .show();  
                    break;
               default:  
            } 
        } 
    }; 
}
