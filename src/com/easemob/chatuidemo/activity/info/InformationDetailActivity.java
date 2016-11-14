package com.easemob.chatuidemo.activity.info;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.easemob.applib.controller.HXSDKHelper;
import com.easemob.chatuidemo.DemoHXSDKModel;
import com.easemob.chatuidemo.activity.BaseActivity;
import com.wenpy.jcc.R;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class InformationDetailActivity extends BaseActivity {
    private TextView tvTitle;
    private WebView wvInformationDetail;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_information_detail);
        tvTitle = (TextView)findViewById(R.id.message_title);
        Intent intent = getIntent();
        Bundle bundlbe = intent.getExtras();
        String id = bundlbe.getString("id");
        String title = bundlbe.getString("title");
        tvTitle.setText(title);
        Open(id);
    }
    
    private void Open(String id){
        wvInformationDetail = (WebView) findViewById(R.id.wv_information_detail);
        //WebView加载web资源
        String url = "http://"+getResources().getString(R.string.server)+"/mobile/show.php?id="+id;      
        wvInformationDetail.loadUrl(url);
        //覆盖WebView默认使用第三方或系统默认浏览器打开网页的行为，使网页用WebView打开
        wvInformationDetail.setWebViewClient(new WebViewClient(){
           @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // TODO Auto-generated method stub
               //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
             view.loadUrl(url);
            return true;
        }
       });
    }
}
