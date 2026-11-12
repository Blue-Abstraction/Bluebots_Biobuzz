package org.firstinspires.ftc.teamcode.Subsystems;

import com.qualcomm.robotcore.hardware.CRServo;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Constants.HardwareNames;
import org.firstinspires.ftc.teamcode.Constants.TurretConstants;
import org.firstinspires.ftc.teamcode.Util.CachedCRServo;
public class Turret implements Subsystem {

    public enum State { SPIN_CW, SPIN_CCW, OFF }
    private State state = State.OFF;
    private double power = 0;

    private final CachedCRServo servo;
    public Turret(HardwareMap hw) {
        CRServo raw = hw.tryGet(CRServo.class, HardwareNames.TURRET);
        if (raw != null) {
            servo = new CachedCRServo(raw, .01);
            servo.setReversed(TurretConstants.REVERSED);
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
            case SPIN_CW:
                power = TurretConstants.SPIN_POWER;
                break;
            case SPIN_CCW:
                power = -TurretConstants.SPIN_POWER;
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

    public double getPower() {
        return power;
    }

    @Override
    public void addTelemetry(Telemetry t) {
        if (!isConnected()) {
            t.addData("Turret", "NOT CONNECTED");
            return;
        }
        t.addData("Turret", "%s power %.2f", state, power);
    }

    @Override
    public boolean isConnected() {
        return servo != null;
    }
}
