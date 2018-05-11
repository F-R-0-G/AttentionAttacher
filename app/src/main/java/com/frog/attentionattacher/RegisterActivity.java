package com.frog.attentionattacher;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.text.TextUtils;
import android.transition.Transition;
import android.transition.TransitionInflater;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.widget.Button;
import android.widget.EditText;

import com.frog.attentionattacher.db.PersonalInfoData;
import com.frog.attentionattacher.utils.ActivityCollector;

import org.litepal.LitePal;
import org.litepal.crud.DataSupport;

import java.util.List;

public class RegisterActivity extends AppCompatActivity {

    private FloatingActionButton fab;
    private CardView cvAdd;
    private EditText account;
    private EditText password;
    private EditText repeatPassword;
    private EditText username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCollector.addActivity(this);

        Window window = getWindow();
        //隐藏标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //隐藏状态栏
        //定义全屏参数
        int flag = WindowManager.LayoutParams.FLAG_FULLSCREEN;
        //设置当前窗体为全屏显示
        window.setFlags(flag, flag);
        setContentView(R.layout.activity_register);

        ShowEnterAnimation();
        initView();
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                animateRevealClose();
            }
        });

        Button register = (Button) findViewById(R.id.bt_start);
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String accountIn = account.getText().toString();
                String passwordIn = password.getText().toString();
                String repeatPasswordIn = repeatPassword.getText().toString();
                String usernameIn = username.getText().toString();
                if (TextUtils.isEmpty(accountIn) || TextUtils.isEmpty(passwordIn)) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(RegisterActivity.this);
                    dialog.setTitle("创建失败");
                    dialog.setMessage("账号密码不能为空");
                    dialog.setCancelable(false);
                    dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            account.setText("");
                            password.setText("");
                            repeatPassword.setText("");
                            username.setText("");
                        }
                    });
                    dialog.show();
                } else if (!accountIn.equals(repeatPasswordIn)) {
                    AlertDialog.Builder dialog = new AlertDialog.Builder(RegisterActivity.this);
                    dialog.setTitle("创建失败");
                    dialog.setMessage("两次输入的密码不同");
                    dialog.setCancelable(false);
                    dialog.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            account.setText("");
                            password.setText("");
                            repeatPassword.setText("");
                            username.setText("");
                        }
                    });
                } else {
                    LitePal.getDatabase();
                    PersonalInfoData data = new PersonalInfoData();
                    List<PersonalInfoData> list = DataSupport.findAll(PersonalInfoData.class);
                    int id = list.size() + 1;
                    data.setId(id);
                    data.setAccount(accountIn);
                    data.setPassword(passwordIn);
                    data.setUsername(usernameIn);
                    data.save();
                    //个人信息写入数据库
                    Intent intent = new Intent(RegisterActivity.this, LoginSuccessActivity.class);
                    intent.putExtra("user_id", id);
                    startActivity(intent);
                    //进入界面不需要出现两次，直接finish
                }
            }
        });


    }


    private void initView() {
        fab = findViewById(R.id.fab);
        cvAdd = findViewById(R.id.cv_add);
        account = findViewById(R.id.et_username);
        password = findViewById(R.id.et_password);
        repeatPassword = findViewById(R.id.et_repeatpassword);
        username = findViewById(R.id.et_user_name);
    }


    private void ShowEnterAnimation() {
        Transition transition = TransitionInflater.from(this).inflateTransition(R.transition.fabtransition);
        getWindow().setSharedElementEnterTransition(transition);

        transition.addListener(new Transition.TransitionListener() {
            @Override
            public void onTransitionStart(Transition transition) {
                cvAdd.setVisibility(View.GONE);
            }

            @Override
            public void onTransitionEnd(Transition transition) {
                transition.removeListener(this);
                animateRevealShow();
            }

            @Override
            public void onTransitionCancel(Transition transition) {

            }

            @Override
            public void onTransitionPause(Transition transition) {

            }

            @Override
            public void onTransitionResume(Transition transition) {

            }


        });
    }

    public void animateRevealShow() {
        Animator mAnimator = ViewAnimationUtils.createCircularReveal(cvAdd, cvAdd.getWidth()/2,0, fab.getWidth() / 2, cvAdd.getHeight());
        mAnimator.setDuration(500);
        mAnimator.setInterpolator(new AccelerateInterpolator());
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
            }

            @Override
            public void onAnimationStart(Animator animation) {
                cvAdd.setVisibility(View.VISIBLE);
                super.onAnimationStart(animation);
            }
        });
        mAnimator.start();
    }

    public void animateRevealClose() {
        Animator mAnimator = ViewAnimationUtils.createCircularReveal(cvAdd,cvAdd.getWidth()/2,0, cvAdd.getHeight(), fab.getWidth() / 2);
        mAnimator.setDuration(500);
        mAnimator.setInterpolator(new AccelerateInterpolator());
        mAnimator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                cvAdd.setVisibility(View.INVISIBLE);
                super.onAnimationEnd(animation);
                fab.setImageResource(R.drawable.plus);
                RegisterActivity.super.onBackPressed();
            }

            @Override
            public void onAnimationStart(Animator animation) {
                super.onAnimationStart(animation);
            }
        });
        mAnimator.start();
    }
    @Override
    public void onBackPressed() {
        animateRevealClose();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }
}
