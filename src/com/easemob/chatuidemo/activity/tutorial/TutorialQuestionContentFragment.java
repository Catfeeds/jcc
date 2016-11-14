package com.easemob.chatuidemo.activity.tutorial;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.http.Header;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.FragmentManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.Drawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.Window;
import android.view.View.OnClickListener;
import android.view.ViewGroup.LayoutParams;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemLongClickListener;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.PopupMenu;
import android.widget.Spinner;
import android.widget.PopupMenu.OnMenuItemClickListener;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.SimpleAdapter;
import android.widget.TabHost;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.SimpleAdapter.ViewBinder;

import com.baidu.mapapi.cloud.BoundSearchInfo;
import com.easemob.EMError;
import com.easemob.applib.controller.HXSDKHelper;
import com.easemob.chat.EMChatManager;
import com.easemob.chat.EMMessage;
import com.easemob.chat.EMMessage.ChatType;
import com.easemob.chatuidemo.DemoApplication;
import com.easemob.chatuidemo.DemoHXSDKModel;
import com.wenpy.jcc.R;
import com.easemob.chatuidemo.adapter.ArrayWheelAdapter;
import com.easemob.chatuidemo.adapter.GridImageAdapter;
import com.easemob.chatuidemo.adapter.ImageAdapter;
import com.easemob.chatuidemo.adapter.NumericWheelAdapter;
import com.easemob.chatuidemo.adapter.TalentAdapter;
import com.easemob.chatuidemo.adapter.VoicePlayClickListener;
import com.easemob.chatuidemo.domain.Info;
import com.easemob.chatuidemo.task.CommonDownloadTask;
import com.easemob.chatuidemo.utils.CommonUtils;
import com.easemob.chatuidemo.utils.ConnServer;
import com.easemob.chatuidemo.utils.DialogDemo;
import com.easemob.chatuidemo.utils.MD5;
import com.easemob.chatuidemo.widget.OnWheelScrollListener;
import com.easemob.chatuidemo.widget.WheelView;
import com.easemob.exceptions.EaseMobException;
import com.easemob.util.VoiceRecorder;
import com.google.android.gms.internal.da;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;

/**
 * 拍照提问内容
 * 
 */
public class TutorialQuestionContentFragment extends Fragment {

