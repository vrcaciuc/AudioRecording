package com.example.vladcaciuc.audiorecording;

import android.app.Activity;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Environment;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import java.io.File;
import java.io.IOException;
import java.util.Timer;


public class MainActivity extends Activity {
    Button play, stop, record;
    TextView tvTimer;

    private String outputFile = null;
    private MediaRecorder myAudioRecorder;
    private MediaPlayer m;

    private long startTime = 0L;
    private Handler customHandler = new Handler();
    long timeInMilliseconds = 0L;
    long timeSwapBuff = 0L;
    long updatedTime = 0L;
    private int secs, mins;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

//        play=(Button)findViewById(R.id.play_btn);
        stop=(Button)findViewById(R.id.stop_btn);
        record=(Button)findViewById(R.id.record_btn);
        tvTimer=(TextView) findViewById((R.id.timer_tv)) ;
        stop.setTransformationMethod(null);
        record.setTransformationMethod(null);

        record.setEnabled(true);
        stop.setEnabled(false);
//        play.setEnabled(false);

        outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/recording.3gp";

        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                record();
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                stop();
            }
        });

//        play.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) throws IllegalArgumentException,SecurityException,IllegalStateException {
//                play();
//            }
//        });
    }

    public void record(){

        resetTimer();
        record.setBackgroundColor(getResources().getColor(R.color.colorgrey));
        stop.setBackgroundColor(getResources().getColor(R.color.colorgreen));

        startTime = SystemClock.uptimeMillis();
        customHandler.postDelayed(updateTimerThread, 0);

        checkMediaRecorder();

        File outFile = new File(outputFile);
        if(outFile.exists()){
            outFile.delete();
        }

        myAudioRecorder=new MediaRecorder();
        myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        myAudioRecorder.setOutputFile(outputFile);

        record.setEnabled(false);
//        play.setEnabled(false);
        stop.setEnabled(true);

        try {
            myAudioRecorder.prepare();
            myAudioRecorder.start();
        }

        catch (IllegalStateException e) {e.printStackTrace();}

        catch (IOException e) {e.printStackTrace();}
    }

    public void stop(){

        stop.setBackgroundColor(getResources().getColor(R.color.colorgrey));
        record.setBackgroundColor(getResources().getColor(R.color.colorgreen));

        timeSwapBuff += timeInMilliseconds;
        customHandler.removeCallbacks(updateTimerThread);

        myAudioRecorder.stop();
        myAudioRecorder.release();
        myAudioRecorder  = null;

        record.setEnabled(false);
        stop.setEnabled(false);
//        play.setEnabled(true);
    }

//    public void play(){
//
//        checkMediaPlayer();
//        m = new MediaPlayer();
//
//        stop.setEnabled(false);
//        record.setEnabled(false);
//        play.setEnabled(false);
//
//        try {m.setDataSource(outputFile);}
//        catch (IOException e) {e.printStackTrace();}
//
//        try {m.prepare();}
//        catch (IOException e) {e.printStackTrace();}
//
//        m.start();
//        if (mins > 0) {
//            countDown(mins * 60 + secs);
//        } else
//            countDown(secs);
//        m.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
//            @Override
//            public void onCompletion(MediaPlayer mp) {
//                record.setEnabled(true);
//            }
//        });
//    }

    private void countDown(final int i) {
        int temp = i;
        new CountDownTimer(temp * 1000, 1000) {

            public void onTick(long millisUntilFinished) {
                tvTimer.setText(String.format("%02d", millisUntilFinished / (1000 * 60)) + ":"
                        + String.format("%02d", (millisUntilFinished / 1000) % 60));
            }

            public void onFinish() {
                tvTimer.setText(String.format("%02d", i / (1000 * 60)) + ":"
                        + String.format("%02d", i / 1000));
            }
        }.start();
    }

    private void checkMediaRecorder() {
        if(myAudioRecorder != null){
            myAudioRecorder.release();
        }
    }

    private void checkMediaPlayer() {
        if(m != null){
            try {m.release();}
            catch (Exception e) {e.printStackTrace();}
        }
    }

    private Runnable updateTimerThread = new Runnable() {

        public void run() {

            timeInMilliseconds = SystemClock.uptimeMillis() - startTime;
            updatedTime = timeSwapBuff + timeInMilliseconds;

            secs = (int) (updatedTime / 1000);
            mins = secs / 60;
            secs = secs % 60;
            tvTimer.setText(String.format("%02d", mins) + ":"
                    + String.format("%02d", secs));
            customHandler.postDelayed(this, 0);
        }
    };

    private void resetTimer(){
        startTime = 0L;
        Handler customHandler = new Handler();
        timeInMilliseconds = 0L;
        timeSwapBuff = 0L;
        updatedTime = 0L;
        mins = 0;
        secs = 0;
    }

}
