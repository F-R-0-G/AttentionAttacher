package com.frog.attentionattacher.app;

import android.app.Application;
import android.content.Context;

import com.facebook.drawee.backends.pipeline.Fresco;

import org.litepal.LitePal;

/**
 * 获取全局context
 * framed by Wen Sun
 */

public class BasicApp extends Application {

    private static Context context;

    public static Context getContext() {
        return context;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Fresco.initialize(this);
        LitePal.initialize(this);
        context = getApplicationContext();
    }

}
