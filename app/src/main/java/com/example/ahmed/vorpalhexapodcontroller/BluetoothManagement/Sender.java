package com.example.ahmed.vorpalhexapodcontroller.BluetoothManagement;

/**
 * Created by ahmed on 11/13/17.
 */

public interface Sender {
    void send(byte[] bytes);

    void send(Byte[] bytes);
}
