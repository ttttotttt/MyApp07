package com.example.myapp07;
//Github連携https://po-what.com/link-github-androidstudio

import androidx.appcompat.app.AppCompatActivity;
import android.content.ContentResolver;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.speech.RecognitionListener;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.content.ActivityNotFoundException;
import android.speech.RecognizerIntent;
import 	android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
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

import static android.speech.RecognizerIntent.RESULT_NO_MATCH;
import static android.speech.RecognizerIntent.getVoiceDetailsIntent;
import static android.speech.SpeechRecognizer.ERROR_NO_MATCH;
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
    private Intent recognizIntent;
    private boolean recognizeActive;
    private SpeechRecognizer speechRecognizer;
    private String ss;
    // the key constant
    public static final String EXTRA_MESSAGE
//            = "com.example.testactivitytrasdata.MESSAGE";
            = "YourPackageName.MESSAGE";

    static final int RESULT_SUBACTIVITY = 1000;

    private TextToSpeech tts;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //指定したレイアウトxmlファイルと関連付け（呪文）
        setContentView(R.layout.activity_main);
        ss = getLocalClassName();

        initSpeech();

        // 認識結果を表示させる
        textView = (TextView)findViewById(R.id.textView);

        final Button buttonStart = (Button)findViewById(R.id.recognizeButton);

        startSpeech();
        //左のボタンの挙動
        buttonStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // 音声認識を開始
                buttonStart.setText("recognaize!!");

                startSpeech();
            }
        });

//        IDから名前検索
        scView = findViewById(R.id.recognaizeTextView);
        scView.setText("MainActivity!!");

        final TextView editText= findViewById(R.id.textView2);
        Button button = findViewById(R.id.activityButton);
        //右のボタンの挙動

        button.setText("mediaPlay");
        button.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                mediaPlayerStart("AudioSource.mp3");
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
                Log.d("button","Click to start");
                textView.setText("Record Start");
//                initAudioRecord(44100);
//                audioRcordStart();
                //10ミリ秒後に5900ミリ秒間隔でタスク実行
                timer.scheduleAtFixedRate(
                        new TimerTask()
                        {
                            @Override
                            public void run()
                            {
                                filenameNum+=1;
                                Log.d("rec","recNow");
                                mediarecoderStart(10000);
                            }
                        }, 10, 10900);
            }
        });

        //録音停止ボタン
        button = findViewById(R.id.StopButton);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // テキストを設定して表示
                Log.d("button","Click Stop");
                textView.setText("Record Stop");
                audioRcordStop();
//                mediaRecoderStop();
//                timer.cancel();
//                timer = new Timer();
            }
        });
    }

    //https://developer.android.com/reference/android/app/Activity#onResume()
    @Override
    protected void onResume() {
        super.onResume();
        if(recognizeActive) { startSpeech(); }
    }

    private void initSpeech(){

        //音声認識試し　https://akira-watson.com/android/recognizerintent.html
        // 言語選択 0:日本語、1:英語、2:オフライン、その他:General
        lang = 0;

        // 音声認識の　Intent インスタンス
        recognizIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        //putExtra 言語設定
        if(lang == 0){
            // 日本語
            recognizIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,Locale.JAPAN.toString() );
        } else if(lang == 1){
            // 英語
            recognizIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ENGLISH.toString() );
        } else if(lang == 2){
            recognizIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
            // Off line mode
            recognizIntent.putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, true);
        } else{
            recognizIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        }
        //
        recognizIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1000);
        recognizIntent.putExtra(RecognizerIntent.EXTRA_PROMPT, "音声を入力");
        //ユーザーのスピーチに応答してWeb検索のみを実行するかどうかを示します。
        recognizIntent.putExtra(RecognizerIntent.EXTRA_WEB_SEARCH_ONLY, false);
