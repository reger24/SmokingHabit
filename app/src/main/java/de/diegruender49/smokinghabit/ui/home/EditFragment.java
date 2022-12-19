package de.diegruender49.smokinghabit.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import de.diegruender49.smokinghabit.databinding.FragmentHomeBinding;

public class EditFragment extends Fragment {

    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        EditViewModel homeViewModel =
                new ViewModelProvider(this).get(EditViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        final EditText logDate = binding.editLogDate;
        final EditText logTime = binding.editLogTime;
        final EditText logDesc = binding.editLogDesc;

        homeViewModel.getText().observe(getViewLifecycleOwner(), logDate::setText);
        homeViewModel.getText().observe(getViewLifecycleOwner(), logTime::setText);
        homeViewModel.getText().observe(getViewLifecycleOwner(), logDesc::setText);
        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}