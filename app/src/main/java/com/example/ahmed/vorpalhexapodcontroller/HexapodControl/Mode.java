package com.example.ahmed.vorpalhexapodcontroller.HexapodControl;

/**
 * Created by ahmed on 11/12/17.
 * <p>
 * Mode for the hexa pod W, D, F for "walk", "dance", "fight"
 */

public enum Mode {
    Walk,
    Dance,
    Fight;

    @Override
    public String toString() {
        switch (this) {
            case Walk:
                return "W";
            case Dance:
                return "D";
            case Fight:
                return "F";
            default:
                throw new IllegalStateException();
        }
    }
}
