package com.example.gdcp.videoviewdemo;

import android.app.Dialog;
import android.content.Context;
import android.media.MediaMetadata;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * Created by asus- on 2017/3/19.
 */

public class DialogManager {
    private Dialog dialog;
    private ImageView mIcon;
    private Context mContext;
    private TextView message;

    public DialogManager(Context mContext) {
        this.mContext = mContext;
    }

    public void showRecordingDialog() {
        dialog = new Dialog(mContext, R.style.Theme_AudioDialog);
        View view = LayoutInflater.from(mContext).inflate(R.layout.toast, null);
        dialog.setContentView(view);
        mIcon = (ImageView) view.findViewById(R.id.imageView);
        message= (TextView) view.findViewById(R.id.message);
        dialog.show();
    }

    public boolean isShowing(){
        if (dialog!=null){
        return dialog.isShowing();
        }
        return false;
    }

    public void showBrightnessDialog(String text) {
        if (dialog != null && dialog.isShowing()) {
            mIcon.setImageResource(R.mipmap.light3);
            message.setText(text);
        }
    }

    public void showSeekDialog(String text) {
        if (dialog != null && dialog.isShowing()) {
            mIcon.setImageResource(R.mipmap.seekto);
            message.setText(text);
        }
    }
    public void showBackDialog(String text) {
        if (dialog != null && dialog.isShowing()) {
            mIcon.setImageResource(R.mipmap.back);
            message.setText(text);
        }
    }


    public void showVolumeDialog(String text) {
        if (dialog != null && dialog.isShowing()) {
            mIcon.setImageResource(R.mipmap.voice5);
            message.setText(text);
        }
    }



    public void dimissDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
            dialog = null;
        }
    }


}
