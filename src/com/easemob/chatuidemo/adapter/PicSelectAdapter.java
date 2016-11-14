package com.easemob.chatuidemo.adapter;

import java.util.ArrayList;
import java.util.List;

import com.easemob.chatuidemo.utils.CommonAdapter;
import com.easemob.chatuidemo.utils.ViewHolder;
import com.wenpy.jcc.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.Toast;

public class PicSelectAdapter extends CommonAdapter<String>
{

	/**
	 * 用户选择的图片，存储为图片的完整路径
	 */
	public ArrayList<String> mSelectedImage = new ArrayList<String>();

	/**
	 * 文件夹路径
	 */
	private String mDirPath;
	private Context context;
	private int maxvalue;

	public PicSelectAdapter(Context context, List<String> mDatas, int itemLayoutId,
			String dirPath,int maxvalue)
	{
		super(context, mDatas, itemLayoutId);
		this.mDirPath = dirPath;
		this.context=context;
		this.maxvalue=maxvalue;
	}

	@SuppressLint("NewApi")
	@Override
	public void convert(final ViewHolder helper, final String item)
	{
		//设置no_pic
		helper.setImageResource(R.id.id_item_image, R.drawable.picselect_pictures_no);
		//设置no_selected
				helper.setImageResource(R.id.id_item_select,
						R.drawable.picselect_picture_unselected);
		//设置图片
		helper.setImageByUrl(R.id.id_item_image, mDirPath + "/" + item);
		
		final ImageView mImageView = helper.getView(R.id.id_item_image);
		final ImageView mSelect = helper.getView(R.id.id_item_select);
		
		mImageView.setColorFilter(null);
		//设置ImageView的点击事件
		mImageView.setOnClickListener(new OnClickListener()
		{
			//选择，则将图片变暗，反之则反之
			@Override
			public void onClick(View v)
			{
			    
			      // 已经选择过该图片
	                if (mSelectedImage.contains(mDirPath + "/" + item))
	                {
	                    mSelectedImage.remove(mDirPath + "/" + item);
	                    mSelect.setImageResource(R.drawable.picselect_picture_unselected);
	                    mImageView.setColorFilter(null);
	                } else
	                // 未选择该图片
	                {
	                  if(mSelectedImage.size()>=maxvalue){
	                    Toast.makeText(context, "最多只能选择"+maxvalue+"张图片",
	                      Toast.LENGTH_SHORT).show();
	                  }else{
	                    mSelectedImage.add(mDirPath + "/" + item);
	                    mSelect.setImageResource(R.drawable.picselect_pictures_selected);
	                    mImageView.setColorFilter(Color.parseColor("#77000000"));
	                }
			    }
			}
		});
		
		/**
		 * 已经选择过的图片，显示出选择过的效果
		 */
		if (mSelectedImage.contains(mDirPath + "/" + item))
		{
			mSelect.setImageResource(R.drawable.picselect_pictures_selected);
			mImageView.setColorFilter(Color.parseColor("#77000000"));
		}

	}
}
