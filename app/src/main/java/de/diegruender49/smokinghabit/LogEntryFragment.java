package de.diegruender49.smokinghabit;

import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import org.richit.easiestsqllib.EasiestDB;

import java.util.Calendar;

import de.diegruender49.smokinghabit.placeholder.PlaceholderContent;

/**
 * A fragment representing a list of Items.
 */
public class LogEntryFragment extends Fragment {

    // TODO: Customize parameter argument names
    private static final String ARG_COLUMN_COUNT = "column-count";
    // TODO: Customize parameters
    private int mColumnCount = 1;

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public LogEntryFragment() {
    }

    // TODO: Customize parameter initialization
    @SuppressWarnings("unused")
    public static LogEntryFragment newInstance(int columnCount) {
        LogEntryFragment fragment = new LogEntryFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_COLUMN_COUNT, columnCount);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            mColumnCount = getArguments().getInt(ARG_COLUMN_COUNT);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_log_entry_list, container, false);

        // Set the adapter
        if (view instanceof RecyclerView) {
            Context context = view.getContext();
            RecyclerView recyclerView = (RecyclerView) view;
            if (mColumnCount <= 1) {
                recyclerView.setLayoutManager(new LinearLayoutManager(context));
            } else {
                recyclerView.setLayoutManager(new GridLayoutManager(context, mColumnCount));
            }

            // show last smokelog entries
            Calendar cal = Calendar.getInstance();
            cal.add(Calendar.HOUR, - (24 * 2));
            SmokeStatistic smstat = new SmokeStatistic(getContext());
            EasiestDB easiestDB = smstat.getDatabase();
            Cursor cursor = easiestDB.getSelect("SELECT smoketime, reasontag FROM smokelog WHERE smoketime >= " + cal.getTimeInMillis());
            if (cursor != null && cursor.moveToLast()) {
                int tmpcnt = 1;
                do {
                    PlaceholderContent.PlaceholderItem item = PlaceholderContent.createPlaceholderItem(tmpcnt++,cursor.getLong(0),cursor.getString(1));
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