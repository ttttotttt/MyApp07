package com.example.myapp07;
//gittest
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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

import android.os.Bundle;
import android.app.Activity;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.speech.RecognizerIntent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    Button playButton,stopButton;
    //ボタン押下のデバック用
    TextView textView;

    private MediaPlayer mp;
    private MediaRecorder rec;

//音声認識用
    private static final int REQUEST_CODE = 1000;

    private int lang ;

//画面遷移用のクラス
    private  ScreenActivity sc;

    /* 録音先のパス */
    static final String filePath = Environment.getExternalStorageDirectory() + "/sample.wav";
    // the key constant
    public static final String EXTRA_MESSAGE
//            = "com.example.testactivitytrasdata.MESSAGE";
            = "YourPackageName.MESSAGE";

    private TextView scView;
    static final int RESULT_SUBACTIVITY = 1000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        指定したレイアウトxmlファイルと関連付け
        setContentView(R.layout.activity_main);

//お㎜性認識　https://akira-watson.com/android/recognizerintent.html
        // 言語選択 0:日本語、1:英語、2:オフライン、その他:General
        lang = 0;

        // 認識結果を表示させる
        textView = (TextView)findViewById(R.id.textView);

        final Button buttonStart = (Button)findViewById(R.id.recognizeButton);
        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 音声認識を開始
                buttonStart.setText("recognaize!!");
                speech();
            }
        });

//        IDから名前検索
        scView = findViewById(R.id.recognaizeTextView);
        scView.setText("MainActivity!!");

        final TextView editText= findViewById(R.id.textView2);
        Button button = findViewById(R.id.activityButton);
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplication(), SubScreenActivity.class);
                if(editText.getText() != null){
                    String str = editText.getText().toString();
                    intent.putExtra(EXTRA_MESSAGE, str);
                }
                startActivityForResult( intent, RESULT_SUBACTIVITY );

                // in order to clear the edittext
                editText.setText("lalaland");
            }
        });

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
//音声認識
    private void speech(){
        // 音声認識の　Intent インスタンス
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        if(lang == 0){
            // 日本語
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,Locale.JAPAN.toString() );
        }
        else if(lang == 1){
            // 英語
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ENGLISH.toString() );
        }
        else if(lang == 2){
            // Off line mode
            intent.putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, true);
        }
        else{
            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        }

        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 100);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "音声を入力");

        try {
            // インテント発行
            startActivityForResult(intent, REQUEST_CODE);
        }
        catch (ActivityNotFoundException e) {
            e.printStackTrace();
            textView.setText(R.string.error);
        }
    }

    protected void onActivityResult( int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

//        if(resultCode == RESULT_OK && requestCode == RESULT_SUBACTIVITY &&
//                null != data/*intent*/) {
//            String res = data/*intent*/.getStringExtra(ScreenActivity.EXTRA_MESSAGE);
//            scView.setText(res);
//        }
//音声認識
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            // 認識結果を ArrayList で取得
            ArrayList<String> candidates =
                    data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

            if(candidates.size() > 0) {
                // 認識結果候補で一番有力なものを表示
                textView.setText( candidates.get(0));
            }
        }

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
//    参考サイト
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
//        内部ストレージの保存先(/Audioは端末のパスをみながら入力)
        filePath = Environment.getExternalStorageDirectory().getPath() + "/tomoaudio.3gp";
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