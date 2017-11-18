package com.example.gdcp.videoviewdemo;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {
    private static final int UPDATE_UI = 1;
    @BindView(R.id.videoPlayer)
    MyVideoView videoPlayer;
    @BindView(R.id.lock_img)
    ImageView lockImg;
    @BindView(R.id.operation_bg)
    ImageView operationBg;
    @BindView(R.id.voice_num)
    ImageView voiceNum;
    @BindView(R.id.framLayout)
    FrameLayout framLayout;
    @BindView(R.id.seebar)
    SeekBar seebar;
    @BindView(R.id.pause)
    ImageView pause;
    @BindView(R.id.time_current_tv)
    TextView timeCurrentTv;
    @BindView(R.id.time_total_tv)
    TextView timeTotalTv;
    @BindView(R.id.left_layout)
    LinearLayout leftLayout;
    @BindView(R.id.voice_img)
    ImageView voiceImg;
    @BindView(R.id.voice_seeBar)
    SeekBar voiceSeeBar;
    @BindView(R.id.screen_img)
    ImageView screenImg;
    @BindView(R.id.control_barlayout)
    LinearLayout controlBarlayout;
    @BindView(R.id.total_relative)
    RelativeLayout totalRelative;
    private boolean isFull=false;
    private int screen_width;
    private int screen_height;
    private AudioManager audioManager;
    private int threshold = 20;   //是否误触的临界值
    private boolean isAdjust=false;
    float lastX = 0, lastY = 0;
    private float screenBrightness;
    private DialogManager dialogManager;
    private Handler uiHandler=new Handler(){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what==UPDATE_UI){
                int totalLength=videoPlayer.getDuration();
                int curPosition=videoPlayer.getCurrentPosition();
                updateTextViewWithTimeFormat(timeTotalTv,totalLength);
                updateTextViewWithTimeFormat(timeCurrentTv,curPosition);
                seebar.setMax(totalLength);
                seebar.setProgress(curPosition);
                uiHandler.sendEmptyMessageDelayed(UPDATE_UI,500);
            }else if(msg.what==2){
                if (controlBarlayout.getVisibility()==View.VISIBLE){
                    controlBarlayout.setVisibility(View.GONE);
                }
            }
        }
    };
    private boolean isHide=false;
    private boolean isSeekTo=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);
        initData();
        initEvent();
    }

    private void initEvent() {
        videoPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
                uiHandler.sendEmptyMessage(UPDATE_UI);
                uiHandler.sendEmptyMessageDelayed(2,5000);
            }
        });
        videoPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                pause.setImageResource(R.mipmap.pause1);
            }
        });

        seebar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                     /* if (fromUser){
                          videoPlayer.seekTo(progress);
                      }*/
                      //updateTextViewWithTimeFormat(timeCurrentTv,progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                uiHandler.removeMessages(UPDATE_UI);
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
               int pos=seebar.getProgress();
               videoPlayer.seekTo(pos);
               uiHandler.sendEmptyMessage(UPDATE_UI);
            }
        });
        voiceSeeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (fromUser){
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
        videoPlayer.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                float x=event.getX();
                float y=event.getY();
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:
                        if(controlBarlayout.getVisibility()==View.GONE ){
                            controlBarlayout.setVisibility(View.VISIBLE);
                            uiHandler.sendEmptyMessageDelayed(2,5000);
                        }else {
                            controlBarlayout.setVisibility(View.GONE);
                            //uiHandler.removeMessages(2);
                        }
                        lastX=x;
                        lastY=y;
                        break;
                    case MotionEvent.ACTION_MOVE:
                        //当前的手指滑动的偏移量
                        float deltaX = x - lastX;
                        float deltaY = y - lastY;
                        float absDetlaX = Math.abs(deltaX);  //偏移量的绝对值
                        float absDetlaY = Math.abs(deltaY);
                        if (absDetlaX>threshold&& absDetlaY > threshold){
                             if (absDetlaX<absDetlaY){
                                 isAdjust=true;
                                 isSeekTo=true;
                             }else {
                                 isAdjust=false;
                                 isSeekTo=false;
                                 //changeVideoViewPosition(deltaX);
                             }
                        }else if (absDetlaX < threshold && absDetlaY > threshold){
                            isAdjust=true;
                            isSeekTo=false;
                        }else if (absDetlaX >threshold && absDetlaY < threshold){
                            isAdjust=false;
                            isSeekTo=true;
                        }/*else if (absDetlaX<threshold && absDetlaY < threshold){
                            isAdjust=false;
                        }*/
                        if (isAdjust&&isFull) {
                        //判断手势合法后   区分手势调节音量,亮度
                        if (!dialogManager.isShowing()) {
                            dialogManager.showRecordingDialog();
                        }
                        if (x < screen_width / 2) {
                            //调节亮度
                            if (deltaY > 0) {
                                //降低亮度
                            } else {

                            }
                            changeBrightness(-deltaY);
                        } else {
                            //调节音量
                            if (deltaY > 0) {
                                //减小音量
                            } else {

                            }
                            changeVolume(-deltaY);

                        }

                    }else {
                            /*if (absDetlaX<absDetlaY) {
                                changeVideoViewPosition(deltaX);
                            }*/
                        /*if (isHide){
                            isHide=!isHide;
                            controlBarlayout.setVisibility(View.VISIBLE);
                        }else {
                            isHide=!isHide;
                            controlBarlayout.setVisibility(View.GONE);
                        }*/
                    }

                    if (isSeekTo){
                        if (!dialogManager.isShowing()) {
                            dialogManager.showRecordingDialog();
                        }
                        changeVideoViewPosition(deltaX);
                    }



                            lastX=x;
                            lastY=y;
                        break;
                    case MotionEvent.ACTION_UP:
                        dialogManager.dimissDialog();
                        isAdjust=false;
                        isSeekTo=false;
                        lastX=0;
                        lastY=0;
                        break;
                }
                return true;
            }
        });

    }
    /*调节视频进度*/
    private void changeVideoViewPosition(float deltaX) {
        int max=videoPlayer.getDuration();
        int cur=videoPlayer.getCurrentPosition();
        int index = (int) (deltaX / screen_height * max);
        int pos = Math.max(cur + index, 0);
        videoPlayer.seekTo(pos);
        int second=pos/1000;
        int hh=second/3600;
        int mm=second%3600/60;
        int ss=second%60;
        String str=null;
        if (hh!=0){
            str=String.format("%02d:%02d:%02d",hh,mm,ss);
        }else {
            str=String.format("%02d:%02d",mm,ss);
        }
        if (deltaX>0){
            dialogManager.showSeekDialog(str);
        }else {
            dialogManager.showBackDialog(str);
        }

    }

    /*调节音量*/
    private void changeVolume(float value) {
        int max = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        int current = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        int index = (int) (value / screen_height * max*3);
        int volume = Math.max(current + index, 0);
        audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, volume, 0);
        voiceSeeBar.setProgress(volume);
        Log.i("volume", "changeVolume: "+volume);
        float xx=(float)volume/max;
        xx=Float.parseFloat(String.format("%.2f", xx));
        int radio= (int) (xx*100);
        if (radio>100){
            radio=100;
        }
        dialogManager.showVolumeDialog(radio+"%");

    }
    /*调节亮度*/
    private void changeBrightness(float value) {
        WindowManager.LayoutParams attributes = getWindow().getAttributes();
        screenBrightness=attributes.screenBrightness;
        float index=value/screen_height;
        screenBrightness=screenBrightness+index;
        if(screenBrightness>1.0f){
            screenBrightness=1.0f;
        }
        if(screenBrightness<0.01f){
            screenBrightness=0.01f;
        }
        Log.i("screenBrightness", "screenBrightness: "+screenBrightness);
        attributes.screenBrightness=screenBrightness;
        getWindow().setAttributes(attributes);
        float xx=(screenBrightness/1);
        xx=Float.parseFloat(String.format("%.2f", xx));
        int radio= (int) (xx*100);
        dialogManager.showBrightnessDialog(radio+"%");
    }

    private void initData() {
        dialogManager=new DialogManager(this);
        Uri videoUri = getIntent().getData();
        if (videoUri == null) { // 正常从当前应用的主界面跳转过来
            videoPlayer.setVideoURI(Uri.parse("http://cdn.can.cibntv.net/12/201702161000/rexuechangan01/rexuechangan01.m3u8"));
        } else {    // 从第三方应用跳转过来
            videoPlayer.setVideoURI(videoUri);
        }
        audioManager = (AudioManager) getSystemService(AUDIO_SERVICE);
        //获取当前设备最大音量值
        int maxVol=audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        //获取当前设备音量值
        int curVol = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        voiceSeeBar.setMax(maxVol);
        voiceSeeBar.setProgress(curVol);
        screen_width=getResources().getDisplayMetrics().widthPixels;
        screen_height = getResources().getDisplayMetrics().heightPixels;
    }







    @OnClick({R.id.lock_img, R.id.pause, R.id.screen_img})
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.lock_img:
                break;
            case R.id.pause:
                if (videoPlayer.isPlaying()){
                    pause.setImageResource(R.mipmap.pause6);
                    videoPlayer.pause();
                    uiHandler.removeMessages(UPDATE_UI);
                }else {
                    pause.setImageResource(R.mipmap.play5);
                    videoPlayer.start();
                    uiHandler.sendEmptyMessage(UPDATE_UI);
                }
                break;
            case R.id.screen_img:
                if(isFull){
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
                    screenImg.setImageResource(R.mipmap.screen);
                }else {
                    setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                    screenImg.setImageResource(R.mipmap.screen_out);
                }
                break;
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        uiHandler.removeMessages(UPDATE_UI);
    }

    private void updateTextViewWithTimeFormat(TextView textView, int millisecond){
           int second=millisecond/1000;
           int hh=second/3600;
           int mm=second%3600/60;
           int ss=second%60;
           String str=null;
           if (hh!=0){
               str=String.format("%02d:%02d:%02d",hh,mm,ss);
           }else {
               str=String.format("%02d:%02d",mm,ss);
           }
           textView.setText(str);
    }

/*监听屏幕方向的改变
* */
    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        //屏幕方向为横向的时候
        if (getResources().getConfiguration().orientation==Configuration.ORIENTATION_LANDSCAPE){
            setVideoViewScale(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            voiceSeeBar.setVisibility(View.VISIBLE);
            voiceImg.setVisibility(View.VISIBLE);
            isFull=true;
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }//屏幕方向为纵向的时候
        else {
            setVideoViewScale(ViewGroup.LayoutParams.MATCH_PARENT, dip2px(MainActivity.this, 240));
            voiceSeeBar.setVisibility(View.GONE);
            voiceImg.setVisibility(View.GONE);
            isFull=false;
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FORCE_NOT_FULLSCREEN);
        }
    }

    private void setVideoViewScale(int width, int height) {
        ViewGroup.LayoutParams layoutParams = videoPlayer.getLayoutParams();
        layoutParams.width = width;
        layoutParams.height = height;
        videoPlayer.setLayoutParams(layoutParams);
        ViewGroup.LayoutParams layoutParams1 = totalRelative.getLayoutParams();
        layoutParams1.width = width;
        layoutParams1.height = height;
        totalRelative.setLayoutParams(layoutParams1);
    }

    //dp转px
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
