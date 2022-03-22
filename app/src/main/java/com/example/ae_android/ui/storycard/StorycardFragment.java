package com.example.ae_android.ui.storycard;

import android.Manifest;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
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
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.example.ae_android.MainActivity;
import com.example.ae_android.NaverRecognizer;
import com.example.ae_android.R;
import com.example.ae_android.databinding.FragmentStorycardBinding;
import com.example.ae_android.utils.AudioWriterPCM;
import com.naver.speech.clientapi.SpeechRecognitionResult;

import java.lang.ref.WeakReference;
import java.util.List;

public class StorycardFragment extends DialogFragment {

    private static final String TAG = MainActivity.class.getSimpleName();
    private static final String CLIENT_ID = "i5sifrmz5f";

    private StorycardFragment.RecognitionHandler handler;
    private NaverRecognizer naverRecognizer;
    private String csrResult;
    private AudioWriterPCM writer;

    private FragmentStorycardBinding binding;

    public void handleMessage(Message msg){
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
                binding.answerEditText.setText(csrResult);
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
                binding.answerEditText.setText(csrResult);
                break;
            //인식 오류가 발생한 경우
            case R.id.recognitionError:
                if(writer != null){
                    writer.close();
                }
                csrResult = "Error code : " + msg.obj.toString();
                binding.answerEditText.setText(csrResult);
                binding.recordButton.setText("녹음하기");
                binding.recordButton.setEnabled(true);
                break;
            //음성 인식 비활성화 상태인 경우
            case R.id.clientInactive:
                if(writer != null) {
                    writer.close();
                }
                binding.recordButton.setText("녹음하기");
                binding.recordButton.setEnabled(true);
                break;

        }
    }

    static class RecognitionHandler extends Handler {
        private final WeakReference<StorycardFragment> sFragment;
        RecognitionHandler(StorycardFragment fragment) {
            sFragment = new WeakReference<StorycardFragment>(fragment);
        }

        @Override
        public void handleMessage(Message msg){
            StorycardFragment fragment = sFragment.get();
            if(fragment != null) {
                fragment.handleMessage(msg);
            }
        }
    }

    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        StorycardViewModel storycardViewModel =
                new ViewModelProvider(this).get(StorycardViewModel.class);

        binding = FragmentStorycardBinding.inflate(inflater, container, false);
        View root = binding.getRoot();

        root.setClipToOutline(true);
        binding.widget1.setVisibility(View.VISIBLE);
        binding.widget2.setVisibility(View.GONE);

        binding.dismissButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        binding.answerButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.widget1.setVisibility(View.GONE);
                binding.widget2.setVisibility(View.VISIBLE);
            }
        });

        handler = new RecognitionHandler(this);
        naverRecognizer = new NaverRecognizer(getActivity(), handler, CLIENT_ID);
        binding.recordButton.setOnClickListener(new View.OnClickListener() {
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
                            csrResult = "";
                            binding.answerEditText.setText("Connecting...");
                            binding.recordButton.setText("정지하기");
                            naverRecognizer.recognize();
                        } else{
                            Log.d(TAG,"Stop and Wait Final Result");
                            binding.recordButton.setText("녹음하기");
                            naverRecognizer.getSpeechRecognizer().stop();
                        }
                    }
                }
                //사용자의 OS 버전이 마시멜로우 이하일 때
                else{
                    //음성 인식 기능 처리
                    if(!naverRecognizer.getSpeechRecognizer().isRunning()){
                        csrResult = "";
                        binding.answerEditText.setText("Connecting...");
                        binding.recordButton.setText("정지하기");
                        naverRecognizer.recognize();
                    } else{
                        Log.d(TAG, "Stop and Wait Final Result");
                        binding.recordButton.setText("녹음하기");
                        naverRecognizer.getSpeechRecognizer().stop();
                    }
                }
            }
        });

        //final TextView textView = binding.textGallery;
        //galleryViewModel.getText().observe(getViewLifecycleOwner(), textView::setText);

        this.setCancelable(false);

        return root;
    }

    @Override
    public void onStart() {
        super.onStart();
        naverRecognizer.getSpeechRecognizer().initialize();
    }

    @Override
    public void onResume() {
        super.onResume();

        binding.answerEditText.setText("");
        binding.recordButton.setText("녹음하기");
        binding.recordButton.setEnabled(true);

        Window window = getDialog().getWindow();
        Point size = new Point();

        Display display = window.getWindowManager().getDefaultDisplay();
        display.getSize(size);

        int width = size.x;

        window.setLayout((int) (width * 0.9), (int) (width * 0.95));
        window.setGravity(Gravity.CENTER);
    }

    @Override
    public void onStop() {
        super.onStop();
        naverRecognizer.getSpeechRecognizer().release();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

}
