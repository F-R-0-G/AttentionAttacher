package com.frog.attentionattacher;

import android.animation.ValueAnimator;
import android.app.AlarmManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.transition.Explode;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.frog.attentionattacher.db.AttentionTimeData;
import com.frog.attentionattacher.db.PersonalInfoData;
import com.frog.attentionattacher.gson.Weather;
import com.frog.attentionattacher.service.AutoUpdateService;
import com.frog.attentionattacher.utils.ActivityCollector;
import com.frog.attentionattacher.utils.Alarm_Clock;
import com.frog.attentionattacher.utils.AnalyzeWeatherUtil;
import com.frog.attentionattacher.utils.HttpUtil;
import com.frog.attentionattacher.utils.PrefUtils;
import com.frog.attentionattacher.utils.ToastUtil;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionButton;
import com.oguzdev.circularfloatingactionmenu.library.FloatingActionMenu;
import com.oguzdev.circularfloatingactionmenu.library.SubActionButton;
import com.shawnlin.numberpicker.NumberPicker;

import org.litepal.crud.DataSupport;

import java.io.IOException;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public class LoginSuccessActivity extends AppCompatActivity implements View.OnClickListener{

    private int id;
    private String mWeatherId;

    private Chronometer chronometer;
    private ProgressBar progressBar;
    private NumberPicker numberPicker;
    private Button stopButton;
    private Button cancelButton;
    private Button resumeButton;
    private Button startAttachAttention;
    private Alarm_Clock alarm_clock;

    private DrawerLayout mDrawerLayout;
    private ScrollView mainBody;
    private ImageView bingPicImg;
    private SwipeRefreshLayout swipeRefreshLayout;

    private TextView userName;
    private ImageView userIcon;

    private int mLastX;
    private int mLastY;
    private int mDownX;
    private int mDownY;
    private final int ADAPTER_VALUE = 25;

    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;

    private final int START_SETTINGS_FOR_WEATHER=11;

    private static final String TAG = "LoginSuccessActivity";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ActivityCollector.addActivity(this);
        ActivityCollector.finishOthers(this);
        prefs = PreferenceManager.getDefaultSharedPreferences(this);
        editor = PreferenceManager.getDefaultSharedPreferences(LoginSuccessActivity.this).edit();
        //缓存数据
        //初始化
        Window window = getWindow();
        //隐藏标题栏
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        //隐藏状态栏
        int flag = WindowManager.LayoutParams.FLAG_FULLSCREEN;
        //定义全屏参数
        window.setFlags(flag, flag);
        //设置当前窗体为全屏显示
        setContentView(R.layout.activity_login_success);

        Explode explode = new Explode();
        explode.setDuration(500);
        getWindow().setExitTransition(explode);
        getWindow().setEnterTransition(explode);

        ImageView icon = new ImageView(this);
        icon.setImageDrawable(getResources().getDrawable(R.drawable.plus));
        FloatingActionButton.LayoutParams params = new FloatingActionButton.LayoutParams(160, 160);
        FloatingActionButton.LayoutParams conParams = new FloatingActionButton.LayoutParams(90, 90);
        final FloatingActionButton actionButton = new FloatingActionButton.Builder(this)
                .setContentView(icon, conParams)
                .setLayoutParams(params)
                .setPosition(FloatingActionButton.POSITION_TOP_RIGHT)
                .setTheme(FloatingActionButton.THEME_DARK)
                .build();
        actionButton.setOnTouchListener(new View.OnTouchListener() {

            @Override
            public boolean onTouch(View v, MotionEvent event) {
                int x = (int) event.getRawX();
                int y = (int) event.getRawY();
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN: {
                        mDownX = (int) event.getRawX();
                        mDownY = (int) event.getRawY();
                        break;
                    }
                    case MotionEvent.ACTION_MOVE: {
                        int deltaX = x - mLastX;
                        int deltaY = y - mLastY;

                        int translationX = (int) actionButton.getTranslationX() + deltaX;
                        int translationY = (int) actionButton.getTranslationY() + deltaY;
                        actionButton.setTranslationX(translationX);
                        actionButton.setTranslationY(translationY);
                        break;
                    }
                    case MotionEvent.ACTION_UP: {
                        actionButton.callOnClick();
                        break;
                    }
                    default:
                        break;
                }

                mLastX = x;
                mLastY = y;
                return true;
            }
        });
        SubActionButton.Builder itemBuilder = new SubActionButton.Builder(this);
        ImageView itemIcon1 = new ImageView(this);
        itemIcon1.setImageDrawable(getResources().getDrawable(R.drawable.nav_personal_info));
        ImageView itemIcon2 = new ImageView(this);
        itemIcon2.setImageDrawable(getResources().getDrawable(R.drawable.nav_settings));
        ImageView itemIcon3 = new ImageView(this);
        itemIcon3.setImageDrawable(getResources().getDrawable(R.drawable.ic_back));
        SubActionButton.LayoutParams subParams = new FloatingActionButton.LayoutParams(100, 100);
        SubActionButton button1 = itemBuilder.setContentView(itemIcon1)
                .setLayoutParams(subParams)
                .setTheme(FloatingActionButton.THEME_DARK)
                .build();
        SubActionButton button2 = itemBuilder.setContentView(itemIcon2)
                .setLayoutParams(subParams)
                .setTheme(FloatingActionButton.THEME_DARK)
                .build();
        SubActionButton button3 = itemBuilder.setContentView(itemIcon3)
                .setLayoutParams(subParams)
                .setTheme(FloatingActionButton.THEME_DARK)
                .build();

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                
            }
        });

        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent settings = new Intent(LoginSuccessActivity.this, SettingsActivity.class);
                startActivityForResult(settings, START_SETTINGS_FOR_WEATHER);
            }
        });

        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(LoginSuccessActivity.this);
                dialog.setTitle("彻底退出");
                dialog.setMessage("不再考虑考虑？");
                dialog.setCancelable(false);
                dialog.setPositiveButton("退出", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ToastUtil.showToast(LoginSuccessActivity.this,
                                "很惭愧，就做了一点微小的工作", Toast.LENGTH_SHORT);
                        ActivityCollector.finishAll();
                    }
                });
                dialog.setNegativeButton("算了", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        ToastUtil.showToast(LoginSuccessActivity.this,
                                "你们哪，不要整天想着搞一个大新闻", Toast.LENGTH_SHORT);
                        mDrawerLayout.closeDrawers();
                    }
                });
                dialog.show();
            }
        });

        FloatingActionMenu actionMenu = new FloatingActionMenu.Builder(this)
                .addSubActionView(button1)
                .addSubActionView(button2)
                .addSubActionView(button3)
                .setRadius(180)
                .setStartAngle(-180)
                .setEndAngle(-270)
                .attachTo(actionButton)
                .build();

        mainBody = (ScrollView) findViewById(R.id.main_body_layout);
        swipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipe_refresh);
        swipeRefreshLayout.setColorSchemeResources(R.color.colorPrimary);
        //下拉刷新

        chronometer = findViewById(R.id.Clock_chronometer);
        chronometer.setVisibility(View.INVISIBLE);
        progressBar = findViewById(R.id.Clock_ProgressBar);
        // 创建计时器和进度条
        numberPicker = findViewById(R.id.number_picker);
        String[] displayNumber = new String[13];
        displayNumber[0] = "1:00";
        for (int i = 1; i < 13; i++){
            displayNumber[i] = Integer.toString(i*5) + ":" + "00";
        }
        numberPicker.setMinValue(1);
        numberPicker.setMaxValue(displayNumber.length);
        numberPicker.setDisplayedValues(displayNumber);
        numberPicker.setValue(6);
        // 创建时间选择器
        startAttachAttention = findViewById(R.id.start_attach_attention);
        startAttachAttention.setOnClickListener(this);
        //开始专注按钮
        stopButton = findViewById(R.id.stop_button);
        stopButton.setOnClickListener(this);
        stopButton.setVisibility(View.INVISIBLE);
        // 暂停按钮，设为不可见
        cancelButton = findViewById(R.id.cancel_button);
        cancelButton.setOnClickListener(this);
        cancelButton.setVisibility(View.INVISIBLE);
        // 取消按钮，设为不可见
        resumeButton = findViewById(R.id.resume_button);
        resumeButton.setOnClickListener(this);
        resumeButton.setVisibility(View.INVISIBLE);
        //继续按钮，设为不可见
        alarm_clock = new Alarm_Clock(chronometer, progressBar,
                LoginSuccessActivity.this, numberPicker);
        // 计时器实例
        bingPicImg = (ImageView) findViewById(R.id.bing_pic_img);
        Intent update = new Intent(this, AutoUpdateService.class);
        startService(update);
        //启动后台更新服务

        if (PrefUtils.isSaveBackgroundMode()) {
            changeBackgroundByWeather();
        } else {
            String bingPic = prefs.getString("bing_pic", null);
            if (bingPic != null) {
                Glide.with(LoginSuccessActivity.this).load(bingPic).into(bingPicImg);
            } else {
                loadBingPic();
            }
        }

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                if (PrefUtils.isSaveBackgroundMode()) {
                    requestWeather(mWeatherId);
                } else {
                    loadBingPic();
                }
            }
        });
        //加载必应每日一图（可替换为本地服务器数据）

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        Button openDrawer = (Button) findViewById(R.id.nav_open_drawer);
        openDrawer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
        });
        //滑动侧边栏

        userIcon=(ImageView) findViewById(R.id.nav_icon_image);
        userName=(TextView)findViewById(R.id.nav_user_name);
        Intent rawIntent = getIntent();
        Bundle bundle = rawIntent.getExtras();
        if (bundle != null) {
            id = bundle.getInt("user_id");
            List<PersonalInfoData> list = DataSupport.findAll(PersonalInfoData.class);
            String name = list.get(id - 1).getUsername();
            editor.putString("user_name", name);
            editor.apply();
            userName.setText(name);
            //初始化个人信息
        } else {
            userName.setText(prefs.getString("user_name", null));
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.start_attach_attention:
                int num = numberPicker.getValue();
                alarm_clock.startCounting(num, startAttachAttention, stopButton);
                startAttachAttention.setVisibility(View.INVISIBLE);
                stopButton.setVisibility(View.VISIBLE);
                break;
            case R.id.resume_button:
                stopButton.setVisibility(View.VISIBLE);
                resumeButton.setVisibility(View.INVISIBLE);
                cancelButton.setVisibility(View.INVISIBLE);
                alarm_clock.resumeAlarm();
                break;
            case R.id.stop_button:
                resumeButton.setVisibility(View.VISIBLE);
                cancelButton.setVisibility(View.VISIBLE);
                stopButton.setVisibility(View.INVISIBLE);
                alarm_clock.pauseAlarm();
                break;
            case R.id.cancel_button:
                resumeButton.setVisibility(View.INVISIBLE);
                cancelButton.setVisibility(View.INVISIBLE);
                alarm_clock.cancelAlarm();
                break;
            default:
                break;
        }
    }
    //开始专注按钮的具体实现

    private void loadBingPic() {
        String requestBingPic = "http://guolin.tech/api/bing_pic";//Thanks!
        HttpUtil.sendOkHttpRequest(requestBingPic, new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showToast(LoginSuccessActivity.this,
                                "图片加载失败", Toast.LENGTH_SHORT);
                        Glide.with(LoginSuccessActivity.this).load(R.drawable.bg).into(bingPicImg);
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String bingPic = response.body().string();
                editor.putString("bing_pic", bingPic);
                editor.apply();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Glide.with(LoginSuccessActivity.this).load(bingPic).into(bingPicImg);
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        });
    }
    //必应每日一图的具体实现

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case 11:
                if (resultCode == RESULT_OK) {
                    mWeatherId = data.getStringExtra("weather_id");
                }
                break;
            default:
                break;
        }
    }

    private void changeBackgroundByWeather() {
        String weatherString = prefs.getString("weather", null);
        if (weatherString != null) {
            Weather weather = AnalyzeWeatherUtil.handleWeatherResponse(weatherString);
            mWeatherId = weather.basic.weatherId;
            showWeatherInfo(weather);
        } else {
            mainBody.setVisibility(View.INVISIBLE);
            requestWeather(mWeatherId);
        }
    }

    /**
     * 根据天气id请求城市天气信息
     */
    public void requestWeather(final String weatherId) {
        String weatherUrl = "http://guolin.tech/api/weather?cityid=" + weatherId + "&key=41407a0da87447a6abcd65bb8f0c1794";
        HttpUtil.sendOkHttpRequest(weatherUrl, new Callback() {
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                final String responseText = response.body().string();
                final Weather weather = AnalyzeWeatherUtil.handleWeatherResponse(responseText);
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        if (weather != null && "ok".equals(weather.status)) {
                            SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(
                                    LoginSuccessActivity.this).edit();
                            editor.putString("weather", responseText);
                            editor.apply();
                            showWeatherInfo(weather);
                        } else {
                            ToastUtil.showToast(LoginSuccessActivity.this,
                                    "获取天气信息失败", Toast.LENGTH_SHORT);
                        }
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }

            @Override
            public void onFailure(Call call, IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        ToastUtil.showToast(LoginSuccessActivity.this,
                                "获取天气信息失败", Toast.LENGTH_SHORT);
                        Glide.with(LoginSuccessActivity.this).load(R.drawable.bg).into(bingPicImg);
                        swipeRefreshLayout.setRefreshing(false);
                    }
                });
            }
        });
    }

    /**
     * 处理并展示Weather中的数据
     */
    String cityName;
    String updateTime;
    String degree;
    String weatherInfo;

    private void showWeatherInfo(final Weather weather) {
        cityName = weather.basic.cityName;
        updateTime = weather.basic.update.updateTime.split(" ")[1];
        degree = weather.now.temperature + "℃";
        weatherInfo = weather.now.more.info;
        mainBody.setVisibility(View.VISIBLE);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (weatherInfo.equals("多云")) {
                    Log.d("ELA", weatherInfo);
                    Glide.with(LoginSuccessActivity.this).load(R.drawable.cloudy).into(bingPicImg);
                } else if (weatherInfo.equals("阵雨")) {
                    Glide.with(LoginSuccessActivity.this).load(R.drawable.rainy).into(bingPicImg);
                } else {
                    Glide.with(LoginSuccessActivity.this).load(R.drawable.sunny).into(bingPicImg);
                }
                swipeRefreshLayout.setRefreshing(false);
            }
        });

    }
    //根据天气切换背景的具体实现

    private long mExitTime = 0;

    //计时器，虽然放在这里很丑，但放在实例区明显不合适，就凑合一下（可理解）
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            if ((System.currentTimeMillis() - mExitTime) > 1000) {
                ToastUtil.showToast(this, "再按一次退出程序", Toast.LENGTH_SHORT);
                mExitTime = System.currentTimeMillis();
            } else {
                Intent home = new Intent(Intent.ACTION_MAIN);
                home.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                home.addCategory(Intent.CATEGORY_HOME);
                startActivity(home);
            }
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    //实现再按一次退出，退出时说骚话并以home形式存储

    @Override
    protected void onDestroy() {
        super.onDestroy();
        ActivityCollector.removeActivity(this);
    }
}