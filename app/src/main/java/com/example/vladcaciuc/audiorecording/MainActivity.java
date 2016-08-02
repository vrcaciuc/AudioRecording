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
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;


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

    private ListView lvItem;
    private ItemsAdapter adapter;
    private ArrayList<ItemModel> myList;
    private String fileName;
    private String playAudioFile;
    private int fileCount = 0;
    File dir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        stop=(Button)findViewById(R.id.stop_btn);
        record=(Button)findViewById(R.id.record_btn);
        tvTimer=(TextView) findViewById((R.id.timer_tv)) ;
        lvItem = (ListView) findViewById(R.id.lvItem);
        stop.setTransformationMethod(null);
        record.setTransformationMethod(null);
        myList = new ArrayList<ItemModel>();

        record.setEnabled(true);
        stop.setEnabled(false);

        dir = new File(Environment.getExternalStorageDirectory() + "/Audio Recordings");
        if(!(dir.exists() && dir.isDirectory())) {
            dir.mkdirs();
        }
//        lvItem.setAdapter(new ItemsAdapter(myList, getApplicationContext()));
//        lvItem.setAdapter(adapter);

        lvItem.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {play(position);
            }
        });

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
    }

    public void displayItems(){
        adapter = new ItemsAdapter(myList, getApplicationContext());
        lvItem.setAdapter(adapter);
    }



    public void generateNewFileName(){
        fileCount++;
        fileName = "Recording" + fileCount;
        outputFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/Audio Recordings/";
    }

    public void record(){

        resetTimer();
        generateNewFileName();
        record.setBackgroundColor(getResources().getColor(R.color.colorgrey));
        stop.setBackgroundColor(getResources().getColor(R.color.colorgreen));

        startTime = SystemClock.uptimeMillis();
        customHandler.postDelayed(updateTimerThread, 0);

        checkMediaRecorder();

        myAudioRecorder=new MediaRecorder();
        myAudioRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        myAudioRecorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        myAudioRecorder.setAudioEncoder(MediaRecorder.OutputFormat.AMR_NB);
        myAudioRecorder.setOutputFile(outputFile + fileName + ".3gp");

        record.setEnabled(false);
        stop.setEnabled(true);

        try {
            myAudioRecorder.prepare();
            myAudioRecorder.start();
        }

        catch (IllegalStateException e) {e.printStackTrace();}

        catch (IOException e) {e.printStackTrace();}
    }

    public void stop(){

        myList.add(new ItemModel(fileName, tvTimer.toString()));
        int i;
        for (i = 0; i < myList.size(); i ++) {
            System.out.println("File name: " + myList.get(i).getName());
        }
        displayItems();

        tvTimer.setText(getResources().getString(R.string.timer_val));
        stop.setBackgroundColor(getResources().getColor(R.color.colorgrey));
        record.setBackgroundColor(getResources().getColor(R.color.colorgreen));

        timeSwapBuff += timeInMilliseconds;
        customHandler.removeCallbacks(updateTimerThread);

        myAudioRecorder.stop();
        myAudioRecorder.release();
        myAudioRecorder  = null;

        record.setEnabled(false);
        stop.setEnabled(false);
    }

    public void play(int pos){

        playAudioFile = myList.get(pos).getName() + ".3gp";
        tvTimer.setText(myList.get(pos).getDuration());
        checkMediaPlayer();
        m = new MediaPlayer();

        stop.setEnabled(false);
        record.setEnabled(false);

        try {m.setDataSource(outputFile + playAudioFile);}
        catch (IOException e) {e.printStackTrace();}

        try {m.prepare();}
        catch (IOException e) {e.printStackTrace();}

        m.start();
        if (mins > 0) {
            countDown(mins * 60 + secs);
        } else
            countDown(secs);

        m.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                record.setEnabled(true);
            }
        });
    }

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
