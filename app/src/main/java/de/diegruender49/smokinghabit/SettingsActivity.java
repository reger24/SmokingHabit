package de.diegruender49.smokinghabit;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.preference.PreferenceFragmentCompat;
import androidx.preference.PreferenceManager;

public class SettingsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .replace(R.id.settings, new SettingsFragment())
                    .commit();
        }
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
        }

    }

    public static class SettingsFragment extends PreferenceFragmentCompat {
        @Override
        public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
            setPreferencesFromResource(R.xml.root_preferences, rootKey);
            SharedPreferences pref = PreferenceManager.getDefaultSharedPreferences(this.getContext());

            // add current value to 24h smoke limit summary
            CharSequence tmps = this.findPreference("smoke24hlimit").getSummary();
            String limit24herg = pref.getString("smoke24hlimit", "20");
            this.findPreference("smoke24hlimit").setSummary(tmps + " = " + limit24herg);

            // add current value to nextsmokeminlimit summary
            tmps = this.findPreference("nextsmokeminlimit").getSummary();
            String nextsmokeminlimit = pref.getString("nextsmokeminlimit", "30");
            this.findPreference("nextsmokeminlimit").setSummary(tmps + " = " + nextsmokeminlimit);

            // add current value to doubleclickdelay summary
            tmps = this.findPreference("doubleclickdelay").getSummary();
            String doubleclickdelay = pref.getString("doubleclickdelay", "60");
            this.findPreference("doubleclickdelay").setSummary(tmps + " = " + doubleclickdelay);

        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == 16908332 /* @Todo sould be unknown R.id.HOME */) {
            startActivity(new Intent(this, MainActivity.class));
            return true;
        }


        return super.onOptionsItemSelected(item);
    }
}