package com.frog.attentionattacher;

import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.graphics.Bitmap;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.transition.Explode;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.frog.attentionattacher.db.PersonalInfoData;
import com.frog.attentionattacher.utils.ActivityCollector;

import org.litepal.crud.DataSupport;

import java.util.List;

public class LoginActivity extends AppCompatActivity {

    private EditText etUsername;
    private EditText etPassword;
    private Button btGo;
    private CardView cv;
    private FloatingActionButton fab;

    private SharedPreferences preferences;
    private Editor editor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCollector.addActivity(this);

        Window window = getWindow();
        //隐藏标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //隐藏状态栏
        int flag= WindowManager.LayoutParams.FLAG_FULLSCREEN;
        //定义全屏参数
        window.setFlags(flag, flag);
        //设置当前窗体为全屏显示
        setContentView(R.layout.activity_login);

        preferences = getSharedPreferences("welcomeActivity", MODE_PRIVATE);
        //初始化
        //判断是否首次进入
        initView();
        setListener();
        if (preferences.getBoolean("welcome_firstStart", true)) {
            editor = preferences.edit();
            editor.putBoolean("welcome_firstStart", false);
            editor.apply();
            //设置为false，不再显示引导页
            getWindow().setExitTransition(null);
            getWindow().setEnterTransition(null);
            startActivity(new Intent(LoginActivity.this, WelcomeActivity.class));
        } else {
            //不是首次登录，无事发生}
        }
    }

    private void initView() {
        etUsername = findViewById(R.id.et_username);
        etPassword = findViewById(R.id.et_password);
        btGo = findViewById(R.id.bt_login);
        cv = findViewById(R.id.cv);
        fab = findViewById(R.id.fab);
    }

    private void setListener() {
        btGo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Explode explode = new Explode();
                explode.setDuration(500);
                getWindow().setExitTransition(explode);
                getWindow().setEnterTransition(explode);
                //动画效果



                //
                ActivityOptionsCompat oc2 = ActivityOptionsCompat.makeSceneTransitionAnimation(LoginActivity.this);
                Intent i2 = new Intent(LoginActivity.this,LoginSuccessActivity.class);
                startActivity(i2);

            }
        });
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getWindow().setExitTransition(null);
                getWindow().setEnterTransition(null);
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(
                        LoginActivity.this, fab, fab.getTransitionName());
                etUsername.setText("");
                etPassword.setText("");
                startActivity(new Intent(LoginActivity.this, RegisterActivity.class), options.toBundle());
            }
        });
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        fab.setVisibility(View.GONE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        fab.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }
}
