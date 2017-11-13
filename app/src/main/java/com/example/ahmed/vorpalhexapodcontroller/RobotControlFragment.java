package com.example.ahmed.vorpalhexapodcontroller;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
 * {@link RobotControlFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link RobotControlFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RobotControlFragment extends Fragment implements View.OnClickListener {
    private OnFragmentInteractionListener mListener;
    // W (walk) Low Step | High Step | Small Step | Scamper
    // D (dance) Freestyle | Ballet | Waves | Hands
    // F (fight) Front Legs | Front Legs, Unison | Swivel | Lean
    private Controller motionController;
    private static final String NO_ASCII_ENCODING_ERR = "Couldn't find ASCII encoding, packet creation will fail";
    private Activity parentActivity;

    public RobotControlFragment() {
        // Required empty public constructor
        motionController = new Controller();
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RobotControlFragment.
     */
    // TODO: Rename and change types and number of parameters
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
        parentActivity = this.getActivity();

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
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onClick(View v) {
        Button b = (Button) v;
        DpadDirection direction = (DpadDirection) b.getTag();
        this.motionController.setDirection(direction);

        Toast.makeText(getActivity(), this.motionController.getPacket().toString(), Toast.LENGTH_SHORT).
                show();
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
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void hookDirectionsToButtons() {
        ViewGroup buttonLayout = getView().findViewById(R.id.dPadButtonConstraintLayout);
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
        ViewGroup radioLayout = getView().findViewById(R.id.modesRdioGroup);

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
        final Spinner spinnerSubModes = this.getActivity().findViewById(R.id.spinnerSubMode);

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

                // TODO: Change text on motion buttons
                Toast.makeText(parentActivity, item.toString(), Toast.LENGTH_SHORT).show();
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

    static final Hashtable<Mode, String[]> modeTexts = new Hashtable<>();

    static {
        modeTexts.put(Mode.Walk, new String[]{"Forward", "Backward", "Left", "Right"});
    }
}