package com.easemob.chatuidemo.adapter;

import java.io.File;
import java.util.List;

import org.w3c.dom.Text;

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
 * 问题列表Adapter实现
 *
 */
public class QuestionListAdapter extends BaseAdapter {  
    
    protected static final int SUCCESS_GET_IMAGE = 0;  
    private Context context;  
    private List<Info> Infos;  
    private LayoutInflater mInflater;  
  
    // 自己定义的构造函数  
    public QuestionListAdapter(Context context, List<Info> Infos) {  
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

        View view = null;  
        if (convertView != null) {  
            view = convertView;  
        } else {  
            view = mInflater.inflate(R.layout.row_question, null);  
        }  
  
        TextView tv_msg = (TextView) view.findViewById(R.id.unread_msg_number);  
        TextView tv_title = (TextView) view.findViewById(R.id.title);
        TextView tv_date = (TextView) view.findViewById(R.id.date);
        Info info = Infos.get(position);  
        
        String title = "[%s]%t";
        title = title.replace("%s", info.getObj()).replace("%t", info.getTitle());
        tv_title.setText(title);
        tv_date.setText(info.getDate());
        if(info.getImage().equals("1")){
            tv_msg.setVisibility(View.INVISIBLE);
        }else
            tv_msg.setVisibility(View.VISIBLE);
  
        return view;  
    }  
} 
