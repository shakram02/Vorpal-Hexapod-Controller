package com.example.ahmed.vorpalhexapodcontroller.BluetoothManagement;

import android.bluetooth.BluetoothDevice;

/**
 * Created by ahmed on 11/12/17.
 * <p>
 * A wrapper around bluetooth devices
 */

public class SimpleBluetoothDevice {
    private BluetoothDevice device;

    public SimpleBluetoothDevice(BluetoothDevice device) {
        this.device = device;
    }

    public String getAddress() {
        return device.getAddress();
    }

    public String getName() {
        // TODO: get name from GATT stuff
        return device.getName();
    }

    @Override
    public String toString() {
        return String.format("%s\n%s", device.getName(), device.getAddress());
    }
}
