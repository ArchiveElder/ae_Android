package com.example.ae_android.ui.storycard;

import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.ae_android.databinding.FragmentStorycardBinding;

public class StorycardFragment extends DialogFragment {

    private FragmentStorycardBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        StorycardViewModel storycardViewModel =
                new ViewModelProvider(this).get(StorycardViewModel.class);

        binding = FragmentStorycardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        root.setClipToOutline(true);
        binding.widget1.setVisibility(View.VISIBLE);
        binding.widget2.setVisibility(View.GONE);

        binding.dismissButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        binding.answerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.widget1.setVisibility(View.GONE);
                binding.widget2.setVisibility(View.VISIBLE);
            }
        });

        //final TextView textView = binding.textGallery;
        //galleryViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        this.setCancelable(false);



        return root;
    }

    @Override
    public void onResume() {
        super.onResume();

        Window window = getDialog().getWindow();
        Point size = new Point();

        Display display = window.getWindowManager().getDefaultDisplay();
        display.getSize(size);

        int width = size.x;

        window.setLayout((int) (width * 0.9), (int) (width * 0.95));
        window.setGravity(Gravity.CENTER);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
