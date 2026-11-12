package org.firstinspires.ftc.teamcode.Subsystems;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Constants.HardwareNames;
import org.firstinspires.ftc.teamcode.Constants.HoodConstants;
import org.firstinspires.ftc.teamcode.Util.CachedCRServo;

public class Hood implements Subsystem {

    public enum State { RAISE, LOWER, OFF }
    private State state = State.OFF;
    private final CachedCRServo servo;
    private double power = 0;

    public Hood(HardwareMap hw) {
        CRServo raw = hw.tryGet(CRServo.class, HardwareNames.HOOD);
        if (raw != null) {
            servo = new CachedCRServo(raw, .01);
            servo.setReversed(HoodConstants.REVERSED);
        } else {
            servo = null;
        }
    }

    public void setState(State state) {
        this.state = state;
    }
    public State getState() {
        return state;
    }
    @Override
    public void periodic() {
        if (servo == null) return;

        switch (state) {
            case RAISE:
                power = HoodConstants.POWER;
                break;
            case LOWER:
                power = -HoodConstants.POWER;
                break;
            case OFF:
            default:
                power = 0;
                break;
        }

        servo.setPower(power);
    }

    @Override
    public void stop() {
        power = 0;
        setState(State.OFF);
        if (servo != null) {
            servo.setPower(0);
        }
    }

    @Override
    public void addTelemetry(Telemetry t) {
        if (!isConnected()) {
            t.addData("Hood", "NOT CONNECTED");
            return;
        }
        t.addData("Hood","%s power %.2f", state, power);
    }

    @Override
    public boolean isConnected() {
        return servo != null;
    }
}
