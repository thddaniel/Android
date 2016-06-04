package com.compscitutorials.basigarcia.navigationdrawervideotutorial;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.net.DatagramPacket;


/**
 * A simple {@link Fragment} subclass.
 */
public class Task2 extends Fragment implements OnTaskCompleted {

    private ClientTask task;
    private View rootView;


    public Task2() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        task = null;


        rootView = inflater.inflate(R.layout.fragment_task2, container, false);
        final Button startButton = (Button) rootView.findViewById(R.id.startTask2);
        final TextView resultText = (TextView) rootView.findViewById(R.id.task2_result);
        rootView.findViewById(R.id.task2_progress).setVisibility(View.INVISIBLE);
        startButton.setTag(1);
        startButton.setText("Start");

        startButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final int status = (Integer) v.getTag();
                if (status == 1) {
                    startButton.setText("Reset");
                    v.setTag(0); //pause
                    rootView.findViewById(R.id.task2_progress).setVisibility(View.VISIBLE);
                    Log.d("COMP61242 Task2", " start task2");
                    sendUDP();
                } else {

                    Log.d("COMP61242 Task2", " restart ");
                    startButton.setText("Start");
                    v.setTag(1); //pause
                    rootView.findViewById(R.id.task2_progress).setVisibility(View.INVISIBLE);

                    resultText.setText(" ");
                    if (task != null) {
                        task.cancel(true);
                    }
                }

            }
        });
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
        task.execute("NACK2");
    }

    public void onTaskCompleted(DatagramPacket packet) {
        String messageString;
        String recordMessage;
        String checkbits = "";

        if (packet != null) {
            byte[] packetBytes =  packet.getData();
            int[] bits = ErrorControl.bytes2BitsIntArray(packetBytes, packet.getLength());
            //CRC with the generator polynomial x^8 + x^2 +x +1
            int[] generator = {1, 0, 0, 0, 0, 0, 1, 1, 1};
            if (ErrorControl.crc(bits, generator)) {
                messageString = new String(packetBytes, 0, packet.getLength() - 1);
            } else {
                messageString = getString(R.string.transmission_error);
                recordMessage = new String(packetBytes, 0, packet.getLength() - 1);
                messageString = messageString + "\nWrong packet message:\n " + recordMessage + "\n Try again!";
            }
            for (int i = bits.length - 8; i < bits.length; i++) {
                checkbits += bits[i];
            }
            messageString = messageString + "\n\nCheck bits: " + checkbits;
        }else{
            messageString = getString(R.string.udp_response_fail);
        }

        final TextView resultText = (TextView) rootView.findViewById(R.id.task2_result);
        resultText.setText(messageString);
        rootView.findViewById(R.id.task2_progress).setVisibility(View.INVISIBLE);

    }

}
