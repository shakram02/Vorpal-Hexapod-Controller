package com.example.ahmed.vorpalhexapodcontroller.BluetoothManagement;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.SystemClock;
import android.util.Log;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.UUID;
import java.util.concurrent.LinkedBlockingQueue;

/**
 * Created by ahmed on 11/12/17.
 * <p>
 * Software representation for a connected bluetooth device
 */
public class BluetoothBoard extends Thread implements Closeable, Sender {
    private final BluetoothSocket boardSocket;
    private InputStream mmInStream;
    private OutputStream mmOutStream;
    // TODO: change the UUID to something dynamic ?
    private static final UUID BT_MODULE_UUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); // "random" unique identifier
    private final LinkedBlockingQueue<Byte[]> receivedMessagesQueue;

    public BluetoothBoard(BluetoothDevice device) {
        boardSocket = createBluetoothSocket(device);
        receivedMessagesQueue = new LinkedBlockingQueue<>();
        // TODO Create receiver thread
    }

    public void connect() throws IOException {
        // TODO: this blocks
        boardSocket.connect();
        // Get the input and output streams, using temp objects because
        // member streams are final
        mmInStream = boardSocket.getInputStream();
        mmOutStream = boardSocket.getOutputStream();
    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) {
        try {
            return device.createRfcommSocketToServiceRecord(BT_MODULE_UUID);
        } catch (Exception e) {
            Log.e(getClass().getName(), "Could not create Insecure RFComm Connection", e);
            throw new RuntimeException(e);
        }
    }

    public void send(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            sb.append(b);
            sb.append(" ");
        }

        try {
            mmOutStream.write(bytes);
        } catch (IOException e) {
            Log.e(getClass().getName(), e.toString());
        }

        Log.i(getClass().getSimpleName(), "OnWire:" + sb.toString());
        SystemClock.sleep(100); //pause and wait for rest of data. Adjust this depending on your sending speed.
    }

    /* Call this from the main activity to send data to the remote device */
    public void send(Byte[] data) {

        byte[] bytes = new byte[data.length];

        for (int i = 0; i < data.length; i++) {
            bytes[i] = data[i];
        }

        this.send(bytes);
    }

    public void run() {
        byte[] buffer;
        int byteCount;

        try {
            while (true) {
                byteCount = mmInStream.available();
                if (byteCount == 0) {
                    continue;
                }

//                SystemClock.sleep(100); //pause and wait for rest of data. Adjust this depending on your sending speed.
                byteCount = mmInStream.available(); // how many bytes are ready to be read?
                buffer = new byte[byteCount];
                byteCount = mmInStream.read(buffer, 0, byteCount); // record how many bytes we actually read

                if (byteCount == -1) {
                    close();
                    return;
                }

                Byte[] data = new Byte[byteCount];
                for (int i = 0; i < byteCount; i++) {
                    data[i] = buffer[i];
                }

                receivedMessagesQueue.add(data);
            }
        } catch (IOException e) {
            e.printStackTrace();

            // TODO: test exception execution path
            throw new RuntimeException(e);
        }
    }


    public Byte[] getMessage() {
        try {
            return this.receivedMessagesQueue.take();
        } catch (InterruptedException e) {
            throw new RuntimeException("Couldn't take an item from message queue");
        }
    }

    /**
     * Stops receiving messages then closes the bluetooth socket
     */
    public void close() {
        try {
            boardSocket.close();
        } catch (IOException e) {
            Log.e(getClass().getName(), e.toString());
        }
    }
}
