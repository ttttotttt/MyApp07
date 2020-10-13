package com.example.myapp07;
//Github連携https://po-what.com/link-github-androidstudio

import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;

import android.media.MediaCodec;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.content.ActivityNotFoundException;
import android.speech.RecognizerIntent;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;
import java.util.Locale;

import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.media.MicrophoneInfo;

import android.media.AudioFormat;
import android.media.AudioRecord;

import java.util.Timer;
import java.util.TimerTask;

import android.media.MediaMuxer;
import android.media.MediaMuxer.OutputFormat;
import android.media.MediaFormat;

import static java.lang.Math.max;

public class MainActivity extends AppCompatActivity {
    //ボタン押下のデバック用
    Button button;
    TextView textView;
    private TextView scView;
    AudioRecord recordMic, recordComover;

    //タスクを実行するタイマー http://android-note.open-memo.net/sub/event__schedule_task_with_timer.html
    Timer timer;
    int filenameNum =0 ;
    //音声認識用
    private static final int REQUEST_CODE = 1000;
    private int lang ;
    // the key constant
    public static final String EXTRA_MESSAGE
//            = "com.example.testactivitytrasdata.MESSAGE";
            = "YourPackageName.MESSAGE";

    static final int RESULT_SUBACTIVITY = 1000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //指定したレイアウトxmlファイルと関連付け（呪文）
        setContentView(R.layout.activity_main);


        //音声認識試し　https://akira-watson.com/android/recognizerintent.html
        // 言語選択 0:日本語、1:英語、2:オフライン、その他:General
        lang = 0;

        // 認識結果を表示させる
        textView = (TextView)findViewById(R.id.textView);

        final Button buttonStart = (Button)findViewById(R.id.recognizeButton);
        //左のボタンの挙動
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
        //右のボタンの挙動
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Intent intent = new Intent(getApplication(), SubScreenActivity.class);
                if(editText.getText() != null){
                    String str = editText.getText().toString();
                    intent.putExtra(EXTRA_MESSAGE, str);
                }
                //音声認識
//                startSpeechRecognition();
                startActivityForResult( intent, RESULT_SUBACTIVITY );

