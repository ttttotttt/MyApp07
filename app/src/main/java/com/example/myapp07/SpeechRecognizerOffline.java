//package com.example.myapp07;
//import android.Manifest;
//import android.app.Activity;
//import android.content.Intent;
//import android.content.pm.PackageManager;
//import android.graphics.Color;
//import android.os.Bundle;
//import android.speech.RecognitionListener;
//import android.speech.RecognizerIntent;
//import android.speech.SpeechRecognizer;
//import android.view.ViewGroup;
//import android.view.Window;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import androidx.core.app.ActivityCompat;
//import androidx.core.content.ContextCompat;
//
//public class SpeechRecognizerOffline extends Activity{
//
//    // 定数
//    private final static int WC = ViewGroup.LayoutParams.WRAP_CONTENT;
//    private final static int PERMISSIONS_REQUEST_RECORD_AUDIO = 0;
//
//    // UI
//    private TextView label;
//    private ArrayList<String> texts = new ArrayList<>();
//    private boolean permission = false;
//    private boolean activityActive = false;
//
//    // 音声認識
//    private Intent intent;
//    private SpeechRecognizer recognizer;
//    private boolean recognizeActive;
//
//    // アクティビティ起動時に呼ばれる
//    @Override
//    public void onCreate(Bundle bundle) {
//        super.onCreate(bundle);
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        setContentView(R.layout.activity_main);
//
//        // レイアウトの生成
//        LinearLayout layout = new LinearLayout(this);
//        layout.setBackgroundColor(Color.WHITE);
//        layout.setOrientation(LinearLayout.VERTICAL);
//        setContentView(layout);
//
//        // ラベルの生成
//        this.label = new TextView(this);
//        this.label.setText("");
//        this.label.setTextSize(16.0f);
//        this.label.setTextColor(Color.rgb(0, 0, 0));
//        this.label.setLayoutParams(new LinearLayout.LayoutParams(WC, WC));
//        layout.addView(this.label);
//
//        // 音声認識の初期化
//        initRecognizer();
//
//        // パーミッションのチェック
//        checkPermission();
//    }
//
//    @Override
//    protected void onResume() {
//        super.onResume();
//        this.activityActive = true;
//        if (this.permission && !this.recognizeActive) startRecognizer();
//    }
//
//    @Override
//    protected void onPause() {
//        super.onPause();
//        this.activityActive = false;
//    }
//
//    // テキストの追加
//    private void AddText(String text) {
//        // テキストの追加
//        this.texts.add(0, text);
//        while (this.texts.size() > 10) {
//            this.texts.remove(this.texts.size()-1);
//        }
//
//        // ラベルの更新
//        String labelText = "";
//        for (int i = 0; i < texts.size(); i++) {
//            labelText = this.texts.get(i) + "\n" + labelText;
//        }
//        this.label.setText(labelText);
//    }
//
//    // パーミッションのチェック
//    private void checkPermission() {
//        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO)
//                != PackageManager.PERMISSION_GRANTED) {
//            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
//                    Manifest.permission.RECORD_AUDIO)) {
//                // パーミッション不許可
//                AddText("RECORD_AUDIOのパーミッションを許可してください");
//            } else {
//                // パーミッション許可のリクエスト
//                ActivityCompat.requestPermissions(this,
//                        new String[]{Manifest.permission.RECORD_AUDIO},
//                        PERMISSIONS_REQUEST_RECORD_AUDIO);
//            }
//        } else {
//            // パーミッション許可済み
//            this.permission = true;
//
//            // 音声認識の開始
//            startRecognizer();
//        }
//    }
//
//    // パーミッション許可のリクエストの結果の取得
//    @Override
//    public void onRequestPermissionsResult(int requestCode,
//                                           String[] permissions, int[] grantResults) {
//        if (requestCode == PERMISSIONS_REQUEST_RECORD_AUDIO) {
//            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                // パーミッション許可済み
//                this.permission = true;
//
//                // 音声認識の開始
//                startRecognizer();
//            } else {
//                // パーミッション不許可
//                AddText("RECORD_AUDIOのパーミッションを許可してください");
//            }
//        }
//    }
//
//    // 音声認識の初期化
//    private void initRecognizer() {
//        // RecognizerIntentインテントの生成
//        this.intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
//        this.intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
//
//        // SpeechRecognizerの生成
//        this.recognizer = SpeechRecognizer.createSpeechRecognizer(this);
//        this.recognizer.setRecognitionListener(new RecognitionListener() {
//            // ユーザーの話を聞く準備ができた時に呼ばれる
//            public void	onReadyForSpeech(Bundle params) {
//                // onResultsが2回呼ばれる不具合の対応
//                recognizeActive = true;
//            }
//
//            // 認識結果の取得時に呼ばれる
//            public void	onResults(Bundle results) {
//                // onResultsが2回呼ばれる不具合の対応
//                if (!recognizeActive) return;
//
//                // 認識テキストの取得
//                List<String> recData = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
//                if (recData.size() > 0) {
//                    AddText(recData.get(0));
//                }
//
//                // 音声認識
//                recognizeActive = false;
//                startRecognizer();
//            }
//
//            // エラー時に呼ばれる
//            public void	onError(int error) {
//                // エラー表示
//                AddText(error2str(error));
//
//                // スリープ（これがないと次の音声認識に失敗することがある）
//                try {
//                    Thread.sleep(1000);
//                } catch (Exception e) {
//                }
//
//                // 音声認識
//                recognizeActive = false;
//                startRecognizer();
//            }
//
//            // その他
//            public void	onBeginningOfSpeech() {}
//            public void	onEndOfSpeech() {}
//            public void	onBufferReceived(byte[] buffer) {}
//            public void	onPartialResults(Bundle results) {}
//            public void	onRmsChanged(float rmsdB) {}
//            public void	onEvent(int eventType, Bundle params) {}
//        });
//    }
//
//    // 音声認識の開始
//    private void startRecognizer() {
//        if (!this.activityActive) return;
//        this.recognizer.startListening(this.intent);
//    }
//
//    // エラー → 文字列
//    private String error2str(int error) {
//        if (error == SpeechRecognizer.ERROR_AUDIO) {
//            return "ERROR_AUDIO";
//        } else if (error == SpeechRecognizer.ERROR_CLIENT) {
//            return "ERROR_CLIENT";
//        } else if (error == SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS) {
//            return "ERROR_INSUFFICIENT_PERMISSIONS";
//        } else if (error == SpeechRecognizer.ERROR_NETWORK) {
//            return "ERROR_NETWORK";
//        } else if (error == SpeechRecognizer.ERROR_NETWORK_TIMEOUT) {
//            return "ERROR_NETWORK_TIMEOUT";
//        } else if (error == SpeechRecognizer.ERROR_NO_MATCH) {
//            return "ERROR_NO_MATCH";
//        } else if (error == SpeechRecognizer.ERROR_RECOGNIZER_BUSY) {
//            return "ERROR_RECOGNIZER_BUSY";
//        } else if (error == SpeechRecognizer.ERROR_SERVER) {
//            return "ERROR_SERVER";
//        } else if (error == SpeechRecognizer.ERROR_SPEECH_TIMEOUT) {
//            return "ERROR_SPEECH_TIMEOUT";
//        }
//        return "ERROR";
//    }
//}
