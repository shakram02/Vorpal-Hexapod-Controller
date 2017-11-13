package com.example.ahmed.vorpalhexapodcontroller;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.TextView;

import com.example.ahmed.vorpalhexapodcontroller.BluetoothManagement.BluetoothBoard;
import com.example.ahmed.vorpalhexapodcontroller.BluetoothManagement.SimpleBluetoothDevice;

import java.util.Set;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link BluetoothConnectionFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link BluetoothConnectionFragment#newInstance} factory method to
 * create an instance of this fragment.
 *
 * After pressing one button on the
 * left, you stay in that mode until a different mode button is pressed. You
 * don't have to keep holding them.
 */
public class BluetoothConnectionFragment extends Fragment {
    // GUI Components
    private TextView mBluetoothStatus;
    private TextView mReadBuffer;
    private Button mScanBtn;
    private Button mOffBtn;
    private Button mListPairedDevicesBtn;
    private Button mDiscoverBtn;
    private BluetoothAdapter mBTAdapter;
    private Set<BluetoothDevice> mPairedDevices;
    private ArrayAdapter<SimpleBluetoothDevice> mBTArrayAdapter;
    private ListView mDevicesListView;
    private CheckBox mLedButton;
    private BroadcastReceiver blReceiver;
    private BluetoothBoard mBluetoothBoard; // bluetooth background worker thread to send and receive data

    private final static int REQUEST_ENABLE_BT = 1; // used to identify adding bluetooth names

    private OnFragmentInteractionListener mListener;

    public BluetoothConnectionFragment() {
        // Required empty public constructor
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
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        }

        mBluetoothStatus = getView().findViewById(R.id.bluetoothStatus);
        mReadBuffer = getView().findViewById(R.id.readBuffer);
        mScanBtn = getView().findViewById(R.id.scan);
        mOffBtn = getView().findViewById(R.id.off);
        mDiscoverBtn = getView().findViewById(R.id.discover);
        mListPairedDevicesBtn = getView().findViewById(R.id.PairedBtn);
        mLedButton = getView().findViewById(R.id.checkboxLED1);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
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
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
