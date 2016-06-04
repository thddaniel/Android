package com.compscitutorials.basigarcia.navigationdrawervideotutorial;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.net.DatagramPacket;


/**
 * A simple {@link Fragment} subclass.
 */
public class Task5 extends Fragment implements OnTaskCompleted{

    private ClientTask task;
    private View rootView;


    public Task5() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        task = null;


        rootView = inflater.inflate(R.layout.fragment_task5, container, false);
        final Button startButton = (Button) rootView.findViewById(R.id.startTask5);
        final TextView resultText = (TextView) rootView.findViewById(R.id.task5_result);
        rootView.findViewById(R.id.task5_progress).setVisibility(View.INVISIBLE);
        startButton.setTag(1);
        startButton.setText("Start");

        startButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final int status = (Integer) v.getTag();
                if (status == 1) {
                    startButton.setText("Reset");
                    v.setTag(0); //pause
                    rootView.findViewById(R.id.task5_progress).setVisibility(View.VISIBLE);

                    sendUDP();
                } else {


                    startButton.setText("Start");
                    v.setTag(1); //pause
                    rootView.findViewById(R.id.task5_progress).setVisibility(View.INVISIBLE);

                    resultText.setText("");
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
        task.execute("NACK5");
    }

    public void onTaskCompleted(DatagramPacket packet) {
        String messageString;
        if (packet != null) {
            byte[] packetBytes =  packet.getData();
            int[] bits = ErrorControl.bytes2BitsIntArray(packetBytes, packet.getLength());

            short[] encoded = new short[bits.length];
            //0 is 000 and 1 is 111 (Ie decimal 7).
            for (int i = 0; i < bits.length; i++) {
                encoded[i] = (short) (bits[i] * 7);
            }
            //A ½ K=7 (171 133) convolutional encoder
            //These connections represent (171)8 and (133)8 in binary form.
            int [][]g = {{1,  1, 1, 1,  0, 0, 1}, {1,  0, 1, 1,  0, 1, 1}};
            //The decoder does not deliver the final 6 bits
            short[] decoded = new short[(encoded.length / 2) - 6];
            bbsdvd08.bbsdvd(g, encoded.length, encoded, decoded);
            //Always get two of the flushing zero bits on the end of any message.These can be discarded.
            bits = new int[decoded.length - 2];
            for (int i = 0; i < bits.length; i++) {
                bits[i] = (int) decoded[i];
            }

            int[] generator = {1, 0, 0, 0, 0, 0, 1, 1, 1};
            if (ErrorControl.crc(bits, generator)) {
                packetBytes = ErrorControl.bits2Bytes(bits, (packet.getLength()) / 2);
                messageString = new String(packetBytes, 0, (packet.getLength() / 2) - 2);

            }
            else {
                packetBytes = ErrorControl.bits2Bytes(bits, (packet.getLength()) / 2);
                messageString = getString(R.string.transmission_error);
            }
            String s = "";
            for (int i = bits.length - 8; i < bits.length; i++) {
                s += bits[i];
            }
             /* "CRC: " + new String(packetBytes, (packet.getLength() / 2) - 2,
                    1) + " (" + s + ")\nReceived String: " + new String(packetBytes, 0,
                    (packet.getLength() / 2) - 2);*/
        }
        else {
            messageString = getString(R.string.udp_response_fail);

        }
        final TextView resultText = (TextView) rootView.findViewById(R.id.task5_result);
        resultText.setText(messageString);
        rootView.findViewById(R.id.task5_progress).setVisibility(View.INVISIBLE);

    }
}
