package com.easemob.chatuidemo.activity.job;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.ContactsContract.CommonDataKinds.Email;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;
import android.widget.AdapterView.OnItemClickListener;
import com.easemob.EMValueCallBack;
import com.easemob.applib.controller.HXSDKHelper;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMContactManager;
import com.easemob.chatuidemo.DemoApplication;
import com.easemob.chatuidemo.DemoHXSDKHelper;
import com.wenpy.jcc.R;
import com.easemob.chatuidemo.activity.BaseActivity;
import com.easemob.chatuidemo.activity.ChatActivity;
import com.easemob.chatuidemo.activity.PicSelectActivity;
import com.easemob.chatuidemo.activity.main.MainActivity;
import com.easemob.chatuidemo.adapter.GridImageAdapter;
import com.easemob.chatuidemo.adapter.GridViewAdapter;
import com.easemob.chatuidemo.adapter.ViewPagerAdapter;
import com.easemob.chatuidemo.db.InviteMessgeDao;
import com.easemob.chatuidemo.db.UserDao;
import com.easemob.chatuidemo.domain.Info;
import com.easemob.chatuidemo.domain.User;
import com.easemob.chatuidemo.utils.ConnServer;
import com.easemob.chatuidemo.utils.DialogDemo;
import com.easemob.chatuidemo.utils.MD5;
import com.easemob.chatuidemo.utils.UserUtils;
import com.easemob.chatuidemo.video.util.Utils;
import com.easemob.chatuidemo.widget.ActionSheet;
import com.easemob.chatuidemo.widget.ActionSheet.OnActionSheetSelected;
import com.easemob.chatuidemo.widget.AutoGridView;
import com.google.android.gms.internal.ad;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.squareup.picasso.Picasso;

public class ResumeActivity extends BaseActivity implements OnActionSheetSelected, OnCancelListener {
    private final  static int CAMERA_REQUEST_CODE = 1;
    private final  static int GALLERY_REQUEST_CODE = 2;	
	private static final int REQUESTCODE_PICK = 1;
	private static final int REQUESTCODE_CUTTING = 2;
    private TextView tvEdit;	
	private EditText etName;
    private TextView tvGender;
	private RadioGroup rgGender;
	private RadioButton rbMale;
	private RadioButton rbFemale;
    private EditText etBirth;
    private EditText etEducation;
    private EditText etIntension;
    private EditText etSalary;
    private EditText etSpecialty;
    private EditText etAwards;
    private EditText etSchool;
    private EditText etMajor;
    private EditText etPhone;
    private EditText etMail;
	private ProgressDialog dialog;
	private Button btSave;
    private Button btResName;
    private Button btApply;
    private LinearLayout llMain;
    private LinearLayout llBottom;
    private LinearLayout llOper;
    private LinearLayout llSubmit;
    private RelativeLayout rlPicContainer;
    private List<Map<String, Object>> mResumeData;
    private List<Map<String, Object>> mAblumData;
    private List<Map<String, Object>> mReturnData;
    private ArrayList<String> paths = new ArrayList<String>();
    private GridViewAdapter adapter;
    private File cache;
    private ImageView ivGoto;
    private boolean enableUpdate;
    public String mId = "";
    private String mGender = "男";
    private String from = "";
    private String mEdit = "resume_update";
    private String job_id;
    private AutoGridView gvAlbum;
    protected StringBuffer mPicUrl = new StringBuffer();
    protected int mPicCount = 0;
    private AlertDialog alertDialog;
    private LinearLayout llAddResume;
    private LinearLayout llSubmitResume;
    private LinearLayout llProgressBar;
    private Button btAddResume;
    private TextView tvEditName;
    private EditText etResumeName;
    private ProgressBar bar;
	
