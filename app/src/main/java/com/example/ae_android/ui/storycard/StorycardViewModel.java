package com.example.ae_android.ui.storycard;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

public class StorycardViewModel extends ViewModel{

    private final MutableLiveData<String> mText;

    public StorycardViewModel() {
        mText = new MutableLiveData<>();
        mText.setValue("This is storycard fragment");
    }

    public LiveData<String> getText() {
        return mText;
    }
}
