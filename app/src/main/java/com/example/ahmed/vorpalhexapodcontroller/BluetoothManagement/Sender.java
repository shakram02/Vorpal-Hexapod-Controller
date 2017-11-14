package com.example.ahmed.vorpalhexapodcontroller.BluetoothManagement;

import java.io.IOException;

/**
 * Created by ahmed on 11/13/17.
 */

public interface Sender {
    void connect() throws IOException;

    void send(byte[] bytes);

    void send(Byte[] bytes);
}
