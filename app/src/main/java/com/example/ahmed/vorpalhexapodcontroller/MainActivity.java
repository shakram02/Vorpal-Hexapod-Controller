package com.example.ahmed.vorpalhexapodcontroller;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;

import com.example.ahmed.vorpalhexapodcontroller.BluetoothManagement.Sender;

public class MainActivity extends AppCompatActivity implements BluetoothConnectionFragment.OnFragmentInteractionListener {

    private RobotControlFragment robotControlFragment;
    private BluetoothConnectionFragment bluetoothConnectionFragment;
    private int counter = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        if (savedInstanceState != null) {
            return;
        }

        robotControlFragment = new RobotControlFragment();
        robotControlFragment.setArguments(getIntent().getExtras());

        bluetoothConnectionFragment = BluetoothConnectionFragment.newInstance();
        bluetoothConnectionFragment.setArguments(getIntent().getExtras());
        this.loadFragment(bluetoothConnectionFragment);
    }

    @Override
    public void onFragmentInteraction(Sender sender) {
        robotControlFragment.setSenderDevice(sender);
        loadFragment(robotControlFragment);
    }

    private void loadFragment(Fragment fragment) {
        // Remove the existing fragment from view
//        if (getSupportFragmentManager().findFragmentById(R.id.fragment_container) != null) {
//            getSupportFragmentManager()
//                    .beginTransaction().
//                    remove(getSupportFragmentManager().findFragmentById(R.id.fragment_container)).commit();
//        }

//        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
//            // Add the first fragment without adding it to back stack, so pressing back now exits
//            getSupportFragmentManager()
//                    .beginTransaction()
//                    .add(R.id.fragment_container, fragment)
//                    .commit();
//        } else {
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, fragment)
                .addToBackStack(fragment.getClass().getSimpleName())
                .commit();
//        }


    }

}

