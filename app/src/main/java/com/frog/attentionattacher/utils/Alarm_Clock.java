package com.frog.attentionattacher.utils;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.drm.DrmManagerClient;
import android.media.MediaDrm;
import android.os.SystemClock;
import android.view.View;
import android.view.animation.LinearInterpolator;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ProgressBar;
import android.widget.TimePicker;

import com.frog.attentionattacher.AlarmActivity;
import com.shawnlin.numberpicker.NumberPicker;


import java.util.Calendar;
import java.util.TimeZone;

/* A Alarm_Clock wheel
*  Framed by Green-Wood
*
*  创建实例时需要传入一个ProgressBar和一个Chronometer
*  pickTimeAndStarted方法会返回所选择的时间（毫秒），需要context和AlarmManager*/

public class Alarm_Clock {
    private Chronometer chronometer;
    private ProgressBar progressBar;
    private NumberPicker numberPicker;
    private ValueAnimator valueAnimator;
    private static final int MAX_PROGRESS = 10000;
    private long selectTime;
    private Context context;
    private long pauseTime;

    public Alarm_Clock (Chronometer chronometer, ProgressBar progressBar, Context context,
                        NumberPicker numberPicker){
        this.chronometer = chronometer;
        this.progressBar = progressBar;
        this.context = context;
        this.numberPicker = numberPicker;
    }


    public long startCounting(final int numberChoosed, Button startButton, Button stopButton, Button cancelButton){
        numberPicker.setVisibility(View.INVISIBLE);
        chronometer.setVisibility(View.VISIBLE);
        if (numberChoosed == 1) selectTime = 60 * 1000;
        else selectTime = (numberChoosed - 1) * 5 * 60 * 1000;
        ToastUtil.showToast(context, "设置成功");

        chronometer.setBase(SystemClock.elapsedRealtime() + selectTime);  // 设置倒计时
        chronometer.setCountDown(true);
        chronometer.start();
        initProgressBar(selectTime, startButton, stopButton, cancelButton);

        return selectTime;
    }

    // 初始化一个ProgressBar，并在结束时停止chronometer
    private void initProgressBar(long duration, final Button startButton,
                                 final Button stopButton, final Button cancelButton) {
        progressBar.setMax(MAX_PROGRESS);
        progressBar.setProgress(0);

        valueAnimator = ValueAnimator.ofInt(0, MAX_PROGRESS);
        valueAnimator.setDuration(duration);
        valueAnimator.setInterpolator(new LinearInterpolator());
        valueAnimator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                int value = (int) valueAnimator.getAnimatedValue();
                progressBar.setProgress(value);
                if (value == MAX_PROGRESS) {
                    chronometer.stop();
                    progressBar.setProgress(0);
                    chronometer.setVisibility(View.INVISIBLE);
                    numberPicker.setVisibility(View.VISIBLE);
                    startButton.setVisibility(View.VISIBLE);
                    stopButton.setVisibility(View.INVISIBLE);
                    if (chronometer.getBase() != 0){
                        return;
                    }
                    NotificationUtils notification = new NotificationUtils(context);
                    notification.sendNotification("时间","时间到了");
                }
            }
        });
        valueAnimator.start();
    }

    public void pauseAlarm(){
        valueAnimator.pause();
        chronometer.stop();
        pauseTime = SystemClock.elapsedRealtime();
    }

    public void resumeAlarm(){
        if (pauseTime != 0){
            chronometer.setBase(chronometer.getBase() + (SystemClock.elapsedRealtime() - pauseTime));
        }
        else {
            chronometer.setBase(SystemClock.elapsedRealtime());
        }
        valueAnimator.resume();
        chronometer.start();
    }

    public void cancelAlarm(){
        valueAnimator.end();
    }
}