                // in order to clear the edittext
                editText.setText("lalaland");
            }
        });



        textView = findViewById(R.id.textView);
        //録音開始ボタン
        timer = new Timer();
        button = findViewById(R.id.PlayButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // テキストを設定して表示
                Log.d("button","Click start");
                textView.setText("Click Play");
                initAudioRecord();
                audioRcordStart();
//                mediarecoderStart(5000);
                //10ミリ秒後に5900ミリ秒間隔でタスク実行
//                timer.scheduleAtFixedRate(
//                        new TimerTask()
//                        {
//                            @Override
//                            public void run()
//                            {
//                                filenameNum+=1;
//                                Log.d("rec","recNow");
//
////                                mediarecoderStart(5000);
//                            }
//                        }, 10, 5900);
            }
        });

        //録音停止ボタン
        button = findViewById(R.id.StopButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // テキストを設定して表示
                textView.setText("Click Stop");
                Log.d("button","Click Stop");
                audioRcordStop();
//                mediaRecoderStop();
//                timer.cancel();
//                timer = new Timer();
            }
        });
    }
    //音声認識
    private void speech(){
        // 音声認識の　Intent インスタンス
        Intent recognizIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        if(lang == 0){
            // 日本語
            recognizIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,Locale.JAPAN.toString() );
        }
        else if(lang == 1){
            // 英語
            recognizIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ENGLISH.toString() );
        }
        else if(lang == 2){
            // Off line mode
            recognizIntent.putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, true);
        }
        else{
            recognizIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        }

        recognizIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 100);
        recognizIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "音声を入力");

        //音声認識インテントからのオーディオの録音/保存 https://www.it-swarm-ja.tech/ja/android/%E9%9F%B3%E5%A3%B0%E8%AA%8D%E8%AD%98%E3%82%A4%E3%83%B3%E3%83%86%E3%83%B3%E3%83%88%E3%81%8B%E3%82%89%E3%81%AE%E3%82%AA%E3%83%BC%E3%83%87%E3%82%A3%E3%82%AA%E3%81%AE%E9%8C%B2%E9%9F%B3%E4%BF%9D%E5%AD%98/1046175013/
        // secret parameters that when added provide audio url in the result
        recognizIntent.putExtra("Android.speech.extra.GET_AUDIO_FORMAT", "audio/AMR");
        recognizIntent.putExtra("Android.speech.extra.GET_AUDIO", true);

        try {
            // インテント発行
            startActivityForResult(recognizIntent, REQUEST_CODE);
//            startActivityForResult(intent, REQUEST_CODE);
//            mediaPlayerStart();
        }
        catch (ActivityNotFoundException e) {
            e.printStackTrace();
            textView.setText(R.string.error);
        }
    }

        protected void onActivityResult( int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("url","intent?");

//        if(resultCode == RESULT_OK && requestCode == RESULT_SUBACTIVITY &&
//                null != data/*intent*/) {
//            String res = data/*intent*/.getStringExtra(ScreenActivity.EXTRA_MESSAGE);
//            scView.setText(res);
//        }


        // 音声認識
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

        //音声認識インテントからのオーディオの録音/保存
        // the resulting text is in the getExtras:
        Bundle bundle = data.getExtras();
        //マッチの中で一番の候補とか選べる部分
        ArrayList<String> matches = bundle.getStringArrayList(RecognizerIntent.EXTRA_RESULTS);
        Log.d("url", matches.get(0));
        Bundle ams =data.getBundleExtra("Android.speech.extra.GET_AUDIO_FORMAT");
//        Log.d("url", ams.getString("Android.speech.extra.GET_AUDIO_FORMAT"));
//        Log.d("dataaction",data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS).get(0));
        // the recording url is in getData:

        try {
            Uri audioUri = data.getData();
               String stringuri;
            if (audioUri == null) {
                stringuri = "uri=null";
            } else {
                stringuri = audioUri.toString();
            }
            Log.d("url", stringuri);
            ContentResolver contentResolver = getContentResolver();
            InputStream filestream = contentResolver.openInputStream(audioUri);
        }catch (Exception e) {Log.d("url", "stringuri");
            e.printStackTrace();
        }
            textView.setText(R.string.error);

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

    //音の録音 https://techbooster.org/android/multimedia/2109/
    //AndroidStudioDocuments https://developer.android.com/reference/android/media/MediaRecorder?hl=ja#getActiveMicrophones()
    MediaRecorder recorder=null,recorder2=null;
    void mediarecoderStart(int nMilliRec){
        recorder = new MediaRecorder();
//        String h = mediaDevices.enumerateDevices();
//        audioSourceにエラーが出たので（最初はMIC）
//        https://developer.android.com/reference/android/media/MediaRecorder?hl=ja#setAudioSource(int)
//        原因はアプリ側のパーミッションのせってい
//        https://teratail.com/questions/58057
        //AudioSource https://developer.android.com/reference/android/media/MediaRecorder.AudioSource?hl=ja#VOICE_RECOGNITION
        recorder.setAudioSource(MediaRecorder.AudioSource.VOICE_RECOGNITION);
//        VOICE_RECOGNITION)
        recorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
        //外部ストレージの保存先
        String filePath;
//        filePath= Environment.getExternalStorageDirectory() + "/audio.3gp";
//        内部ストレージの保存先(/Audioは端末のパスをみながら入力)
        filePath = Environment.getExternalStorageDirectory().getPath() + "/tomoaudio"+filenameNum+".mp3";
//        filePath = Environment.getExternalStorageDirectory().getPath() + "/konchiwa.mp3";
        recorder.setOutputFile(filePath);
        recorder.setMaxDuration(nMilliRec);
        recorder.setAudioChannels(2);

        //マイクの設定？https://developer.android.com/reference/android/media/MediaRecorder?hl=ja#setPreferredMicrophoneDirection(int)
//        recorder.setPreferredMicrophoneDirection(3);
//        strage/emulated/0/audio.3gp
        List<MicrophoneInfo> m;
//        m = recorder.getActiveMicrophones();

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
//        recorder2.stop();
//        recorder2.reset();   //オブジェクトのリセットy
//        recorder2.release(); //Recorderオブジェクトの解
    }

    //音の再生 http://android-dev-talk.blogspot.com/2012/06/mediaplayerbgm.html
    MediaPlayer mp = null;
    void mediaPlayerStart(){
        String filePath;
//        filePath= Environment.getExternalStorageDirectory() + "/audio.3gp";
//        内部ストレージの保存先(/Audioは端末のパスをみながら入力)
        filePath = Environment.getExternalStorageDirectory().getPath() + "/konchiwa.mp3";
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

    //音の録音
    AudioRecord ar;
    boolean bIsRecording = true;
    int samplingRate,frameRate,oneFrameDataCount,oneFrameSizeInByte,audioBufferSizeInByte,bufSize;
    private WaveFile wav = new WaveFile();
    private short[] shortData,shortDataCam;
    //http://steavevaivai.hatenablog.com/entry/2015/01/03/225505
    void initAudioRecord(){
        String filePath;
        filePath = Environment.getExternalStorageDirectory().getPath() + "/wabMic.wav";
        wav.createFile(filePath);
        // サンプリングレート (Hz)
        // 全デバイスサポート保障は44100のみ
        samplingRate = 44100;

        // AudioRecordオブジェクトを作成
        bufSize = android.media.AudioRecord.getMinBufferSize(samplingRate,
                AudioFormat.CHANNEL_IN_MONO,
//                AudioFormat.CHANNEL_IN_STEREO,
                AudioFormat.ENCODING_PCM_16BIT);
//                AudioFormat.ENCODING_PCM_16BIT)*2;
        ar = new AudioRecord(
                //CAMCORDER=5は内部マイク,MIC=1は外部マイク,DEFAULT=0は外部,VOICE_RECOGNITION外部
//                MediaRecorder.AudioSource.CAMCORDER,
                MediaRecorder.AudioSource.MIC,
                samplingRate,
                AudioFormat.CHANNEL_IN_MONO,
//                AudioFormat.CHANNEL_IN_STEREO,
                AudioFormat.ENCODING_PCM_16BIT,
                bufSize);
        shortData = new short[bufSize/2];
        // コールバックを指定
        ar.setRecordPositionUpdateListener(new AudioRecord.OnRecordPositionUpdateListener() {
            // フレームごとの処理
            @Override
            public void onPeriodicNotification(AudioRecord recorder) {
                // TODO Auto-generated method stub
                ar.read(shortData, 0, bufSize/2); // 読み込む
                Log.d("AudioRecord", "read " + shortData.length + " bytes");;
                wav.addBigEndianData(shortData); // ファイルに書き出す
            }

            @Override
            public void onMarkerReached(AudioRecord recorder) {
                // TODO Auto-generated method stub
            }
        });
        // コールバックが呼ばれる間隔を指定
        ar.setPositionNotificationPeriod(bufSize/2);
    }

    void audioRcordStart(){
        ar.startRecording();
    }
//    AudioRecord関連
//　　　https://gitlab.com/axet/android-audio-library/blob/master/src/main/java/com/github/axet/audiolibrary/app/Sound.java
//    http://blog.livedoor.jp/sce_info3-craft/archives/8280133.html
//    https://seesaawiki.jp/w/moonlight_aska/d/%a5%c7%a1%bc%a5%bf%a5%b9%a5%c8%a5%ea%a1%bc%a5%e0%a4%c8%a4%b7%a4%c6%b2%bb%c0%bc%a5%c7%a1%bc%a5%bf%a4%f2%c6%c9%a4%df%b9%fe%a4%e0
//　　　https://qiita.com/ino-shin/items/214dba25f49fa098402f
//    http://steavevaivai.hatenablog.com/entry/2015/01/03/225505

    void audioRcordStop(){
        Log.d("AudioRecord", "stop");
        ar.stop();
//        ar.release();
        bIsRecording=false;
    }
//    MediaMuxer muxer;
//    void MediaMuxerStart(){
//        muxer = new MediaMuxer("temp.mp4", OutputFormat.MUXER_OUTPUT_MPEG_4);
//        //多くの場合、MediaFormatはMediaCodec.getOutputFormat（）から取得されます
//        //またはMediaExtractor.getTrackFormat（）。
//        MediaFormat audioFormat = new MediaFormat();
//        MediaFormat videoFormat = new MediaFormat();
//        int audioTrackIndex = muxer.addTrack(audioFormat);
//        int videoTrackIndex = muxer.addTrack(videoFormat();
//        ByteBuffer inputBuffer = ByteBuffer.allocate(bufferSize);
//        boolean finished = false;
//        MediaCodec.BufferInfo bufferInfo = new BufferInfo();
//
//        muxer.start();
//        while(!finished) {
//            // getInputBuffer() will fill the inputBuffer with one frame of encoded
//            // sample from either MediaCodec or MediaExtractor, set isAudioSample to
//            // true when the sample is audio data, set up all the fields of bufferInfo,
//            // and return true if there are no more samples.
//            finished = getInputBuffer(inputBuffer, isAudioSample, bufferInfo);
//            if (!finished) {
//                int currentTrackIndex = isAudioSample ? audioTrackIndex : videoTrackIndex;
//                muxer.writeSampleData(currentTrackIndex, inputBuffer, bufferInfo);
//            }
//        };
//    }
//    void MediaMuxerStop(){
//        muxer.stop();
//        muxer.release();
//    }

    //今日の日付と時刻を取得
    private String getToday() {
        Date date = new Date();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
        return sdf.format(date);
    }

}