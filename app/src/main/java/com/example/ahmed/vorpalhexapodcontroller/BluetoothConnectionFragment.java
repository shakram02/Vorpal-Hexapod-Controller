package com.example.ahmed.vorpalhexapodcontroller;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ListView;
import android.widget.Switch;
import android.widget.Toast;

import com.example.ahmed.vorpalhexapodcontroller.BluetoothManagement.BluetoothBoard;
import com.example.ahmed.vorpalhexapodcontroller.BluetoothManagement.Sender;
import com.example.ahmed.vorpalhexapodcontroller.BluetoothManagement.SimpleBluetoothDevice;
import com.example.ahmed.vorpalhexapodcontroller.UiHelpers.ToastHandler;

import java.io.IOException;
import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link onBluetoothDeviceReadyListener} interface
 * to handle interaction events.
 * Use the {@link BluetoothConnectionFragment#newInstance} factory method to
 * create an instance of this fragment.
 * <p>
 * After pressing one button on the
 * left, you stay in that mode until a different mode button is pressed. You
 * don't have to keep holding them.
 */
public class BluetoothConnectionFragment extends Fragment {
    private final static int REQUEST_ENABLE_BT = 1; // used to identify adding bluetooth names
    private final static int REQUEST_LOCATION_PERMISSION = 2;

    private Button mListPairedDevicesBtn;
    private Button mDiscoverBtn;
    private BluetoothAdapter mBTAdapter;
    private ArrayAdapter<SimpleBluetoothDevice> btDeviceListAdapter;
    private ListView lvDevices;
    private BroadcastReceiver blReceiver;
    private boolean discoveryReceiverRegistered;
    private ToastHandler toastHandler;
    private FragmentActivity parentActivity;
    private onBluetoothDeviceReadyListener mListener;
    private Switch swBluetoothState;

    public BluetoothConnectionFragment() {
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @return A new instance of fragment BluetoothConnectionFragment.
     */
    public static BluetoothConnectionFragment newInstance() {
        return new BluetoothConnectionFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_bluetooth_connection, container, false);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof onBluetoothDeviceReadyListener) {
            mListener = (onBluetoothDeviceReadyListener) context;
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        parentActivity = this.getActivity();
        toastHandler = new ToastHandler(parentActivity);

        setupUiComponents();
        setupBluetooth();
        setupClickListeners();
        setupDeviceListAdapter();
    }

    private void setupDeviceListAdapter() {
        btDeviceListAdapter = new ArrayAdapter<>(this.getContext(), android.R.layout.simple_list_item_1);
        lvDevices.setAdapter(btDeviceListAdapter); // assign model to view
        lvDevices.setOnItemClickListener(onDeviceClickedHandler);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Ask for location permission if not already allowed
        if (ContextCompat.checkSelfPermission(this.getContext(),
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this.parentActivity,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_LOCATION_PERMISSION);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (permissions.length == 0 || grantResults.length == 0) {
            return;
        }

        if (requestCode != REQUEST_LOCATION_PERMISSION) {
            return;
        }

        if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
            toastHandler.showToast("I won't be able to find unpaired devices",
                    Toast.LENGTH_LONG);

            mDiscoverBtn.setEnabled(false);
            mDiscoverBtn.setText(R.string.btn_txt_location_needed);
        }
    }

    private void setupBluetooth() {
        mBTAdapter = BluetoothAdapter.getDefaultAdapter(); // get a handle on the bluetooth radio

        if (mBTAdapter == null) {
            // TODO: better show a message
            // Device does not support Bluetooth
            toastHandler.showToast("Bluetooth device not found, exiting...",
                    Toast.LENGTH_SHORT);
            this.parentActivity.finish();
        }

        if (mBTAdapter.isEnabled()) {
            swBluetoothState.setChecked(true);
        } else {
            swBluetoothState.setChecked(false);
        }
    }

    private void setupUiComponents() {
        swBluetoothState = parentActivity.findViewById(R.id.swBluetoothState);
        swBluetoothState.setTextOff("On");
        swBluetoothState.setTextOn("Off");

        lvDevices = parentActivity.findViewById(R.id.devicesListView);
        mDiscoverBtn = parentActivity.findViewById(R.id.btnDiscover);
        mListPairedDevicesBtn = parentActivity.findViewById(R.id.btnShowPaired);
    }

    private void setupClickListeners() {
        swBluetoothState.setOnCheckedChangeListener(this::bluetoothStateClickListener);
        mListPairedDevicesBtn.setOnClickListener(v -> showPairedDevices());
        mDiscoverBtn.setOnClickListener(v -> discover());
    }

    private void bluetoothStateClickListener(CompoundButton compoundButton, boolean nextState) {
        if (nextState) {
            // The switch was in the non checked state
            bluetoothOn();
        } else {
            bluetoothOff();
        }
    }

    private void bluetoothOn() {
        Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
        startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
    }

    private void bluetoothOff() {
        mBTAdapter.disable();

    }

    private void discover() {
        if (!mBTAdapter.isEnabled()) {
            toastHandler.showToast("Bluetooth not on", Toast.LENGTH_SHORT);
            return;
        }

        // Check if the device is already discovering
        else if (mBTAdapter.isDiscovering()) {
            mBTAdapter.cancelDiscovery();
            toastHandler.showToast("Discovery stopped", Toast.LENGTH_SHORT);
            return;
        }

        btDeviceListAdapter.clear();
        mBTAdapter.startDiscovery();
        toastHandler.showToast("Discovery started", Toast.LENGTH_SHORT);

        // Setup a broadcast receiver
        this.blReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                if (!BluetoothDevice.ACTION_FOUND.equals(intent.getAction())) {
                    toastHandler.showToast("No devices found", Toast.LENGTH_SHORT);
                    return;
                }

                BluetoothDevice device = intent.getParcelableExtra(
                        android.bluetooth.BluetoothDevice.EXTRA_DEVICE);
                // add the name to the list
                btDeviceListAdapter.add(new SimpleBluetoothDevice(device));
                btDeviceListAdapter.notifyDataSetChanged();
                toastHandler.showToast("Device Found", Toast.LENGTH_SHORT);
            }
        };

        parentActivity.registerReceiver(blReceiver, new IntentFilter(BluetoothDevice.ACTION_FOUND));
        discoveryReceiverRegistered = true;
    }


    // The UI component that calls this action should be hidden when bluetooth is off
    private void showPairedDevices() {
        if (!mBTAdapter.isEnabled()) {
            toastHandler.showToast("Bluetooth not on", Toast.LENGTH_SHORT);
            return;
        }

        // No devices will be returned if the bluetooth isn't on
        Set<BluetoothDevice> mPairedDevices = mBTAdapter.getBondedDevices();
        btDeviceListAdapter.clear();

        // put it's one to the adapter
        for (android.bluetooth.BluetoothDevice device : mPairedDevices) {
            btDeviceListAdapter.add(new SimpleBluetoothDevice(device));
        }
    }


    private AdapterView.OnItemClickListener onDeviceClickedHandler = new AdapterView.OnItemClickListener() {
        public void onItemClick(AdapterView<?> av, View v, int position, long id) {

            if (!mBTAdapter.isEnabled()) {
                toastHandler.showToast("Bluetooth not on", Toast.LENGTH_SHORT);
                return;
            }

            if (mBTAdapter.isDiscovering()) {
                // Cancel discovery once the user knows which device to connect to
                mBTAdapter.cancelDiscovery();
            }

            SimpleBluetoothDevice btDev = (SimpleBluetoothDevice) av.getItemAtPosition(position);
            final String address = btDev.getAddress();
            android.bluetooth.BluetoothDevice device = mBTAdapter.getRemoteDevice(address);
            BluetoothBoard bluetoothSender = new BluetoothBoard(device);

            // TODO: display a "connecting..." message with a spinner
            Runnable connectTask = () -> {
                try {
                    bluetoothSender.connect();
                    mListener.onBluetoothDeviceReady(bluetoothSender);
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
            };

            Thread connectionThread = new Thread(connectTask);
            connectionThread.setUncaughtExceptionHandler(toastHandler::threadErrorToast);
            connectionThread.start();
        }
    };

    // Enter here after user selects "yes" or "no" to enabling radio
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent Data) {
        // Check which request we're responding to
        if (requestCode != REQUEST_ENABLE_BT) {
            return;
        }

        // Make sure the request was successful
        if (resultCode == Activity.RESULT_OK) {
            swBluetoothState.setChecked(true);
        } else {
            swBluetoothState.setChecked(false);
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;

        if (mBTAdapter.isDiscovering()) {
            mBTAdapter.cancelDiscovery();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (!discoveryReceiverRegistered) {
            return;
        }

        parentActivity.unregisterReceiver(blReceiver);
        discoveryReceiverRegistered = false;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface onBluetoothDeviceReadyListener {
        void onBluetoothDeviceReady(Sender sender);
    }
}
