package com.example.ahmed.vorpalhexapodcontroller.HexapodControl;

import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
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
public class Controller {
    private Mode currentMode = Mode.Walk;
    private SubMode subMode;
    private DpadDirection direction;

    public Controller() throws UnsupportedEncodingException {
        if (Charset.isSupported("UTF-8")) {
            throw new UnsupportedEncodingException("UTF-8 encoding isn't supported");
        }
    }

    private static final String packetHeader = "V1";

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


    public Byte[] getPacket() throws UnsupportedEncodingException {
        String payload = this.currentMode.toString()
                + this.subMode.toString()
                + this.direction.toString();

        ArrayList<Byte> packet = new ArrayList<>();

        for (Byte b : packetHeader.getBytes("UTF-8")) {
            packet.add(b);
        }

        packet.add(((byte) payload.length()));

        for (Byte b : payload.getBytes()) {
            packet.add(b);
        }

        // Checksum
        if (payload.length() > 256) {
            throw new UnsupportedOperationException("Length is too long");
        }
        packet.add((byte) payload.length());

        this.onAfterSend();
        return (Byte[]) packet.toArray();
    }

    private void onAfterSend() {
        this.direction = DpadDirection.Stop;    // Revert to default direction
    }

    /*
      // if we get here, there is a full frame to send to the robot
        BlueTooth.print("V1");
        BlueTooth.write(length);
        //Serial.print("#SNDV1:Len="); Serial.println(length);
        {
            int checksum = length;  // the length byte is included in the checksum
            for (int i = 0; i < length && SDGamepadRecordFile.available()>0; i++) {
              int c = SDGamepadRecordFile.read();
              checksum += c;
              BlueTooth.write(c);
              //Serial.write(c);Serial.print("(");Serial.print(c);Serial.print(")");
            }
            checksum = (checksum % 256);
            BlueTooth.write(checksum);
     */

    /**
     * int receiveDataHandler() {

     while (BlueTooth.available() > 0) {
     unsigned int c = BlueTooth.read();

     //Serial.print(millis());
     //Serial.print("'"); Serial.write(c); Serial.print("' ("); Serial.print((int)c);
     //Serial.print(")S="); Serial.print(packetState); Serial.print(" a="); Serial.print(BlueTooth.available()); Serial.println("");
     //Serial.print(millis()); Serial.println("");
     switch (packetState) {
     case P_WAITING_FOR_HEADER:
     if (c == 'V') {
     packetState = P_WAITING_FOR_VERSION;
     //Serial.print("GOTV ");
     } else {
     // may as well flush up to the next header
     int flushcount = 0;
     while (BlueTooth.available() > 0 && (BlueTooth.peek() != 'V')) {
     BlueTooth.read(); // toss up to next possible header start
     flushcount++;
     }
     Serial.println(flushcount);
     packetErrorChirp(c);
     return 0;
     }
     break;
     case P_WAITING_FOR_VERSION:
     if (c == '1') {
     packetState = P_WAITING_FOR_LENGTH;
     //Serial.print("G1 ");
     } else if (c == 'V') {
     // this can actually happen if the checksum was a 'V' and some noise caused a
     // flush up to the checksum's V, that V would be consumed by state WAITING FOR HEADER
     // leaving the real 'V' header in position 2. To avoid an endless loop of this situation
     // we'll simply stay in this state (WAITING FOR VERSION) if we see a 'V' in this state.

     // do nothing here
     } else {
     packetErrorChirp(c);
     }
     break;
     case P_WAITING_FOR_LENGTH:
     { // need scope for local variables
     packetLength = c;
     packetLengthReceived = 0;
     packetState = P_READING_DATA;

     //Serial.print("L="); Serial.println(packetLength);
     }
     break;
     case P_READING_DATA:
     packetData[packetLengthReceived++] = c;
     if (packetLengthReceived == packetLength) {
     packetState = P_WAITING_FOR_CHECKSUM;
     }
     //Serial.print("CHAR("); Serial.print(c); Serial.print("/"); Serial.write(c); Serial.println(")");
     break;

     case P_WAITING_FOR_CHECKSUM:

     {
     unsigned int sum = packetLength;
     for (int i = 0; i < packetLength; i++) {
     // uncomment the next line if you need to see the packet bytes
     //Serial.print(packetData[i]);Serial.print("-");
     sum += packetData[i];
     }
     sum = (sum % 256);

     if (sum != c) {
     packetErrorChirp(c);
     Serial.print("cs fail "); Serial.print(sum); Serial.print("!="); Serial.print((int)c); Serial.print("len="); Serial.println(packetLength);
     } else {
     LastValidReceiveTime = millis();  // set the time we received a valid packet
     processPacketData();
     packetState = P_WAITING_FOR_HEADER;
     //dumpPacket();   // comment this line out unless you are debugging packet transmissions
     return 1; // new data arrived!
     }
     }
     break;
     }
     }

     return 0; // no new data arrived
     }
     */
}
