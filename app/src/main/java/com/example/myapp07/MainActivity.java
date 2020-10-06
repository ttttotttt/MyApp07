package com.example.myapp07;

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
import android.content.ActivityNotFoundException;
import android.speech.RecognizerIntent;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity {
    Button playButton,stopButton;
    //ボタン押下のデバック用
    TextView textView;

    int filenameNum =0 ;

    //タスクを実行するタイマー http://android-note.open-memo.net/sub/event__schedule_task_with_timer.html
    Timer timer;


    //音声認識用
    private static final int REQUEST_CODE = 1000;

    private int lang ;

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

        timer = new Timer();

        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // テキストを設定して表示
                Log.d("button","startTimer");
                textView.setText("Click Play");
                timer.scheduleAtFixedRate(
                        new TimerTask()
                        {
                            @Override
                            public void run()
                            {
                                filenameNum+=1;
                                Log.d("rec","recNow");
                                mediarecoderStart(5000);
                            }
                        }, 10, 5900);
                //10ミリ秒後に100ミリ秒間隔でタスク実行
            }
        });

        stopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // テキストを設定して表示
                textView.setText("Click Stop");
//                Log.d("button","timerStop");
                Log.d("button","mplayer");
                mediaPlayerStart();
//                mediaRecoderStop();
//                timer.cancel();
//                timer = new Timer();
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
    // 参考サイト https://techbooster.org/android/multimedia/2109/
    //AndroidStudioDocuments https://developer.android.com/reference/android/media/MediaRecorder?hl=ja#getActiveMicrophones()
    MediaRecorder recorder;
    void mediarecoderStart(int nMilliRec){
        recorder = new MediaRecorder();
//        audioSourceにエラーが出たので（最初はMIC）
//        https://developer.android.com/reference/android/media/MediaRecorder?hl=ja#setAudioSource(int)
//        原因はアプリ側のパーミッションのせってい　
//        https://teratail.com/questions/58057
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
        //外部ストレージの保存先
        String filePath;
//        filePath= Environment.getExternalStorageDirectory() + "/audio.3gp";
//        内部ストレージの保存先(/Audioは端末のパスをみながら入力)
        filePath = Environment.getExternalStorageDirectory().getPath() + "/tomoaudio"+filenameNum+".mp3";
        recorder.setOutputFile(filePath);
        recorder.setMaxDuration(nMilliRec);
//        strage/emulated/0/audio.3gp

        //録音準備＆録音開始
        try {
            recorder.prepare();
            recorder.start();
        } catch (Exception e) {
            e.printStackTrace();
            mediaRecoderStop();
        }
    }
    void mediaRecoderStop(){
        recorder.stop();
        recorder.reset();   //オブジェクトのリセット
        //release()前であればsetAudioSourceメソッドを呼び出すことで再利用可能
        recorder.release(); //Recorderオブジェクトの解
    }

    //音の再生
    //http://android-dev-talk.blogspot.com/2012/06/mediaplayerbgm.html
    MediaPlayer mp = null;
    void mediaPlayerStart(){
        String filePath;
//        filePath= Environment.getExternalStorageDirectory() + "/audio.3gp";
//        内部ストレージの保存先(/Audioは端末のパスをみながら入力)
        filePath = Environment.getExternalStorageDirectory().getPath() + "/tomoaudio"+2+".mp3";
        mp = new MediaPlayer();
        try {
            mp.setDataSource(filePath);
            mp.prepare();
        } catch (IllegalArgumentException e) {
        } catch (SecurityException e) {
        } catch (IllegalStateException e) {
        } catch (IOException e) {
        }
        mp.start();
    }

    //今日の日付と時刻を取得
    private String getToday() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
        return sdf.format(date);
    }

}