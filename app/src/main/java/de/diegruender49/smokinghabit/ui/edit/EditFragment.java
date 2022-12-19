package de.diegruender49.smokinghabit.ui.edit;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

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
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}