package de.diegruender49.smokinghabit;

import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.preference.PreferenceManager;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.snackbar.Snackbar;

import org.richit.easiestsqllib.EasiestDB;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

public class MainActivity extends AppCompatActivity {
    private final String TAG = this.getClass().getSimpleName();
    public final String REASON_PC = "";
    EasiestDB easiestDB; // for direct access to database
    SmokeStatistic smstat; // statistic object handling all gets ond sets to/from database

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.button_charts);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, MonthBarChartActivity.class));
            }
        });

        TableLayout stattable = findViewById(R.id.stattable);
        stattable.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, LogEntryListActivity.class));
            }
        });

        smstat = new SmokeStatistic(this.getApplicationContext());
        easiestDB = smstat.getDatabase();

        // action of smokenow button
        findViewById(R.id.button_smokenow).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                boolean added = smstat.addSmokeLog(REASON_PC);
                if (added) {
                    SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(MainActivity.this);
                    boolean shallclose = pref.getBoolean("closeonlog", false);

                    if (shallclose) {
                        finishAffinity(); // close activity/screen
                    } else {
                        // display feedback text success and make smokenow button invisible to prevent double click
                        View vfab = findViewById(R.id.button_smokenow);
                        vfab.setVisibility(View.INVISIBLE);

                        Snackbar.make(view, R.string.log_entry_added, Snackbar.LENGTH_LONG)
                                .setAction("Action", null).show();

                    }

                } else {
                    // display feedback text of likely double registration
                    Snackbar.make(view, R.string.log_entry_not_added, Snackbar.LENGTH_LONG)
                            .setAction("Action", null).show();
                }

            }
        });

        TextView tv = findViewById(R.id.smokelogtext);
        tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, LogEntryListActivity.class));
            }
        });
        tv.setText("");

        // show last 3 smokelog entries
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR, -48);
        Cursor cursor = easiestDB.getSelect("SELECT smoketime, reasontag FROM smokelog WHERE smoketime >= " + cal.getTimeInMillis());
        if (cursor != null) {
            int cnt = cursor.getCount() - 4;
            if (cnt > 0) {
                cursor.moveToPosition(cnt);
            }
            while (cursor.moveToNext()) {
                Date ddd = new Date(cursor.getLong(0));
                String timestr = DateFormat.getDateTimeInstance().format(ddd);
                String labeltxt = " - " + cursor.getString(1);
                tv.append(timestr + labeltxt + "\n");
            }
            cursor.close();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this);
        String limit24herg = pref.getString("smoke24hlimit", "20");
        String limitfreetxterg = pref.getString("nextsmokeminlimit", "30");

        // set traffic light colors for row above/below limit
        View tmprv = findViewById(R.id.day24hcountrow);
        int count24h = smstat.get24hCount();
        if (count24h >= Integer.valueOf(limit24herg)) {
            tmprv.setBackgroundResource(R.color.lightred);
        } else if (count24h >= Integer.valueOf(limit24herg) - 2) {
            tmprv.setBackgroundResource(R.color.lightyellow);
        } else tmprv.setBackgroundResource(R.color.lightgreen);

        tmprv = findViewById(R.id.daycountrow);
        int counttoday = smstat.getTodayCount();
        if (counttoday >= Integer.valueOf(limit24herg)) {
            tmprv.setBackgroundResource(R.color.lightred);
        } else if (counttoday >= Integer.valueOf(limit24herg) - 2) {
            tmprv.setBackgroundResource(R.color.lightyellow);
        } else tmprv.setBackgroundResource(R.color.lightgreen);

        tmprv = findViewById(R.id.smokefreetimerow);
        if (smstat.getSmokefreeMinutes() < Integer.valueOf(limitfreetxterg)) {
            tmprv.setBackgroundResource(R.color.lightred);
        } else if (smstat.getSmokefreeMinutes() <= Integer.valueOf(limitfreetxterg) * 0.85) {
            tmprv.setBackgroundResource(R.color.lightyellow);
        } else tmprv.setBackgroundResource(R.color.lightgreen);

        // update UI statistic numbers
        TextView tmpv = findViewById(R.id.day24hcount);
        tmpv.setText(Integer.toString(count24h));

        tmpv = findViewById(R.id.daycount);
        tmpv.setText(Integer.toString(counttoday));

        tmpv = findViewById(R.id.smokefreetime);
        tmpv.setText(Long.toString(smstat.getSmokefreeMinutes()) + " " + getString(R.string.minutes));

        tmpv = findViewById(R.id.button_smokenow);
        tmpv.setVisibility(View.VISIBLE); // in case button was clicked (and set invisible)

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            startActivity(new Intent(this, SettingsActivity.class));
            return true;
        }
        if (id == R.id.calcstat) {
            StatAvgIntentService.startActionFoo(MainActivity.this);
            return true;
        }
        if (id == R.id.showchart) {
            startActivity(new Intent(MainActivity.this, MonthBarChartActivity.class));
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}