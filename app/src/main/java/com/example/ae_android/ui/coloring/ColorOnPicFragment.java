package com.example.ae_android.ui.coloring;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.example.ae_android.R;

import org.opencv.android.OpenCVLoader;
import org.opencv.android.Utils;
import org.opencv.core.Mat;


public class ColorOnPicFragment extends Fragment {

    ProgressDialog dlg ;
    private boolean isOpenCVReady = false;

    private Bitmap picture_copy;
    private Bitmap original_copy;
    private ImageView canvas;

    public native void detectEdgeJNI(long inputImage, long outputImage, int th1, int th2);

    public native void MeanShiftFilteringJNI(long inputImage,  long outputImage,  double sp, double sr);

    static {
        System.loadLibrary("opencv_java4");
        System.loadLibrary("native-lib");
    }

    public void ColoringProcessing(double i, double j) {
        if (!isOpenCVReady) {
            return;
        }

        if(i == 0) {
            picture_copy = original_copy.copy(original_copy.getConfig(), true);
        } else {
            double sp = i;
            double sr = j;

            Mat src = new Mat();
            Utils.bitmapToMat(original_copy, src);
            Mat mean = new Mat();
            MeanShiftFilteringJNI(src.getNativeObjAddr(), mean.getNativeObjAddr(), sp, sr);
            Utils.matToBitmap(mean, picture_copy);

        }

        Mat src2 = new Mat();
        Utils.bitmapToMat(picture_copy, src2);
        Mat edge = new Mat();
        detectEdgeJNI(src2.getNativeObjAddr(), edge.getNativeObjAddr(), 75, 175);
        Utils.matToBitmap(edge, picture_copy);

        //canvas.setImageBitmap(picture_copy);

        int sW = picture_copy.getWidth();
        int sH = picture_copy.getHeight();

        int[] pixels = new int[sW*sH];
        picture_copy.getPixels(pixels, 0, sW, 0, 0, sW, sH);
        for (int k = 0; k < pixels.length; k++) {
            if (pixels[k] == Color.WHITE)
                pixels[k] = Color.TRANSPARENT;
        }

        picture_copy = Bitmap.createBitmap(pixels, 0, sW, sW, sH, Bitmap.Config.ARGB_8888);

        canvas.setImageBitmap(picture_copy);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

         View view = inflater.inflate(R.layout.fragment_color_on_pic, container, false);



        return view;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        dlg = new ProgressDialog(getActivity());
        dlg.setMessage("잠시만 기다려주세요...");
        dlg.setProgressStyle(ProgressDialog.STYLE_SPINNER);


        if (OpenCVLoader.initDebug()) {
            isOpenCVReady = true;
        }

        // canvas = findViewById(캔버스 아이디);

        // byte[] bytes = intent.getByteArrayExtra("image"); 원본 이미지 가져오기 & 이미지 처리
        byte[] bytes = null;
        if (bytes != null) {
            original_copy = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);

            ColoringProcessing(3, 20.0);
        }

    }



}