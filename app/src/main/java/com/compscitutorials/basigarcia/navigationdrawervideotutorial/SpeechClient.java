package com.compscitutorials.basigarcia.navigationdrawervideotutorial;

/**
 * Created by tanghao on 27/04/2016.
 */


import android.os.AsyncTask;
import android.util.Log;
import android.util.Pair;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.ArrayList;

/**
 * Async task that sends a UDP message and returns the response
 * Classes that use this class must implement OnTaskCompleted interface
 */
public class SpeechClient extends AsyncTask<String, Void, Pair<ArrayList<Integer>, byte[]>> {
    //Both are the same object byt java types requires two different object types
    private SpeechCompleted listener;
    private DatagramSocket s;



    public SpeechClient(Task6 context){
        this.listener = (SpeechCompleted) context;
        s = null;
    }

    @Override
    protected Pair<ArrayList<Integer>, byte[]> doInBackground(String... mess) {
        DatagramSocket s = null;
        DatagramPacket p;

        ArrayList<Integer> failed = new ArrayList<>();
        byte[] audio = new byte[150 * 160];

        ArrayList<Integer> retransmit = new ArrayList<>();
        int succeeded = 0;
        byte[][] sound = new byte[150][];
        int[] repeated = new int[150];

        String m = mess[0];
        String message;
        try {
            s = new DatagramSocket(5000);


            InetAddress local = InetAddress.getByName("192.168.0.100");


            for (int i = 0; i < 150; i++) {
                if (isCancelled()) {
                    break;
                }
                message = m + i;
                p = send(message, s, local);

                if (p != null) {
                    byte[] packetBytes = p.getData();
                    int[] bits = ErrorControl.bytes2BitsIntArray(packetBytes, p.getLength());
                    int[] generator = {1, 0, 0, 0, 0, 0, 1, 1, 1};
                    if (ErrorControl.crc(bits, generator)) {
                        int seqNo = (packetBytes[160] < 0 ?
                                (int) packetBytes[160] + 256 : packetBytes[160]);
                        sound[seqNo] = packetBytes;
                        succeeded++;
                    } else {
                        retransmit.add(i);
                    }
                } else {
                    retransmit.add(i);
                }
            }

            int index;
            while (!retransmit.isEmpty() && !isCancelled()) {
                for (int i = 0; i < retransmit.size(); i++) {
                    index = retransmit.get(i);
                    if (repeated[index] < 5) {
                        message = m + index;
                        p = send(message, s, local);
                        if (p != null) {
                            byte[] packetBytes = p.getData();
                            int[] bits = ErrorControl.bytes2BitsIntArray(packetBytes, p.getLength());
                            int[] generator = {1, 0, 0, 0, 0, 0, 1, 1, 1};
                            if (ErrorControl.crc(bits, generator)) {
                                int seqNo = (packetBytes[160] < 0 ?
                                        (int) packetBytes[160] + 256 : packetBytes[160]);
                                sound[seqNo] = packetBytes;
                                succeeded++;
                            } else {
                                repeated[index]++;
                            }
                        } else {
                            repeated[index]++;
                        }
                    } else {
                        retransmit.remove(i);
                    }
                }
            }

            Log.d("COMP61242 task6", "Number of packets saved: " + succeeded + "\nNumber of " +
                    "packets failed:  " + failed.size());


            index = 0;
            for (int i = 0; i < 150; i++) {
                if (isCancelled()) {
                    break;
                }
                if (sound[i] != null) {
                    System.arraycopy(sound[i], 0, audio, index, 160);
                }
                else {
                    failed.add(i);
                }
                index += 160;
            }


        } catch (SocketException | UnknownHostException e) {
            Log.d("COMP61242 UDP", "Couldn't establish a connection: " + e);
        }

        if (s != null) {
            s.close();
        }
        if (isCancelled()) {
            Log.d("COMP61242 UDP", "Cancelled");
        }

        return new Pair<>(failed, audio);
    }

    public DatagramPacket send(String messageStr, DatagramSocket s, InetAddress local) {
        int msg_length = messageStr.length();
        byte[] message = messageStr.getBytes();
        DatagramPacket p = new DatagramPacket(message, msg_length, local, 5000);
        try {
            s.send(p);
            p = new DatagramPacket(new byte[162], 162);
            s.setSoTimeout(5000);
            s.receive(p);
            return p;
        } catch (SocketException e) {
            Log.d("COMP61242 UDP", "Timeout Exception: Did not receive a packet in 5 seconds");
        } catch (IOException e) {
            Log.d("COMP61242 UDP", "IO Exception: Did not receive a packet in 5 seconds");
        }
        return null;
    }

    @Override
    protected void onPostExecute(Pair<ArrayList<Integer>, byte[]> pair){
        listener.SpeechCompleted(pair);
    }

    @Override
    protected void onCancelled (Pair<ArrayList<Integer>, byte[]> pair) {
        //this should be done before it gets here, but as a safety precaution
        if (s != null) {
            s.close();
        }
    }

}