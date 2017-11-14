package com.example.ahmed.vorpalhexapodcontroller;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Spinner;

import com.example.ahmed.vorpalhexapodcontroller.BluetoothManagement.Sender;
import com.example.ahmed.vorpalhexapodcontroller.HexapodControl.ControlPacket;
import com.example.ahmed.vorpalhexapodcontroller.HexapodControl.Controller;
import com.example.ahmed.vorpalhexapodcontroller.HexapodControl.DpadDirection;
import com.example.ahmed.vorpalhexapodcontroller.HexapodControl.Mode;
import com.example.ahmed.vorpalhexapodcontroller.HexapodControl.SubMode;
import com.example.ahmed.vorpalhexapodcontroller.UiHelpers.RepeatListener;
import com.example.ahmed.vorpalhexapodcontroller.UiHelpers.SpinnerSubModeItem;

import java.io.IOException;
import java.util.Hashtable;


/**
 *
 */
public class RobotControlFragment extends Fragment implements View.OnClickListener {

    // W (walk) Low Step | High Step | Small Step | Scamper
    // D (dance) Freestyle | Ballet | Waves | Hands
    // F (fight) Front Legs | Front Legs, Unison | Swivel | Lean
    private Controller motionController;
    private FragmentActivity parentView;
    private Sender bluetoothSender;
    private static final int TOUCH_DELAY_MS = 200;
    private static final int BEFORE_REPEAT_DELAY_MS = 400;
    static final Hashtable<Mode, String[]> modeTexts = new Hashtable<>();

    static {
        modeTexts.put(Mode.Walk, new String[]{"Forward", "Backward", "Left", "Right"});
    }

    public RobotControlFragment() {
        // Required empty public constructor
        motionController = new Controller();
    }

    public void setSenderDevice(Sender bluetoothSender) {
        this.bluetoothSender = bluetoothSender;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        parentView = this.getActivity();
        // TODO: check if we're in the correct view or not to avoid null pointer exceptions
        this.hookModesToRadios();

        this.hookSubModesToSpinner();
        this.updateSubModeSpinnerTexts();

        this.hookDirectionsToButtons();
        this.updateButtonTexts();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_robot_control, container, false);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        try {
            bluetoothSender.close();
            Log.d(getClass().getSimpleName(), "Bluetooth connection closed");
        } catch (IOException e) {
            Log.e(getClass().getSimpleName(), e.getMessage());
        }
    }

    @Override
    public void onClick(View v) {
        Button b = (Button) v;
        DpadDirection direction = (DpadDirection) b.getTag();
        this.motionController.setDirection(direction);

        // TODO: add packets to queue, keep sending on a background thread each 100ms.
        // when the queue is empty a stop is sent
        ControlPacket packet = this.motionController.getPacket();
        Log.d(getClass().getSimpleName(), packet.toString());
        this.bluetoothSender.send(packet.encode());
    }

    private void hookDirectionsToButtons() {
        ViewGroup buttonLayout = this.parentView.findViewById(R.id.dPadButtonConstraintLayout);
        RepeatListener listener = new RepeatListener(BEFORE_REPEAT_DELAY_MS, TOUCH_DELAY_MS, this);
        // Set the click listeners of motion buttons
        for (int i = 0; i < buttonLayout.getChildCount(); i++) {
            Button b = (Button) buttonLayout.getChildAt(i);
            b.setOnTouchListener(listener);

            switch (b.getId()) {
                case R.id.btnBack:
                    b.setTag(DpadDirection.Backward);
                    break;
                case R.id.btnForward:
                    b.setTag(DpadDirection.Forward);
                    break;
                case R.id.btnLeft:
                    b.setTag(DpadDirection.Left);
                    break;
                case R.id.btnRight:
                    b.setTag(DpadDirection.Right);
                    break;
                case R.id.btnSpecial:
                    b.setTag(DpadDirection.Special);
                    break;
                default:
                    b.setTag(DpadDirection.Stop);
                    break;
            }
        }
    }

    private void hookModesToRadios() {
        ViewGroup radioLayout = this.parentView.findViewById(R.id.modesRdioGroup);
        // We're in another view, TODO: check why this throws a null pointer
        if (radioLayout == null) {
            return;
        }

        for (int i = 0; i < radioLayout.getChildCount(); i++) {

            RadioButton b = (RadioButton) radioLayout.getChildAt(i);

            Mode mode;
            switch (b.getId()) {
                case R.id.radioWalk:
                    mode = Mode.Walk;
                    break;
                case R.id.radioDance:
                    mode = Mode.Dance;
                    break;
                case R.id.radioKungFu:
                    mode = Mode.Fight;
                    break;
                default:
                    mode = Mode.Walk;
                    break;
            }

            b.setOnClickListener(v -> motionController.setMode(mode));
            updateSubModeSpinnerTexts(); // Update the spinner
        }
    }

    private void hookSubModesToSpinner() {
        final Spinner spinnerSubModes = this.parentView.findViewById(R.id.spinnerSubMode);

        SpinnerSubModeItem[] subModeItems = {
                new SpinnerSubModeItem("Low Step", SubMode.One),
                new SpinnerSubModeItem("High Step", SubMode.Two),
                new SpinnerSubModeItem("Small Step", SubMode.Two),
                new SpinnerSubModeItem("Scamper", SubMode.Two)
        };

        ArrayAdapter<SpinnerSubModeItem> adapter = new ArrayAdapter<>(this.getActivity(),
                android.R.layout.simple_spinner_item, subModeItems);
        spinnerSubModes.setAdapter(adapter);

        spinnerSubModes.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SpinnerSubModeItem item = (SpinnerSubModeItem) parent.getItemAtPosition(position);
                motionController.setSubMode(item.getSubMode());
                updateButtonTexts();
            }
        });
    }

    private void updateButtonTexts() {

    }

    private void updateSubModeSpinnerTexts() {
        // TODO Change sub modes in spinner
//        final Mode mode = motionController.getCurrentMode();
//        final Spinner spinnerSubModes = this.getActivity().findViewById(R.id.spinnerSubMode);

    }
}