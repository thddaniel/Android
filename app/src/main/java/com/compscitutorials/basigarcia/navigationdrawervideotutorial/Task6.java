package com.compscitutorials.basigarcia.navigationdrawervideotutorial;


import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class Task6 extends Fragment implements SpeechCompleted{

    private SpeechClient task;
    private View rootView;
    private byte[] originalAudio = new byte[150 * 160];
    private byte[] zeroAudio = new byte[150 * 160];
    private byte[] repeatAudio = new byte[150 * 160];
    private ArrayList<Integer> failed;
    private MediaPlayer mediaPlayer;
    private boolean task1Flag;
    private boolean task2Flag;
    private boolean task3Flag;


    public Task6() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        task = null;
        failed = new ArrayList<>();

        rootView = inflater.inflate(R.layout.fragment_task6, container, false);
        final Button startButton1 = (Button) rootView.findViewById(R.id.startTask6_1);
        final Button startButton2 = (Button) rootView.findViewById(R.id.startTask6_2);
        final Button startButton3 = (Button) rootView.findViewById(R.id.startTask6_3);
        final Button changeButton = (Button) rootView.findViewById(R.id.changeStrategy);
        final TextView resultText = (TextView) rootView.findViewById(R.id.task6_result);
        rootView.findViewById(R.id.task6_progress).setVisibility(View.INVISIBLE);
        startButton1.setTag(1);
        startButton1.setText("Start");
        startButton2.setTag(1);
        startButton2.setText("Start");
        startButton3.setTag(1);
        startButton3.setText("Start");
        changeButton.setTag(1);
        changeButton.setVisibility(View.INVISIBLE);

       // startButton1.setOnClickListener(buttonListener);
       // startButton2.setOnClickListener(buttonListener);
       // startButton3.setOnClickListener(buttonListener);


        startButton1.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final int status = (Integer) v.getTag();
                if (status == 1) {
                    startButton1.setText("Reset");
                    v.setTag(0); //pause
                    rootView.findViewById(R.id.task6_progress).setVisibility(View.VISIBLE);

                    sendUDP();
                    task1Flag = true;
                    changeButton.setVisibility(View.VISIBLE);
                    changeButton.setText("1");
                } else {


                    startButton1.setText("Start");
                    v.setTag(1); //pause
                    rootView.findViewById(R.id.task6_progress).setVisibility(View.INVISIBLE);
                    failed.clear();
                    resultText.setText("");
                    task1Flag = false;
                    changeButton.setVisibility(View.INVISIBLE);
                    if (task != null) {
                        task.cancel(true);
                        mediaPlayer.release();
                    }
                }

            }
        });


        startButton2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final int status = (Integer) v.getTag();
                if (status == 1) {
                    startButton2.setText("Reset");
                    v.setTag(0); //pause
                    rootView.findViewById(R.id.task6_progress).setVisibility(View.VISIBLE);

                    sendUDP();
                    task1Flag = true;
                    changeButton.setVisibility(View.VISIBLE);
                    changeButton.setText("1");
                } else {


                    startButton2.setText("Start");
                    v.setTag(1); //pause
                    rootView.findViewById(R.id.task6_progress).setVisibility(View.INVISIBLE);
                    failed.clear();
                    resultText.setText("");
                    task2Flag = false;
                    changeButton.setVisibility(View.INVISIBLE);
                    if (task != null) {
                        task.cancel(true);
                        mediaPlayer.release();
                    }
                }

            }
        });


        startButton3.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final int status = (Integer) v.getTag();
                if (status == 1) {
                    startButton3.setText("Reset");
                    v.setTag(0); //pause
                    rootView.findViewById(R.id.task6_progress).setVisibility(View.VISIBLE);

                    sendUDP();
                    task1Flag = true;
                    changeButton.setVisibility(View.VISIBLE);
                    changeButton.setText("1");

                } else {


                    startButton3.setText("Start");
                    v.setTag(1); //pause
                    rootView.findViewById(R.id.task6_progress).setVisibility(View.INVISIBLE);
                    failed.clear();
                    resultText.setText("");
                    changeButton.setVisibility(View.INVISIBLE);
                    task3Flag = false;
                    if (task != null) {
                        task.cancel(true);
                        mediaPlayer.release();
                    }
                }

            }
        });

        /*Use to compare the difference between three audio */
        changeButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final int status = (Integer) v.getTag();
                if (status == 1) {
                    changeButton.setText("2");
                    v.setTag(2);
                    playAudio(originalAudio);

                } else if(status == 2){

                    changeButton.setText("3");
                    v.setTag(3);
                    playAudio(zeroAudio);
                }else if(status == 3){
                    changeButton.setText("1");
                    v.setTag(1);
                    playAudio(repeatAudio);

                }

            }
        });
        return rootView;
    }

    /*
    private View.OnClickListener buttonListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            // default method for handling onClick Events..
            switch (v.getId()) {

                case R.id.startTask6_1:
                    // do your code
                    break;

                case R.id.startTask6_2:
                    // do your code
                    break;

                case R.id.startTask6_3:
                    // do your code
                    break;

                default:
                    break;
            }
        }


    };

*/


    @Override
    public void onStop() {
        super.onStop();
        if (task != null) {
            task.cancel(true);
        }
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    private void sendUDP()
    {
        task = new SpeechClient(this);
        task.execute("NACK6_");
    }

    private MediaPlayer.OnCompletionListener mediaListener = new MediaPlayer.OnCompletionListener(){
        public void onCompletion(MediaPlayer player) {
            player.release();
        }
    };

    public void SpeechCompleted(Pair<ArrayList<Integer>, byte[]> pair) {
        String messageString;
        failed = pair.first;
        originalAudio = pair.second;
        zeroAudio = ErrorControl.zeroPLC(originalAudio,failed);
        repeatAudio = ErrorControl.repeatPLC(originalAudio, failed);

        if(task1Flag){
            playAudio(originalAudio);
        }else if(task2Flag){
            playAudio(zeroAudio);
        }else if(task3Flag){
            playAudio(repeatAudio);
        }

/*
        // To convert to Waveform Audio Format (WAV) format
        byte[] wav = ErrorControl.convert_a_law_to_byte_wav(audio, audio.length);

        //write to the file
        File file = null;
        FileOutputStream outputStream;

        try {
            file = File.createTempFile("audio", ".wav");
            outputStream = new FileOutputStream(file);
            outputStream.write(wav);
            outputStream.close();
        }
        catch (IOException e) {
            Log.d("COMP61242 Task6", "Unable to create audio file: " + e);
        }

        mediaPlayer = MediaPlayer.create(getActivity(), Uri.fromFile(file));

        mediaPlayer.setOnCompletionListener(mediaListener);
        mediaPlayer.start();//Play!

        if (file != null) {
            boolean deleted = file.delete();
            Log.d("COMP61242 Task6", "File deleted: " + deleted);
        }
*/
        String s = "";
        if (failed.size() > 0) {
            s += failed.get(0);
            for (int i = 1; i < failed.size(); i++) {
                s += ", " + failed.get(i);
            }
        }

        messageString = "Number of failed packets: " + failed.size() + "\nFailed Packets: "
                + s;

        final TextView resultText = (TextView) rootView.findViewById(R.id.task6_result);
        resultText.setText(messageString);
        rootView.findViewById(R.id.task6_progress).setVisibility(View.INVISIBLE);
        //changeButton.setVisibility(View.INVISIBLE);

    }

    public void playAudio(byte[] audio) {

        // To convert to Waveform Audio Format (WAV) format
        byte[] wav = ErrorControl.convert_a_law_to_byte_wav(audio, audio.length);

        //write to the file
        File file = null;
        FileOutputStream outputStream;

        try {
            file = File.createTempFile("audio", ".wav");
            outputStream = new FileOutputStream(file);
            outputStream.write(wav);
            outputStream.close();
        }
        catch (IOException e) {
            Log.d("COMP61242 Task6", "Unable to create audio file: " + e);
        }

        mediaPlayer = MediaPlayer.create(getActivity(), Uri.fromFile(file));

        mediaPlayer.setOnCompletionListener(mediaListener);
        mediaPlayer.start();//Play!

        if (file != null) {
            boolean deleted = file.delete();
            Log.d("COMP61242 Task6", "File deleted: " + deleted);
        }


    }
}
