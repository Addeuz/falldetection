package com.example.falldetector.ui.home;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.example.falldetector.R;
import com.example.falldetector.RidingActivity;

public class HomeFragment extends Fragment {

    private HomeViewModel homeViewModel;

    public static final String INTENT_MESSAGE = "com.example.falldetector.TEST";

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);
        View root = inflater.inflate(R.layout.fragment_home, container, false);
        final TextView textView = root.findViewById(R.id.text_home);
        homeViewModel.getText().observe(getViewLifecycleOwner(), new Observer<String>() {
            @Override
            public void onChanged(@Nullable String s) {
                textView.setText(s);
            }
        });

        Button startClockButton = (Button) root.findViewById(R.id.startClockButton);
        startClockButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                System.out.println("Hello from startclockbutton");
                Intent intent = new Intent(getContext(), RidingActivity.class);
                intent.putExtra(INTENT_MESSAGE, "Hello from main activity");
                startActivity(intent);
            }
        });

        return root;
    }
}