//        recognizIntent.putExtra(RecognizerIntent.RESULT_AUDIO_ERROR,1);

        recognizIntent.putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true);

        //音声認識インテントからのオーディオの録音/保存 https://www.it-swarm-ja.tech/ja/android/%E9%9F%B3%E5%A3%B0%E8%AA%8D%E8%AD%98%E3%82%A4%E3%83%B3%E3%83%86%E3%83%B3%E3%83%88%E3%81%8B%E3%82%89%E3%81%AE%E3%82%AA%E3%83%BC%E3%83%87%E3%82%A3%E3%82%AA%E3%81%AE%E9%8C%B2%E9%9F%B3%E4%BF%9D%E5%AD%98/1046175013/
        // secret parameters that when added provide audio url in the result
        recognizIntent.putExtra("android.speech.extra.GET_AUDIO_FORMAT", "audio/AMR");
        recognizIntent.putExtra("android.speech.extra.GET_AUDIO", true);

    }

    //音声認識
    private void startSpeech(){
        timer = new Timer();
        timer.scheduleAtFixedRate(new TimerTask() {
            @Override
            public void run() { if(recognizeActive) { startSpeech(); }

        try {
            startActivityForResult(recognizIntent, REQUEST_CODE);
            //SpeechRecognizerを使う音声認識
//            speechRecognizer();
            recognizeActive = true;
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
            textView.setText(R.string.error);
        }
            }
        }, 10, 5000);

    }

    //SpeechRecognizerを使う音声認識
    public void speechRecognizer(){
        SpeechRecognizer recognizer= SpeechRecognizer.createSpeechRecognizer(this);
        recognizer.setRecognitionListener(new RecognitionListener() {

            // ユーザーの話を聞く準備ができた時に呼ばれる
            public void	onReadyForSpeech(Bundle params) {
                // onResultsが2回呼ばれる不具合の対応
                recognizeActive = true;
            }

            // 認識結果の取得時に呼ばれる
            public void	onResults(Bundle results) {
                // onResultsが2回呼ばれる不具合の対応
                if (!recognizeActive) return;
                // 認識テキストの取得
                List<String> recData = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                textView.setText(recData.get(0));
                if (recData.size() > 0) {
//                        AddText(recData.get(0));
                }
                //テキストデータのセーブ
//                    saveFile("save.txt",recData.get(0)+","+getToday()+"\n");
//                    Log.d("saveText",readFile("save.txt"));

                //音声認識
                recognizeActive = false;
                startSpeech();
            }

            public void	onError(int error) {// ネットワークエラーか認識エラーが起きた時に呼ばれる
                // エラー表示
//                    AddText(error2str(error));
                // スリープ（これがないと次の音声認識に失敗することがある）
                try {
                    //次の認識ニ移るまでの秒数
                    Thread.sleep(500);
//                    Log.d("button","click cc");
                } catch (Exception e) {
//                    Thread.sleep(299);
                }
//                 音声認識
                recognizeActive = false;
                startSpeech();
            }

            // その他
            public void	onBeginningOfSpeech() {}    // 話し始めたときに呼ばれる
            public void	onEndOfSpeech() {}          // 話し終わった時に呼ばれる
            public void	onBufferReceived(byte[] buffer) {}  // 結果に対する反応などで追加の音声が来たとき呼ばれる しかし呼ばれる保証はないらしい
            public void	onPartialResults(Bundle results) {} // 部分的な認識結果が利用出来るときに呼ばれる // 利用するにはインテントでEXTRA_PARTIAL_RESULTSを指定する必要がある
            public void	onRmsChanged(float rmsdB) {}    // サウンドレベルが変わったときに呼ばれる// 呼ばれる保証はない
            public void	onEvent(int eventType, Bundle params) {}    // 将来の使用のために予約されている
        });
//        startActivityForResult();
        recognizer.startListening(recognizIntent);
    }

    //音声認識が終わると自動で呼び出されるメソッド
    @Override
    protected void onActivityResult( int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.d("recognaize","startRecognize1");
        // 音声認識
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            Log.d("recognaize","startRecognize3");
            // 認識結果を ArrayList で取得
            ArrayList<String> candidates =
                    data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (!candidates.isEmpty()) {
                for (String match : candidates) {
                }
                //Result code for various error.
            } else if(resultCode == RecognizerIntent.RESULT_AUDIO_ERROR){
                Log.i("req", "RESULT_SERVER_ERROR");
            } else if(resultCode == RecognizerIntent.RESULT_CLIENT_ERROR){
                Log.i("req", "RESULT_SERVER_ERROR");
            } else if(resultCode == RecognizerIntent.RESULT_NETWORK_ERROR){
                Log.i("req", "RESULT_SERVER_ERROR");
            } else if(resultCode == RecognizerIntent.RESULT_NO_MATCH){
                Log.i("req","RESULT_SERVER_ERROR");
            } else if(resultCode == RecognizerIntent.RESULT_SERVER_ERROR){
                Log.i("req", "RESULT_SERVER_ERROR");
            }

            int a = data.getFlags();
            if(candidates.size() > 0) {
                // 認識結果候補で一番有力なものを表示
                textView.setText(candidates.get(0));
                Log.d("recognaize", candidates.get(0));
                //音声認識インテントからのオーディオの録音/保存https://www.it-swarm-ja.tech/ja/android/%E9%9F%B3%E5%A3%B0%E8%AA%8D%E8%AD%98%E3%82%A4%E3%83%B3%E3%83%86%E3%83%B3%E3%83%88%E3%81%8B%E3%82%89%E3%81%AE%E3%82%AA%E3%83%BC%E3%83%87%E3%82%A3%E3%82%AA%E3%81%AE%E9%8C%B2%E9%9F%B3%E4%BF%9D%E5%AD%98/1046175013/
                // the resulting text is in the getExtras:
                Bundle bundle = data.getExtras();
                ArrayList<String> matches = bundle.getStringArrayList(RecognizerIntent.EXTRA_RESULTS);
                //認識候補の中から一番信頼度の高いものをピックアップ
                Bundle ams =data.getBundleExtra("Android.speech.extra.GET_AUDIO_FORMAT");
                // the recording url is in getData
                Uri audioUri = data.getData();
                ContentResolver contentResolver = getContentResolver();
                InputStream filestream = null;
                OutputStream outputStream = null;
                DataOutputStream dos = null;
                try {
                    filestream = contentResolver.openInputStream(audioUri);
                    String filePath;
                    filePath = Environment.getExternalStorageDirectory().getPath() + "/wAmr"+getToday()+".amr";
                    outputStream = new FileOutputStream(new File(filePath));
                    dos = new DataOutputStream(new FileOutputStream(filePath));
                    int read = 0;
                    initAudioRecord(8000);
                    byte[] readdata = new byte[bufSize/2];
                    dos.write(readdata);
                    while((read = filestream.read(readdata)) !=-1 ){
                        outputStream.write(readdata,0,read);
                    }
                }catch (Exception e) {
                    e.printStackTrace();
                }
            }else{
                Log.d("recognaize", "miss the recognize");
                startSpeech();
            }
        }
        Log.d("recognaize","req"+requestCode);
        Log.d("recognaize","res"+resultCode);
    }

    //音の録音 https://techbooster.org/android/multimedia/2109/
    //AndroidStudioDocuments https://developer.android.com/reference/android/media/MediaRecorder?hl=ja#getActiveMicrophones()
    MediaRecorder recorder=null;
    void mediarecoderStart(int nMilliRec){
        recorder = new MediaRecorder();
//        String h = mediaDevices.enumerateDevices();
        // audioSourceにエラーが出たので（最初はMIC）
        // https://developer.android.com/reference/android/media/MediaRecorder?hl=ja#setAudioSource(int)
        // 原因はアプリ側のパーミッションのせってい
        // https://teratail.com/questions/58057
        //AudioSource https://developer.android.com/reference/android/media/MediaRecorder.AudioSource?hl=ja#VOICE_RECOGNITION
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
//        VOICE_RECOGNITION)
        recorder.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
        //外部ストレージの保存先
        String filePath;
//        filePath= Environment.getExternalStorageDirectory() + "/audio.3gp";
//        内部ストレージの保存先(/Audioは端末のパスをみながら入力)
        filePath = Environment.getExternalStorageDirectory().getPath() + "/tomo"+getToday()+"_"+filenameNum+".wav";
//        filePath = Environment.getExternalStorageDirectory().getPath() + "/konchiwa.mp3";
//        filePath = Environment.getExternalStorageDirectory().getPath() + "/AudioSource.mp3";
        recorder.setOutputFile(filePath);
        recorder.setMaxDuration(nMilliRec);
        recorder.setAudioChannels(1);

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
    void mediaPlayerStart(String FILE_NAME){
        String filePath;
//        filePath= Environment.getExternalStorageDirectory() + "/audio.3gp";
//        内部ストレージの保存先(/Audioは端末のパスをみながら入力)
        filePath = Environment.getExternalStorageDirectory().getPath() + "/"+FILE_NAME;
        mp = new MediaPlayer();
        try {
            mp.setDataSource(filePath);
            mp.prepare();
        } catch (IllegalArgumentException e) {
        } catch (SecurityException e) {
        } catch (IllegalStateException e) {
        } catch (IOException e) {
            textView.setText("Can't Play Sound");
        }
        mp.start();
    }

    //音の録音
    AudioRecord ar;
    boolean bIsRecording = true;
    int frameRate,oneFrameDataCount,oneFrameSizeInByte,audioBufferSizeInByte,bufSize;
    private WaveFile wav = new WaveFile();
    private short[] shortData,shortDataCam;
    //http://steavevaivai.hatenablog.com/entry/2015/01/03/225505
    void initAudioRecord(int samplingRate){
        String filePath;
        filePath = Environment.getExternalStorageDirectory().getPath() + "/wabMic.wav";
        wav.createFile(filePath);
        // サンプリングレート (Hz)
        // 全デバイスサポート保障は44100のみ
//        samplingRate = 44100;
        //サンプルレート8000Hz(AMR) or 16000Hz(AMR_WB?)
//        samplingRate = 8000;


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
                // TOD Auto-generated method stub
                ar.read(shortData, 0, bufSize/2); // 読み込む
                Log.d("AudioRecord", "read " + shortData.length + " bytes");;
                wav.addBigEndianData(shortData); // ファイルに書き出す
            }

            @Override
            public void onMarkerReached(AudioRecord recorder) {
                // TOD Auto-generated method stub
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
        SimpleDateFormat sdf;
//        sdf = new SimpleDateFormat("yyyyMMddHHmmss", Locale.getDefault());
        sdf = new SimpleDateFormat("MMdd", Locale.getDefault());
        return sdf.format(date);
    }

}