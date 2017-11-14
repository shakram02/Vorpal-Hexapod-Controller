package com.example.ahmed.vorpalhexapodcontroller.HexapodControl;

import android.util.Log;

import com.example.ahmed.vorpalhexapodcontroller.BluetoothManagement.Encodable;

import java.io.Serializable;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.util.ArrayList;

/**
 * Created by ahmed on 11/12/17.
 * <p>
 * Sends the robot commands based on the specs.
 * <p>
 * A 3-byte gamepad function specification, which consists of:
 * A mode letter: W, D, or F
 * A mode number: the literal characters 1, 2, 3, or 4
 * A DPAD letter: f, b, l, r, s, w.
 * For example, W2f which means that the function to execute is walk mode 2 with
 * the forward DPAD button pressed. As such, this type of command always starts
 * with a literal character of W, D, or F.
 * <p>
 * A 4-byte leg motion command. This command consists of:
 * The literal character L.
 * The next byte is the leg selection mask, which has one bit for each leg that is affected
 * by the command. The low bit is leg 0, the next higher bit is leg 1, etc.
 * A 1-byte bitmaks of flags. Currently, the flags may take the numeric value 0 or 1,
 * where 0 means no flags and 1 means that hip motions should be mirrored on the left and
 * right sides of the robot (useful for most types of walking).
 * <p>
 * The hip position value, from 0 to 179, followed by the knee value, from 0 to 179.
 * For either the hip or knee position, the special value 255 means no movement is required.
 * This allows the leg motion command to move just the hips or just the knees.
 * There may be several of these concatenated in order to move different groups of legs
 * to different positions in a single packet.
 */
public class ControlPacket implements Encodable {
    private final Mode mode;
    private final SubMode subMode;
    private final DpadDirection dpadDirection;
    private static final String PACKET_CHARSET = "UTF-8";
    private static final String PACKET_HEADER = "V1";

    ControlPacket(final Mode mode, final SubMode subMode, final DpadDirection dpadDirection) {
        // Node that we need to get a copy
        this.mode = mode.copy();
        this.subMode = subMode.copy();
        this.dpadDirection = dpadDirection.copy();
    }

    public Byte[] encode() {

        String payload = this.mode.toString()
                + this.subMode.toString()
                + this.dpadDirection.toString();
        Log.d("PACKET_CONTENT", payload);

        ArrayList<Byte> packet = new ArrayList<>();

        try {
            packet.addAll(this.encodeHeader());
            packet.add(((byte) (payload.length() & 0xFF)));
            packet.addAll(this.encodePayload(payload));
            packet.add(this.encodeChecksum(payload));
        } catch (UnsupportedEncodingException exc) {
            throw new RuntimeException(exc);
        }

        Byte[] result = new Byte[packet.size()];
        packet.toArray(result);
        return result;
    }

    private ArrayList<Byte> encodeHeader() throws UnsupportedEncodingException {
        ArrayList<Byte> result = new ArrayList<>();

        for (Byte b : PACKET_HEADER.getBytes(PACKET_CHARSET)) {
            result.add(b);
        }

        return result;
    }

    private ArrayList<Byte> encodePayload(String payload) throws UnsupportedEncodingException {
        ArrayList<Byte> result = new ArrayList<>();

        for (Byte b : payload.getBytes(PACKET_CHARSET)) {
            result.add(b);
        }

        return result;
    }

    private Byte encodeChecksum(String payload) {
        // Checksum = (length + sum of data) % 256

        int checksum = payload.length();
        for (byte b : payload.getBytes()) {
            checksum += b;
        }

        return (byte) ((checksum % 256));
    }

    @Override
    public String toString() {
        String payload = this.mode + this.subMode.toString() + this.dpadDirection;
        return String.format("%s%s%s", PACKET_HEADER, payload.length(), payload);
    }
}
