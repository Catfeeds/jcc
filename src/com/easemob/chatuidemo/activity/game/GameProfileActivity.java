package com.easemob.chatuidemo.activity.game;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.easemob.applib.controller.HXSDKHelper;
import com.easemob.chatuidemo.DemoHXSDKModel;
import com.easemob.chatuidemo.activity.BaseActivity;
import com.wenpy.jcc.R;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
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

public class GameProfileActivity extends BaseActivity {
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
        
        Intent intent = getIntent();
        String title = intent.getStringExtra("title");
        if(title!=null)
            tvTitle.setText(title);
        
        Bundle bundlbe = intent.getExtras();
        mId = bundlbe.getString("id");
        String url = "http://"+getResources().getString(R.string.server)+"/mobile/game.php?id="+mId;
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
        wvDetail.setWebViewClient(new WebViewClient(){
           @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
               if(url.endsWith(".apk")){
                   Uri uri = Uri.parse(url);
                   Intent viewIntent = new Intent(Intent.ACTION_VIEW,uri);
                   GameProfileActivity.this.startActivity(viewIntent);
                   return true;
               }
               return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                pbProgress.setVisibility(View.GONE);
                super.onPageFinished(view, url);
            }
        });
    }
      
    @Override  
    public void onResume() {// 继承自Activity  
        super.onResume();  
        wvDetail.onResume();  
    }  
}
