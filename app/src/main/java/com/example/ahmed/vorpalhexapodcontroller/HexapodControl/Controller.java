package com.example.ahmed.vorpalhexapodcontroller.HexapodControl;

import android.util.Log;

public class Controller {
    private Mode currentMode;
    private SubMode subMode;
    private DpadDirection direction;

    public Controller() {
        subMode = SubMode.One;
        currentMode = Mode.Walk;
        direction = DpadDirection.Stop;
    }

    public Mode getCurrentMode() {
        return currentMode;
    }

    public SubMode getSubMode() {
        return subMode;
    }

    public DpadDirection getDirection() {
        return direction;
    }

    public void setMode(Mode hexapodMode) {
        this.currentMode = hexapodMode;
    }

    public void setSubMode(SubMode hexapodSubMode) {
        this.subMode = hexapodSubMode;
    }

    public void setDirection(DpadDirection dpadDirection) {
        this.direction = dpadDirection;
    }

    public ControlPacket getPacket() {
        Log.d(getClass().getSimpleName(), String.format("%s | %s | %s ", this.currentMode, this.subMode, this.direction));
        return new ControlPacket(this.currentMode, this.subMode, this.direction);
    }
}
