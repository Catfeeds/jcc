package com.easemob.chatuidemo.activity.talent;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;

import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentManager;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
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
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.SimpleAdapter.ViewBinder;

import com.baidu.mapapi.cloud.BoundSearchInfo;
import com.easemob.EMError;
import com.easemob.applib.controller.HXSDKHelper;
import com.easemob.chat.EMChatManager;
import com.easemob.chatuidemo.DemoApplication;
import com.easemob.chatuidemo.DemoHXSDKModel;
import com.wenpy.jcc.R;
import com.easemob.chatuidemo.activity.BaseActivity;
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
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * 师生风采添加
 * 
 */
public class TalentsAddActivity extends BaseActivity {
    public static final int REQUEST_CODE_LOCAL = 1;
    private GridView gvImage;
    private Button btSumbit;
    private EditText etTitle;
    private TextView tvTitle;
    private LinearLayout llContent;
    private EditText etContent;
    private List<Map<String, Object>> mListData;
    private List<Map<String, Object>> mPicData;
    private StringBuffer mPicUrl;
    private int mPicCount = 0;
    private SimpleAdapter adapter;
    private String pathImage;                       //选择图片路径
    private View progressBar;
    private String mType;
    private String mEdit;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_talents_add);
		
		Intent intent = getIntent();
        mType = intent.getStringExtra("type");
		
        mListData = new ArrayList<Map<String,Object>>();
        
		gvImage = (GridView)this.findViewById(R.id.gv_image);
		btSumbit = (Button)findViewById(R.id.bt_submit);
        etTitle = (EditText)findViewById(R.id.title);
        tvTitle = (TextView)findViewById(R.id.message_title);
        llContent = (LinearLayout)findViewById(R.id.ll_content);
        etContent = (EditText)findViewById(R.id.content);
        
        if(mType.equals("seikatsu")){
            tvTitle.setText("生活秀");
            llContent.setVisibility(View.VISIBLE);
            mEdit = "lift_show_add";
        }else{
            tvTitle.setText("师生风采");      
            mEdit = "elegant_demeanour_add";      
        }
        
        btSumbit.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                //progressBar.setVisibility(View.VISIBLE);
                if(mListData.size()<1){
                    Toast.makeText(TalentsAddActivity.this, "图片不能为空", 0).show();
                    return;
                }
                if(etTitle.getText().toString().equals("")){
                    etTitle.requestFocus();
                    Toast.makeText(TalentsAddActivity.this, "标题不能为空", 0).show();
                    return;
                }
                DialogDemo.show(TalentsAddActivity.this,"正在上传，请稍后...");
                String url = "http://"+getResources().getString(R.string.server)+"/api.php";
               mPicUrl = new StringBuffer();
               mPicUrl.append("[");
               mPicCount = mListData.size()-1;
                try {
                    for(int i=1;i<mListData.size();i++){
                        uploadFile(mListData.get(i).get("url").toString(), url);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    //progressBar.setVisibility(View.GONE);
                }
            }
        });
        
        gvImage.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position,
                    long id) {
                if( mListData.size() == 10) { //第一张为默认图片
                    Toast.makeText(TalentsAddActivity.this, "图片数9张已满", Toast.LENGTH_SHORT).show();
                }
                else if(position == 0) { //点击图片位置为+ 0对应0张图片
                    selectPicFromLocal();
                }
                else {
                    Intent intent = new Intent(Intent.ACTION_VIEW);    
                    intent.setDataAndType(Uri.parse("file://"+mListData.get(position).get("url").toString()), "image/*");
                    startActivity(intent);
                }
            }
        });
        gvImage.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View v,
                    final int position, long id) {
                if(position > 0){
                    AlertDialog.Builder builder = new Builder(TalentsAddActivity.this);
                    builder.setMessage("是否删除?")
                    .setTitle("提示")
                    .setPositiveButton("删除", new DialogInterface.OnClickListener() {
                        
                        @Override
                        public void onClick(DialogInterface arg0, int arg1) {
                            mListData.remove(position);
                            adapter.notifyDataSetChanged();                            
                        }
                    })
                    .setNegativeButton("取消", null)
                    .create()
                    .show();
                    
                }
                return false;
            }
        });

        progressBar = (View) findViewById(R.id.progress_bar);
        
        init();
	}
	
	void init(){
        mListData = new ArrayList<Map<String,Object>>();

        Map<String, Object> map = new HashMap<String, Object>();
        map.put("image", R.drawable.user_add);
        map.put("text", "");
        mListData.add(map);

        adapter = new SimpleAdapter(getApplicationContext(),mListData,R.layout.gridview_image_no_title,
                new String[]{"image"},new int[]{R.id.iv_image});
        adapter.setViewBinder(new ViewBinder() {  
          @Override  
          public boolean setViewValue(View view, Object data,  
                  String textRepresentation) {  
              // TODO Auto-generated method stub  
              if(view instanceof ImageView && data instanceof Bitmap){  
                  ImageView i = (ImageView)view;  
                  i.setImageBitmap((Bitmap) data);  
                  return true;  
              }  
              return false;  
          }
        }); 
        gvImage.setAdapter(adapter);
        
        etTitle.setText("");
	}
	
	/** 
	* @param path 
	*            要上传的文件路径 
	* @param url 
	*            服务端接收URL 
	* @throws Exception 
	*/  
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
    	            mPicCount--;
    	            if(mPicCount == 0){
    	                if(mPicUrl.length() > 1)
    	                    mPicUrl.deleteCharAt(mPicUrl.length()-1);
    	                mPicUrl.append("]");
    	                new GetDataThread(handler).start();
    	            }
                    super.onFinish();
                }
    
                @Override  
    	        public void onSuccess(int statusCode, Header[] headers,  
    	                byte[] responseBody) {  
    	            // 上传成功后要做的工作  
    	            //Toast.makeText(TalentsAddActivity.this, "上传成功", Toast.LENGTH_LONG).show();  
    	            //progress.setProgress(0);
                    ConnServer server = new ConnServer();
                    List<Map<String,Object>> Data = new ArrayList<Map<String,Object>>();

                    try {
                        Data = server.parseJSON(responseBody);
                        //System.out.println("responseBody:"+new String(responseBody)+";data:"+Data);
                        if(Boolean.valueOf(Data.get(0).get("status").toString())){
                            mPicUrl.append("{\"photo\":\"").append(Data.get(0).get("msg").toString()).append("\"},");
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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_LOCAL & data != null) { // 发送本地图片
            Uri uri = data.getData();  
            if (!TextUtils.isEmpty(uri.getAuthority())) {  
                //查询选择图片  
                Cursor cursor = getContentResolver().query(  
                        uri,  
                        new String[] { MediaStore.Images.Media.DATA },  
                        null,   
                        null,   
                        null);  
                //返回 没找到选择图片  
                if (null == cursor) {  
                    return;  
                }  
                //光标移动至开头 获取图片路径  
                cursor.moveToFirst();  
                pathImage = cursor.getString(cursor  
                        .getColumnIndex(MediaStore.Images.Media.DATA));  
            }
        } 
    }
	/**
     * 从图库获取图片
     */
    public void selectPicFromLocal() {
        Intent intent;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");

        } else {
            intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        }
        startActivityForResult(intent, REQUEST_CODE_LOCAL);
    }
	
    private class GetDataThread extends Thread { 
         
        private Handler handler; 
 
        public GetDataThread(Handler handler) { 
            this.handler = handler; 
        } 
 
        @Override 
        public void run() { 
            Map<String, String> params = new HashMap<String, String>();
            params.put("method", mEdit);
            params.put("user", EMChatManager.getInstance().getCurrentUser());
            params.put("title", etTitle.getText().toString());
            params.put("photos", mPicUrl.toString());
            params.put("content", etContent.toString());
            String url = "http://"+getResources().getString(R.string.server)+"/api.php";
            ConnServer server = new ConnServer();
            mListData = server.GetData(url,params ,mEdit, handler);
        }
    }
    
    private final Handler handler = new Handler(Looper.getMainLooper()) {
         
        public void handleMessage(Message msg) { // 处理Message，更新ListView
            DialogDemo.dismiss();
            switch(msg.what){ 
                case STATE_FINISH: 
                {
                    //init();
                    Toast.makeText(TalentsAddActivity.this, "上传成功", 1).show();
                    setResult(100);
                    finish();
                }
                    break;
                case STATE_FAIL:
                {
                    String info = "Error!";
                    if(msg.obj != null){
                        info = info+"\n"+msg.obj;
                    }
                    Toast.makeText(TalentsAddActivity.this, info, 1).show();
                    break;
                }
                case STATE_NULL:
                    //Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.datanull),Toast.LENGTH_SHORT).show(); 
                    break;
                case STATE_LINKERR:
                {
                    String type = msg.getData().getString("type");
                    Toast.makeText(getApplicationContext(), "网络连接失败",Toast.LENGTH_SHORT) .show();  
                    break;
                }
                case STATE_ERROR: 
                    Toast.makeText(getApplicationContext(), "网络连接错误",Toast.LENGTH_SHORT) .show();  
                    break;
               default:  
            } 
        } 
    }; 
    
	public void back(View view) {
		finish();
	}

    //刷新图片
    @Override
    protected void onResume() {
        super.onResume();
        if(!TextUtils.isEmpty(pathImage)){
            Bitmap addbmp=BitmapFactory.decodeFile(pathImage);
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("image", addbmp);
            map.put("url", pathImage);
            mListData.add(map);
//            adapter = new SimpleAdapter(this, 
//                    mListData, R.layout.gridview_image_no_title, 
//                    new String[] { "image"}, new int[] { R.id.iv_image}); 
//            adapter.setViewBinder(new ViewBinder() {  
//                @Override  
//                public boolean setViewValue(View view, Object data,  
//                        String textRepresentation) {  
//                    // TODO Auto-generated method stub  
//                    if(view instanceof ImageView && data instanceof Bitmap){  
//                        ImageView i = (ImageView)view;  
//                        i.setImageBitmap((Bitmap) data);  
//                        return true;  
//                    }  
//                    return false;  
//                }
//            }); 
//            gvImage.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            //刷新后释放防止手机休眠后自动添加
            pathImage = null;
        }
    }
}
