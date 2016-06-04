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
import java.util.ArrayList;


/**
 * A simple {@link Fragment} subclass.
 */
public class Task3 extends Fragment implements OnTaskCompleted {


    private ClientTask task;
    private View rootView;
    private ArrayList<int[]> udpPackets;
    private String recordMessage="The 1 time send UDP:";
    private int sendUDPCount = 1;


    public Task3() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        task = null;
        udpPackets = new ArrayList<>();


        rootView = inflater.inflate(R.layout.fragment_task3, container, false);
        final Button startButton = (Button) rootView.findViewById(R.id.startTask3);
        final TextView resultText = (TextView) rootView.findViewById(R.id.task3_result);
        rootView.findViewById(R.id.task3_progress).setVisibility(View.INVISIBLE);
        startButton.setTag(1);
        startButton.setText("Start");

        startButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                final int status = (Integer) v.getTag();
                if (status == 1) {
                    startButton.setText("Reset");
                    v.setTag(0); //pause
                    rootView.findViewById(R.id.task3_progress).setVisibility(View.VISIBLE);

                    sendUDP();
                } else {


                    startButton.setText("Start");
                    v.setTag(1); //pause
                    rootView.findViewById(R.id.task3_progress).setVisibility(View.INVISIBLE);

                    resultText.setText(" ");
                    recordMessage = "The 1 time send UDP:";
                    sendUDPCount = 1;
                    udpPackets.clear();
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
        task.execute("NACK3");
    }

    public void onTaskCompleted(DatagramPacket packet) {
        String messageString;

        //String newMessage;
        if (packet != null) {
            byte[] packetBytes =  packet.getData();
            int[] bits = ErrorControl.bytes2BitsIntArray(packetBytes, packet.getLength());
            //CRC with the generator polynomial x^8 + x^2 +x +1
            int[] generator = {1, 0, 0, 0, 0, 0, 1, 1, 1};
            if (ErrorControl.crc(bits, generator)) {
                messageString = new String(packetBytes, 0, packet.getLength() - 1);
                messageString = recordMessage + "\n\nFinal result: " + messageString;
                //recordMessage = "";
            } else {
                messageString = "1";
                recordMessage = recordMessage +"\n There was an error in the transmission:\n";
                udpPackets.add(bits);

                ArrayList<int[]> messagePermutations = ErrorControl.hybridARQ(udpPackets);
                boolean success = false;
                int i = 0;
                while (!success && i < messagePermutations.size()) {
                    bits = messagePermutations.get(i);
                    packetBytes = ErrorControl.bits2Bytes(bits, packet.getLength());
                    messageString = new String(packetBytes, 0, packet.getLength() - 1);
                    recordMessage += "\n"+i+": "+messageString;
                    if (ErrorControl.crc(bits, generator)) {
                        //packetBytes = ErrorControl.bits2Bytes(bits, packet.getLength());
                        //messageString = new String(packetBytes, 0, packet.getLength() - 1);
                        success = true;
                        messageString = recordMessage+ "\n\nFinal result: " + messageString;
                        //recordMessage = "";
                    }
                    i++;
                }
                if (!success) {
                    sendUDP();
                    sendUDPCount ++;
                    recordMessage += "\n\nThe "+sendUDPCount+" times send UDP: ";
                }

            }
        }else{
            messageString = getString(R.string.udp_response_fail);
        }
        messageString = messageString + "\nSendUDP times: " + sendUDPCount ;//+ "\nNumber of packets: "+udpPackets.size();//same
        final TextView resultText = (TextView) rootView.findViewById(R.id.task3_result);
        resultText.setText(messageString);
        rootView.findViewById(R.id.task3_progress).setVisibility(View.INVISIBLE);

    }

}
