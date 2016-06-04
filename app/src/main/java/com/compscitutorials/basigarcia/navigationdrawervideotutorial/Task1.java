package com.compscitutorials.basigarcia.navigationdrawervideotutorial;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.net.DatagramPacket;


/**
 * A simple {@link Fragment} subclass.
 */
public class Task1 extends Fragment implements OnTaskCompleted  {

    private ClientTask task;
    //private String messageString;
    //private boolean isFinished;
    private View rootView;




    public Task1() {

        //task = new ClientTask(this);
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {


        task = null;

        
        rootView = inflater.inflate(R.layout.fragment_task1, container, false);
        // Inflate the layout for this fragment
        final Button startButton = (Button) rootView.findViewById(R.id.startTask1);
        final TextView resultText = (TextView) rootView.findViewById(R.id.task1_result);
        rootView.findViewById(R.id.task1_progress).setVisibility(View.INVISIBLE);
        startButton.setTag(1);
        startButton.setText("Start");

        startButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final int status = (Integer) v.getTag();
                if (status == 1) {
                    startButton.setText("Reset");
                    v.setTag(0); //pause
                    rootView.findViewById(R.id.task1_progress).setVisibility(View.VISIBLE);

                    Log.d("COMP61242 Task1", " start task1");

                    //isFinished = false;
                    sendUDP();

                    //Log.d("COMP61242 Task1", " wait response");
                    //while(!isFinished);
                    //Log.d("COMP61242 Task1", " Get result");
                    //rootView.findViewById(R.id.task1_progress).setVisibility(View.INVISIBLE);
                    //resultText.setText(messageString);


                } else {

                    Log.d("COMP61242 Task1", " restart ");
                    startButton.setText("Start");
                    v.setTag(1); //pause
                    rootView.findViewById(R.id.task1_progress).setVisibility(View.INVISIBLE);

                    resultText.setText(" ");
                    if (task != null) {
                        task.cancel(true);
                    }
                }

            }
        });
        //Log.d("COMP61242 Task1", " End onCreateView");
        return rootView;
    }


    @Override
    public void onStop() {
        super.onStop();

        if (task != null) {
            task.cancel(true);
        }
    }

    private void sendUDP()
    {
        task = new ClientTask(this);
        task.execute("NACK1");
    }


    public void onTaskCompleted(DatagramPacket packet) {

        //Log.d("COMP61242 Task1", " on taskcompleted");

        String messageString;
        //messageString = getString(R.string.udp_response_fail);
        if (packet != null) { // always not equal to null
            messageString = new String(packet.getData(), 0, packet.getLength());
            Log.d("COMP61242 Task1", messageString );
        }else{
            messageString = getString(R.string.udp_response_fail);
        }
        //Log.d("COMP61242 Task1", messageString );
        final TextView resultText = (TextView) rootView.findViewById(R.id.task1_result);
        resultText.setText(messageString);
        rootView.findViewById(R.id.task1_progress).setVisibility(View.INVISIBLE);
        Log.d("COMP61242 Task1", " finish taskcompleted");
        //isFinished = true;
    }


}
