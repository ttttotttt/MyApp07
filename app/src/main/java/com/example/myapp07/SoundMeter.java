package com.example.myapp07;
//https://www.it-swarm-ja.tech/ja/android/android%ef%bc%9a%e3%82%b5%e3%82%a6%e3%83%b3%e3%83%89%e3%83%ac%e3%83%99%e3%83%ab%e3%82%92%e6%a4%9c%e5%87%ba%e3%81%99%e3%82%8b/1069885029/

import android.media.MediaRecorder;

import java.io.IOException;
import java.lang.Object;

public class SoundMeter {

    private MediaRecorder mRecorder = null;

    public void MRstart() throws IOException {
        if (mRecorder == null) {
            mRecorder = new MediaRecorder();
            mRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            mRecorder.setAudioChannels(2);
/*            出力ファイル形式を設定(APIレベル1で 追加?)
            setAudioSource（）/ setVideoSource（）の後、ただしprepare（）の前
            */
            mRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
//            出力ファイル名を設定
            mRecorder.setOutputFile("filename");
            mRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
//            mRecorder.setOutputFile("/dev/null");
            mRecorder.prepare();
//録音のスタート
            mRecorder.start();
        }
    }

    public void stop() {
        if (mRecorder != null) {
            mRecorder.stop();
            mRecorder.release();
            mRecorder = null;
        }
    }

    public double getAmplitude() {
        if (mRecorder != null)
            return  mRecorder.getMaxAmplitude();
        else
            return 0;

    }
}
