package com.example.ae_android.ui;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.ae_android.R;


public class coloringFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_coloring, container, false);

        ImageButton imageUpload_button = view.findViewById(R.id.imageUpload_button);

        //사진등록 버튼 클릭시 이벤기
        imageUpload_button.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                //팝업 띄우기
                final View coloringPopup = getLayoutInflater().inflate(R.layout.fragment_coloring_popup, null);
                final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setView(coloringPopup);

                final AlertDialog alertDialog = builder.create();
                alertDialog.show();



                }
        }) ;

        return view;

    }

    public coloringFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

}