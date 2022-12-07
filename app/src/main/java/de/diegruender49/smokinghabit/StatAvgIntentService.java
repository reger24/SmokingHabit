package de.diegruender49.smokinghabit;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.core.app.JobIntentService;

import org.richit.easiestsqllib.EasiestDB;

import java.util.Calendar;

/**
 * An {@link JobIntentService} subclass for handling asynchronous task requests in
 * a service on a separate handler thread.
 * <p>
 * <p>
 * TODO: Customize class - update intent actions, extra parameters and static
 * helper methods.
 */
public class StatAvgIntentService extends JobIntentService {

    // IntentService can perform, e.g. ACTION_FETCH_NEW_ITEMS
    private static final String ACTION_FOO = "de.diegruender49.smokinghabit.action.CALCAVG";

    static void enqueueWork(Context context, Intent work) {
        enqueueWork(context, StatAvgIntentService.class, 123, work);
    }

    @Override
    protected void onHandleWork(@NonNull Intent intent) {
        if (intent != null) {
            final String action = intent.getAction();
            if (ACTION_FOO.equals(action)) {
                handleActionFoo();
            }
        }
    }

    /**
     * Starts this service to perform action Foo with the given parameters. If
     * the service is already performing a task this action will be queued.
     *
     * @see JobIntentService
     */
    // TODO: Customize helper method
    public static void startActionFoo(Context context) {
        Intent intent = new Intent(context, StatAvgIntentService.class);
        intent.setAction(ACTION_FOO);
        enqueueWork(context, StatAvgIntentService.class, 123, intent);
    }


    /**
     * Calc averages and store in database for charting
     */
    private void handleActionFoo() {

        SmokeStatistic smstat = new SmokeStatistic(this.getApplicationContext());
        EasiestDB edb = smstat.getDatabase();
        Cursor c = edb.getAllDataFrom("smokelog");
        Calendar cal1 = Calendar.getInstance();

        int day1;
        int day2 = 0;
        int firsttime = 0;
        long timelast = 0;

        while (c.moveToNext()) {
            long l1 = c.getLong(1);
            cal1.setTimeInMillis(l1);
            day1 = cal1.get(Calendar.DAY_OF_MONTH);
            if (day1 == day2 && day2 != 0) {

            } else {
                // calc first smoke time of the day
                day2 = day1;
                firsttime = cal1.get(Calendar.HOUR_OF_DAY);
                Log.i("StatAvgService.handleActionFoo", "Day " + day1 + " Time " + Long.toString(firsttime));
            }

            long diff = cal1.getTimeInMillis() - timelast;
            long difftime = cal1.getTimeInMillis();

            Log.i("StatAvgService.handleActionFoo", cal1.get(Calendar.DAY_OF_YEAR) + " " + android.text.format.DateFormat.format(" d.M.yyyy hh:mm", difftime) + " " + diff / 60000 + " " + difftime);

            timelast = cal1.getTimeInMillis();

        }
        c.close();
    }

}