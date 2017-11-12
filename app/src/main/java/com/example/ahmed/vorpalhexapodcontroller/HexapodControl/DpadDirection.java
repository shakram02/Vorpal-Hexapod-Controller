package com.example.ahmed.vorpalhexapodcontroller.HexapodControl;

/**
 * Created by ahmed on 11/12/17.
 * <p>
 * A DPAD letter: f, b, l, r, s, w.
 */

public enum DpadDirection {
    Special,
    Forward,
    Backward,
    Right,
    Stop,
    Left;

    @Override
    public String toString() {
        switch (this) {
            case Special:
                return "w";
            case Forward:
                return "f";
            case Backward:
                return "b";
            case Right:
                return "r";
            case Left:
                return "l";
            case Stop:
                return "s";
            default:
                throw new IllegalStateException();
        }

    }
}
