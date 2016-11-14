package com.easemob.chatuidemo.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.easemob.applib.controller.HXSDKHelper;
import com.easemob.chatuidemo.DemoHXSDKModel;
import com.wenpy.jcc.R;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings.PluginState;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.FrameLayout;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TextView;

public class WebBrowerActivity extends BaseActivity {
    private TextView tvTitle;
    private WebView wvDetail;
    private ProgressBar pbProgress;
    private View mCustomView = null; 
    private FrameLayout mFullscreenContainer;
    private LinearLayout mContentView;
    private String mId;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        mContentView = (LinearLayout) findViewById(R.id.main_content);  
        wvDetail = (WebView) findViewById(R.id.wv_detail);
        tvTitle = (TextView)findViewById(R.id.message_title);
        pbProgress = (ProgressBar)findViewById(R.id.progressbar);
        
        wvDetail.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                //返回值是true的时候控制去WebView打开，为false调用系统浏览器或第三方浏览器
              view.loadUrl(url);
              return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                pbProgress.setVisibility(View.GONE);
                super.onPageFinished(view, url);
            }
            
        });
        
        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        String url = intent.getStringExtra("url");
        if(title!=null)
            tvTitle.setText(title);
        
        if(url != null)
            Open(url);
    }
    
    private void Open(String url){
        wvDetail.loadUrl(url);
        wvDetail.getSettings().setJavaScriptEnabled(true);
        wvDetail.getSettings().setPluginState(PluginState.ON);
        wvDetail.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        wvDetail.getSettings().setAllowFileAccess(true);
        wvDetail.getSettings().setDefaultTextEncodingName("UTF-8");
        wvDetail.getSettings().setLoadWithOverviewMode(true);
        wvDetail.getSettings().setUseWideViewPort(true);
    }
      
    @Override  
    public void onResume() {
        super.onResume();  
        wvDetail.onResume();  
    }
    
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
         if (keyCode == KeyEvent.KEYCODE_BACK && wvDetail.canGoBack()) {
              wvDetail.goBack();
              return true;
         }
         return super.onKeyDown(keyCode, event);
    }

    @Override
    public void back(View view) {
        if (wvDetail.canGoBack()) {
            wvDetail.goBack();
            return;
        }
        super.back(view);
    }
    
}
