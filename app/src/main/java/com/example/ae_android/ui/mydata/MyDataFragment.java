package com.example.ae_android.ui.mydata;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.ae_android.R;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.interfaces.datasets.ILineDataSet;

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link MyDataFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class MyDataFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private LineChart chart;
    private ImageView backImg;
    private TextView score;

    public MyDataFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment MyDataFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static MyDataFragment newInstance(String param1, String param2) {
        MyDataFragment fragment = new MyDataFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_data, container, false);

        chart = view.findViewById(R.id.chart);
        backImg = view.findViewById(R.id.back_my_data);
        score = view.findViewById(R.id.data_score); // 딥러닝 결과로 점수 변경하게 해야 함

        backImg.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                // 이전 화면으로 이동
            }
        });

        ArrayList<Entry> values = new ArrayList<>();

        for(int i = 0; i < 5; i++) {
            float val = (float) (Math.random() * 80);
            values.add(new Entry(i, val));
        }

        LineDataSet set1;
        set1 = new LineDataSet(values, "DataSet 1");

        ArrayList<ILineDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);

        LineData data = new LineData(dataSets);

        set1.setColor(Color.BLUE);
        set1.setCircleColor(Color.BLUE);

        chart.setData(data);

        return view;
    }
}