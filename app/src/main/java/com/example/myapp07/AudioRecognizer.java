
//https://akira-watson.com/android/recognizerintent.html
//package com.example.myapp07;
//
//import android.os.Bundle;
//import android.app.Activity;
//import android.content.ActivityNotFoundException;
//import android.content.Intent;
//import android.speech.RecognizerIntent;
//import android.view.View;
//import android.widget.Button;
//import android.widget.TextView;
//import java.util.ArrayList;
//import java.util.Locale;
//
//public class AudioRecognizer extends ScreenActivity{
//    private static final int REQUEST_CODE = 1000;
//    private TextView textView;
//
//    private int lang ;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_main);
//
//        // 言語選択 0:日本語、1:英語、2:オフライン、その他:General
//        lang = 0;
//
//        // 認識結果を表示させる
//        textView = (TextView)findViewById(R.id.textView);
//
//        Button buttonStart = (Button)findViewById(R.id.button_start);
//        buttonStart.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                // 音声認識を開始
//                speech();
//            }
//        });
//    }
//
//    private void speech(){
//        // 音声認識の　Intent インスタンス
//        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
//
//        if(lang == 0){
//            // 日本語
//            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE,Locale.JAPAN.toString() );
//        }
//        else if(lang == 1){
//            // 英語
//            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, Locale.ENGLISH.toString() );
//        }
//        else if(lang == 2){
//            // Off line mode
//            intent.putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, true);
//        }
//        else{
//            intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL,
//                    RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
//        }
//
//        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 100);
//        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "音声を入力");
//
//        try {
//            // インテント発行
//            startActivityForResult(intent, REQUEST_CODE);
//        }
//        catch (ActivityNotFoundException e) {
//            e.printStackTrace();
//            textView.setText(R.string.error);
//        }
//    }
//
//    // 結果を受け取るために onActivityResult を設置
//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//        if(requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
//            // 認識結果を ArrayList で取得
//            ArrayList<String> candidates =
//                    data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
//
//            if(candidates.size() > 0) {
//                // 認識結果候補で一番有力なものを表示
//                textView.setText( candidates.get(0));
//            }
//        }
//    }
//}
