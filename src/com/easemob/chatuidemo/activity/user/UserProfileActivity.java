package com.easemob.chatuidemo.activity.user;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.easemob.EMValueCallBack;
import com.easemob.applib.controller.HXSDKHelper;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMContactManager;
import com.easemob.chatuidemo.DemoApplication;
import com.easemob.chatuidemo.DemoHXSDKHelper;
import com.wenpy.jcc.R;
import com.easemob.chatuidemo.activity.BaseActivity;
import com.easemob.chatuidemo.activity.ChatActivity;
import com.easemob.chatuidemo.activity.main.MainActivity;
import com.easemob.chatuidemo.adapter.GridImageAdapter;
import com.easemob.chatuidemo.adapter.ViewPagerAdapter;
import com.easemob.chatuidemo.db.InviteMessgeDao;
import com.easemob.chatuidemo.db.UserDao;
import com.easemob.chatuidemo.domain.Info;
import com.easemob.chatuidemo.domain.User;
import com.easemob.chatuidemo.utils.ConnServer;
import com.easemob.chatuidemo.utils.DialogDemo;
import com.easemob.chatuidemo.utils.MD5;
import com.easemob.chatuidemo.utils.UserUtils;
import com.squareup.picasso.Picasso;

public class UserProfileActivity extends BaseActivity implements OnClickListener{
	
	private static final int REQUESTCODE_PICK = 1;
	private static final int REQUESTCODE_CUTTING = 2;
	private ImageView headAvatar;
	private ImageView headPhotoUpdate;
	private ImageView iconRightArrow;
	private TextView tvUserSign;
	private TextView tvNickName;
	private TextView tvUsername;
	private TextView tvInterest;
    private TextView tvFoods;
    private TextView tvPlaces;
    private TextView tvStar;
    private TextView tvMarjor;
	private ProgressDialog dialog;
	private LinearLayout llEditFriendName;
    private LinearLayout llChat;
    private LinearLayout llDelFriend;
    private LinearLayout llBottom;
    private RelativeLayout rlPicContainer;
	private String username;
    private String nick;
    private List<Map<String, Object>> mListData;
    private List<Map<String, Object>> mAblumData;
    private List<Map<String, Object>> mReturnData;
    private GridImageAdapter adapter ;
    private GridView gvAlbum;
    private Button btSave;
    private File cache;
    private ImageView ivGoto;
    private boolean enableUpdate;
	
