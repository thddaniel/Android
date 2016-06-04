package com.compscitutorials.basigarcia.navigationdrawervideotutorial;

import android.widget.TextView;

import java.net.DatagramPacket;


/**
 * Interface that allows a class to handle onPostExecute from ClientTask Async class
 */
public interface OnTaskCompleted {
    void onTaskCompleted(DatagramPacket p);
}