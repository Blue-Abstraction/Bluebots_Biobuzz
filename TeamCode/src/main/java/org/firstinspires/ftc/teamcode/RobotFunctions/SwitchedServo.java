package org.firstinspires.ftc.teamcode.RobotFunctions;

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

    public void quickSwitch() {
        if (isUsingList()) {
            servo1.setPosition(positions.get(positions.indexOf(servo1.getPosition()) + 1 % positions.size()));
            if (servo2 != null) {
                servo2.setPosition(positions.get(positions.indexOf(servo2.getPosition()) + 1 % positions.size()));
            }
        } else {
            if (servo1.getPosition() == pos1) {
                secondaryPos();
            } else {
                primaryPos();
            }

            if (servo2 != null) {
                if (servo2.getPosition() == pos1) {
                    secondaryPos();
                } else {
                    primaryPos();
                }
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
