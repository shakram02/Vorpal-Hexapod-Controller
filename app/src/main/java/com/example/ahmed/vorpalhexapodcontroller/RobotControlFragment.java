package com.example.ahmed.vorpalhexapodcontroller;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.ahmed.vorpalhexapodcontroller.HexapodControl.Controller;
import com.example.ahmed.vorpalhexapodcontroller.HexapodControl.DpadDirection;
import com.example.ahmed.vorpalhexapodcontroller.HexapodControl.Mode;
import com.example.ahmed.vorpalhexapodcontroller.HexapodControl.SubMode;
import com.example.ahmed.vorpalhexapodcontroller.UiHelpers.SpinnerSubModeItem;

import java.util.Hashtable;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * <p>
 * Use the {@link RobotControlFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RobotControlFragment extends Fragment implements View.OnClickListener {

    // W (walk) Low Step | High Step | Small Step | Scamper
    // D (dance) Freestyle | Ballet | Waves | Hands
    // F (fight) Front Legs | Front Legs, Unison | Swivel | Lean
    private Controller motionController;
    private FragmentActivity parentView;

    static final Hashtable<Mode, String[]> modeTexts = new Hashtable<>();

    static {
        modeTexts.put(Mode.Walk, new String[]{"Forward", "Backward", "Left", "Right"});
    }

    public RobotControlFragment() {
        // Required empty public constructor
        motionController = new Controller();
    }

    public static RobotControlFragment newInstance(String param1, String param2) {
        RobotControlFragment fragment = new RobotControlFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        parentView = this.getActivity();

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
    public void onClick(View v) {
        Button b = (Button) v;
        DpadDirection direction = (DpadDirection) b.getTag();
        this.motionController.setDirection(direction);

        Toast.makeText(getActivity(), this.motionController.getPacket().toString(), Toast.LENGTH_SHORT).
                show();
    }

    private void hookDirectionsToButtons() {
        ViewGroup buttonLayout = this.parentView.findViewById(R.id.dPadButtonConstraintLayout);
        // Set the click listeners of motion buttons
        for (int i = 0; i < buttonLayout.getChildCount(); i++) {
            Button b = (Button) buttonLayout.getChildAt(i);
            b.setOnClickListener(this);

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