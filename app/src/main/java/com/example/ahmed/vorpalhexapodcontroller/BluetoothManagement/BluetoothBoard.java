package com.example.ahmed.vorpalhexapodcontroller.BluetoothManagement;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.os.SystemClock;
import android.util.Log;

import java.io.Closeable;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Method;
import java.util.UUID;

/**
 * Created by ahmed on 11/12/17.
 */
public class BluetoothBoard extends Thread implements Closeable {
    private final BluetoothSocket boardSocket;
    private final InputStream mmInStream;
    private final OutputStream mmOutStream;
    private static final UUID BTMODULEUUID = UUID.fromString("00001101-0000-1000-8000-00805F9B34FB"); // "random" unique identifier

    public BluetoothBoard(BluetoothDevice device) throws IOException {
        boardSocket = createBluetoothSocket(device);

        boardSocket.connect();
        // Get the input and output streams, using temp objects because
        // member streams are final
        mmInStream = boardSocket.getInputStream();
        mmOutStream = boardSocket.getOutputStream();

        this.send("HELLO!!");
    }

    private BluetoothSocket createBluetoothSocket(BluetoothDevice device) throws IOException {
        try {
            final Method m = device.getClass().getMethod("createInsecureRfcommSocketToServiceRecord", UUID.class);
            return (BluetoothSocket) m.invoke(device, BTMODULEUUID);
        } catch (Exception e) {
            Log.e(getClass().getName(), "Could not create Insecure RFComm Connection", e);
        }
        return device.createRfcommSocketToServiceRecord(BTMODULEUUID);
    }


    public void run() {
        byte[] buffer;
        int bytes;

        // Keep listening to the InputStream until an exception occurs
        while (true) {

            // Read from the InputStream
            try {
                bytes = mmInStream.available();
                if (bytes == 0) {
                    continue;
                }

                buffer = new byte[1024];
                SystemClock.sleep(100); //pause and wait for rest of data. Adjust this depending on your sending speed.
                bytes = mmInStream.available(); // how many bytes are ready to be read?
                bytes = mmInStream.read(buffer, 0, bytes); // record how many bytes we actually read
                //TODO: message received

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    /* Call this from the main activity to send data to the remote device */
    public void send(String input) {
        byte[] bytes = input.getBytes();           //converts entered String into bytes
        try {
            mmOutStream.write(bytes);
        } catch (IOException e) {
            Log.e(getClass().getName(), e.toString());
        }
    }

    /* Call this from the main activity to shutdown the connection */
    public void close() {
        try {
            // TODO: call this in onSuspend / onKill
            boardSocket.close();
        } catch (IOException e) {
            Log.e(getClass().getName(), e.toString());
        }
    }
}
