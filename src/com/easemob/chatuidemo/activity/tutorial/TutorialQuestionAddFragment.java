package com.easemob.chatuidemo.activity.tutorial;

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

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.AlertDialog.Builder;
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
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageView;
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
import android.widget.SimpleAdapter.ViewBinder;

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
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * 拍照提问添加
 * 
 */
public class TutorialQuestionAddFragment extends Fragment {
    public static final int REQUEST_CODE_LOCAL = 1;
    private static final int STATE_ERROR = -1; 
    private static final int STATE_FAIL = 0; 
    private static final int STATE_FINISH = 1; 
    private static final int STATE_LINKERR = 2; 
    private static final int STATE_TIMEOUT = 3; 
    private static final int STATE_NULL = 4; 
    private Spinner spSubject;
    private Spinner spType;
    private EditText etKyeword;
    private EditText etQuestion;
    private GridView gvImage;
    private ImageView ivAddImage;
    private Button btSend;
    private List<Map<String, Object>> mListData;
    private List<Map<String, Object>> mPicData;
    private List<Map<String, Object>> mTypeData;
    private String m_strPicUrl = "";
    private int mPicCount = 0;
    private SimpleAdapter adapter;
    private String pathImage; 
    private ArrayAdapter<String> typeAdapter;
    private String[] arrType = new String[]{};
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        return inflater.inflate(R.layout.fragment_question_add, container, false);
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
				
        mListData = new ArrayList<Map<String,Object>>();
        mTypeData = new ArrayList<Map<String,Object>>();
        
        spSubject = (Spinner)getActivity().findViewById(R.id.subject);
        spType = (Spinner)getActivity().findViewById(R.id.type);
        etKyeword = (EditText)getActivity().findViewById(R.id.keyword);
        etQuestion = (EditText)getActivity().findViewById(R.id.question);
		gvImage = (GridView)getActivity().findViewById(R.id.gv_image);
		ivAddImage = (ImageView)getActivity().findViewById(R.id.addimage);
		btSend = (Button)getActivity().findViewById(R.id.send);

		typeAdapter= new ArrayAdapter<String>(getActivity(), 
                 android.R.layout.simple_spinner_item,arrType);
        typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spType.setAdapter(typeAdapter);
        
		ArrayAdapter<CharSequence> arr_adapter = ArrayAdapter.createFromResource(getActivity(), 
                R.array.subject,android.R.layout.simple_spinner_item);
		arr_adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spSubject.setAdapter(arr_adapter); 
		
        spSubject.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1,
                    int arg2, long arg3) {
               String strSubject = spSubject.getSelectedItem().toString();
               List tmp = new ArrayList<String>();
               for(int i=0;i<mTypeData.size();i++){
                   if(mTypeData.get(i).get("parent").toString().equals(strSubject)){
                       tmp.add(mTypeData.get(i).get("name").toString());
                   }
               }
               arrType = new String[tmp.size()];
               arrType = (String[])tmp.toArray(new String[tmp.size()]);
               typeAdapter= new ArrayAdapter<String>(getActivity(), 
                       android.R.layout.simple_spinner_item,arrType);
              typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
              spType.setAdapter(typeAdapter);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                // TODO Auto-generated method stub
                
            }
        });
        
		btSend.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                //progressBar.setVisibility(View.VISIBLE);
                if(etQuestion.getText().toString().equals("") && mListData.size()==0){
                    Toast.makeText(getActivity(), "提问问题不能为空", 0).show();
                    return;
                }
                
               DialogDemo.show(getActivity(),"正在提交，请稍后...");
