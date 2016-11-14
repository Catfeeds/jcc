package com.easemob.chatuidemo.activity;

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
import com.easemob.chatuidemo.activity.talent.TalentsCommentActivity;
import com.easemob.chatuidemo.adapter.ContactAdapter;
import com.easemob.chatuidemo.adapter.ImageAdapter;
import com.easemob.chatuidemo.domain.Info;
import com.easemob.chatuidemo.domain.User;
import com.easemob.chatuidemo.utils.ConnServer;
import com.easemob.chatuidemo.utils.DialogDemo;
import com.wenpy.jcc.R;

import android.content.Intent;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.Toast;

public class ImageViewFragment extends Fragment {
    private static final int STATE_ERROR = -1; 
    private static final int STATE_FAIL = 0; 
    private static final int STATE_FINISH = 1; 
    private static final int STATE_LINKERR = 2; 
    private static final int STATE_TIMEOUT = 3; 
    private static final int STATE_NULL = 4; 
    private GridView gvFriendList;
    private List<Info> contactList;
    private ImageAdapter mAdapter;
    private List<Map<String, Object>> mReturnData;
    private File cache;
    private String mID;
    private String mType;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        return inflater.inflate(R.layout.fragment_grid_img_view, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        contactList = new ArrayList<Info>();
        //initView();       

        gvFriendList = (GridView)getView().findViewById(R.id.gv_img);        
        gvFriendList.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                    long arg3) {
                Intent intent = new Intent(getActivity(),
                        TalentsCommentActivity.class);
                intent.putExtra("id", mReturnData.get(arg2).get("id").toString());
                intent.putExtra("type", mType);
                startActivity(intent);
            }
        });
        //创建缓存目录，系统一运行就得创建缓存目录的，
        cache = new File(Environment.getExternalStorageDirectory(), "cache");
        
        if(!cache.exists()){
            cache.mkdirs();
        }        

        DialogDemo.show(getActivity(),"正在加载，请稍后...");
        //获取数据，主UI线程是不能做耗时操作的，所以启动子线程来做
        new Thread(){
            public void run() {
                String edit = "elegant_demeanour_photo_get";
                Map<String, String> params = new HashMap<String, String>();
                params.put("method", edit);
                params.put("mainid", mID);
                String url = "http://"+getResources().getString(R.string.server)+"/api.php";
                ConnServer server = new ConnServer();
                mReturnData = server.GetData(url,params ,edit, handler);
            };
        }.start();
    }

    public void SetId(String id){
        mID = id;
    }
    
    public void setType(String strtype){
       mType = strtype;
    }
    
    private Handler handler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            DialogDemo.dismiss(); 
            switch(msg.what){
                case STATE_FINISH:
                    List<Info> contacts = new ArrayList<Info>();
                    for(int i=0;i<mReturnData.size();i++){
                        Info info = new Info();
                        info.setImage(mReturnData.get(i).get("photo").toString());
                        contacts.add(info);
                    }
    //             Log.d("server", "size:"+contacts.size()+"=>"+contacts.get(0).getImage());
                    mAdapter = new ImageAdapter(getActivity(),contacts,cache);
                    gvFriendList.setAdapter(mAdapter);
                    break;
                case STATE_ERROR:
                    Toast.makeText(getActivity(), "network error", 0).show();                    
                    break;
                case STATE_FAIL:
                    Toast.makeText(getActivity(), msg.obj+"", 0).show();
                    break;
            }
        };
    };
    
    /**
     * 获取图片列表
     */
    private void getContactList() {
        contactList.clear();
        for(int i=0;i<20;i++){
            Info info = new Info();
            info.setImage("http://cdn-img.easyicon.net/png/11602/1160289.gif");
            contactList.add(info);
        }
    }
}
