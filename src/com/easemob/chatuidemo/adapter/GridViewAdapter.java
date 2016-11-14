package com.easemob.chatuidemo.adapter;

import java.io.File;
import java.util.List;
import com.easemob.chatuidemo.activity.user.UserProfileActivity;
import com.easemob.chatuidemo.utils.ConnServer;
import com.squareup.picasso.Picasso;
import com.wenpy.jcc.R;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class GridViewAdapter extends BaseAdapter{

	private Context context;
	private List<String> info;
	private LayoutInflater inflater;
	private int mode = 1;
	
	public GridViewAdapter(Context mContext,List<String> list){
		this.context = mContext;
		this.info = list;
		this.inflater = LayoutInflater.from(context);
	}
	
	public GridViewAdapter(Context mContext,List<String> list,int mode){
		this.context = mContext;
		this.info = list;
		this.mode = mode;
		this.inflater = LayoutInflater.from(context);
	}
	
	public void setList(List<String> list){
	    this.info=list;
	  }
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return info.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return info.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder = null;
		if(convertView == null){
			convertView = inflater.inflate(R.layout.gridview_image_no_title, null);
			viewHolder = new ViewHolder();
			viewHolder.hpotoiv = (ImageView)convertView.findViewById(R.id.iv_image);
//            viewHolder.hpotoiv.setLayoutParams(
//                    new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));
//			viewHolder.hpotoiv.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
//			viewHolder.hpotoiv.setPadding(8, 8, 8, 8);
			convertView.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		 if(TextUtils.isEmpty(info.get(position))){
			 viewHolder.hpotoiv.setImageResource(R.drawable.square_add); 
		    }else if("loading".equals(info.get(position))){
		    	viewHolder.hpotoiv.setImageResource(R.drawable.app_addin_pic);   
		    }else{
                if(info.get(position).indexOf("http") == -1)
                    Picasso.with(context).load(new File(info.get(position))).into(viewHolder.hpotoiv);
                else{
                    switch(mode){
    		        case 1:
                        Picasso.with(context).load(info.get(position)).into(viewHolder.hpotoiv);
    		            break;
    		        case 2:
    		            asyncloadImage(viewHolder.hpotoiv, info.get(position)); 
    		            break;
    		        }
                }
		    }
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
                return service.getImageURI(params[0], new File(Environment.getExternalStorageDirectory(), "cache"));  
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
    
	static class ViewHolder{
		private ImageView hpotoiv;
	}

}
