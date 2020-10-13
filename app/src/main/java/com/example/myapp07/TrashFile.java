//package com.example.myapp07;
//
//import android.media.MediaPlayer;
//import android.media.MediaRecorder;
//import android.os.Environment;
//
//import java.io.File;
//import java.io.IOException;
//
//
//public class TrashFile {
//
//    private MediaPlayer mp;
//    private MediaRecorder rec;
//    /* 録音先のパス */
//    static final String filePath = Environment.getExternalStorageDirectory() + "/sample.wav";
//
//    private void startRecord() {
//        /* ファイルが存在する場合は削除 */
////        String filePath;
//        File wavFile = new File(filePath);
//        if (wavFile.exists()) {
//            wavFile.delete();
//        }
//        wavFile = null;
//        try {
//            rec = new MediaRecorder();
//            rec.setAudioSource(MediaRecorder.AudioSource.MIC);
//            rec.setOutputFormat(MediaRecorder.OutputFormat.DEFAULT);
//            rec.setAudioEncoder(MediaRecorder.AudioEncoder.DEFAULT);
//            rec.setOutputFile(filePath);
//
//            rec.prepare();
//            rec.start();
////            textView.setText("Rec Now");
//        } catch(Exception e){
//            e.printStackTrace();
//        }
//    }
//    //参考サイト    https://hack-le.com/androidrecplay/
//    // 再生 ちなみにエラーも何も出ずにunfortunity stop する　パスが合っていないのだろうか
//    private void startPlay() {
////        mp = MediaPlayer.create(this, R.raw.bgm);
//        try {
//            mp.prepare();
//            mp.start();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//    }
//
//
//    private void stopRecord() {
//        try {
//            rec.stop();
//            rec.reset();
//            rec.release();
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    }
//}
