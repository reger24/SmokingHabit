package de.diegruender49.smokinghabit.ui.notifications;

import android.database.Cursor;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import org.richit.easiestsqllib.EasiestDB;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.Date;

import de.diegruender49.smokinghabit.SmokeStatistic;
import de.diegruender49.smokinghabit.databinding.FragmentNotificationsBinding;

public class NotificationsFragment extends Fragment {

    private FragmentNotificationsBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        NotificationsViewModel notificationsViewModel =
                new ViewModelProvider(this).get(NotificationsViewModel.class);

        binding = FragmentNotificationsBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final TextView textView = binding.textNotifications;
//        notificationsViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);
        // show last smokelog entries
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.HOUR, - (24 * 2));
        SmokeStatistic smstat = new SmokeStatistic(getContext());
        EasiestDB easiestDB = smstat.getDatabase();
        Cursor cursor = easiestDB.getSelect("SELECT smoketime, reasontag FROM smokelog WHERE smoketime >= " + cal.getTimeInMillis());
        if (cursor != null) {
            cursor.moveToLast();
            do {
                Date ddd = new Date(cursor.getLong(0));
                String timestr = DateFormat.getDateTimeInstance().format(ddd);
                String labeltxt = " - " + cursor.getString(1);
                textView.append(timestr + labeltxt  + "\n");
            } while (cursor.moveToPrevious());
            textView.append("\n");
            cursor.close();
        }
        easiestDB.close();
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}