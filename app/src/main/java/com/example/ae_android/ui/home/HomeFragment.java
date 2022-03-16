package com.example.ae_android.ui.home;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.example.ae_android.R;
import com.example.ae_android.databinding.FragmentHomeBinding;
import com.example.ae_android.ui.coloringFragment;
import com.example.ae_android.ui.recordFragment;

public class HomeFragment extends Fragment {

    private FragmentHomeBinding binding;

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {

        //HomeFragment 위에 두개의 Fragment(Coloring과 record)띄우기
        FragmentTransaction transaction = getFragmentManager().beginTransaction();

        coloringFragment fragment1 = new coloringFragment();
        recordFragment fragment2 = new recordFragment();

        transaction.replace(R.id.coloring_frame, fragment1);
        transaction.replace(R.id.record_frame, fragment2);
        transaction.commit();

        //기본으로 생성된 코드
        HomeViewModel homeViewModel =
                new ViewModelProvider(this).get(HomeViewModel.class);

        binding = FragmentHomeBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        return root;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }
}