    public static final int REQUEST_CODE_LOCAL = 1;
    private static final int STATE_ERROR = -1; 
    private static final int STATE_FAIL = 0; 
    private static final int STATE_FINISH = 1; 
    private static final int STATE_LINKERR = 2; 
    private static final int STATE_TIMEOUT = 3; 
    private static final int STATE_NULL = 4; 
    private EditText etQuestion1;
    private GridView gvQuestionFile1;
    private EditText etAnswer1;
    private LinearLayout llVoice1;
    private ImageView ibVoice1;
    private Button btRecord1;
    private Button btAnswerSend1;
    private RelativeLayout rlComplaint;
    private EditText etReason;
    private Button btComplaint;    
    private EditText etQuestion2;
    private GridView gvQuestionFile2;
    private RelativeLayout rlAddImage;
    private ImageView ivAddImage;
    private Button btSend;
    private EditText etAnswer2;
    private LinearLayout llVoice2;
    private ImageView ibVoice2;
    private Button btRecord2;
    private Button btAnswerSend2;
    private LinearLayout llQuestion1;
    private LinearLayout llQuestion2;
    private LinearLayout llAnswer1;
    private LinearLayout llAnswer2;
    private List<Map<String, Object>> mListData;
    private List<Map<String, Object>> mPicData;
    private List<Info> mGvImageData1;
    private List<Info> mGvImageData2;
    private String m_strPicUrl;
    private String m_strVioceUrl = "";
    private int mPicCount = 0;
    private ImageAdapter adapter1;
    private ImageAdapter adapter2;
    private String pathImage;  
    private String mID;
    private String sVoiceFile1 = "";
    private String sVoiceFile2 = "";
    private String sQuestionFile1 = "";
    private String sQuestionFile2 = "";
    private MediaPlayer mediaPlayer;
    private File cache;
    private int mDegree = 0;
    private boolean bPlaySt = false;
    private SimpleAdapter adapter;
    private Drawable[] micImages;
    private ImageView micImage;
    private VoiceRecorder voiceRecorder;
    private View recordingContainer;
    private TextView recordingHint;
    private PowerManager.WakeLock wakeLock;
    private Handler micImageHandler = new Handler() {
        @Override
        public void handleMessage(android.os.Message msg) {
            // 切换msg切换图片
            micImage.setImageDrawable(micImages[msg.what]);
        }
    };
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        return inflater.inflate(R.layout.fragment_question_content, container, false);
    }
    
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
				
        mListData = new ArrayList<Map<String,Object>>();
        mPicData = new ArrayList<Map<String,Object>>();
        wakeLock = ((PowerManager) getActivity().getSystemService(Context.POWER_SERVICE)).newWakeLock(
                PowerManager.SCREEN_DIM_WAKE_LOCK, "demo");
        
        etQuestion1 = (EditText)getActivity().findViewById(R.id.question1);
        gvQuestionFile1 = (GridView)getActivity().findViewById(R.id.gv_question_file1);
        etAnswer1 = (EditText)getActivity().findViewById(R.id.answer1);
        llVoice1 = (LinearLayout)getActivity().findViewById(R.id.ll_voice1);
        ibVoice1 = (ImageView)getActivity().findViewById(R.id.voice1);
        btRecord1 = (Button)getActivity().findViewById(R.id.record1);
        btAnswerSend1 = (Button)getActivity().findViewById(R.id.answersend1);
        rlComplaint = (RelativeLayout)getActivity().findViewById(R.id.rl_complaint);
        etReason = (EditText)getActivity().findViewById(R.id.reason);
        btComplaint = (Button)getActivity().findViewById(R.id.complaint);
        rlAddImage = (RelativeLayout)getActivity().findViewById(R.id.rl_addimage);
        etQuestion2 = (EditText)getActivity().findViewById(R.id.question2);
        gvQuestionFile2 = (GridView)getActivity().findViewById(R.id.gv_question_file2);
        ivAddImage = (ImageView)getActivity().findViewById(R.id.addimage);
        btSend = (Button)getActivity().findViewById(R.id.send);
        etAnswer2 = (EditText)getActivity().findViewById(R.id.answer2);
        llVoice2 = (LinearLayout)getActivity().findViewById(R.id.ll_voice2);
        ibVoice2 = (ImageView)getActivity().findViewById(R.id.voice2);
        btRecord2 = (Button)getActivity().findViewById(R.id.record2);
        btAnswerSend2 = (Button)getActivity().findViewById(R.id.answersend2);
        llQuestion1 = (LinearLayout)getActivity().findViewById(R.id.ll_question1);
        llQuestion2 = (LinearLayout)getActivity().findViewById(R.id.ll_question2);
        llAnswer1 = (LinearLayout)getActivity().findViewById(R.id.ll_answer1);
        llAnswer2 = (LinearLayout)getActivity().findViewById(R.id.ll_answer2);
        

        recordingContainer = getActivity().findViewById(R.id.recording_container);
        recordingHint = (TextView)getActivity().findViewById(R.id.recording_hint);
        micImage = (ImageView) getActivity().findViewById(R.id.mic_image);
        // 动画资源文件,用于录制语音时
        micImages = new Drawable[] { getResources().getDrawable(R.drawable.record_animate_01),
                getResources().getDrawable(R.drawable.record_animate_02),
                getResources().getDrawable(R.drawable.record_animate_03),
                getResources().getDrawable(R.drawable.record_animate_04),
                getResources().getDrawable(R.drawable.record_animate_05),
                getResources().getDrawable(R.drawable.record_animate_06),
                getResources().getDrawable(R.drawable.record_animate_07),
                getResources().getDrawable(R.drawable.record_animate_08),
                getResources().getDrawable(R.drawable.record_animate_09),
                getResources().getDrawable(R.drawable.record_animate_10),
                getResources().getDrawable(R.drawable.record_animate_11),
                getResources().getDrawable(R.drawable.record_animate_12),
                getResources().getDrawable(R.drawable.record_animate_13),
                getResources().getDrawable(R.drawable.record_animate_14) 
                };
        
        voiceRecorder = new VoiceRecorder(micImageHandler);
        
        btRecord1.setOnTouchListener(new PressToSpeakListen());
        btRecord2.setOnTouchListener(new PressToSpeakListen());
        
        btComplaint.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                //progressBar.setVisibility(View.VISIBLE);
                if(etReason.getText().toString().equals("")){
                    Toast.makeText(getActivity(), "投诉理由不能为空", 0).show();
                    return;
                }
                
               DialogDemo.show(getActivity(),"正在提交，请稍后...");
               new Thread() {             
                   @Override 
                   public void run() { 
                       String edit = "tutorial_complaint_add";
                       Map<String, String> params = new HashMap<String, String>();
                       params.put("method", edit);
                       params.put("complaint", etReason.getText().toString());
                       params.put("id", mID);
                       
                       ConnServer server = new ConnServer();
                       String path = "http://"+getResources().getString(R.string.server)+"/api.php";
                       server.GetData(path,params,edit,handler);
                   }
               }.start();
            }
        });
		btSend.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                //progressBar.setVisibility(View.VISIBLE);
                if(etQuestion2.getText().toString().equals("") && mGvImageData1.size() ==0){
                    Toast.makeText(getActivity(), "提问问题不能为空", 0).show();
                    return;
                }
                
              DialogDemo.show(getActivity(),"正在提交，请稍后...");
              String url = "http://"+getResources().getString(R.string.server)+"/api.php";
              ConnServer server = new ConnServer();
              m_strPicUrl = "";
