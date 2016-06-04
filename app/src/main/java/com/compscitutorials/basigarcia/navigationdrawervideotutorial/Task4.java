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
public class Task4 extends Fragment implements OnTaskCompleted{

    private ClientTask task;
    private View rootView;


    public Task4() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        task = null;


        rootView = inflater.inflate(R.layout.fragment_task4, container, false);
        final Button startButton = (Button) rootView.findViewById(R.id.startTask4);
        final TextView resultText = (TextView) rootView.findViewById(R.id.task4_result);
        rootView.findViewById(R.id.task4_progress).setVisibility(View.INVISIBLE);
        startButton.setTag(1);
        startButton.setText("Start");

        startButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final int status = (Integer) v.getTag();
                if (status == 1) {
                    startButton.setText("Reset");
                    v.setTag(0); //pause
                    rootView.findViewById(R.id.task4_progress).setVisibility(View.VISIBLE);

                    sendUDP();
                } else {


                    startButton.setText("Start");
                    v.setTag(1); //pause
                    rootView.findViewById(R.id.task4_progress).setVisibility(View.INVISIBLE);

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
        task.execute("NACK4");
    }

    public void onTaskCompleted(DatagramPacket packet) {

        String messageString;
        if (packet != null) {
            byte[] packetBytes =  packet.getData();
            int[] bits = ErrorControl.bytes2BitsIntArray(packetBytes, packet.getLength());
            int[] barkerCode = {1,0,1,1,0,1,1,1,0,0,0};
            int[] decoded = ErrorControl.barkerDecode(bits, barkerCode);

            int[] generator = {1, 0, 0, 0, 0, 0, 1, 1, 1};
            if (ErrorControl.crc(decoded, generator)) {
                packetBytes = ErrorControl.bits2Bytes(decoded, packet.getLength() / 11);
                messageString = new String(packetBytes, 0, packet.getLength() / 11 - 1);
            }
            else {
                messageString = getString(R.string.transmission_error);
            }
        }
        else {
            messageString = getString(R.string.udp_response_fail);
        }

        final TextView resultText = (TextView) rootView.findViewById(R.id.task4_result);
        resultText.setText(messageString);
        rootView.findViewById(R.id.task4_progress).setVisibility(View.INVISIBLE);

    }
}