//               new GetTeacherThread(handler).start();
                String url = "http://"+getResources().getString(R.string.server)+"/api.php";
                m_strPicUrl = "";
                mPicCount = mListData.size();
                try {
                    if(mListData.size()>0){
                        for(int i=0;i<mListData.size();i++){
                            uploadFile(mListData.get(i).get("url").toString(), url);
                        }
                    }else
                        new GetDataThread(handler).start();
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
                Intent intent = new Intent(Intent.ACTION_VIEW);    
                intent.setDataAndType(Uri.parse("file://"+mListData.get(position).get("url").toString()), "image/*");
                startActivity(intent);
            }
        });
        gvImage.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View v,
                    final int position, long id) {
                if(position > 0){
                    AlertDialog.Builder builder = new Builder(getActivity());
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
        ivAddImage.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                if( mListData.size() == 2) {
                    Toast.makeText(getActivity(), "图片数2张已满", Toast.LENGTH_SHORT).show();
                    return;
                }
                selectPicFromLocal();
                
            }
        });

        DialogDemo.show(getActivity(),"正在加载，请稍后...");
        new GetTypeThread(handler).start();
    }
	
	void init(){
        mListData = new ArrayList<Map<String,Object>>();

        Map<String, Object> map = new HashMap<String, Object>();

        adapter = new SimpleAdapter(getActivity(),mListData,R.layout.gridview_image_no_title,
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
	}
	
	/**
	 * 选择解答老师  //已取消，改为自动
	 */
//	public void SelectTeacher(){
//	    String[] teachers = new String[mTeacherData.size()];
//	    for(int i=0;i<mTeacherData.size();i++){
//	        teachers[i] = mTeacherData.get(0).get("user").toString();
//	    }
//	    
//        AlertDialog.Builder builder=new AlertDialog.Builder(getActivity());
//        builder.setTitle("选择解答老师");
//        builder.setIcon(android.R.drawable.ic_dialog_info);
//        builder.setSingleChoiceItems(teachers, 0,null);
//        builder.setPositiveButton("确定", null);
//        builder.setNegativeButton("取消", null);
//        AlertDialog dialog=builder.create();
//        dialog.show();
//	}
	
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
    	                if(m_strPicUrl.length() > 1)
    	                    m_strPicUrl.substring(0, m_strPicUrl.length()-1);
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
                            m_strPicUrl = m_strPicUrl+Data.get(0).get("msg").toString()+",";
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
    	            Message msg = handler.obtainMessage(); 
    	            msg.what = STATE_FAIL;
                    msg.obj = "图片上传失败!";
                    handler.sendMessage(msg);
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
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_LOCAL & data != null) { // 发送本地图片
            Uri uri = data.getData();  
            if (!TextUtils.isEmpty(uri.getAuthority())) {  
                //查询选择图片  
                Cursor cursor = getActivity().getContentResolver().query(  
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
            String edit = "tutorial_question_add";
            Map<String, String> params = new HashMap<String, String>();
            params.put("method", edit);
            params.put("user", EMChatManager.getInstance().getCurrentUser());
            params.put("type", spType.getSelectedItem().toString());
            params.put("keyword", etKyeword.getText().toString());
            params.put("subject", spSubject.getSelectedItem().toString());
            params.put("question", etQuestion.getText().toString());
            params.put("file", m_strPicUrl);
            
            ConnServer server = new ConnServer();
            String path = "http://"+getResources().getString(R.string.server)+"/api.php";
            server.GetData(path,params,edit,handler);
        }
    }

    private class GetTypeThread extends Thread { 

        private Handler handler; 
 
        public GetTypeThread(Handler handler) { 
            this.handler = handler; 
        } 
 
        @Override 
        public void run() { 
            String edit = "question_type_get";
            Map<String, String> params = new HashMap<String, String>();
            params.put("method", edit);
            
            ConnServer server = new ConnServer();
            String path = "http://"+getResources().getString(R.string.server)+"/api.php";
            mTypeData = server.GetData(path,params,edit,handler);
        }
    }

    private final Handler handler = new Handler(Looper.getMainLooper()) {
         
        public void handleMessage(Message msg) { // 处理Message，更新ListView
            DialogDemo.dismiss();
            System.out.println("msg.obj:"+msg.obj);
            switch(msg.what){ 
                case STATE_FINISH: 
                {
                    if(msg.obj.toString().equals("question_type_get")){
                        if(mTypeData.size()>0){
                            List tmp = new ArrayList<String>();
                            for(int i=0;i<mTypeData.size();i++){
                                if(mTypeData.get(i).get("name").toString().equals(spSubject.getSelectedItem().toString()))
                                    tmp.add(mTypeData.get(i).get("name").toString());
                            }
                            arrType = (String[])tmp.toArray(new String[tmp.size()]);
                            typeAdapter= new ArrayAdapter<String>(getActivity(), 
                                    android.R.layout.simple_spinner_item,arrType);
                            typeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                            spType.setAdapter(typeAdapter);
                        }
                    }
                    else if(msg.obj.toString().equals("tutorial_question_add")){
                        Toast.makeText(getActivity(), "提交成功", 1).show();
                        getFragmentManager().popBackStack();
                    }
                }
                    break;
                case STATE_FAIL:
                {
                    String info = "Fail!";
                    if(msg.obj != null){
                        info = info+"\n"+msg.obj;
                    }
                    Toast.makeText(getActivity(), info, 1).show();
                    break;
                }
                case STATE_NULL:
                    //Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.datanull),Toast.LENGTH_SHORT).show(); 
                    break;
                case STATE_LINKERR:
                {
                    Toast.makeText(getActivity(), "网络连接失败",Toast.LENGTH_SHORT) .show();  
                    break;
                }
                case STATE_ERROR: 
                    Toast.makeText(getActivity(), "网络连接错误",Toast.LENGTH_SHORT) .show();  
                    break;
               default:  
            } 
        } 
    }; 

    //刷新图片
    @Override
    public void onResume() {
        super.onResume();
        if(!TextUtils.isEmpty(pathImage)){
            if( mListData.size() == 2) {
                Toast.makeText(getActivity(), "图片数2张已满", Toast.LENGTH_SHORT).show();
                return;
            }
            Bitmap addbmp=BitmapFactory.decodeFile(pathImage);
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("image", addbmp);
            map.put("url", pathImage);
            mListData.add(map);
            if(mListData.size()>0)
                gvImage.setVisibility(View.VISIBLE);
//            adapter = new SimpleAdapter(getActivity(), 
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
