package com.easemob.chatuidemo.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.easemob.applib.controller.HXSDKHelper;
import com.easemob.chatuidemo.DemoHXSDKModel;
import com.wenpy.jcc.R;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.webkit.WebSettings.PluginState;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class EnrollmentMainActivity extends BaseActivity {
    private TextView tvTitle;
    private WebView wvContent;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_enrollment_main);
        tvTitle = (TextView)findViewById(R.id.message_title);
        Open();
    }
    
    private void Open(){
        wvContent = (WebView) findViewById(R.id.wv_content);
        //WebView加载web资源
        String url = "http://"+getResources().getString(R.string.server)+"/mobile/admissions.php";      
        wvContent.loadUrl(url);

        //覆盖WebView默认使用第三方或系统默认浏览器打开网页的行为，使网页用WebView打开
        wvContent.getSettings().setJavaScriptEnabled(true);
        wvContent.getSettings().setPluginState(PluginState.ON);
//        wvContent.getSettings().setPluginsEnabled(true);//可以使用插件
        wvContent.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        wvContent.getSettings().setAllowFileAccess(true);
        wvContent.getSettings().setDefaultTextEncodingName("UTF-8");
        wvContent.getSettings().setLoadWithOverviewMode(true);
        wvContent.getSettings().setUseWideViewPort(true);
        
        wvContent.setWebViewClient(new WebViewClient(){
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
