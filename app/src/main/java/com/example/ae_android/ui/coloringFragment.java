package com.example.ae_android.ui;

import static android.app.Activity.RESULT_OK;

import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.fragment.app.Fragment;

import android.provider.MediaStore;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.example.ae_android.R;


public class coloringFragment extends Fragment {

    public coloringFragment() {
        // Required empty public constructor
    }

    static AlertDialog alertDialog;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_coloring, container, false);

        ImageButton imageUpload_button = view.findViewById(R.id.imageUpload_button);

        //사진등록 버튼 클릭시 이벤트
        imageUpload_button.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                //팝업 띄우기
                final View coloringPopup = getLayoutInflater().inflate(R.layout.fragment_coloring_popup, null);
                final AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setView(coloringPopup);

                alertDialog = builder.create();
                alertDialog.show();

                //팝업 내 변수들 선언: coloringPopupFragment에서 구현하면 안됨!!! 여기서 해야함
                ImageButton pickPhoto_Button = coloringPopup.findViewById(R.id.pickPhoto_button);
                ImageButton takePhoto_Button = coloringPopup.findViewById(R.id.takePhoto_button);

                //컬러링할 사진 고르기 버튼
                pickPhoto_Button.setOnClickListener(new Button.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        System.out.println("야야");
                        //갤러리 호출
                        Intent intent = new Intent(Intent.ACTION_PICK);
                        intent.setDataAndType(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                        intent.setAction(Intent.ACTION_PICK);
                        activityResultLauncher.launch(intent);
                    }

                }) ;
                
                //그린 그림 찍어 올리기 버튼
                takePhoto_Button.setOnClickListener(new View.OnClickListener(){
                    @Override
                    public void onClick(View view) {

                    }
                });
                }
        }) ;

        return view;

    }


    //컬러링할 사진 고르기 버튼->갤러리 호출 시 콜백함수
    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if(result.getResultCode() == RESULT_OK) {
                        Intent intent = result.getData();
                        Uri uri = intent.getData();
                    }
                }
            }
    );

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

}