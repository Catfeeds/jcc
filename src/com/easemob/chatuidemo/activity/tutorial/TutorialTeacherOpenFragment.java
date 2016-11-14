package com.easemob.chatuidemo.activity.tutorial;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentManager;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.MediaStore;
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
import android.widget.RadioButton;
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
import com.easemob.chatuidemo.utils.ImageUtils;
import com.easemob.chatuidemo.widget.OnWheelChangedListener;
import com.easemob.chatuidemo.widget.OnWheelScrollListener;
import com.easemob.chatuidemo.widget.WheelView;
import com.easemob.exceptions.EaseMobException;
import com.google.android.gms.internal.da;

/**
 * 开通教师辅导
 * 
 */
public class TutorialTeacherOpenFragment extends Fragment{
    static final int CREDENTIAL = 110; 
    static final int RESUME = 111; 
    static final int STATE_ERROR = -1; 
    static final int STATE_FAIL = 0; 
    static final int STATE_FINISH = 1; 
    static final int STATE_LINKERR = 2; 
    static final int STATE_TIMEOUT = 3; 
    static final int STATE_NULL = 4; 
    private Spinner spGrade;
    private Spinner spSubject;
    private ImageView ivDocAdd;
    private ImageView ivDocuments;
    private ImageView ivOtherDocAdd;
    private ImageView ivOtherDocuments;
    private Button btOK;
    private List<Map<String, Object>> mReturnData;
    private ImageView ivMenu;
    private PopupMenu popupMenu;  
    private Menu menu; 
    private FragmentManager manager;
    private TutorialResignFragment resignTutorialFragment;
    private String strCredential = "";
    private String strResume = "";
    private Context context;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        return inflater.inflate(R.layout.fragment_tutorial_teacher_open, container, false);
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onActivityCreated(savedInstanceState);
        context = getActivity();

        spGrade = (Spinner)getActivity().findViewById(R.id.grade);
        spSubject = (Spinner)getActivity().findViewById(R.id.subject);
        ivDocuments = (ImageView)getActivity().findViewById(R.id.iv_documents);
        ivOtherDocuments = (ImageView)getActivity().findViewById(R.id.iv_other_documents);
        ivDocAdd = (ImageView)getActivity().findViewById(R.id.documents);
        ivOtherDocAdd = (ImageView)getActivity().findViewById(R.id.other_documents);
        btOK = (Button)getActivity().findViewById(R.id.bt_ok);
        ivMenu = (ImageView)getActivity().findViewById(R.id.menu);
        
        initData();
	}

    private void initData() {
        manager = getActivity().getSupportFragmentManager();
        resignTutorialFragment = new TutorialResignFragment();
        
        ArrayAdapter<CharSequence> gradeAdapter = ArrayAdapter.createFromResource(getActivity(), 
                R.array.grade,android.R.layout.simple_spinner_item);
        gradeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spGrade.setAdapter(gradeAdapter); 

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), 
               R.array.subject,android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spSubject.setAdapter(adapter);
        
        ivDocAdd.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent(Intent.ACTION_PICK, null);   
                intent.setDataAndType(   
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,   
                        "image/*");   
                startActivityForResult(intent, CREDENTIAL);
            }
        });

        ivOtherDocAdd.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View arg0) {

                Intent intent = new Intent(Intent.ACTION_PICK, null);   
                intent.setDataAndType(   
                        MediaStore.Images.Media.EXTERNAL_CONTENT_URI,   
                        "image/*");   
                startActivityForResult(intent, RESUME);
            }
        });
        
        btOK.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View arg0) {
                DialogDemo.show(getActivity(),"正在提交，请稍后...");
                new TutorialOpenThread(handler).start();
            }
        });
        
        ivMenu.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                popupMenu.show();
            }
        });
        popupMenu = new PopupMenu(getActivity(), ivMenu);  
        menu = popupMenu.getMenu();        
        menu.add(Menu.NONE, Menu.FIRST + 0, 0, "辞职辅导员");  
        
  
        // 通过XML文件添加菜单项  
        MenuInflater menuInflater = getActivity().getMenuInflater();  
        menuInflater.inflate(R.menu.popupmenu, menu);  
  
        // 监听事件  
        popupMenu.setOnMenuItemClickListener(new OnMenuItemClickListener() {  
  
            @Override  
            public boolean onMenuItemClick(MenuItem item) {  
                switch (item.getItemId()) { 
                case Menu.FIRST + 0: 
                    {
                        Log.i("tutorial",DemoApplication.getInstance().getTutorial()+"");
                        if(DemoApplication.getInstance().getTutorial()!= 1){
                            Toast.makeText(getActivity(), "您尚未开通辅导员", 0).show();
                            return false;
                        }
                        manager.beginTransaction().replace(R.id.fragment_container, 
                                resignTutorialFragment).addToBackStack(null).commit();
                    }
                    break; 
                default:  
                    break;  
                }  
                return false;  
            }  
        });  
    }

    
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == 0)   
            return;   

        Uri uri = data.getData();
        Log.e("requestCode","requestCode = " + requestCode);
        if (requestCode == CREDENTIAL) {
            try {
                Bitmap bitmap = ImageUtils.compressImage(
                        MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri),
                        500);
                saveMyBitmap(bitmap, "documents");
                strCredential = Environment.getExternalStorageDirectory()+
                        "/cache/documents.jpg";

                ivDocuments.setVisibility(View.VISIBLE);
                ivDocuments.setImageBitmap(BitmapFactory.decodeFile(strCredential));
            } catch (FileNotFoundException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }            
        }

        if (requestCode == RESUME) {   
            try {
                Bitmap bitmap = ImageUtils.compressImage(
                        MediaStore.Images.Media.getBitmap(context.getContentResolver(), uri),
                        500);
                saveMyBitmap(bitmap, "resume");
                strResume = Environment.getExternalStorageDirectory()+
                        "/cache/resume.jpg";
                ivOtherDocuments.setVisibility(View.VISIBLE);
                ivOtherDocuments.setImageBitmap(BitmapFactory.decodeFile(strResume));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }       
        }  

    }

    public void saveMyBitmap(Bitmap mBitmap,String bitName)  {
        File f = new File( Environment.getExternalStorageDirectory()+
                "/cache/" + bitName + ".jpg");
        Log.e("File","File = " + f);
        FileOutputStream fOut = null;
        try {
           fOut = new FileOutputStream(f);
        } catch (FileNotFoundException e) {
                e.printStackTrace();
        }
        mBitmap.compress(Bitmap.CompressFormat.JPEG, 50, fOut);
        try {
            fOut.flush();
        } catch (IOException e) {
            e.printStackTrace();
        }
        try {
            fOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    private class TutorialOpenThread extends Thread { 

        private Handler handler;
 
        public TutorialOpenThread(Handler handler) { 
            this.handler = handler; 
        } 
 
        @Override 
        public void run() { 
            String edit = "tutorial_add";
            Map<String, String> params = new HashMap<String, String>();
            params.put("method", edit);
            params.put("grade", spGrade.getSelectedItem().toString());
            params.put("subject", spSubject.getSelectedItem().toString());
            params.put("credential", strCredential);
            params.put("resume", strResume);
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
