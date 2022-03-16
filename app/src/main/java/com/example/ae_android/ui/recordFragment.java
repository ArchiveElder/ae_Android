package com.example.ae_android.ui;

import android.media.Image;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.ae_android.R;


public class recordFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_record, container, false);

        ImageButton record_button = view.findViewById(R.id.record_button);
        ImageButton pause_button = view.findViewById(R.id.pause_button);

        //녹음 버튼 클릭 시 이벤트
        record_button.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                record_button.setVisibility(view.GONE);
                pause_button.setVisibility(view.VISIBLE);
            }
        }) ;

        //녹음 중단 버튼 클릭 시 이벤트
        pause_button.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                pause_button.setVisibility(view.GONE);
                record_button.setVisibility(view.VISIBLE);
            }
        }) ;

        return view;
    }

    public recordFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


}