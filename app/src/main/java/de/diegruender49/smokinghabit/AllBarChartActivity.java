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


public class AllBarChartActivity extends AppCompatActivity implements OnChartValueSelectedListener {
    private static final int PERMISSION_STORAGE = 0;

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
        setTitle(R.string.all_statistic);

        Button fab = findViewById(R.id.buttonAll);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AllBarChartActivity.this, AllBarChartActivity.class));
            }
        });

        fab = findViewById(R.id.buttonMonth);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AllBarChartActivity.this, MonthBarChartActivity.class));
            }
        });

        fab = findViewById(R.id.buttonDay);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AllBarChartActivity.this, DayBarChartActivity.class));
            }
        });

        fab = findViewById(R.id.buttonHome);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(AllBarChartActivity.this, MainActivity.class));
            }
        });

        fab = findViewById(R.id.buttonMonth);
        fab.setVisibility(View.VISIBLE);

        fab = findViewById(R.id.buttonDay);
        fab.setVisibility(View.VISIBLE);

        fab = findViewById(R.id.buttonAll);
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

        //    IAxisValueFormatter xAxisFormatter = new DayAxisValueFormatter(chart);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxisPosition.BOTTOM);
        xAxis.setTypeface(tfLight);
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f); // only intervals of 1 day
        xAxis.setLabelCount(7);
        xAxis.setTextSize(14f);

        YAxis leftAxis = chart.getAxisLeft();
        leftAxis.setTypeface(tfLight);
        leftAxis.setLabelCount(8, false);
        //  leftAxis.setValueFormatter(custom);
        leftAxis.setPosition(YAxisLabelPosition.OUTSIDE_CHART);
        leftAxis.setSpaceTop(15f);
        leftAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)
        leftAxis.setTextSize(14f);
        leftAxis.setGranularity(1f);

        YAxis rightAxis = chart.getAxisRight();
        rightAxis.setDrawGridLines(false);
        rightAxis.setTypeface(tfLight);
        rightAxis.setLabelCount(8, false);
        rightAxis.setSpaceTop(15f);
        rightAxis.setAxisMinimum(0f); // this replaces setStartAtZero(true)
        rightAxis.setGranularity(1f);

        Legend l = chart.getLegend();
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.LEFT);
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        l.setDrawInside(false);
        l.setForm(LegendForm.SQUARE);
        l.setFormSize(9f);
        l.setTextSize(11f);
        l.setXEntrySpace(4f);

        XYMarkerView mv = new XYMarkerView(this, new DefaultValueFormatter(0));
        mv.setChartView(chart); // For bounds control
        chart.setMarker(mv); // Set the marker to the chart

        setData();
        chart.invalidate();
    }

    /**
     * Set and prepare chart data from database
     */
    private void setData() {

        float start = 1f;

        long startday;
        // calculate last 1 year form current 0:00 in the past for chart start
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.YEAR, -1); // set a date to cover all relevant data in select statement
        cal.set(Calendar.HOUR, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        startday = cal.getTimeInMillis(); // 30 days in past

        SmokeStatistic smsc = new SmokeStatistic(this);
        Cursor cursor = smsc.getDatabase().getSelect("SELECT smoketime FROM smokelog WHERE smoketime >= " + startday);
        ArrayList<BarEntry> values = new ArrayList<>();

        int cursorcnt = cursor.getCount();
        int[] harray = new int[cursorcnt + 1];
        cal.get(Calendar.DAY_OF_YEAR);
        int lastday = 0;
        int tmpday;
        int arridx = 0;
        while (cursor.moveToNext()) {
            cal.setTimeInMillis(cursor.getLong(0));
            tmpday = cal.get(Calendar.DAY_OF_YEAR);
            if (tmpday == lastday) {
                harray[arridx]++;
            } else {
                lastday = tmpday;
                arridx++;
                harray[arridx]++;
            }
        }
        cursor.close();

        for (int i = 1; i <= arridx; i++) {
            if (harray[i] > 0) {
                values.add(new BarEntry(i, harray[i]));
            }
        }

        BarDataSet set1;

        if (chart.getData() != null && chart.getData().getDataSetCount() > 0) {
            set1 = (BarDataSet) chart.getData().getDataSetByIndex(0);
            set1.setValues(values);
            chart.getData().notifyDataChanged();
            chart.notifyDataSetChanged();

        } else {
            set1 = new BarDataSet(values, getString(R.string.day_of_all));

            set1.setDrawIcons(false);

            int barcolor = ContextCompat.getColor(this, android.R.color.holo_blue_dark);

            ArrayList<IBarDataSet> dataSets = new ArrayList<>();
            set1.setColor(barcolor);
            dataSets.add(set1);

            BarData data = new BarData(dataSets);
            data.setValueTextSize(10f);
            data.setValueTypeface(tfLight);
            data.setBarWidth(0.9f);

            chart.setData(data);
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
