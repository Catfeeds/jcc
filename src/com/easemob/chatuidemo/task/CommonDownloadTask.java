package com.easemob.chatuidemo.task;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class CommonDownloadTask extends AsyncTask<String, Integer, Long> {
    private Handler mHandler = null;
    private String mUrl = null;
    private String mObj = null;
    private int mWhat = 0;
    
    public CommonDownloadTask(Handler handler, String url, int what, String obj) {
        mHandler = handler;
        mUrl = url;
        mWhat = what;
        mObj = obj;
    }
    
    @Override
    protected Long doInBackground(String... params) {
        OutputStream output = null;
        try {
            URL url = new URL(mUrl);
            // 创建一个HttpURLConnection连接  
            HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
            InputStream input = urlConn.getInputStream();
            //文件夹
            File dir = new File(Environment.getExternalStorageDirectory()+"/cache/");  
            if(!dir.exists())
            {
                dir.mkdir();
            }
            //本地文件
            File file = new File(Environment.getExternalStorageDirectory()+"/cache/" + params[0]);  
            if(!file.exists()){
                file.createNewFile();  
                //写入本地
                output = new FileOutputStream(file);  
                byte buffer [] = new byte[1024];
                int inputSize = -1;
                while((inputSize = input.read(buffer)) != -1) {
                    output.write(buffer, 0, inputSize);
                }
                output.flush();
            }
            
        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally{  
            try{  
                if(output!=null)
                    output.close();  
            }  
            catch(Exception e){  
                e.printStackTrace();  
            }  
        }
        //发送处理
        if (mHandler != null) {
            String response = "";
            if(!params[0].equals("")){
                response = Environment.getExternalStorageDirectory()+"/cache/" + params[0];
            }
            sendResponse(mHandler, response, mWhat, mObj);
        } else {
            Log.w("server", "mHandler == null, what = " + mWhat);
        }
        return null;
    }
    
    private void sendResponse(Handler handler, String response, int what, String obj) {
        if (handler != null) {
            if (null == response) {
                handler.sendEmptyMessage(what);
            } else {
                Message msg = new Message();
                Bundle bundle = new Bundle();
                bundle.putString("response", response);
                msg.setData(bundle);
                msg.what = what;
                msg.obj = obj;
                handler.sendMessage(msg);
            }
        }
    }
}
