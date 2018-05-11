package com.frog.attentionattacher.utils;

import android.animation.ValueAnimator;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Context;
import android.content.Intent;
import android.os.SystemClock;
import android.view.animation.LinearInterpolator;
import android.widget.Chronometer;
import android.widget.ProgressBar;
import android.widget.TimePicker;

import com.frog.attentionattacher.AlarmActivity;



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
    private ValueAnimator valueAnimator;
    private static final int MAX_PROGRESS = 10000;
    private long selectTime;

    public Alarm_Clock (Chronometer chronometer, ProgressBar progressBar){
        this.chronometer = chronometer;
        this.progressBar = progressBar;
    }


    public long pickTimeAndStarted(final Context context, final AlarmManager am){
        final Calendar currentTime = Calendar.getInstance();
        // 创建一个时间选择器
        TimePickerDialog timePickerDialog = new TimePickerDialog(context, 0, new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                // 设置当前时间

                Calendar c = Calendar.getInstance();
                // 先设置为默认时间
                c.setTimeInMillis(System.currentTimeMillis());
                c.setTimeZone(TimeZone.getDefault());
                // 根据用户选择的时间来设置Calendar对象
                c.set(Calendar.HOUR_OF_DAY, hourOfDay);
                c.set(Calendar.MINUTE, minute);
                c.set(Calendar.SECOND, 0);
                c.set(Calendar.MILLISECOND, 0);
                selectTime = c.getTimeInMillis();
                // 创建一个PendingIntent
                Intent intent = new Intent(context, AlarmActivity.class);
                PendingIntent sender = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_CANCEL_CURRENT);

                long systemTime = System.currentTimeMillis();
                if (systemTime > selectTime){
                    c.add(Calendar.DAY_OF_MONTH, 1);
                    selectTime = c.getTimeInMillis();
                    ToastUtil.showToast(context, "你也想拥有永恒的青春？");
                    return;
                }
                // 设置AlarmManager在Calendar对应的时间启动Activity
                else {
                    am.setExact(AlarmManager.RTC_WAKEUP, selectTime, sender);
                    ToastUtil.showToast(context, "设置成功");
                }

//                        long delta = (selectTime - systemTime) % 1000;     // 去掉0.1秒，防止跳一位
                long alltime = selectTime - systemTime;
                long setChronometerTime = SystemClock.elapsedRealtime() + alltime;
                chronometer.setBase(setChronometerTime);  // 设置倒计时
                chronometer.setCountDown(true);
                chronometer.start();
                initProgressBar(alltime);
            }
        }, currentTime.get(Calendar.HOUR_OF_DAY), currentTime.get(Calendar.MINUTE), true);
        timePickerDialog.show();

        return selectTime;
    }

    // 初始化一个ProgressBar，并在结束时停止chronometer
    private void initProgressBar(long duration) {
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
                }
            }
        });
        valueAnimator.start();
    }
}
