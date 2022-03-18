package com.example.ae_android.ui.mmse;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentManager;

import com.example.ae_android.R;

public class MMSECustomDialog extends Dialog {
    private Button testBtn;
    private Button passBtn;
    private Intent intent;

    public MMSECustomDialog(@NonNull Context context) {
        super(context);
        setContentView(R.layout.mmse_custom_dialog);

        testBtn = findViewById(R.id.mmse_dialog_test_btn);
        passBtn = findViewById(R.id.mmse_dialog_pass_btn);

        testBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(isShowing()) {
                    // 화면 전환
                }
            }
        });
        passBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });
    }
}
