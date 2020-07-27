package com.example.myapp07;

import androidx.appcompat.app.AppCompatActivity;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
//1.とりあえずボタンで録音を開始すする
//https://www.it-swarm-ja.tech/ja/android/android%ef%bc%9a%e3%82%b5%e3%82%a6%e3%83%b3%e3%83%89%e3%83%ac%e3%83%99%e3%83%ab%e3%82%92%e6%a4%9c%e5%87%ba%e3%81%99%e3%82%8b/1069885029/
//ある瞬間の音を検知し，そこから5秒間の音声を録音する
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;

public class MainActivity extends AppCompatActivity {
    Button playButton,stopButton;
    TextView textView;
    private MediaPlayer mp;
    private MediaRecorder rec;
    /* 録音先のパス */
    static final String filePath = Environment.getExternalStorageDirectory() + "/sample.wav";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        指定したレイアウトxmlファイルと関連付け
        setContentView(R.layout.activity_main);
//        IDから名前検索
        playButton = findViewById(R.id.PlayButton);
        stopButton = findViewById(R.id.StopButton);
        textView = findViewById(R.id.textView);
        mp = MediaPlayer.create(this, R.raw.bgm);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // テキストを設定して表示
                Log.d("button","click p");
                textView.setText("Click Play");
//                startPlay();
                mediarecoderStart();
//                startRecord();
            }
        });
        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // テキストを設定して表示
                textView.setText("Click Stop");
//                stopRecord();
                mediaRecoderStop();
            }
        });
    }

    private void startRecord() {
        /* ファイルが存在する場合は削除 */
        File wavFile = new File(filePath);
        if (wavFile.exists()) {
            wavFile.delete();
        }
        wavFile = null;
        try {
            rec = new MediaRecorder();
            rec.setAudioSource(MediaRecorder.AudioSource.MIC);
            rec.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
            rec.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
            rec.setOutputFile(filePath);

            rec.prepare();
            rec.start();
            textView.setText("Rec Now");
        } catch(Exception e){
            e.printStackTrace();
        }
    }
    //参考サイト    https://hack-le.com/androidrecplay/
    // 再生 ちなみにエラーも何も出ずにunfortunity stop する　パスが合っていないのだろうか
    private void startPlay() {
        mp = MediaPlayer.create(this, R.raw.bgm);
        try {
            mp.prepare();
            mp.start();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void stopRecord() {
        try {
            rec.stop();
            rec.reset();
            rec.release();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    /*
    protected void saveText(){
        String message = "";
        String fileName = filenameText.getText().toString();
        String inputText = contentText.getText().toString();

        try {
            FileOutputStream outStream = openFileOutput(fileName, MODE_PRIVATE);
            OutputStreamWriter writer = new OutputStreamWriter(outStream);
            writer.write(inputText);
            writer.flush();
            writer.close();

            message = "File saved.";
        } catch (FileNotFoundException e) {
            message = e.getMessage();
            e.printStackTrace();
        } catch (IOException e) {
            message = e.getMessage();
            e.printStackTrace();
        }

        Toast.makeText(getBaseContext(), message, Toast.LENGTH_LONG).show();
    }
    */
//    https://techbooster.org/android/multimedia/2109/
    MediaRecorder recorder;
    void mediarecoderStart(){
        recorder = new MediaRecorder();
//        audioSourceにエラーが出たので（最初はMIC）
//        https://developer.android.com/reference/android/media/MediaRecorder?hl=ja#setAudioSource(int)
//        原因はアプリ側のパーミッションのせってい　
//        https://teratail.com/questions/58057
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
        //外部ストレージの保存先
        String filePath = Environment.getExternalStorageDirectory() + "/audio.3gp";
//        内部ストレージの保存先
        filePath = Environment.getExternalStorageDirectory().getPath() + "/Audio/tomoaudio.3gp";
        recorder.setOutputFile(filePath);
//        strage/emulated/0/audio.3gp
        textView.setText("Rec2 Now");
        //録音準備＆録音開始
        try {
            recorder.prepare();
        } catch (Exception e) {
            e.printStackTrace();
        }
        recorder.start();   //録音
        filePath = Environment.getExternalStorageDirectory()+ "/audio.3gp";
        textView.setText(filePath);
    }
    void mediaRecoderStop(){
        recorder.stop();
        recorder.reset();   //オブジェクトのリセット
        //release()前であればsetAudioSourceメソッドを呼び出すことで再利用可能
        recorder.release(); //Recorderオブジェクトの解
    }

}