	@Override
	protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_my_resume);
        mResumeData = new ArrayList<Map<String,Object>>();
        mAblumData = new ArrayList<Map<String,Object>>();
        mReturnData = new ArrayList<Map<String,Object>>();
        
        Intent intent = getIntent();
        from = intent.getStringExtra("from");
        
        initView();
        initListener();
        initData();
        
        Log.e("Resume","mEdit="+mEdit);
        if(mEdit.equals("resume_update")){
            DialogDemo.show(this,2);
            new GetDataThread(handler).start();
        }
	}

    private void initView() {
	    tvEdit = (TextView) findViewById(R.id.edit);
	    llMain = (LinearLayout)findViewById(R.id.main);
        llBottom= (LinearLayout)findViewById(R.id.ll_bottom);
        llOper = (LinearLayout)findViewById(R.id.ll_oper);
        llSubmit = (LinearLayout)findViewById(R.id.ll_submit);
		etName = (EditText) findViewById(R.id.name);
		tvGender = (TextView) findViewById(R.id.tv_gender);
		rgGender = (RadioGroup) findViewById(R.id.gender);
		rbMale = (RadioButton)findViewById(R.id.male);
	    rbFemale = (RadioButton)findViewById(R.id.female);
		etBirth = (EditText) findViewById(R.id.brith);
		etEducation = (EditText) findViewById(R.id.education);
		etIntension = (EditText) findViewById(R.id.intension);
        etSalary = (EditText) findViewById(R.id.salary);
        etSpecialty = (EditText) findViewById(R.id.specialty);
        etAwards = (EditText) findViewById(R.id.awards);
        etSchool = (EditText) findViewById(R.id.school);
        etMajor = (EditText) findViewById(R.id.major);
        etPhone = (EditText) findViewById(R.id.etphone);
        etMail = (EditText) findViewById(R.id.mail);
		btSave = (Button) findViewById(R.id.bt_save);
        btResName = (Button) findViewById(R.id.bt_resume_name);
        btApply = (Button) findViewById(R.id.bt_submit);
		gvAlbum = (AutoGridView) findViewById(R.id.agv_album);
		ivGoto = (ImageView)findViewById(R.id.iv_goto);
	}
    
    private void initListener() {
        rgGender.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            
            @Override
            public void onCheckedChanged(RadioGroup arg0, int arg1) {
                int radioButtonId = arg0.getCheckedRadioButtonId();
                if(radioButtonId == R.id.female)
                    mGender = "女";
                else
                    mGender = "男";
            }
        });  
        etBirth.setInputType(InputType.TYPE_NULL);  
        etBirth.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View arg0) {
                showDialog(1);
            }
        });
        
        tvEdit.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View arg0) {
                if(tvEdit.getText().toString().equals("编辑")){
                    for(int i=0;i<llMain.getChildCount();i++){
                        LinearLayout fv = (LinearLayout)llMain.getChildAt(i);
                        for(int j=0;j<fv.getChildCount();j++){
                            View v = fv.getChildAt(j);
                            v.setEnabled(true);
                        }
                    }
                    llOper.setVisibility(View.VISIBLE);
                    tvGender.setEnabled(true);
                    rbFemale.setEnabled(true);
                    rbMale.setEnabled(true);
                    tvEdit.setText("取消");
                }else{
                    tvEdit.setText("编辑");
                    for(int i=0;i<llMain.getChildCount();i++){
                        LinearLayout fv = (LinearLayout)llMain.getChildAt(i);
                        for(int j=0;j<fv.getChildCount();j++){
                            View v = fv.getChildAt(j);
                            v.setEnabled(false);
                        }
                    }
                    llOper.setVisibility(View.GONE);
                    tvGender.setEnabled(false);
                    rbFemale.setEnabled(false);
                    rbMale.setEnabled(false);
                }
            }
        });
        
        btSave.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View arg0) {
                String name = etName.getText().toString();
                String birther = etBirth.getText().toString();
                String education = etEducation.getText().toString();
                String phone = etPhone.getText().toString();
                if (TextUtils.isEmpty(name)) {
                    Toast.makeText(getApplicationContext(), "名字不能为空", Toast.LENGTH_SHORT).show();
                    etName.requestFocus();
                    return;
                } else if (TextUtils.isEmpty(birther)) {
                    Toast.makeText(getApplicationContext(), "出生日期不能为空", Toast.LENGTH_SHORT).show();
                    etBirth.requestFocus();
                    return;
                } else if (TextUtils.isEmpty(education)) {
                    Toast.makeText(getApplicationContext(), "学历不能为空", Toast.LENGTH_SHORT).show();
                    etEducation.requestFocus();
                    return;
                } else if (TextUtils.isEmpty(phone)) {
                    Toast.makeText(getApplicationContext(), "手机不能为空", Toast.LENGTH_SHORT).show();
                    etPhone.requestFocus();
                    return;
                }
                DialogDemo.show(ResumeActivity.this,"正在保存，请稍后...");
                String url = "http://"+getResources().getString(R.string.server)+"/api.php";
                mPicCount = paths.size()-1;
                try {
                    for(int i=1;i<paths.size();i++){
                        if(paths.get(i).indexOf("http") == -1)
                            uploadFile(paths.get(i), url);
                        else
                            mPicCount--;
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    //progressBar.setVisibility(View.GONE);
                }
                System.out.println("size="+mPicCount);
                if(mPicCount == 0)
                    new SaveResumeThread(handler).start();
//                new SaveResumeThread(handler).start();
            }
        });
        
        btResName.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View arg0) {
                alertDialog = new AlertDialog.Builder(ResumeActivity.this).create();  
                View alertDialogView = View.inflate(ResumeActivity.this, R.layout.activity_resume_diag, null);
                alertDialog.setView(alertDialogView);
                alertDialog.show();
                Window window = alertDialog.getWindow();  
                window.setContentView(R.layout.activity_resume_diag);  
                llAddResume = (LinearLayout) window.findViewById(R.id.ll_add_resume);
                llProgressBar = (LinearLayout) window.findViewById(R.id.ll_progressBar);
                llSubmitResume = (LinearLayout) window.findViewById(R.id.ll_submit_resume);
                etResumeName = (EditText) window.findViewById(R.id.default_resume);  
                tvEditName = (TextView) window.findViewById(R.id.edit_name);  
                btApply = (Button) window.findViewById(R.id.bt_apply); 
                btAddResume = (Button) window.findViewById(R.id.add_resume); 
                bar = (ProgressBar) window.findViewById(R.id.progressBar); 
                llProgressBar.setVisibility(View.GONE);
                etResumeName.setText(mResumeData.get(0).get("resume_name").toString());  
                llAddResume.setVisibility(View.GONE);
                    
                btApply.setOnClickListener(new OnClickListener() {
                    
                    @Override
                    public void onClick(View arg0) {
                        DialogDemo.show(ResumeActivity.this,2);
                        new SendResumeThread(handler).start();
                    }
                });
            }
        });
        btApply.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View arg0) {
                DialogDemo.show(ResumeActivity.this,"正在保存，请稍后...");
                new SendResumeThread(handler).start();
            }
        });
        gvAlbum.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position,
                    long id) {
                // TODO Auto-generated method stub
                if (TextUtils.isEmpty(paths.get(position))) {
                    if(paths.size()>6){
                        Toast.makeText(getApplicationContext(), "照片最多只能上传5张", 0).show();
                        return;
                    }
                    ActionSheet.showSheet(ResumeActivity.this,
                            ResumeActivity.this, ResumeActivity.this, "1");
                } else {
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    String url = "";
                    if(paths.get(position).indexOf("http") == -1)
                        url = paths.get(position);
                    else{
                        String path = paths.get(position);
                        String name = MD5.getMD5(path) + path.substring(path.lastIndexOf(".")); 
                        url = cache.getAbsolutePath()+"/"+name;
                    }
                    Log.e("img","imgurl="+url);
                    
                    intent.setDataAndType(Uri.parse("file://"+url), "image/*");
                    startActivity(intent);
                }
            }
        });
    }
    
    private void initData() {
        rbFemale.setEnabled(false);
        rbMale.setEnabled(false);
        tvGender.setEnabled(false);
        if(from.equals("my")){
            for(int i=0;i<llMain.getChildCount();i++){
                LinearLayout fv = (LinearLayout)llMain.getChildAt(i);
                for(int j=0;j<fv.getChildCount();j++){
                    View v = fv.getChildAt(j);
                    v.setEnabled(false);
                }
            }
            llSubmit.setVisibility(View.GONE);
        }else if(from.equals("apply")){
            for(int i=0;i<llMain.getChildCount();i++){
                LinearLayout fv = (LinearLayout)llMain.getChildAt(i);
                for(int j=0;j<fv.getChildCount();j++){
                    View v = fv.getChildAt(j);
                    v.setEnabled(false);
                }
            }
            llSubmit.setVisibility(View.VISIBLE);
        }else if(from.equals("add")){
            Intent intent = getIntent();
            job_id = intent.getStringExtra("job_id");
            
            mEdit = "resume_add";
            llOper.setVisibility(View.VISIBLE);
            llSubmit.setVisibility(View.GONE);
            rbFemale.setEnabled(true);
            rbMale.setEnabled(true);
            tvGender.setEnabled(true);
        }

        paths.add("");
        adapter = new GridViewAdapter(ResumeActivity.this, paths, 2);
        gvAlbum.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        
        cache = new File(Environment.getExternalStorageDirectory(), "cache");
        
        if(!cache.exists()){
            cache.mkdirs();
        }      
    }

    protected Dialog onCreateDialog(int id) {
        //用来获取日期和时间的  
        Calendar calendar = Calendar.getInstance();   
          
        Dialog dialog = null;  
        switch(id) {  
            case 1://DATE_DIALOG  
                DatePickerDialog.OnDateSetListener dateListener =   
                    new DatePickerDialog.OnDateSetListener() {  
                        @Override  
                        public void onDateSet(DatePicker datePicker,   
                                int year, int month, int dayOfMonth) {
                            
                            etBirth.setText(year + "-" +   
                                    (month+1) + "-" + dayOfMonth);
                        }  
                    };  
                dialog = new DatePickerDialog(this,  
                        dateListener,  
                        calendar.get(Calendar.YEAR),  
                        calendar.get(Calendar.MONTH),  
                        calendar.get(Calendar.DAY_OF_MONTH));  
                break;  
            case 2://TIME_DIALOG  
                TimePickerDialog.OnTimeSetListener timeListener =   
                    new TimePickerDialog.OnTimeSetListener() {  
                          
                        @Override  
                        public void onTimeSet(TimePicker timerPicker,  
                                int hourOfDay, int minute) {  
                            etBirth.setText(hourOfDay + "时" +   
                                     minute + "分");  
                        }  
                    };  
                    dialog = new TimePickerDialog(this, timeListener,  
                            calendar.get(Calendar.HOUR_OF_DAY),  
                            calendar.get(Calendar.MINUTE),  
                            false);   //是否为二十四制  
                break;  
            default:  
                break;  
        }  
        return dialog;  
    }

    public void uploadFile(String path, String url) throws Exception {  
        File file = new File(path);  
        if (file.exists() && file.length() > 0) {  
            AsyncHttpClient client = new AsyncHttpClient();  
            RequestParams params = new RequestParams();  
            params.put("method", "upload_pic"); 
            params.put("uploadedfile", file);
            // 上传文件  
            client.post(url, params, new AsyncHttpResponseHandler() {  
                @Override
                public void onFinish() {
                    //progressBar.setVisibility(View.GONE);
                    mPicCount --;
                    if(mPicCount == 0){
                        if(mPicUrl.length() > 1)
                            mPicUrl.deleteCharAt(mPicUrl.length()-1);
                        new SaveResumeThread(handler).start();
                    }
                    super.onFinish();
                }
    
                @Override  
                public void onSuccess(int statusCode, Header[] headers,  
                        byte[] responseBody) {
                    ConnServer server = new ConnServer();
                    List<Map<String,Object>> Data = new ArrayList<Map<String,Object>>();

                    try {
                        Data = server.parseJSON(responseBody);
                        if(Boolean.valueOf(Data.get(0).get("status").toString())){
                            mPicUrl.append(Data.get(0).get("msg").toString()).append(",");
                        }
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }  
          
                @Override  
                public void onFailure(int statusCode, Header[] headers,  
                        byte[] responseBody, Throwable error) {  
                    // 上传失败后要做到工作  
                    //Toast.makeText(TalentsAddActivity.this, "上传失败", Toast.LENGTH_LONG).show();  
                }  
          
                @Override  
                public void onProgress(int bytesWritten, int totalSize) {  
                    // TODO Auto-generated method stub  
                    super.onProgress(bytesWritten, totalSize);  
                    int count = (int) ((bytesWritten * 1.0 / totalSize) * 100);  
                    // 上传进度显示  
                    //progress.setProgress(count);  
                    //Log.e("上传 Progress>>>>>", bytesWritten + " / " + totalSize);  
                }  
          
                @Override  
                public void onRetry(int retryNo) {  
                    // TODO Auto-generated method stub  
                    super.onRetry(retryNo);  
                    // 返回重试次数  
                }  
          
            }); 
        }
    }
    
    private class SendResumeThread extends Thread { 
         
        private Handler handler; 
 
        public SendResumeThread(Handler handler) { 
            this.handler = handler; 
        }
 
        @Override 
        public void run() {
            String edit = "job_resume_add";
            Map<String, String> params = new HashMap<String, String>();
            params.put("method", edit);
            params.put("job_id", job_id);
            params.put("resume_id", mId );
            String path = "http://"+getResources().getString(R.string.server)+"/api.php"; 
            ConnServer server = new ConnServer();
            mReturnData = server.GetData(path,params,edit,handler);
        }
    }
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case CAMERA_REQUEST_CODE:
			if (data == null || data.getData() == null) {
				return;
			}
			break;
		case GALLERY_REQUEST_CODE:
		    List<String> selectedImage = new ArrayList<String>();
		    selectedImage = (ArrayList<String>) data.getSerializableExtra("value");
		    paths.addAll(selectedImage);
            adapter.setList(paths);
            adapter.notifyDataSetChanged();
			break;
		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}
	
	public void show(){
	    mId = mResumeData.get(0).get("id").toString();
	    String albums = mResumeData.get(0).get("photo").toString();
	    String[] temp = albums.split(",");
	    for(int i=0;i<temp.length;i++)
	        paths.add(temp[i]);
	    adapter.setList(paths);
	    adapter.notifyDataSetChanged();
	    
	    etName.setText(mResumeData.get(0).get("name").toString());
	    mGender = mResumeData.get(0).get("gender").toString();
	    if(mGender.equals("女"))
	        rbFemale.setChecked(true);
	    else
	        rbMale.setChecked(true);
	    etBirth.setText(mResumeData.get(0).get("birther").toString());
        etEducation.setText(mResumeData.get(0).get("education").toString());
        etIntension.setText(mResumeData.get(0).get("intension").toString());
        etSalary.setText(mResumeData.get(0).get("salary").toString());
        etSpecialty.setText(mResumeData.get(0).get("specialty").toString());
        etAwards.setText(mResumeData.get(0).get("awards").toString());
        etSchool.setText(mResumeData.get(0).get("school").toString());
        etMajor.setText(mResumeData.get(0).get("subject").toString());
        etPhone.setText(mResumeData.get(0).get("phone").toString());
        etMail.setText(mResumeData.get(0).get("mail").toString());
	}

	public void showPic(){
	    if(mAblumData.size()>0)
	        rlPicContainer.setVisibility(View.VISIBLE);
	    
        List<Info> Infos = new ArrayList<Info>();
        for(int i=0;i<mAblumData.size();i++){
            Info info = new Info();
            info.setImage(mAblumData.get(i).get("skin").toString());
            Infos.add(info);
        }
        adapter.notifyDataSetChanged();
        gvAlbum.setAdapter(adapter);
	}

    private class GetUserPicThread extends Thread { 
         
        private Handler handler; 
 
        public GetUserPicThread(Handler handler) { 
            this.handler = handler; 
        } 
 
        @Override 
        public void run() {
            String edit = "album_get";
            Map<String, String> params = new HashMap<String, String>();
            params.put("method", edit);
            params.put("user", EMChatManager.getInstance().getCurrentUser());
            ConnServer server = new ConnServer();
            String path = "http://"+getResources().getString(R.string.server)+"/api.php"; 
            mAblumData = server.GetData(path,params,edit,handler);
        }
    }

    private class SaveResumeThread extends Thread { 
         
        private Handler handler; 
 
        public SaveResumeThread(Handler handler) { 
            this.handler = handler; 
        } 
 
        @Override 
        public void run() {
            StringBuffer albumurls = new StringBuffer();
            for(int i=0;i<paths.size();i++){
                if(!paths.get(i).equals("") && paths.get(i).indexOf("http") != -1)
                    albumurls.append(paths.get(i)).append(",");
            }
            albumurls.append(mPicUrl);
            
            Map<String, String> params = new HashMap<String, String>();
            params.put("method", mEdit);
            params.put("user", EMChatManager.getInstance().getCurrentUser());
            params.put("id", mId);
            params.put("name", etName.getText().toString());
            params.put("gender", mGender);
            params.put("birther", etBirth.getText().toString());
            params.put("education", etEducation.getText().toString());
            params.put("intension", etIntension.getText().toString());
            params.put("salary", etSalary.getText().toString());
            params.put("specialty", etSpecialty.getText().toString());
            params.put("awards", etAwards.getText().toString());
            params.put("school", etSchool.getText().toString());
            params.put("subject", etMajor.getText().toString());
            params.put("phone", etPhone.getText().toString());
            params.put("mail", etMail.getText().toString());
            params.put("photo", albumurls.toString());
            ConnServer server = new ConnServer();
            String path = "http://"+getResources().getString(R.string.server)+"/api.php"; 
            mResumeData = server.GetData(path,params,mEdit,handler);
        }
    }
    
    private class GetDataThread extends Thread { 
         
        private Handler handler; 
 
        public GetDataThread(Handler handler) { 
            this.handler = handler; 
        }
 
        @Override 
        public void run() {
            String edit = "resume_get";
            Map<String, String> params = new HashMap<String, String>();
            params.put("method", edit);
            params.put("user", EMChatManager.getInstance().getCurrentUser());
            ConnServer server = new ConnServer();
            String path = "http://"+getResources().getString(R.string.server)+"/api.php"; 
            mResumeData = server.GetData(path,params,edit,handler);
        }
    }

    private Handler handler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            DialogDemo.dismiss();
            switch(msg.what){
                case STATE_FINISH:
                    if(msg.obj.toString().equals("resume_get")){
                        if(mResumeData.size()>0)
                            show();
                        else
                            mEdit = "resume_add";
                    }else if(msg.obj.toString().equals("album_get")){
                        if(mAblumData.size()>0)
                            showPic();
                    }else if(msg.obj.toString().equals("resume_add")){
                        mId = mResumeData.get(0).get("data").toString();
                        if(!mId.equals(""))
                            mEdit = "resume_update";
                        llSubmit.setVisibility(View.VISIBLE);
                        Toast.makeText(ResumeActivity.this, "保存成功", 0).show();
                    }else if(msg.obj.toString().equals("resume_update")){
                        Toast.makeText(ResumeActivity.this, "保存成功", 0).show();
                    }else if(msg.obj.equals("job_resume_add")){
                            Toast.makeText(getApplicationContext(), "提交成功！", 0).show();
                            finish();
                        }
                    break;
                case STATE_ERROR:
                    Toast.makeText(ResumeActivity.this, "network error", 0).show();
                    break;
                case STATE_FAIL:
                    Toast.makeText(ResumeActivity.this, msg.obj+"", 0).show();
                    break;
            }
        };
    };

    @Override
    public void onCancel(DialogInterface dialog) {
       
    }

    @Override
    public void onClick(int whichButton) {
        if (whichButton == 1) {
            String status = Environment.getExternalStorageState();
            if (status.equals(Environment.MEDIA_MOUNTED)) {
                Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                startActivityForResult(intent, CAMERA_REQUEST_CODE);
            } else {
                Toast.makeText(ResumeActivity.this, "没有储存卡",
                        Toast.LENGTH_LONG).show();
            }            
        } else if (whichButton == 2) {  
          //选择图片
          Intent intent=new Intent(ResumeActivity.this,PicSelectActivity.class);
            if (TextUtils.isEmpty(paths.get(paths.size() - 1))) {
            intent.putExtra("maxvalue", 9- (paths.size() - 1));
           } else {
            intent.putExtra("maxvalue", 9 - paths.size());
           }
          startActivityForResult(intent, GALLERY_REQUEST_CODE);
        } 
    }    
}
