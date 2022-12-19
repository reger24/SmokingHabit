package de.diegruender49.smokinghabit;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.richit.easiestsqllib.EasiestDB;

import java.util.Calendar;

import de.diegruender49.smokinghabit.placeholder.PlaceholderContent;
import de.diegruender49.smokinghabit.ui.edit.EditViewModel;

/**
 * A fragment representing a list of Items.
 */
public class LogEntryFragment extends Fragment {


    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public LogEntryFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_log_entry_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            recyclerView.setLayoutManager(new LinearLayoutManager(context));

            // get last smokelog entries
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.HOUR, - (24 * 2));
            SmokeStatistic smstat = new SmokeStatistic(getContext());
            EasiestDB easiestDB = smstat.getDatabase();
            Cursor cursor = easiestDB.getSelect("SELECT smoketime, reasontag, id FROM smokelog WHERE smoketime >= " + cal.getTimeInMillis());
            EditViewModel homeViewModel =  new ViewModelProvider(requireActivity()).get(EditViewModel.class);
            if (cursor != null && cursor.moveToLast()) {
                int tmpcnt = 1;
                do {
                    // remember 1st entry for edit screen
                    if (tmpcnt == 1) homeViewModel.setEntryData(cursor.getInt(2),cursor.getLong(0),cursor.getString(1));
                    PlaceholderContent.PlaceholderItem item = PlaceholderContent.createPlaceholderItem(cursor.getLong(2),tmpcnt++,cursor.getLong(0),cursor.getString(1));
                    PlaceholderContent.addItem(item);
                } while (cursor.moveToPrevious());
                cursor.close();
            }
            easiestDB.close();

            recyclerView.setAdapter(new LogEntryRecyclerViewAdapter(PlaceholderContent.ITEMS));
        }
        return view;
    }
}