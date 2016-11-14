package com.easemob.chatuidemo.activity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.easemob.applib.controller.HXSDKHelper;
import com.easemob.chat.EMChatManager;
import com.easemob.chatuidemo.DemoHXSDKModel;
import com.wenpy.jcc.R;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
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

public class AlbumCommentActivity extends BaseActivity {
    private TextView tvTitle;
    private WebView wvDetail;
    private ProgressBar pbProgress;
    private View mCustomView = null; 
    private FrameLayout mFullscreenContainer;
    private LinearLayout mContentView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);
        Intent intent = getIntent();
        String id = intent.getStringExtra("id");
        
        mFullscreenContainer = (FrameLayout) findViewById(R.id.fullscreen_custom_content);  
        mContentView = (LinearLayout) findViewById(R.id.main_content);  
        wvDetail = (WebView) findViewById(R.id.wv_detail);
        tvTitle = (TextView)findViewById(R.id.message_title);
        pbProgress = (ProgressBar)findViewById(R.id.progressbar);
        tvTitle.setText("群");
        String url = "http://"+getResources().getString(R.string.server)+"/mobile/album_comment.php?id="+id+"&user="+EMChatManager.getInstance().getCurrentUser();
        Open(url);
    }
    
    private void Open(String url){
        wvDetail.loadUrl(url);
        //覆盖WebView默认使用第三方或系统默认浏览器打开网页的行为，使网页用WebView打开
        wvDetail.getSettings().setJavaScriptEnabled(true);
        wvDetail.getSettings().setPluginState(PluginState.ON);
//        wvDetail.getSettings().setPluginsEnabled(true);//可以使用插件
        wvDetail.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        wvDetail.getSettings().setAllowFileAccess(true);
        wvDetail.getSettings().setDefaultTextEncodingName("UTF-8");
        wvDetail.getSettings().setLoadWithOverviewMode(true);
        wvDetail.getSettings().setUseWideViewPort(true);
        
        wvDetail.setWebChromeClient(new MyWebChromeClient());
        wvDetail.setWebViewClient(new WebViewClient(){
           @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                // TODO Auto-generated method stub
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
    }

    public static int getPhoneAndroidSDK() {  
        // TODO Auto-generated method stub  
        int version = 0;  
        try {
            version = Integer.valueOf(android.os.Build.VERSION.SDK);
        } catch (NumberFormatException e) {  
            e.printStackTrace();  
        }  
        return version;  
    } 

    class MyWebChromeClient extends WebChromeClient {  
  
        private CustomViewCallback mCustomViewCallback;  
        private int mOriginalOrientation = 1;  
  
        @Override  
        public void onShowCustomView(View view, CustomViewCallback callback) {  
            getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN);
            onShowCustomView(view, ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE, callback);  
            super.onShowCustomView(view, callback);  
        } 
  
        public void onShowCustomView(View view, int requestedOrientation,  
                WebChromeClient.CustomViewCallback callback) {  
            if (mCustomView != null) {  
                callback.onCustomViewHidden();  
                return;  
            }  
            if (getPhoneAndroidSDK() >= 14) {  
                mFullscreenContainer.addView(view);  
                mCustomView = view;  
                mCustomViewCallback = callback;  
                //mOriginalOrientation = getRequestedOrientation();  
                mContentView.setVisibility(View.INVISIBLE);  
                mFullscreenContainer.setVisibility(View.VISIBLE);  
                mFullscreenContainer.bringToFront();  
  
                setRequestedOrientation(requestedOrientation);  
            }  
  
        }  
  
        public void onHideCustomView() {  
            mContentView.setVisibility(View.VISIBLE);  
            if (mCustomView == null) {  
                return;  
            }  
            mCustomView.setVisibility(View.GONE);  
            mFullscreenContainer.removeView(mCustomView);  
            mCustomView = null;  
            mFullscreenContainer.setVisibility(View.GONE);  
            try {  
                mCustomViewCallback.onCustomViewHidden();  
            } catch (Exception e) {  
            }  
            // Show the content view.  
            final WindowManager.LayoutParams attrs = getWindow().getAttributes();
            attrs.flags &= (~WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().setAttributes(attrs);
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_LAYOUT_NO_LIMITS);
            
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);  
        } 
    }  
    
    @Override  
    public void onPause() {// 继承自Activity  
        super.onPause();  
        wvDetail.onPause();  
    }  
      
    @Override  
    public void onResume() {// 继承自Activity  
        super.onResume();  
        wvDetail.onResume();  
    }  
}
