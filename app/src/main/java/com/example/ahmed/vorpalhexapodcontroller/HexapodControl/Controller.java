package com.example.ahmed.vorpalhexapodcontroller.HexapodControl;

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
        ControlPacket p = new ControlPacket(this.currentMode, this.subMode, this.direction);
        this.onAfterSend();
        return p;
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