	@Override
	protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.activity_user_profile);
        mListData = new ArrayList<Map<String,Object>>();
        mAblumData = new ArrayList<Map<String,Object>>();
        mReturnData = new ArrayList<Map<String,Object>>();
        Intent intent = getIntent();
        username = intent.getStringExtra("username");
        enableUpdate = intent.getBooleanExtra("setting", false);
        initView();
        initListener();
	}
	
	private void initView() {
		headAvatar = (ImageView) findViewById(R.id.user_head_avatar);
		headPhotoUpdate = (ImageView) findViewById(R.id.user_head_headphoto_update);
		tvUsername = (TextView) findViewById(R.id.user_username);
		tvNickName = (TextView) findViewById(R.id.user_nickname);
		tvUserSign = (TextView) findViewById(R.id.user_sign);
        tvInterest = (TextView) findViewById(R.id.interest);
        tvFoods = (TextView) findViewById(R.id.food);
        tvPlaces = (TextView) findViewById(R.id.tour);
        tvStar = (TextView) findViewById(R.id.star);
        tvMarjor = (TextView) findViewById(R.id.marjor);
		llEditFriendName = (LinearLayout) findViewById(R.id.ll_editfriendname);
        llChat = (LinearLayout) findViewById(R.id.ll_chat);
        rlPicContainer = (RelativeLayout) findViewById(R.id.container);
        llDelFriend = (LinearLayout) findViewById(R.id.ll_delfriend);
        llBottom = (LinearLayout) findViewById(R.id.ll_bottom);
		iconRightArrow = (ImageView) findViewById(R.id.ic_right_arrow);
		gvAlbum = (GridView) findViewById(R.id.gv_album);
		ivGoto = (ImageView)findViewById(R.id.iv_goto);
		btSave = (Button)findViewById(R.id.save);
		
        cache = new File(Environment.getExternalStorageDirectory(), "cache");
        
        if(!cache.exists()){
            cache.mkdirs();
        }        
        if(username.equals(EMChatManager.getInstance().getCurrentUser()))
            llBottom.setVisibility(View.GONE);
	}
	
	private void initListener() {
        llEditFriendName.setOnClickListener(this);
        llChat.setOnClickListener(this);
        llDelFriend.setOnClickListener(this);
        if(!username.equals(EMChatManager.getInstance().getCurrentUser())){
            tvFoods.setEnabled(false);
            tvStar.setEnabled(false);
            tvPlaces.setEnabled(false);
            tvMarjor.setEnabled(false);
            btSave.setVisibility(View.GONE);
        }
		if (enableUpdate) {
			headPhotoUpdate.setVisibility(View.VISIBLE);
			iconRightArrow.setVisibility(View.VISIBLE);
			headAvatar.setOnClickListener(this);
		} else {
			headPhotoUpdate.setVisibility(View.GONE);
			iconRightArrow.setVisibility(View.INVISIBLE);
		}
		if (username == null) {
			tvUsername.setText(EMChatManager.getInstance().getCurrentUser());
			UserUtils.setCurrentUserNick(tvNickName);
			UserUtils.setCurrentUserAvatar(this, headAvatar);
		} else if (username.equals(EMChatManager.getInstance().getCurrentUser())) {
			tvUsername.setText(EMChatManager.getInstance().getCurrentUser());
			tvNickName.setText(DemoApplication.getInstance().getNick());
			//Picasso.with(UserProfileActivity.this).load(DemoApplication.getInstance().getAvatar()).placeholder(R.drawable.default_avatar).into(headAvatar);
		} else {
			tvUsername.setText(username);
		}

        tvInterest.setOnClickListener(this);
		tvUserSign.setOnClickListener(this);
		tvFoods.setOnClickListener(this);
		tvPlaces.setOnClickListener(this);
		tvStar.setOnClickListener(this);
		tvMarjor.setOnClickListener(this);
		ivGoto.setOnClickListener(this);
		btSave.setOnClickListener(this);
		
        new GetDataThread(mHandler).start();
        new GetUserPicThread(mHandler).start();
        asyncFetchUserInfo(username);
	}
	
	private void setText(Object object){
	    
	}
	/**
     * 删除联系人
     * 
     * @param toDeleteUser
     */
    public void deleteContact(final String Username) {
        String st1 = getResources().getString(R.string.deleting);
        final String st2 = getResources().getString(R.string.Delete_failed);
        final ProgressDialog pd = new ProgressDialog(getApplicationContext());
        pd.setMessage(st1);
        pd.setCanceledOnTouchOutside(false);
        pd.show();
        new Thread(new Runnable() {
            public void run() {
                try {
                    EMContactManager.getInstance().deleteContact(Username);
                    // 删除db和内存中此用户的数据
                    UserDao dao = new UserDao(getApplicationContext());
                    dao.deleteContact(Username);
                    ((DemoHXSDKHelper)HXSDKHelper.getInstance()).getContactList().remove(Username);
                    ((MainActivity) getApplicationContext()).runOnUiThread(new Runnable() {
                        public void run() {
                            pd.dismiss();
                            finish();
                        }
                    });
                } catch (final Exception e) {
                    ((MainActivity) getApplicationContext()).runOnUiThread(new Runnable() {
                        public void run() {
                            pd.dismiss();
                            Toast.makeText(getApplicationContext(), st2 + e.getMessage(), 1).show();
                        }
                    });
                }
            }
        }).start();
    }

	@Override
	public void onClick(View v) {
	    AlertDialog.Builder builder = new Builder(this);
	    final EditText etValue = new EditText(this);
		switch (v.getId()) {
		case R.id.user_head_avatar:
			uploadHeadPhoto();
			break;
		case R.id.ll_editfriendname:
			final EditText editText = new EditText(this);
			new AlertDialog.Builder(this).setTitle(R.string.setting_nickname).setIcon(android.R.drawable.ic_dialog_info).setView(editText)
					.setPositiveButton(R.string.dl_ok, new DialogInterface.OnClickListener() {

						@Override
						public void onClick(DialogInterface dialog, int which) {
							String nickString = editText.getText().toString();
							if (TextUtils.isEmpty(nickString)) {
								Toast.makeText(UserProfileActivity.this, getString(R.string.toast_nick_not_isnull), Toast.LENGTH_SHORT).show();
								return;
							}
							updateRemoteNick(nickString);
						}
					}).setNegativeButton(R.string.dl_cancel, null).show();
			break;
		case R.id.ll_chat:
		    startActivity(new Intent(getApplicationContext(), ChatActivity.class).putExtra("userId", username));
		    break;
		case R.id.ll_delfriend:
		    new AlertDialog.Builder(this).setTitle("确定删除？").setIcon(android.R.drawable.ic_dialog_info)
            .setPositiveButton(R.string.dl_ok, new DialogInterface.OnClickListener() {

                @Override
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        // 删除此联系人
                        deleteContact(username);
                        // 删除相关的邀请消息
                        InviteMessgeDao dao = new InviteMessgeDao(getApplicationContext());
                        dao.deleteMessage(username);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }).setNegativeButton(R.string.dl_cancel, null).show();		   
		    break;
        case R.id.user_sign:
            String sign = tvUserSign.getText().toString();
            etValue.setText(sign);
            builder.setTitle("个人留言(限100字以内)")
            .setView(etValue)
            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    tvUserSign.setText(etValue.getText().toString());
                }
            })
            .setNegativeButton("取消", null)
            .create()
            .show();
            break;
        case R.id.interest:
            String Interest = tvInterest.getText().toString();
            etValue.setText(Interest);
            builder.setTitle("兴趣爱好")
            .setView(etValue)
            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    tvInterest.setText(etValue.getText().toString());
                }
            })
            .setNegativeButton("取消", null)
            .create()
            .show();
            break;            
		case R.id.food:
		    String Foods = tvFoods.getText().toString();
            etValue.setText(Foods);
		    builder.setTitle("最喜欢的食物")
		    .setView(etValue)
		    .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    tvFoods.setText(etValue.getText().toString());
                }
            })
            .setNegativeButton("取消", null)
            .create()
            .show();
		    break;
		case R.id.tour:
            String Places = tvPlaces.getText().toString();
            etValue.setText(Places);
            builder.setTitle("最想去的地方")
            .setView(etValue)
            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    tvPlaces.setText(etValue.getText().toString());
                }
            })
            .setNegativeButton("取消", null)
            .create()
            .show();
		    break;
		case R.id.star:
            String Star = tvStar.getText().toString();
            etValue.setText(Star);
            builder.setTitle("最喜欢的明星")
            .setView(etValue)
            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    tvStar.setText(etValue.getText().toString());
                }
            })
            .setNegativeButton("取消", null)
            .create()
            .show();
		    break;
		case R.id.marjor:
            String Marjor = tvMarjor.getText().toString();
            etValue.setText(Marjor);
            builder.setTitle("专业")
            .setView(etValue)
            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                
                @Override
                public void onClick(DialogInterface arg0, int arg1) {
                    tvMarjor.setText(etValue.getText().toString());
                }
            })
            .setNegativeButton("取消", null)
            .create()
            .show();
		    break;
		case R.id.iv_goto:
		   Intent intent = new Intent(UserProfileActivity.this,
                    UserAlbumListActivity.class);
           intent.putExtra("username", username);
           startActivity(intent);
           break;
		case R.id.save:
		    DialogDemo.show(this,"正在加载，请稍后...");
	        new SaveUserProfileThread(mHandler).start();
		    break;
		default:
			break;
		}

	}
	
	public void asyncFetchUserInfo(String username){
		((DemoHXSDKHelper)HXSDKHelper.getInstance()).asyncGetUserInfo(username, new EMValueCallBack<User>() {
			
			@Override
			public void onSuccess(User user) {
				if (user != null) {
				    Log.e("user","name="+user.getUsername()+";avatar="+user.getAvatar());
//					tvNickName.setText(user.getNick());
					if(!TextUtils.isEmpty(user.getAvatar())){
						 Picasso.with(UserProfileActivity.this).load("file://"+user.getAvatar()).placeholder(R.drawable.default_avatar).into(headAvatar);
					}else{
						Picasso.with(UserProfileActivity.this).load(R.drawable.default_avatar).into(headAvatar);
					}
				}
			}
			
			@Override
			public void onError(int error, String errorMsg) {
                System.out.println("onError");
			}
		});
	}
		
	
	private void uploadHeadPhoto() {
		AlertDialog.Builder builder = new Builder(this);
		builder.setTitle(R.string.dl_title_upload_photo);
		builder.setItems(new String[] { getString(R.string.dl_msg_take_photo), getString(R.string.dl_msg_local_upload) },
				new DialogInterface.OnClickListener() {

					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						switch (which) {
						case 0:
							Toast.makeText(UserProfileActivity.this, getString(R.string.toast_no_support),
									Toast.LENGTH_SHORT).show();
							break;
						case 1:
							Intent pickIntent = new Intent(Intent.ACTION_PICK,null);
							pickIntent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
							startActivityForResult(pickIntent, REQUESTCODE_PICK);
							break;
						default:
							break;
						}
					}
				});
		builder.create().show();
	}
	
	

	private void updateRemoteNick(final String nickName) {
		dialog = ProgressDialog.show(this, getString(R.string.dl_update_nick), getString(R.string.dl_waiting));
		new Thread(new Runnable() {

			@Override
			public void run() {
				boolean updatenick = ((DemoHXSDKHelper)HXSDKHelper.getInstance()).getUserProfileManager().updateParseNickName(nickName);
				if (UserProfileActivity.this.isFinishing()) {
					return;
				}
				if (!updatenick) {
					runOnUiThread(new Runnable() {
						public void run() {
							Toast.makeText(UserProfileActivity.this, getString(R.string.toast_updatenick_fail), Toast.LENGTH_SHORT)
									.show();
							dialog.dismiss();
						}
					});
				} else {
					runOnUiThread(new Runnable() {
						@Override
						public void run() {
							dialog.dismiss();
							Toast.makeText(UserProfileActivity.this, getString(R.string.toast_updatenick_success), Toast.LENGTH_SHORT)
									.show();
							tvNickName.setText(nickName);
						}
					});
				}
			}
		}).start();
	}

	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		switch (requestCode) {
		case REQUESTCODE_PICK:
			if (data == null || data.getData() == null) {
				return;
			}
			startPhotoZoom(data.getData());
			break;
		case REQUESTCODE_CUTTING:
			if (data != null) {
				setPicToView(data);
			}
			break;
		default:
			break;
		}
		super.onActivityResult(requestCode, resultCode, data);
	}

	public void startPhotoZoom(Uri uri) {
		Intent intent = new Intent("com.android.camera.action.CROP");
		intent.setDataAndType(uri, "image/*");
		intent.putExtra("crop", true);
		intent.putExtra("aspectX", 1);
		intent.putExtra("aspectY", 1);
		intent.putExtra("outputX", 300);
		intent.putExtra("outputY", 300);
		intent.putExtra("return-data", true);
		intent.putExtra("noFaceDetection", true);
		startActivityForResult(intent, REQUESTCODE_CUTTING);
	}
	
	/**
	 * save the picture data
	 * 
	 * @param picdata
	 */
	private void setPicToView(Intent picdata) {
		Bundle extras = picdata.getExtras();
		if (extras != null) {
			Bitmap photo = extras.getParcelable("data");
			Drawable drawable = new BitmapDrawable(getResources(), photo);
			headAvatar.setImageDrawable(drawable);
			uploadUserAvatar(Bitmap2InputStream(photo));
		}

	}
	
	private void uploadUserAvatar(final InputStream data) {
		dialog = ProgressDialog.show(this, getString(R.string.dl_update_photo), getString(R.string.dl_waiting));
		new Thread(new Runnable() {

			@Override
			public void run() {
				((DemoHXSDKHelper)HXSDKHelper.getInstance()).uploadUserAvatar(data,new EMValueCallBack<String>() {
		            
		            @Override
		            public void onSuccess(String url) {
                        dialog.dismiss();
                        Toast.makeText(UserProfileActivity.this, getString(R.string.toast_updatephoto_success),
                                Toast.LENGTH_SHORT).show();
		            }
		            
		            @Override
		            public void onError(int error, String errorMsg) {
                        dialog.dismiss();
                        Toast.makeText(UserProfileActivity.this, errorMsg,
                                Toast.LENGTH_SHORT).show();
		                System.out.println("onError");
		            }
		        });

			}
		}).start();

		dialog.show();
	}
	
	
	public byte[] Bitmap2Bytes(Bitmap bm){
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
		return baos.toByteArray();
	}
	
	public InputStream Bitmap2InputStream(Bitmap bm){
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		bm.compress(Bitmap.CompressFormat.PNG, 100, baos);
		InputStream is = new ByteArrayInputStream(baos.toByteArray());  
		return is;
	}
	public void show(){
	    tvNickName.setText(mListData.get(0).get("nick").toString());
	    tvUsername.setText(mListData.get(0).get("name").toString());
	    tvFoods.setText(mListData.get(0).get("foods").toString());
        tvPlaces.setText(mListData.get(0).get("places").toString());
        tvStar.setText(mListData.get(0).get("star").toString());
        tvMarjor.setText(mListData.get(0).get("subject").toString());
	    String avatar = mListData.get(0).get("avatar").toString();
        String nick = mListData.get(0).get("nick").toString();
//	    if(!avatar.equals("")){
//    	    Picasso.with(UserProfileActivity.this).load(avatar).placeholder(R.drawable.default_avatar).into(headAvatar);
//        }else{
//            Picasso.with(UserProfileActivity.this).load(R.drawable.default_avatar).into(headAvatar);
//        }
        User user = new User(username);
        user.setAvatar(avatar);
        user.setNick(nick);
        UserUtils.saveUserInfo(user);
	}

	public void showPic(){
	    if(mAblumData.size()>0)
	        rlPicContainer.setVisibility(View.VISIBLE);
	    
        List<Info> Infos = new ArrayList<Info>();
        for(int i=0;i<mAblumData.size();i++){
            Info info = new Info();
            info.setImage(mAblumData.get(i).get("skin").toString());
            Infos.add(info);
        }
        adapter = new GridImageAdapter(this,Infos,cache);
        gvAlbum.setAdapter(adapter);
	}

    private class GetUserPicThread extends Thread { 
         
        private Handler handler; 
 
        public GetUserPicThread(Handler handler) { 
            this.handler = handler; 
        } 
 
        @Override 
        public void run() {
            String edit = "album_get";
            Map<String, String> params = new HashMap<String, String>();
            params.put("method", edit);
            params.put("user", username);
            ConnServer server = new ConnServer();
            String path = "http://"+getResources().getString(R.string.server)+"/api.php"; 
            mAblumData = server.GetData(path,params,edit,handler);
        }
    }

    private class SaveUserProfileThread extends Thread { 
         
        private Handler handler; 
 
        public SaveUserProfileThread(Handler handler) { 
            this.handler = handler; 
        } 
 
        @Override 
        public void run() {
            String edit = "user_profile_add";
            Map<String, String> params = new HashMap<String, String>();
            params.put("method", edit);
            params.put("user", EMChatManager.getInstance().getCurrentUser());
            params.put("message", tvUserSign.getText().toString());
            params.put("foods", tvFoods.getText().toString());
            params.put("star", tvStar.getText().toString());
            params.put("places", tvPlaces.getText().toString());
            params.put("subject", tvMarjor.getText().toString());
            ConnServer server = new ConnServer();
            String path = "http://"+getResources().getString(R.string.server)+"/api.php"; 
            mReturnData = server.GetData(path,params,edit,handler);
        }
    }
    
    private class GetDataThread extends Thread { 
         
        private Handler handler; 
 
        public GetDataThread(Handler handler) { 
            this.handler = handler; 
        }
 
        @Override 
        public void run() {
            String edit = "user_get";
            Map<String, String> params = new HashMap<String, String>();
            params.put("method", edit);
            params.put("name", username);
            ConnServer server = new ConnServer();
            String path = "http://"+getResources().getString(R.string.server)+"/api.php"; 
            mListData = server.GetData(path,params,edit,handler);
        }
    }

    private Handler mHandler = new Handler(){
        public void handleMessage(android.os.Message msg) {
            DialogDemo.dismiss();
            switch(msg.what){
                case STATE_FINISH:
                    if(msg.obj.toString().equals("user_get")){
                        if(mListData.size()>0)
                            show();
                    }else if(msg.obj.toString().equals("album_get")){
                        if(mAblumData.size()>0)
                            showPic();
                    }else if(msg.obj.toString().equals("user_profile_add")){
                        Toast.makeText(UserProfileActivity.this, "保存成功", 0).show();
                    }
                    break;
                case STATE_ERROR:
                    Toast.makeText(UserProfileActivity.this, "network error", 0).show();
                    break;
                case STATE_FAIL:
                    Toast.makeText(UserProfileActivity.this, msg.obj+"", 0).show();
                    break;
            }
        };
    };    
}
