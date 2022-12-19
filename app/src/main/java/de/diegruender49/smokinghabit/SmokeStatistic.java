package de.diegruender49.smokinghabit;

import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.util.Log;

import androidx.preference.PreferenceManager;

import org.richit.easiestsqllib.Column;
import org.richit.easiestsqllib.Datum;
import org.richit.easiestsqllib.EasiestDB;

import java.util.Calendar;

/**
 * Class to access Smoke-/Log entry statistics database (sql)
 */
public class SmokeStatistic {
    final private Context context; // app context (from init)
    final private String dbName = "SMOKE_LOG.db"; // file name for database
    EasiestDB easiestDB; // Database reference
    long lastsmoketime = -1; // remember last smoke DateTime
    int todaycount = 0; // remember last count for current day
    int day24hcount = 0; // remember count of last 24 hours

    public SmokeStatistic(Context thecontext) {
        this.context = thecontext;
        easiestDB = EasiestDB.init(context,dbName)
                .addTableColumns("smokelog",
                        new Column("smoketime", "INTEGER"),
                        new Column("reasontag", "TEXT")
                ).doneAddingTables();
    }

    /**
     * Get direct access to underlaying database
     *
     * @return database
     */
    public EasiestDB getDatabase() {
        return easiestDB;
    }

    /**
     * Get count of log entries for current day
     *
     * @return int Count since 0:00 o'clock
     */
    public int getTodayCount() {
        if (todaycount <= 0) { // not calculated yet
            Calendar caltwelf = Calendar.getInstance();
            // set to 0:00 o'clock (as start time for search)
            caltwelf.set(Calendar.HOUR, 0);
            caltwelf.set(Calendar.MINUTE, 0);
            caltwelf.set(Calendar.SECOND, 0);
            caltwelf.set(Calendar.MILLISECOND, 1);
            caltwelf.set(Calendar.AM_PM, Calendar.AM); // AM required, otherwise wrong result after 12:00 (high noon)
            long lzero = caltwelf.getTimeInMillis();

            Cursor cursor = easiestDB.getSelect("SELECT COUNT(smoketime) FROM smokelog WHERE smoketime >= " + Long.toString(lzero));
            if (cursor.moveToNext()) {
                todaycount = cursor.getInt(0);
            }
            cursor.close();

        }
        return todaycount;
    }

    /**
     * Get count of log entries for the last 24 hours
     *
     * @return int Count
     */
    public int get24hCount() {
        if (this.day24hcount <= 0) { // not calculated yet
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.HOUR, -24);
            Long timediff = cal.getTimeInMillis();

            Cursor cursor = easiestDB.getSelect("SELECT COUNT(smoketime) FROM smokelog WHERE smoketime >= " + timediff);
            Log.d("get24hCount", "SELECT COUNT(smoketime) FROM smokelog WHERE smoketime >= " + timediff);
            if (cursor.moveToNext()) {
                this.day24hcount = cursor.getInt(0);
            }
            cursor.close();
        }
        return this.day24hcount;
    }

    /**
     * Determine time of last log entry
     *
     * @return long DateTime
     */
    public long getLastsmoketime() {
        if (lastsmoketime > 0) {
            return lastsmoketime;
        } else {
            Cursor cursor = easiestDB.getSelect("SELECT MAX(smoketime) FROM smokelog");
            if (cursor != null) {
                while (cursor.moveToNext()) {
                    lastsmoketime = cursor.getLong(0);
                }
                cursor.close();
            }
        }
        return lastsmoketime;
    }

    /**
     * Get time between last log entry and now (in seconds)
     *
     * @return long diff in minutes
     */
    public long getSmokefreeMinutes() {
        long timediff = Calendar.getInstance().getTimeInMillis() - getLastsmoketime();
        return timediff / 60000; // 60 seconds * 1000 milliseconds
    }

    /**
     * Add a log entry to database with current DateTime
     *
     * @param reason text label for this log entry
     * @return boolean true on success
     */
    public boolean addSmokeLog(String reason) {
        boolean added = false;
        long tmpnow = System.currentTimeMillis();
        SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this.context);
        String sdefaultdelay = pref.getString("doubleclickdelay", "60");
        long defaultdelay = Long.valueOf(sdefaultdelay);
        if (tmpnow - getLastsmoketime() > (defaultdelay * 1000)) { // one minute delay to prevent double registration
            added = easiestDB.addDataInTable(0,
                    new Datum(1, tmpnow),
                    new Datum(2, reason)
            );
            lastsmoketime = tmpnow;
            todaycount++; //adjust today counter
            day24hcount++; // adjust 24h counter
        }
        return added;
    }

    /**
     * Resets the class local stat values, e.g. after value has changed to init a new calculation
     */
    private void invalidateValues() {
        todaycount = 0; // reset last count for current day
        day24hcount = 0; // reset count of last 24 hours
    }
}
