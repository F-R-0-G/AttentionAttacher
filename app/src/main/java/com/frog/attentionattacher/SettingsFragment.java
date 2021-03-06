package com.frog.attentionattacher;

import android.content.Intent;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.widget.Toast;

import com.frog.attentionattacher.utils.ActivityCollector;
import com.frog.attentionattacher.utils.PrefUtils;
import com.frog.attentionattacher.utils.ToastUtil;

import static android.app.Activity.RESULT_OK;

public class SettingsFragment extends PreferenceFragment {

    final private int CHOOSE_WEATHER_LOCATION=21;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        //设置界面布局
        final CheckBoxPreference backgroundPref = (CheckBoxPreference) getPreferenceManager()
                .findPreference(getString(R.string.save_background_mode));

        backgroundPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                boolean checked = Boolean.valueOf(newValue.toString());
                //保存到SharedPreferences中
                PrefUtils.setPreSaveBackgroundMode(checked);
                if (PrefUtils.isSaveBackgroundMode()) {
                    ToastUtil.showToast(getActivity(), "已切换。设置完成后请刷新主界面。", Toast.LENGTH_SHORT);
                    Intent setLocation = new Intent(getActivity(), ChooseWeatherActivity.class);
                    startActivityForResult(setLocation, CHOOSE_WEATHER_LOCATION);
                }
                //启动时确认
                return true;
            }
        });
        //切换背景图模式
        final CheckBoxPreference nightPref = (CheckBoxPreference) getPreferenceManager()
                .findPreference(getString(R.string.save_night_mode));

        nightPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {

            @Override
            public boolean onPreferenceChange(Preference preference, Object newValue) {
                boolean checked = Boolean.valueOf(newValue.toString());
                //保存到SharedPreferences中
                PrefUtils.setNightMode(checked);
                if (PrefUtils.isNightMode()) {
                    ToastUtil.showToast(getActivity(), "Night", Toast.LENGTH_SHORT);
                }
                //启动时确认
                return true;
            }
        });
        //夜间模式（未实现）
        final Preference accountPref = (Preference) getPreferenceManager()
                .findPreference(getString(R.string.change_account));

        accountPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent changeAccount = new Intent(getActivity(), LoginActivity.class);
                startActivity(changeAccount);
                ActivityCollector.finishAll();
                return true;
            }
        });
        //切换账号

        final Preference statisticPref = getPreferenceScreen()
                .findPreference(getString(R.string.check_statistic));

        statisticPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                Intent intent = new Intent(getActivity(), checkStatistic.class);
                startActivity(intent);
                return true;
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case CHOOSE_WEATHER_LOCATION:
                if(resultCode==RESULT_OK){
                    String weather_id=data.getStringExtra("weather_id");
                    Intent back=new Intent();
                    back.putExtra("weather_id",weather_id);
                    getActivity().setResult(RESULT_OK,back);
                    getActivity().finish();
                }
                break;
            default:
                break;
        }
    }
}
