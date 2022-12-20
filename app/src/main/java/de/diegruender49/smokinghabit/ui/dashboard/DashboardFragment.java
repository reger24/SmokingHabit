package de.diegruender49.smokinghabit.ui.dashboard;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import de.diegruender49.smokinghabit.AllBarChartActivity;
import de.diegruender49.smokinghabit.DayBarChartActivity;
import de.diegruender49.smokinghabit.MainActivity;
import de.diegruender49.smokinghabit.MonthBarChartActivity;
import de.diegruender49.smokinghabit.databinding.FragmentDashboardBinding;

public class DashboardFragment extends Fragment {

    private FragmentDashboardBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        binding = FragmentDashboardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();


        final View daybutton = binding.buttonDay;
        daybutton.setOnClickListener(new View.OnClickListener(){
        @Override
        public void onClick(View view) {
            startActivity(new Intent(requireContext(), DayBarChartActivity.class));
        }
        });

        final View monthbutton = binding.buttonMonth;
        monthbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(requireContext(), MonthBarChartActivity.class));
            }
        });

        final View allbutton = binding.buttonAll;
        allbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(requireContext(), AllBarChartActivity.class));
            }
        });

        final View homebutton = binding.buttonHome;
        homebutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(requireContext(), MainActivity.class));
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