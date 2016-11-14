package com.easemob.chatuidemo.utils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.widget.Toast;

public class DialogDemo extends Activity
{
    private static ProgressDialog progressDialog;  
    private static final int LOGIN_DIALOG = 1; 
    private static final int LOAD_DIALOG = 2; 
    private static final int PRINT_DIALOG = 3; 
    // 错误消息对话框
    
    public static void builder(Context context, String title, String message)
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(title);
        builder.setMessage(message);
        builder.setPositiveButton("确定", null);
        builder.create();
        builder.show();
    }
    
    @SuppressWarnings("deprecation")
    public static void show(Context context,int id,Handler hd)
    {
        final Handler handler = hd;
        progressDialog = new ProgressDialog(context){

            @Override
            protected void onStop() {
                Message msg = handler.obtainMessage(); 
                Bundle b = new Bundle(); 
                b.putInt("state",0); 
                msg.setData(b);
                handler.sendMessage(msg);               
                super.onStop();
            }
            
        };
        progressDialog.setTitle("");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setIndeterminate(true);
        switch (id) {
        case LOAD_DIALOG:
            progressDialog.setMessage("正在加载，请稍后...");
            break;
        default:
            break;
        }
        progressDialog.show();
    }
    
    public static void show(Context context,int id)
    {
        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("");
        progressDialog.setCancelable(false);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setIndeterminate(true);
        switch (id) {
        case LOAD_DIALOG:
            progressDialog.setMessage("正在加载，请稍后...");
            break;
        default:
            break;
        }
        progressDialog.show();
    }
    
    public static void show(Context context,String str)
    { 
        progressDialog = new ProgressDialog(context);
        progressDialog.setTitle("");
        progressDialog.setCancelable(true);
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.setIndeterminate(true);
        progressDialog.setMessage(str);
        progressDialog.show();
    }
    
    public static void dismiss(){
        if(progressDialog!=null)
            progressDialog.dismiss();
    }
}
