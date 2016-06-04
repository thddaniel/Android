package com.compscitutorials.basigarcia.navigationdrawervideotutorial;

/**
 * Created by tanghao on 15/04/2016.
 */

import android.content.Context;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import java.io.InterruptedIOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;


/**
 * Async task that sends a UDP message and returns the response
 * Classes that use this class must implement OnTaskCompleted interface
 */
public class ClientTask extends AsyncTask<String, Void, DatagramPacket> {
    //Both are the same object byt java types requires two different object types
    private OnTaskCompleted listener;
    private DatagramSocket s;

    public ClientTask(Task1 context){
        this.listener = (OnTaskCompleted) context;
        s = null;
    }

    public ClientTask(Task2 context){
        this.listener = (OnTaskCompleted) context;
        s = null;
    }

    public ClientTask(Task3 context){
        this.listener = (OnTaskCompleted) context;
        s = null;
    }

    public ClientTask(Task4 context){
        this.listener = (OnTaskCompleted) context;
        s = null;
    }

    public ClientTask(Task5 context){
        this.listener = (OnTaskCompleted) context;
        s = null;
    }

    @Override
    protected DatagramPacket doInBackground(String... m) {
        String messageStr = m[0];
        DatagramPacket p = null;

        int attempts = 0;
        while (attempts < 5 && !isCancelled()) {
            try {


                InetAddress local = InetAddress.getByName("192.168.0.100");

                int msg_length = messageStr.length();
                byte[] message = messageStr.getBytes();


                s = new DatagramSocket(5000);
                //

                p = new DatagramPacket(message, msg_length, local, 5000);
                Log.d("COMP61242 UDP", "sending message");
                s.send(p);
                Log.d("COMP61242 UDP", "sent");

                p = new DatagramPacket(new byte[512], 512);
                Log.d("COMP61242 UDP", "Listening");
                s.setSoTimeout(5000);
                s.receive(p);
                Log.d("COMP61242 UDP", "receive success");
                s.close();
                return p;
            } catch (InterruptedIOException e) {
                attempts++;
                Log.d("COMP61242 UDP", "Timeout Exception: Did not receive a packet in 5 seconds");
            } catch (Exception e) {
                Log.d("COMP61242 UDP", "Exception: " + e.toString());
            }
            if (s != null) {
                s.close();
            }
        }
        Log.d("COMP61242 UDP", "Failed to receive any packet 5 times");
        p = null;
        return p;
    }

    @Override
    protected void onPostExecute(DatagramPacket p){
        listener.onTaskCompleted(p);
    }

    @Override
    protected void onCancelled (DatagramPacket p) {
        if (s != null) {
            s.close();
        }
    }

}