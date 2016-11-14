package com.easemob.chatuidemo.adapter;

import java.io.File;
import java.util.List;
import com.easemob.chatuidemo.domain.Info;
import com.easemob.chatuidemo.utils.ConnServer;
import com.wenpy.jcc.R;

import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 
 *
 */
public class HomeworkAdapter extends BaseAdapter {
    private Context context;  
    private List<Info> Infos;  
    private File cache;  
    private LayoutInflater mInflater;  

    private class ViewHolder {
        ImageView iv_header;
        TextView tv_title;
        TextView tv_date;
    }
    
    // 自己定义的构造函数  
    public HomeworkAdapter(Context context, List<Info> Infos) {  
        this.context = context;  
        this.Infos = Infos;    
        mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);  
    }  
  
    @Override  
    public int getCount() {  
        return Infos.size();  
    }  
  
    @Override  
    public Object getItem(int position) {  
        return Infos.get(position);  
    }  
  
    @Override  
    public long getItemId(int position) {  
        return position;  
    }  
  
    @Override  
    public View getView(int position, View convertView, ViewGroup parent) {  
        // 1获取item,再得到控件  
        // 2 获取数据  
        // 3绑定数据到item  
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();  
            convertView = mInflater.inflate(R.layout.row_homework_list, null);
            holder.iv_header = (ImageView) convertView.findViewById(R.id.avatar);  
            holder.tv_title = (TextView) convertView.findViewById(R.id.title);
            holder.tv_date = (TextView) convertView.findViewById(R.id.date);
            convertView.setTag(holder);
        } else {  
            holder = (ViewHolder) convertView.getTag();
        }  
  
        Info info = Infos.get(position);  
  
        holder.tv_title.setText(info.getTitle());
        holder.tv_date.setText(info.getDate());
        
        // 异步的加载图片  
//        asyncloadImage(holder.iv_header, info.getImage()); 
  
        return convertView;
    }  
  
    private void asyncloadImage(ImageView iv_header, String path) {  
        ConnServer service = new ConnServer();  
        AsyncImageTask task = new AsyncImageTask(service, iv_header);  
        task.execute(path);  
    }
  
    private final class AsyncImageTask extends AsyncTask<String, Integer, Uri> {  
  
        private ConnServer service;  
        private ImageView iv_header;  
  
        public AsyncImageTask(ConnServer service, ImageView iv_header) {  
            this.service = service;  
            this.iv_header = iv_header;  
        }  
  
        // 后台运行的子线程子线程  
        @Override  
        protected Uri doInBackground(String... params) {  
            try {  
                return service.getImageURI(params[0], cache);  
            } catch (Exception e) {  
                e.printStackTrace();  
            }  
            return null;  
        }  
  
        // 这个放在在ui线程中执行  
        @Override  
        protected void onPostExecute(Uri result) {  
            super.onPostExecute(result);   
            // 完成图片的绑定 
            if (iv_header != null && result != null) {  
                iv_header.setImageURI(result);  
            }else{
                iv_header.setImageResource(R.drawable.empty_photo);
            }
        }  
    }  
  
    /** 
     * 采用普通方式异步的加载图片 
     */  
    /*private void asyncloadImage(final ImageView iv_header, final String path) { 
        final Handler mHandler = new Handler() { 
            @Override 
            public void handleMessage(Message msg) { 
                super.handleMessage(msg); 
                if (msg.what == SUCCESS_GET_IMAGE) { 
                    Uri uri = (Uri) msg.obj; 
                    if (iv_header != null && uri != null) { 
                        iv_header.setImageURI(uri); 
                    } 
 
                } 
            } 
        }; 
        // 子线程，开启子线程去下载或者去缓存目录找图片，并且返回图片在缓存目录的地址 
        Runnable runnable = new Runnable() { 
            @Override 
            public void run() { 
                ContactService service = new ContactService(); 
                try { 
                    //这个URI是图片下载到本地后的缓存目录中的URI 
                    Uri uri = service.getImageURI(path, cache); 
                    Message msg = new Message(); 
                    msg.what = SUCCESS_GET_IMAGE; 
                    msg.obj = uri; 
                    mHandler.sendMessage(msg); 
                } catch (Exception e) { 
                    e.printStackTrace(); 
                } 
            } 
        }; 
        new Thread(runnable).start(); 
    }*/  
} 
