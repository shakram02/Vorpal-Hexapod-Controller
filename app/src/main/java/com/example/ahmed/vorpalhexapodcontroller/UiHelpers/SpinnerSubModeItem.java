package com.example.ahmed.vorpalhexapodcontroller.UiHelpers;

import com.example.ahmed.vorpalhexapodcontroller.HexapodControl.SubMode;

/**
 * Created by ahmed on 11/13/17.
 */

public class SpinnerSubModeItem {
    // TODO: Add sub modes for each mode
    String text;
    SubMode subMode;

    public SpinnerSubModeItem(String text, SubMode subMode) {
        this.text = text;
        this.subMode = subMode;
    }

    public void setText(String text) {
        this.text = text;
    }

    public SubMode getSubMode() {
        return this.subMode;
    }

    @Override
    public String toString() {
        return text;
    }
}
