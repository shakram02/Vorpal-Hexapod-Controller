package com.example.ahmed.vorpalhexapodcontroller;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        if (findViewById(R.id.fragment_container) == null || savedInstanceState != null) {
            return;
        }

        // Create a new Fragment to be placed in the activity layout
        RobotControlFragment firstFragment = RobotControlFragment.newInstance("x", "y");

        // In case this activity was started with special instructions from an
        // Intent, pass the Intent's extras to the fragment as arguments
        firstFragment.setArguments(getIntent().getExtras());

        // Add the fragment to the 'fragment_container' FrameLayout
        getSupportFragmentManager().beginTransaction()
                .add(R.id.fragment_container, firstFragment).commit();
    }

}

