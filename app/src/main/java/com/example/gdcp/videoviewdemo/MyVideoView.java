package com.example.gdcp.videoviewdemo;

import android.content.Context;
import android.media.MediaPlayer;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.VideoView;

/**
 * Created by Administrator on 2017/10/26 0026.
 */

public class MyVideoView extends VideoView{
    int defaulWidth=1920;
    int defaulHeight=1080;
    public MyVideoView(Context context) {
        super(context);
    }

    public MyVideoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MyVideoView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }





    @Override

    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec,heightMeasureSpec);
        int width = getDefaultSize(defaulWidth, widthMeasureSpec);
        int  height = getDefaultSize(defaulHeight, heightMeasureSpec);
            setMeasuredDimension(width,height);


    }





}
