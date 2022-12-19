package de.diegruender49.smokinghabit.ui.edit;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import org.richit.easiestsqllib.Datum;

import java.text.DateFormat;
import java.text.ParseException;

import de.diegruender49.smokinghabit.SmokeStatistic;
import de.diegruender49.smokinghabit.databinding.FragmentEditBinding;

public class EditFragment extends Fragment {

    private FragmentEditBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        EditViewModel editViewModel =
                new ViewModelProvider(requireActivity()).get(EditViewModel.class);

        binding = FragmentEditBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final EditText logDate = binding.editLogDate;
        final EditText logTime = binding.editLogTime;
        final EditText logDesc = binding.editLogDesc;

        logDate.setText(editViewModel.getDateText());
        logTime.setText(editViewModel.getTimeText());
        logDesc.setText(editViewModel.getDescText());

        binding.saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                SmokeStatistic smstat = new SmokeStatistic(getContext());
                smstat.getDatabase().updateData(0,editViewModel.getSqlId(),new Datum("reasontag",logDesc.getText().toString()));
                try {
                    long tmpdt = DateFormat.getDateTimeInstance().parse(logDate.getText().toString() + " " + logTime.getText().toString()).getTime();
                    smstat.getDatabase().updateData(0,editViewModel.getSqlId(),new Datum("smoketime",tmpdt));
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }
        });

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}