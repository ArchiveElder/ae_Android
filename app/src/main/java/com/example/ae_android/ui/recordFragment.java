package com.example.ae_android.ui;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.media.Image;
import android.os.Build;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.speech.RecognitionListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.example.ae_android.MainActivity;
import com.example.ae_android.NaverRecognizer;
import com.example.ae_android.R;
import com.example.ae_android.ui.mmse.MMSEFragment;
import com.example.ae_android.utils.AudioWriterPCM;
import com.naver.speech.clientapi.SpeechRecognitionResult;

import java.lang.ref.WeakReference;
import java.util.List;


public class recordFragment extends Fragment {

    //CSR API 상수 선언
    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String CLIENT_ID = "";

    private RecognitionHandler handler;
    private NaverRecognizer naverRecognizer;
    private String csrResult;
    private AudioWriterPCM writer;

    //XML 변수 선언
    private EditText record_editText;
    private ImageButton record_button;
    private ImageButton pause_button;


    public recordFragment() {
        // Required empty public constructor
    }

    //음성 인식 메시지를 처리
    private void handleMessage(Message msg){
        switch (msg.what) {
            //음성 인식을 시작할 준비가 완료된 경우
            case R.id.clientReady:
                System.out.println("Connected");
                writer = new AudioWriterPCM(Environment.getExternalStorageDirectory().getAbsolutePath() + "/NaverSpeechTest");
                writer.open("Text");
                break;
             //현재 음성 인식이 진행되고 있는 경우
            case R.id.audioRecording:
                writer.write((short[]) msg.obj);
                break;
            //처리가 되고 있는 도중에 결과를 받은 경우
            case R.id.partialResult:
                csrResult = (String)(msg.obj);
                record_editText.setText(csrResult);
                break;
            //최종 인식이 완료되면 유사 결과를 모두 보여주기
            case R.id.finalResult:
                SpeechRecognitionResult speechRecognitionResult = (SpeechRecognitionResult) msg.obj;
                List<String> results = speechRecognitionResult.getResults();
                StringBuilder strBuf = new StringBuilder();
                //전달 받은 모든 문자열을 차례대로 출력
                for(String result : results){
                    strBuf.append(result);
                    strBuf.append("\n");
                }
                csrResult = strBuf.toString();
                record_editText.setText(csrResult);
                break;
             //인식 오류가 발생한 경우
            case R.id.recognitionError:
                if(writer != null){
                    writer.close();
                }
                csrResult = "Error code : " + msg.obj.toString();
                record_editText.setText(csrResult);
                record_button.setEnabled(true);
                pause_button.setEnabled(true);
                break;
             //음성 인식 비활성화 상태인 경우
            case R.id.clientInactive:
                if(writer != null) {
                    writer.close();
                }
                record_button.setEnabled(true);
                pause_button.setEnabled(true);
                break;

        }
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_record, container, false);

        //XML 변수 선언
        record_editText = view.findViewById(R.id.record_editText);
        record_button = view.findViewById(R.id.record_button);
        pause_button = view.findViewById(R.id.pause_button);

        //CSR API 변수 선언
        handler = new RecognitionHandler(this);
        naverRecognizer = new NaverRecognizer(getContext(), handler, CLIENT_ID);

        //녹음 버튼 클릭 시 이벤트
        record_button.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                record_button.setVisibility(view.GONE);
                pause_button.setVisibility(view.VISIBLE);

                //사용자의 OS 버전이 마시멜로우 이상일 경우
                if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                    int permissionResult = getContext().checkSelfPermission(Manifest.permission.RECORD_AUDIO);
                    if(permissionResult == PackageManager.PERMISSION_DENIED) {
                        if(shouldShowRequestPermissionRationale(Manifest.permission.RECORD_AUDIO)) {
                            AlertDialog.Builder dialog = new AlertDialog.Builder(getContext());
                            dialog.setTitle("권한이 필요합니다.")
                                    .setMessage("이 기능을 사용하기 위해서는 권한이 필요합니다. 계속하시겠습니까?")
                                    .setPositiveButton("네", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                        requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, 1000);
                                    }
                                }
                            }).setNegativeButton("아니오", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(getContext(), "기능을 취소했습니다.", Toast.LENGTH_SHORT).show();
                                }
                            }).create().show();
                        }
                        //최초로 권한을 요청하는 경우
                        else{
                            requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO}, 1000);
                        }
                    }
                    //권한이 있는 경우 음성 인식 기능 처리
                    else{
                        if(!naverRecognizer.getSpeechRecognizer().isRunning()){
                            csrResult = "";
                            record_editText.setText("Connecting...");
                            naverRecognizer.recognize();
                        } else{
                            Log.d(TAG,"Stop and Wait Final Result");
                            record_button.setEnabled(false);
                            naverRecognizer.getSpeechRecognizer().stop();
                        }
                    }
                }
                //사용자의 OS 버전이 마시멜로우 이하일 때
                else{
                    //음성 인식 기능 처리
                        if(!naverRecognizer.getSpeechRecognizer().isRunning()){
                            csrResult = "";
                            record_editText.setText("Connecting...");
                            naverRecognizer.recognize();
                        } else{
                            Log.d(TAG, "Stop and Wait Final Result");
                            record_button.setEnabled(false);
                            naverRecognizer.getSpeechRecognizer().stop();
                        }
                }
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

    @Override
    public void onStart(){
        super.onStart();
        //음성인식 서버 초기화를 진행
        naverRecognizer.getSpeechRecognizer().initialize();
    }

    @Override
    public void onResume() {
        super.onResume();
        csrResult = "";
        record_editText.setText("");
        record_button.setEnabled(true);
    }

    @Override
    public void onStop(){
        super.onStop();
        //음성인식 서버 종료
        naverRecognizer.getSpeechRecognizer().release();
    }

    static class RecognitionHandler extends Handler {
        private final WeakReference<recordFragment> rFragment;
        RecognitionHandler(recordFragment fragment) {
            rFragment = new WeakReference<recordFragment>(fragment);
        }

        @Override
        public void handleMessage(Message msg){
            recordFragment fragment = rFragment.get();
            if(fragment != null) {
                fragment.handleMessage(msg);
            }
        }

    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


}