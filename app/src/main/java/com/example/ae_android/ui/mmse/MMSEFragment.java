package com.example.ae_android.ui.mmse;

import androidx.lifecycle.ViewModelProvider;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.example.ae_android.NaverRecognizer;
import com.example.ae_android.R;
import com.example.ae_android.utils.AudioWriterPCM;
import com.naver.speech.clientapi.SpeechConfig;
import com.naver.speech.clientapi.SpeechRecognitionException;
import com.naver.speech.clientapi.SpeechRecognitionListener;
import com.naver.speech.clientapi.SpeechRecognitionResult;
import com.naver.speech.clientapi.SpeechRecognizer;

import java.lang.ref.WeakReference;
import java.util.List;

public class MMSEFragment extends Fragment {

    private static final String CLIENT_ID = "i5sifrmz5f";
    private RecognitionHandler handler;
    private NaverRecognizer naverRecognizer;
    private AudioWriterPCM writer;
    private String mResult;
    private TextView mmse_answer;


    private void handleMessage(Message msg) {
        switch (msg.what) {
            // 음성 인식을 시작할 준비가 완료된 경우
            case R.id.clientReady:
                writer = new AudioWriterPCM(Environment.getExternalStorageDirectory().getAbsolutePath() + "/NaverSpeechTest");
                writer.open("Test");
                break;
            // 현재 음성 인식이 진행되고 있는 경우
            case R.id.audioRecording:
                writer.write((short[]) msg.obj);
                break;
            // 처리가 되고 있는 도중에 결과를 받은 경우
            case R.id.partialResult:
                mResult = (String) (msg.obj);
                mmse_answer.setText(mResult);
                break;
            // 최종 인식이 완료되면 유사 결과를 모두 보여준다.
            case R.id.finalResult:
                SpeechRecognitionResult speechRecognitionResult = (SpeechRecognitionResult) msg.obj;
                List<String> results = speechRecognitionResult.getResults();
                StringBuilder strBuf = new StringBuilder();
                Integer i = 0;
                for(String result : results) {
                    if(i == 0) {
                        strBuf.append(result);
                        i += 1;
                    }
                }
                mResult = strBuf.toString();
                mmse_answer.setText(mResult);
                break;
            // 인식 오류가 발생한 경우
            case R.id.recognitionError:
                if (writer != null) {
                    writer.close();
                }
                mResult = "Error code : " + msg.obj.toString();
                mmse_answer.setText(mResult);
                break;
            // 음성 인식 비활성화 상태인 경우
            case R.id.clientInactive :
                if (writer != null) {
                    writer.close();
                }
                break;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.mmse_fragment, container, false);

        ImageButton mmse_record_button = view.findViewById(R.id.mmse_record_button);
        ImageButton mmse_pause_button = view.findViewById(R.id.mmse_pause_button);
        mmse_answer = view.findViewById(R.id.mmse_answer);
        handler = new RecognitionHandler(this);
        naverRecognizer = new NaverRecognizer(getContext(), handler, CLIENT_ID);

        //녹음 버튼 클릭 시 이벤트
        mmse_record_button.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
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
                            mResult = "";
                            mmse_answer.setText("Connecting...");
                            mmse_pause_button.setVisibility(view.VISIBLE);
                            mmse_record_button.setVisibility(view.GONE);
                            naverRecognizer.recognize();
                        } else{
                            mmse_pause_button.setVisibility(view.GONE);
                            mmse_record_button.setVisibility(view.VISIBLE);
                            naverRecognizer.getSpeechRecognizer().stop();
                        }
                    }
                }
                //사용자의 OS 버전이 마시멜로우 이하일 때
                else{
                    //음성 인식 기능 처리
                    if(!naverRecognizer.getSpeechRecognizer().isRunning()){
                        mResult = "";
                        mmse_answer.setText("Connecting...");
                        mmse_pause_button.setVisibility(view.VISIBLE);
                        mmse_record_button.setVisibility(view.GONE);
                        naverRecognizer.recognize();
                    } else{
                        mmse_pause_button.setVisibility(view.GONE);
                        mmse_record_button.setVisibility(view.VISIBLE);
                        naverRecognizer.getSpeechRecognizer().stop();
                    }
                }
            }
        }) ;

        //녹음 중단 버튼 클릭 시 이벤트
        mmse_pause_button.setOnClickListener(new Button.OnClickListener() {
            @Override
            public void onClick(View view) {
                mmse_pause_button.setVisibility(view.GONE);
                mmse_record_button.setVisibility(view.VISIBLE);
            }
        }) ;

        return view;
    }

    public MMSEFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }
    @Override
    public void onStart() {
        super.onStart(); // 음성인식 서버 초기화는 여기서
        naverRecognizer.getSpeechRecognizer().initialize();
    }
    @Override
    public void onResume() {
        super.onResume();
        mResult = "";
        mmse_answer.setText("");
    }
    @Override
    public void onStop() {
        super.onStop();
        naverRecognizer.getSpeechRecognizer().release();
    }

    static class RecognitionHandler extends Handler {
        private final WeakReference<MMSEFragment> mFragment;
        RecognitionHandler(MMSEFragment fragment) {
            mFragment = new WeakReference<MMSEFragment>(fragment);
        }
        @Override
        public void handleMessage(Message msg) {
            MMSEFragment mmseFragment = mFragment.get();
            if (mmseFragment != null) {
                mmseFragment.handleMessage(msg);
            }
        }
    }
}