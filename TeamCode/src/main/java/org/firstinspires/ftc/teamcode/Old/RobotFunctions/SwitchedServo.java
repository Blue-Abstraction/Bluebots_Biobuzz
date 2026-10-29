package org.firstinspires.ftc.teamcode.Old.RobotFunctions;

import com.qualcomm.robotcore.hardware.Servo;
import java.util.ArrayList;

public class SwitchedServo {
    private Servo servo1, servo2;
    private double pos1, pos2;
    private ArrayList<Double> positions;
    private int primaryPosIndex, secondaryPosIndex;

    public SwitchedServo(Servo servo1, double pos1, double pos2) {
        this.servo1 = servo1;
        this.pos1 = pos1;
        this.pos2 = pos2;
        this.positions = null;
        setDirections();
    }
    public SwitchedServo(Servo servo1, int primaryPosIndex, int secondaryPosIndex, ArrayList<Double> positions) {
        this.servo1 = servo1;
        this.primaryPosIndex = primaryPosIndex;
        this.secondaryPosIndex = secondaryPosIndex;
        this.positions = positions;
        setDirections();
    }
    public SwitchedServo(Servo servo1, Servo servo2, double pos1, double pos2) {
        this.servo1 = servo1;
        this.servo2 = servo2;
        this.pos1 = pos1;
        this.pos2 = pos2;
        this.positions = null;
        setDirections();
    }
    public SwitchedServo(Servo servo1, Servo servo2, int primaryPosIndex, int secondaryPosIndex, ArrayList<Double> positions) {
        this.servo1 = servo1;
        this.servo2 = servo2;
        this.primaryPosIndex = primaryPosIndex;
        this.secondaryPosIndex = secondaryPosIndex;
        this.positions = positions;
        setDirections();
    }

    private boolean isUsingList() { return positions != null; }

    public void primaryPos() {
        atPrimary = true;
        listIndex = primaryPosIndex;
        if (isUsingList()) {
            servo1.setPosition(positions.get(primaryPosIndex));
            if (servo2 != null) {
                servo2.setPosition(positions.get(primaryPosIndex));
        }
        } else {
            servo1.setPosition(pos1);
            if (servo2 != null) {
                servo2.setPosition(pos1);
            }
        }
    }

    public void secondaryPos() {
        atPrimary = false;
        listIndex = secondaryPosIndex;
        if (isUsingList()) {
            servo1.setPosition(positions.get(secondaryPosIndex));
            if (servo2 != null) {
                servo2.setPosition(positions.get(secondaryPosIndex));
            }
        } else {
            servo1.setPosition(pos2);
            if (servo2 != null) {
                servo2.setPosition(pos2);
            }
        }
    }

    /*
     * BUG FIXES:
     *  1. List mode: "index + 1 % size" is "index + (1 % size)" = index + 1 because % runs before +,
     *     so it never wrapped around and crashed (IndexOutOfBounds) at the last position.
     *  2. Two-servo mode: servo1's check called secondaryPos(), which moves BOTH servos; then servo2's
     *     check saw servo2 at pos2 and called primaryPos(), moving both straight back. Net effect:
     *     quickSwitch() did nothing. Now servo1 alone decides, and both servos follow.
     * Also: we remember the current position ourselves instead of comparing doubles from getPosition().
     */
    private boolean atPrimary = true;
    private int listIndex = -1;

    public void quickSwitch() {
        if (isUsingList()) {
            if (listIndex < 0) listIndex = primaryPosIndex;
            listIndex = (listIndex + 1) % positions.size();
            double target = positions.get(listIndex);
            servo1.setPosition(target);
            if (servo2 != null) {
                servo2.setPosition(target);
            }
        } else {
            if (atPrimary) {
                secondaryPos();
            } else {
                primaryPos();
            }
        }
    }

    // use anonymous classes to change, these are default
    protected void setDirections() {
        servo1.setDirection(Servo.Direction.FORWARD);
        if (servo2 != null) {
            servo2.setDirection(Servo.Direction.REVERSE);
        }
    }
}
