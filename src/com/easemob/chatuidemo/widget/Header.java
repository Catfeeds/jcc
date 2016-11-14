package com.easemob.chatuidemo.widget;

import com.wenpy.jcc.R;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * 页面标题栏公用控件
 */
public class Header extends RelativeLayout {
    private TextView tv;
    
    private ImageView iv = new ImageView(getContext());

    public Header(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public Header(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }
    
    public Header(Context context) {
        super(context);
        init();
    }
    
    private void init() {
//         int p = (int) (getResources().getDisplayMetrics().density * 10);
//         setPadding(p, 0, p, 0);
    }

//    public void setBackgroundColor(int color){
//	setBackgroundColor(color);
//    }
    
    public void setTitle(String text) {
    	if(tv==null){
    		tv = new TextView(getContext());
            tv.setTextSize(22);
            tv.setTextColor(Color.WHITE);
            tv.getPaint().setFakeBoldText(true);
            setTitleView(tv);
    	}
    	tv.setText(text);
    }
    
    public void setTitle(String text, OnClickListener listener) {
    	if(tv==null){
    		tv = new TextView(getContext());
            tv.setTextSize(22);
            tv.setTextColor(Color.WHITE);
            tv.getPaint().setFakeBoldText(true);
            tv.setOnClickListener(listener);
            setTitleView(tv);
    	}
    	tv.setText(text);
    }
    
    public void hiddleTitle() {
		// TODO Auto-generated method stub
    	if(tv!=null){
    		tv.setVisibility(View.GONE);
    	}
	}
    
    public void showTitle() {
		// TODO Auto-generated method stub
    	if(tv!=null){
    		tv.setVisibility(View.VISIBLE);
    	}
	}
    //--------------------2015-9-10 hjy ------------------------------
	public void setTitleImageViewRes(View view) {    	
		
		int p = (int) (getResources().getDisplayMetrics().density * 10);
        //ImageView iv = new ImageView(getContext());
      /*  iv.setImageResource(res); 
        iv.setBackgroundResource((R.drawable.common_head_itemselector));*/
        view.setPadding(p, p, p, p);
        setTitleView(view);
    }
    
    public void setTitleView(View view) {
        RelativeLayout.LayoutParams viewLp = new RelativeLayout.LayoutParams(-2, -2);
        viewLp.addRule(RelativeLayout.CENTER_IN_PARENT);
        addView(view, viewLp);
    }
    
    public void setRightView(View view, OnClickListener listener) {
        RelativeLayout.LayoutParams ivLp = new RelativeLayout.LayoutParams(-2, RelativeLayout.LayoutParams.MATCH_PARENT);
        ivLp.addRule(RelativeLayout.CENTER_VERTICAL);
        ivLp.addRule(RelativeLayout.ALIGN_PARENT_RIGHT);
        addView(view, ivLp);
        view.setOnClickListener(listener);
    }

    public void setRightImageViewRes(int res, OnClickListener listener) {
    	int p = (int) (getResources().getDisplayMetrics().density * 10);
        ImageView iv = new ImageView(getContext());
        iv.setImageResource(res);
        iv.setBackgroundResource((R.drawable.common_head_itemselector));
        iv.setPadding(p, p, p, p);
        setRightView(iv, listener);
    }
    
    public void setRightTextViewRes(int res, OnClickListener listener) {
    	int p = (int) (getResources().getDisplayMetrics().density * 10);
        TextView iv = new TextView(getContext());
        iv.setTextColor(Color.parseColor("#FFFFFF"));
        iv.setTextSize(14);
        iv.setText(res);
        iv.setBackgroundResource((R.drawable.common_head_itemselector));
        iv.setPadding(p, p, p, p);
        setRightView(iv, listener);
    }
    
    public void setLeftView(View view, OnClickListener listener) {
        RelativeLayout.LayoutParams ivLp = new RelativeLayout.LayoutParams(-2, RelativeLayout.LayoutParams.MATCH_PARENT);
        ivLp.addRule(RelativeLayout.CENTER_VERTICAL);
        ivLp.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        addView(view, ivLp);
        view.setOnClickListener(listener);
    }
    
    public void setLeftImageVewRes(int res, OnClickListener listener) {
    	int p = (int) (getResources().getDisplayMetrics().density * 10);
        ImageView iv = new ImageView(getContext());
        iv.setImageResource(res);
        iv.setBackgroundResource((R.drawable.common_head_itemselector));
        iv.setPadding(p, p, p, p);
        setLeftView(iv, listener);
    }
    
}
