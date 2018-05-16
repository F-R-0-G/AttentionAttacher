package com.frog.attentionattacher;

import android.os.Bundle;
import android.os.SystemClock;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;

import com.frog.attentionattacher.db.AttentionTimeData;
import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class checkStatistic extends AppCompatActivity {
    private static String[] week = {"周日", "周一", "周二", "周三", "周四", "周五" ,"周六"};
    private static final String TAG = "checkStatistic";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_check_statistic);

        BarChart barChart = findViewById(R.id.chart);
        List<BarEntry> entries = new ArrayList<>();
        final String[] quarters = new String[7];
        Calendar c = Calendar.getInstance();

        for (int i = 0; i < 7; i++){
            String dateInWeek = getDayinWeek(c);
            quarters[6-i] = dateInWeek;
            String dateInString = getDateInString(c);
            long getTime = AttentionTimeData.getTime(dateInString, checkStatistic.this);
            float timeInFloat = (float) (getTime / 1000 / 60);
            entries.add(new BarEntry(6-i, timeInFloat));
            c.add(Calendar.DAY_OF_MONTH, -1);
        }

        BarDataSet set = new BarDataSet(entries, "学习时间（分钟）");
        BarData data = new BarData(set);


        IAxisValueFormatter formatter = new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                return quarters[(int) value];
            }
        };

        barChart.setData(data);
        set.setValueTextSize(12f);         // 设置柱状图上方的值
        data.setBarWidth(0.9f);
        barChart.setFitBars(true);
        barChart.setNoDataText("你还没有开始专注");
        barChart.animateY(3000, Easing.EasingOption.EaseInOutCubic);   // 设置y轴动画
        barChart.getDescription().setEnabled(false);       // 将description label去掉

        XAxis xAxis = barChart.getXAxis();
        xAxis.setDrawGridLines(false);           // 去掉网格线
        xAxis.setTextSize(12f);                // 设置x轴的字体大小
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setValueFormatter(formatter);

        Legend legend = barChart.getLegend();
        legend.setTextSize(12f);  // 设置标签的字体

        barChart.invalidate();

    }

    private String getDateInString(Calendar c){
        String year = Integer.toString(c.get(Calendar.YEAR));
        String month = Integer.toString(c.get(Calendar.MONTH) + 1);
        String day = Integer.toString(c.get(Calendar.DAY_OF_MONTH));
        String calendarData = year + "//" + month + "//" + day;
        return calendarData;
    }

    private String getDayinWeek(Calendar c){
        int dayInWeek = c.get(Calendar.DAY_OF_WEEK);
        return week[dayInWeek - 1];
    }
}
