package com.example.ahmed.vorpalhexapodcontroller;

import android.os.Bundle;
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
        Button switcher = findViewById(R.id.btnSwitchFragment);

        switcher.setOnClickListener(v -> {
            if (counter++ % 2 == 0) {
                loadBluetoothController();
            } else {
                loadRobotController();
            }
        });

        this.loadBluetoothController();
    }

    @Override
    public void onFragmentInteraction(Sender sender) {
        robotControlFragment.setSenderDevice(sender);
        loadRobotController();
    }

    private void loadRobotController() {
        if (getSupportFragmentManager().findFragmentById(R.id.fragment_container) != null) {
            getSupportFragmentManager()
                    .beginTransaction().
                    remove(getSupportFragmentManager().findFragmentById(R.id.fragment_container)).commit();
        }

        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, robotControlFragment)
                .commit();
    }

    private void loadBluetoothController() {
        if (getSupportFragmentManager().findFragmentById(R.id.fragment_container) != null) {
            getSupportFragmentManager()
                    .beginTransaction().
                    remove(getSupportFragmentManager().findFragmentById(R.id.fragment_container)).commit();
        }
        getSupportFragmentManager()
                .beginTransaction()
                .replace(R.id.fragment_container, bluetoothConnectionFragment)
                .commit();
    }

}

