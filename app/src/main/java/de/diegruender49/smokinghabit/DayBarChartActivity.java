package de.diegruender49.smokinghabit;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.RectF;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.Legend.LegendForm;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.XAxis.XAxisPosition;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.components.YAxis.AxisDependency;
import com.github.mikephil.charting.components.YAxis.YAxisLabelPosition;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.formatter.DefaultValueFormatter;
import com.github.mikephil.charting.highlight.Highlight;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.MPPointF;

import java.util.ArrayList;
import java.util.Calendar;

import de.diegruender49.smokinghabit.chartutil.XYMarkerView;


public class DayBarChartActivity extends AppCompatActivity implements OnChartValueSelectedListener {

    protected Typeface tfRegular;
    protected Typeface tfLight;

    private BarChart chart;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tfRegular = Typeface.DEFAULT;
        tfLight = Typeface.SANS_SERIF;

        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_barchart);
        setTitle(R.string.day_statistic);

        Button fab = findViewById(R.id.buttonMonth);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DayBarChartActivity.this, MonthBarChartActivity.class));
            }
        });

        fab = findViewById(R.id.buttonAll);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DayBarChartActivity.this, AllBarChartActivity.class));
            }
        });

        fab = findViewById(R.id.buttonHome);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(DayBarChartActivity.this, MainActivity.class));
            }
        });

        fab = findViewById(R.id.buttonDay);
        fab.setVisibility(View.GONE);

        chart = findViewById(R.id.chart1);
        chart.setOnChartValueSelectedListener(this);

        chart.setDrawBarShadow(false);
        chart.setDrawValueAboveBar(true);

        chart.getDescription().setEnabled(false);

        // set bar values off, no values will be drawn
        chart.setMaxVisibleValueCount(0);

        // scaling can now only be done on x- and y-axis separately
        chart.setPinchZoom(false);

        chart.setDrawGridBackground(false);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxisPosition.BOTTOM);
        xAxis.setTypeface(tfLight);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); // only intervals of 1 day
        xAxis.setLabelCount(12);
        xAxis.setTextSize(14f);

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setTypeface(tfLight);
        leftAxis.setLabelCount(8, false);
        leftAxis.setPosition(YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)
        leftAxis.setTextSize(14f);
        leftAxis.setGranularity(1f); // smallest interval

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setTypeface(tfLight);
        rightAxis.setLabelCount(8, false);
        rightAxis.setSpaceTop(15f);
        rightAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)
        rightAxis.setGranularity(1f); // smallest interval

        Legend chartLegend = chart.getLegend();
        chartLegend.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        chartLegend.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        chartLegend.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        chartLegend.setDrawInside(false);
        chartLegend.setForm(LegendForm.SQUARE);
        chartLegend.setFormSize(9f);
        chartLegend.setTextSize(11f);
        chartLegend.setXEntrySpace(4f);

        XYMarkerView mv = new XYMarkerView(this, new DefaultValueFormatter(0));
        mv.setChartView(chart); // For bounds control
        chart.setMarker(mv); // Set the marker to the chart

        setData();
        chart.invalidate();
    }

    private void setData() {

        long startday;
        // calculate 31 days form current 0:00 in the past for chart start
        Calendar caltmp = Calendar.getInstance();
        caltmp.add(Calendar.MONTH, -1); // past one month
        caltmp.set(Calendar.HOUR, 0);
        caltmp.set(Calendar.MINUTE, 0);
        caltmp.set(Calendar.SECOND, 0);
        caltmp.set(Calendar.MILLISECOND, 0);
        startday = caltmp.getTimeInMillis();

        SmokeStatistic smsc = new SmokeStatistic(this);
        Cursor cursor = smsc.getDatabase().getSelect("SELECT smoketime FROM smokelog WHERE smoketime >= " + startday);
        ArrayList<BarEntry> data = new ArrayList<>();

        final int arrcnt = 24; // 24 hour day
        int[] harray = new int[arrcnt]; // 24 day hours
        while (cursor.moveToNext()) {
            caltmp.setTimeInMillis(cursor.getLong(0));
            int hour = caltmp.get(Calendar.HOUR_OF_DAY); // new Date(cursor.getLong(1)).getHours();
            harray[hour]++;
        }
        cursor.close();

        for (int i = 0; i < arrcnt; i++) { // 00 to 23 o'clock
            data.add(new BarEntry(i, harray[i]));
        }

        BarDataSet set1;

        if (chart.getData() != null && chart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) chart.getData().getDataSetByIndex(0);
            set1.setValues(data);
            chart.getData().notifyDataChanged();
            chart.notifyDataSetChanged();

        } else {
            set1 = new BarDataSet(data, getString(R.string.hour_of_day));

            set1.setDrawIcons(false);

            int barcolor = ContextCompat.getColor(this, android.R.color.holo_blue_dark);

            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
            set1.setColor(barcolor);
            dataSets.add(set1);

            BarData databar = new BarData(dataSets);
            databar.setValueTextSize(10f);
            databar.setValueTypeface(tfLight);
            databar.setBarWidth(0.9f);

            chart.setData(databar);
        }
    }


    private final RectF onValueSelectedRectF = new RectF();

    @Override
    public void onValueSelected(Entry e, Highlight h) {

        if (e == null)
            return;

        RectF bounds = onValueSelectedRectF;
        chart.getBarBounds((BarEntry) e, bounds);
        MPPointF position = chart.getPosition(e, AxisDependency.LEFT);

        Log.i("bounds", bounds.toString());
        Log.i("position", position.toString());

        Log.i("x-index",
                "low: " + chart.getLowestVisibleX() + ", high: "
                        + chart.getHighestVisibleX());

        MPPointF.recycleInstance(position);
    }

    @Override
    public void onNothingSelected() {
    }
}