//              if(mID.equals("")){
//                  if(mGvImageData1.size()>0){
//                      mPicCount = mGvImageData1.size() -1;
//                       try {
//                           for(int i=1;i<mGvImageData1.size();i++){
//                               server.uploadFile(mGvImageData1.get(i).getImage() , url, handler);
//                           }
//                       } catch (Exception e) {
//                           e.printStackTrace();
//                           //progressBar.setVisibility(View.GONE);
//                       }
//                    }
//                }else
                {
                    if(mPicData.size()>0){
                        mPicCount = mPicData.size();
                         try {
                             for(int i=0;i<mPicData.size();i++){
                                 server.uploadFile(mPicData.get(i).get("url").toString() , url, handler);
                             }
                         } catch (Exception e) {
                             e.printStackTrace();
                             //progressBar.setVisibility(View.GONE);
                         }
                      }else
                          new SaveQuestionThread(handler).start();                          
                }
            }
        });
        
		btAnswerSend1.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                //progressBar.setVisibility(View.VISIBLE);
                if(etAnswer1.getText().toString().equals("") && sVoiceFile1.equals("")){
                    Toast.makeText(getActivity(), "回答问题不能为空", 0).show();
                    return;
                }
                
              DialogDemo.show(getActivity(),"正在提交，请稍后...");
              String url = "http://"+getResources().getString(R.string.server)+"/api.php";
              ConnServer server = new ConnServer();
              m_strPicUrl = "";
              if(!sVoiceFile1.equals("")){
                  mPicCount = 1;
                  try {
                     server.uploadFile(sVoiceFile1 , url, handler);
                  }
                  catch (Exception e) {
                     e.printStackTrace();
                 }
              }else
                  new SaveAnswerThread(handler).start();
            }
        });
        btAnswerSend2.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                //progressBar.setVisibility(View.VISIBLE);
                if(etAnswer2.getText().toString().equals("") && sVoiceFile2.equals("")){
                    Toast.makeText(getActivity(), "回答问题不能为空", 0).show();
                    return;
                }
                
              DialogDemo.show(getActivity(),"正在提交，请稍后...");
              String url = "http://"+getResources().getString(R.string.server)+"/api.php";
              ConnServer server = new ConnServer();
              m_strPicUrl = "";
              if(!sVoiceFile2.equals("")){
                  mPicCount = 1;
                  try {
                     server.uploadFile(sVoiceFile2 , url, handler);
                  }
                  catch (Exception e) {
                     e.printStackTrace();
                 }
              }else
                  new SaveAnswerThread(handler).start();
            }
        });
		gvQuestionFile1.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View v, int position,
                    long id) {
                Intent intent = new Intent(Intent.ACTION_VIEW);
                String path = mGvImageData1.get(position).getImage();
                String name = MD5.getMD5(path) + path.substring(path.lastIndexOf(".")); 
                String url = cache.getAbsolutePath()+"/"+name;
                Log.e("img","imgurl="+url);
                
                intent.setDataAndType(Uri.parse("file://"+url), "image/*");
                startActivity(intent);
            }
        });
        ivAddImage.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View v) {
                selectPicFromLocal();
                
            }
        });
        
         /* 播放 */  
        ibVoice1.setOnClickListener(new ImageButton.OnClickListener() {  
            @Override  
            public void onClick(View arg0) {  
                // TODO Auto-generated method stub  
                if (!sVoiceFile1.equals("") && !bPlaySt) {  
                    /* 打开播放程序 */  
                    //openFile(iPlayFile);  
                    playVoice(sVoiceFile1,ibVoice1);
                }  
            }  
        });  
        ibVoice2.setOnClickListener(new ImageButton.OnClickListener() {  
            @Override  
            public void onClick(View arg0) {  
                // TODO Auto-generated method stub  
                if (!sVoiceFile2.equals("")) {  
                    /* 打开播放程序 */  
                    //openFile(iPlayFile);  
                    playVoice(sVoiceFile2, ibVoice2);
                }  
            }  
        });
        gvQuestionFile2.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View v, int position,
                    long id) {
                Intent intent = new Intent(Intent.ACTION_VIEW);    
                String path = mGvImageData2.get(position).getImage();
                String name = MD5.getMD5(path) + path.substring(path.lastIndexOf(".")); 
                String url = cache.getAbsolutePath()+"/"+name;
                Log.e("img","imgurl="+url);
                
                intent.setDataAndType(Uri.parse("file://"+url), "image/*");
                startActivity(intent);
            }
        });
        gvQuestionFile2.setOnItemLongClickListener(new OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> arg0, View arg1,
                    int arg2, long arg3) {
                mPicData.remove(arg2);
                adapter.notifyDataSetChanged();
                return false;
            }
        });
        cache = new File(Environment.getExternalStorageDirectory(), "cache");
        
        if(!cache.exists()){
            cache.mkdirs();
        }
        
        if(!mID.equals("")){
            DialogDemo.show(getActivity(),"正在加载，请稍后...");
            new GetDataThread(handler).start();
        }
	}

    public void playVoice(String filePath, final ImageView ivVoice) {
        if (!(new File(filePath).exists())) {
            return;
        }
      
        AudioManager audioManager = (AudioManager) getActivity().getSystemService(Context.AUDIO_SERVICE);

        mediaPlayer = new MediaPlayer();
        if (HXSDKHelper.getInstance().getModel().getSettingMsgSpeaker()) {
            audioManager.setMode(AudioManager.MODE_NORMAL);
            audioManager.setSpeakerphoneOn(true);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_RING);
        } else {
            audioManager.setSpeakerphoneOn(false);// 关闭扬声器
            // 把声音设定成Earpiece（听筒）出来，设定为正在通话中
            audioManager.setMode(AudioManager.MODE_IN_CALL);
            mediaPlayer.setAudioStreamType(AudioManager.STREAM_VOICE_CALL);
        }
        try {
            mediaPlayer.setDataSource(filePath);
            mediaPlayer.prepare();
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {

                @Override
                public void onCompletion(MediaPlayer mp) {
                    // TODO Auto-generated method stub
                    mediaPlayer.release();
                    mediaPlayer = null;
                    bPlaySt = false;
                    //stopPlayVoice(); // stop animation
                    ivVoice.setImageResource(R.drawable.chatfrom_voice_playing);
                }

            });
            mediaPlayer.start();
            bPlaySt = true;
            showAnimation(ivVoice);
        } catch (Exception e) {
        }
    }
    /**
     * 按住说话listener
     * 
     */
    class PressToSpeakListen implements View.OnTouchListener {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                if (!CommonUtils.isExitsSdcard()) {
                    String st4 = getResources().getString(R.string.Send_voice_need_sdcard_support);
                    Toast.makeText(getActivity(), st4, Toast.LENGTH_SHORT).show();
                    return false;
                }
                try {
                    v.setPressed(true);
                    wakeLock.acquire();
                    if (VoicePlayClickListener.isPlaying)
                        VoicePlayClickListener.currentPlayListener.stopPlayVoice();
                    recordingContainer.setVisibility(View.VISIBLE);
                    recordingHint.setText(getString(R.string.move_up_to_cancel));
                    recordingHint.setBackgroundColor(Color.TRANSPARENT);
                    voiceRecorder.startRecording(null, EMChatManager.getInstance().getCurrentUser(), getActivity());
                } catch (Exception e) {
                    e.printStackTrace();
                    v.setPressed(false);
                    if (wakeLock.isHeld())
                        wakeLock.release();
                    if (voiceRecorder != null)
                        voiceRecorder.discardRecording();
                    recordingContainer.setVisibility(View.INVISIBLE);
                    Toast.makeText(getActivity(), R.string.recoding_fail, Toast.LENGTH_SHORT).show();
                    return false;
                }

                return true;
            case MotionEvent.ACTION_MOVE: {
                if (event.getY() < 0) {
                    recordingHint.setText(getString(R.string.release_to_cancel));
                    recordingHint.setBackgroundResource(R.drawable.recording_text_hint_bg);
                } else {
                    recordingHint.setText(getString(R.string.move_up_to_cancel));
                    recordingHint.setBackgroundColor(Color.TRANSPARENT);
                }
                return true;
            }
            case MotionEvent.ACTION_UP:
                v.setPressed(false);
                recordingContainer.setVisibility(View.INVISIBLE);
                if (wakeLock.isHeld())
                    wakeLock.release();
                if (event.getY() < 0) {
                    // discard the recorded audio.
                    voiceRecorder.discardRecording();

                } else {
                    // stop recording and send voice file
                    String st1 = getResources().getString(R.string.Recording_without_permission);
                    String st2 = getResources().getString(R.string.The_recording_time_is_too_short);
                    String st3 = getResources().getString(R.string.send_failure_please);
                    try {
                        int length = voiceRecorder.stopRecoding();
                        if (length > 0) {
//                            sendVoice(voiceRecorder.getVoiceFilePath(), voiceRecorder.getVoiceFileName(toChatUsername),
//                                    Integer.toString(length), false);
                            if(mDegree == 1){
                                sVoiceFile1 = voiceRecorder.getVoiceFilePath();
                                ibVoice1.setVisibility(View.VISIBLE);
                            }else{
                                sVoiceFile2 = voiceRecorder.getVoiceFilePath();
                                ibVoice2.setVisibility(View.VISIBLE);
                            }
                             
                            Log.e("VoiceFilePath","VoiceFilePath="+sVoiceFile1);
                            
                        } else if (length == EMError.INVALID_FILE) {
                            Toast.makeText(getActivity(), st1, Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), st2, Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        Toast.makeText(getActivity(), st3, Toast.LENGTH_SHORT).show();
                    }

                }
                return true;
            default:
                recordingContainer.setVisibility(View.INVISIBLE);
                if (voiceRecorder != null)
                    voiceRecorder.discardRecording();
                return false;
            }
        }
    }
    
    // show the voice playing animation
    private void showAnimation(ImageView ivVoice) {
        ivVoice.setImageResource(R.anim.voice_from_icon);
        AnimationDrawable voiceAnimation = (AnimationDrawable) ivVoice.getDrawable();
        voiceAnimation.start();
    }
    
	public void show(){
	    if(mListData.size()==0)
	        return;
	    etQuestion1.setText(mListData.get(0).get("question1").toString());
	    etAnswer1.setText(mListData.get(0).get("answer1").toString());
	    etQuestion2.setText(mListData.get(0).get("question2").toString());
        etAnswer2.setText(mListData.get(0).get("answer2").toString());
        if(!mListData.get(0).get("complaint").toString().equals("")){
            etReason.setText(mListData.get(0).get("complaint").toString());
            etReason.setEnabled(false);
            btComplaint.setEnabled(false);
        }
        String qfile1 = mListData.get(0).get("question_file1").toString();
        String qfile2 = mListData.get(0).get("question_file2").toString();
        if(!qfile1.equals("")){
            gvQuestionFile1.setVisibility(View.VISIBLE);
        }
        if(!qfile2.equals("")){
            gvQuestionFile2.setVisibility(View.VISIBLE);
        }
        String[] aPics1 = qfile1.split(",");
        mGvImageData1 = new ArrayList<Info>();
        for(int i=0;i<aPics1.length;i++){
            Info info = new Info();
            info.setImage(aPics1[i]);
            mGvImageData1.add(info);
        }
        adapter1 = new ImageAdapter(getActivity(),mGvImageData1,cache);
        gvQuestionFile1.setAdapter(adapter1);
        
        String[] aPics2 = qfile2.split(",");
        mGvImageData2 = new ArrayList<Info>();
        for(int i=0;i<aPics2.length;i++){
            Info info = new Info();
            info.setImage(aPics2[i]);
            mGvImageData2.add(info);
        }
        adapter2 = new ImageAdapter(getActivity(),mGvImageData2,cache);
        gvQuestionFile2.setAdapter(adapter2);
        
        String path1 = mListData.get(0).get("answer_file1").toString();
        String path2 = mListData.get(0).get("answer_file2").toString();
        if(!path1.equals("")){
            String name = MD5.getMD5(path1) + path1.substring(path1.lastIndexOf("."));  
            getBillVoices(handler,STATE_FINISH,  "voice1", path1, name);
        }
        if(!path2.equals("")){
            String name = MD5.getMD5(path2) + path2.substring(path2.lastIndexOf("."));  
            getBillVoices(handler,STATE_FINISH, "voice2", path2,  name);
        }
        mDegree = Integer.valueOf(mListData.get(0).get("degree").toString());
        SetPermission(mDegree);
	}
		
	public void setID(String id){
	    mID = id;
	}

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_LOCAL & data != null) { // 发送本地图片
            Uri uri = data.getData();  
            if (!TextUtils.isEmpty(uri.getAuthority())) {  
                //查询选择图片  
                Cursor cursor = getActivity().getContentResolver().query(  
                        uri,  
                        new String[] { MediaStore.Images.Media.DATA },  
                        null,   
                        null,   
                        null);  
                //返回 没找到选择图片  
                if (null == cursor) {  
                    return;  
                }  
                //光标移动至开头 获取图片路径  
                cursor.moveToFirst();  
                pathImage = cursor.getString(cursor  
                        .getColumnIndex(MediaStore.Images.Media.DATA));  
            }
        } 
    }
	/**
     * 从图库获取图片
     */
    public void selectPicFromLocal() {
        Intent intent;
        if (Build.VERSION.SDK_INT < 19) {
            intent = new Intent(Intent.ACTION_GET_CONTENT);
            intent.setType("image/*");

        } else {
            intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        }
        startActivityForResult(intent, REQUEST_CODE_LOCAL);
    }
	
    private class GetDataThread extends Thread { 
         
        private Handler handler; 
 
        public GetDataThread(Handler handler) { 
            this.handler = handler; 
        } 
 
        @Override 
        public void run() { 
            String edit = "tutorial_question_get";
            Map<String, String> params = new HashMap<String, String>();
            params.put("method", edit);
            params.put("type", "");
            params.put("user", EMChatManager.getInstance().getCurrentUser());
            params.put("id", mID);
            
            ConnServer server = new ConnServer();
            String path = "http://"+getResources().getString(R.string.server)+"/api.php";
            mListData = server.GetData(path,params,edit,handler);
        }
    }

    private class SaveQuestionThread extends Thread { 
         
        private Handler handler; 
 
        public SaveQuestionThread(Handler handler) { 
            this.handler = handler; 
        } 
 
        @Override 
        public void run() { 
            String edit = "tutorial_question_add";
            Map<String, String> params = new HashMap<String, String>();
            params.put("method", edit);
            params.put("question", etQuestion2.getText().toString());
            params.put("file", m_strPicUrl);
            params.put("user", EMChatManager.getInstance().getCurrentUser());
            params.put("id", mID);
            
            ConnServer server = new ConnServer();
            String path = "http://"+getResources().getString(R.string.server)+"/api.php";
            mListData = server.GetData(path,params,edit,handler);
        }
    }

    private class SaveAnswerThread extends Thread { 
         
        private Handler handler; 
 
        public SaveAnswerThread(Handler handler) { 
            this.handler = handler; 
        } 
 
        @Override 
        public void run() { 
            String edit = "tutorial_answer_add";
            Map<String, String> params = new HashMap<String, String>();
            params.put("method", edit);
            if(mDegree == 1){
                params.put("answer", etAnswer1.getText().toString());
                params.put("file", sVoiceFile1);
            }
            else{
                params.put("answer", etAnswer2.getText().toString());
                params.put("file", sVoiceFile2);
            }
            
            params.put("user", EMChatManager.getInstance().getCurrentUser());
            params.put("id", mID);
            
            ConnServer server = new ConnServer();
            String path = "http://"+getResources().getString(R.string.server)+"/api.php";
            mListData = server.GetData(path,params,edit,handler);
        }
    }
    
    public void getBillVoices(Handler hander, int what, String obj, String mFilePath, String fileName) {
        CommonDownloadTask task = new CommonDownloadTask(hander,  mFilePath,  what, obj);
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB) {
            task.execute(fileName);
        } else {
            task.executeOnExecutor(AsyncTask.SERIAL_EXECUTOR, fileName);
        }
    }
    
    private void SetPermission(int degree){
        System.out.println("degree="+degree);
        int Identity = DemoApplication.getInstance().getIdentity();
        switch(mDegree){
        case 1:
            for(int i=0;i<llQuestion1.getChildCount();i++){
                View v = llQuestion1.getChildAt(i);
                v.setEnabled(false);
            }
            if(Identity ==1)
                llAnswer1.setVisibility(View.GONE);
            else{
                llVoice1.setVisibility(View.VISIBLE);
                ibVoice1.setVisibility(View.GONE);
            }
            llQuestion2.setVisibility(View.GONE);
            llAnswer2.setVisibility(View.GONE);
            break;
        case 2:
            for(int i=0;i<llQuestion1.getChildCount();i++){
                View v = llQuestion1.getChildAt(i);
                v.setEnabled(false);
            }
            for(int i=0;i<llAnswer1.getChildCount();i++){
                View v = llAnswer1.getChildAt(i);
                v.setEnabled(false);
            }
            btRecord1.setVisibility(View.GONE);
            btAnswerSend1.setVisibility(View.GONE);
            gvQuestionFile1.setEnabled(true);
            ibVoice1.setEnabled(true);
            if(Identity !=1)
                llQuestion2.setVisibility(View.GONE);
            llAnswer2.setVisibility(View.GONE);
            break;
        case 3:
            for(int i=0;i<llQuestion1.getChildCount();i++){
                View v = llQuestion1.getChildAt(i);
                v.setEnabled(false);
            }
            for(int i=0;i<llAnswer1.getChildCount();i++){
                View v = llAnswer1.getChildAt(i);
                v.setEnabled(false);
            }
            for(int i=0;i<llQuestion2.getChildCount();i++){
                View v = llQuestion2.getChildAt(i);
                v.setEnabled(false);
            }
            btRecord1.setVisibility(View.GONE);
            btAnswerSend1.setVisibility(View.GONE);
            gvQuestionFile1.setEnabled(true);
            gvQuestionFile2.setEnabled(true);
            ibVoice1.setEnabled(true);
            if(Identity ==1){
                rlAddImage.setVisibility(View.GONE);
                llAnswer2.setVisibility(View.GONE);
            }
            else{
                rlComplaint.setVisibility(View.GONE);
                rlAddImage.setVisibility(View.GONE);
                llVoice2.setVisibility(View.VISIBLE);
                ibVoice2.setVisibility(View.GONE);
            }
            break;
        case 4:
            for(int i=0;i<llQuestion1.getChildCount();i++){
                View v = llQuestion1.getChildAt(i);
                v.setEnabled(false);
            }
            for(int i=0;i<llAnswer1.getChildCount();i++){
                View v = llAnswer1.getChildAt(i);
                v.setEnabled(false);
            }
            for(int i=0;i<llQuestion2.getChildCount();i++){
                View v = llQuestion2.getChildAt(i);
                v.setEnabled(false);
            }
            for(int i=0;i<llAnswer2.getChildCount();i++){
                View v = llAnswer2.getChildAt(i);
                v.setEnabled(false);
            }
            btRecord1.setVisibility(View.GONE);
            btAnswerSend1.setVisibility(View.GONE);
            btRecord2.setVisibility(View.GONE);
            btAnswerSend2.setVisibility(View.GONE);
            gvQuestionFile1.setEnabled(true);
            gvQuestionFile2.setEnabled(true);
            ibVoice1.setEnabled(true);
            ibVoice2.setEnabled(true);
            if(Identity ==1){
                rlAddImage.setVisibility(View.GONE);
            }
            else{
                rlComplaint.setVisibility(View.GONE);
                rlAddImage.setVisibility(View.GONE);
            }
        }
    }

    private final Handler handler = new Handler(Looper.getMainLooper()) {

        public void handleMessage(Message msg) { // 处理Message，更新ListView
            if(!msg.obj.toString().equals("upload_pic"))
                DialogDemo.dismiss();
            switch(msg.what){ 
                case STATE_FINISH: 
                {
                    System.out.println("msg.obj:"+msg.obj);
                    if(msg.obj.toString().equals("tutorial_question_get")){
                        show();
                    }else if(msg.obj.toString().equals("voice1")){
                        Bundle bundle = msg.getData();
                        String response = bundle.getString("response");
                        if(!response.equals("")){
                            llVoice1.setVisibility(View.VISIBLE);
                            btRecord1.setVisibility(View.GONE);
                            btAnswerSend1.setVisibility(View.GONE);
                        }
                        sVoiceFile1 = response;
                    }else if(msg.obj.toString().equals("voice2")){
                        Bundle bundle = msg.getData();
                        String response = bundle.getString("response");
                        if(!response.equals("")){
                            llVoice2.setVisibility(View.VISIBLE);
                            btRecord2.setVisibility(View.GONE);
                            btAnswerSend2.setVisibility(View.GONE);
                        }
                        sVoiceFile2 = response;
                    }else if(msg.obj.toString().equals("tutorial_complaint_add")){
                        Toast.makeText(getActivity(), "投诉成功", 0).show();
                        etReason.setEnabled(false);
                        btComplaint.setEnabled(false);
                    }else if(msg.obj.toString().equals("upload_pic")){
                        Bundle data = msg.getData();
                        m_strPicUrl = m_strPicUrl+ data.getString("url")+",";
                        mPicCount--;
                        if(mPicCount == 0){
                            if(m_strPicUrl.length() > 1)
                                m_strPicUrl.substring(0, m_strPicUrl.length()-1);
                            if(mDegree == 2)
                                new SaveQuestionThread(handler).start();
                            else
                                new SaveAnswerThread(handler).start();
                        }
                    }else if(msg.obj.toString().equals("tutorial_question_add")){
                        Toast.makeText(getActivity(), "提交成功", 1).show();
                        getFragmentManager().popBackStack();
                    }else if(msg.obj.toString().equals("tutorial_answer_add")){
                        Toast.makeText(getActivity(), "提交成功", 1).show();
                        getFragmentManager().popBackStack();
                    }
                }
                    break;
                case STATE_FAIL:
                {
                    String info = "Error!";
                    if(msg.obj != null){
                        info = info+"\n"+msg.obj;
                    }
                    Toast.makeText(getActivity(), info, 1).show();
                    break;
                }
                case STATE_NULL:
                    //Toast.makeText(getActivity().getApplicationContext(), getResources().getString(R.string.datanull),Toast.LENGTH_SHORT).show(); 
                    break;
                case STATE_LINKERR:
                {
                    String type = msg.getData().getString("type");
                    Toast.makeText(getActivity(), "网络连接失败",Toast.LENGTH_SHORT) .show();  
                    break;
                }
                case STATE_ERROR: 
                    Toast.makeText(getActivity(), "网络连接错误",Toast.LENGTH_SHORT) .show();  
                    break;
               default:  
            } 
        } 
    }; 

    //刷新图片
    @Override
    public void onResume() {
        super.onResume();
        if(!TextUtils.isEmpty(pathImage)){
            gvQuestionFile2.setVisibility(View.VISIBLE);
            if( mPicData.size() == 2) {
                Toast.makeText(getActivity(), "图片数2张已满", Toast.LENGTH_SHORT).show();
                return;
            }
            Bitmap addbmp=BitmapFactory.decodeFile(pathImage);
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("image", addbmp);
            map.put("url", pathImage);
            mPicData.add(map);
            if(mPicData.size()>0)
                gvQuestionFile1.setVisibility(View.VISIBLE);
            adapter = new SimpleAdapter(getActivity(), 
                    mPicData, R.layout.gridview_image_no_title, 
                    new String[] { "image"}, new int[] { R.id.iv_image}); 
            adapter.setViewBinder(new ViewBinder() {  
                @Override  
                public boolean setViewValue(View view, Object data,  
                        String textRepresentation) {  
                    // TODO Auto-generated method stub  
                    if(view instanceof ImageView && data instanceof Bitmap){  
                        ImageView i = (ImageView)view;  
                        i.setImageBitmap((Bitmap) data);  
                        return true;  
                    }  
                    return false;  
                }
            }); 
            gvQuestionFile2.setAdapter(adapter);
            adapter.notifyDataSetChanged();
            //刷新后释放防止手机休眠后自动添加
            pathImage = null;
        }
    }
    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        if(mediaPlayer!=null){
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
