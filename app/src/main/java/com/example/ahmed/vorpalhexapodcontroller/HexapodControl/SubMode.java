package com.example.ahmed.vorpalhexapodcontroller.HexapodControl;

/**
 * Created by ahmed on 11/12/17.
 * <p>
 * Submodes of the modes, as documented here:
 * http://vorpalrobotics.com/wiki/index.php?title=Vorpal_Combat_Hexapod_Gamepad_User_Guide
 * <p>
 * A mode number: the literal characters 1, 2, 3, or 4
 */

public enum SubMode {
    One,
    Two,
    Three,
    Four;

    @Override
    public String toString() {
        switch (this) {
            case One:
                return "1";
            case Two:
                return "2";
            case Three:
                return "3";
            case Four:
                return "4";
            default:
                throw new IllegalStateException();
        }
    }

    public SubMode copy() {
        switch (this) {
            case One:
                return One;
            case Two:
                return Two;
            case Three:
                return Three;
            case Four:
                return Four;
            default:
                throw new IllegalStateException();
        }
    }
}
