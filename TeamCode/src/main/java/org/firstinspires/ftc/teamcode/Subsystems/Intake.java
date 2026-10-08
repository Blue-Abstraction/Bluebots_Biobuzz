package org.firstinspires.ftc.teamcode.Subsystems;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Constants.HardwareNames;
import org.firstinspires.ftc.teamcode.Constants.IntakeConstants;
import org.firstinspires.ftc.teamcode.Util.CachedMotor;

/**
 * Intake as a tiny state machine: OpModes pick a State, periodic() turns it into motor power.
 *
 * Why not just setPower() from the OpMode like before? Because once autonomous,
 * TeleOp and sensors all want to control the intake, having ONE place that decides
 * the power prevents them fighting each other. Every top-team subsystem looked like this
 * (23641's Intake.Mode, 23511's Intake.MotorState, 14450's intake state machine).
 */
public class Intake implements Subsystem {
    public enum State { OFF, IN, OUT }

    private final CachedMotor motor;
    private State state = State.OFF;

    public Intake(HardwareMap hw) {
        DcMotorEx raw = hw.tryGet(DcMotorEx.class, HardwareNames.INTAKE);
        if (raw != null) {
            motor = new CachedMotor(raw, 0.01);
            motor.setReversed(IntakeConstants.REVERSED);
        } else {
            motor = null;
        }
    }

    public void setState(State newState) {
        state = newState;
    }

    public State getState() {
        return state;
    }

    /** Convenience for a toggle button: OFF <-> IN. */
    public void toggleIn() {
        state = (state == State.IN) ? State.OFF : State.IN;
    }

    @Override
    public void periodic() {
        if (motor == null) return;
        switch (state) {
            case IN:
                motor.setPower(IntakeConstants.IN_POWER);
                break;
            case OUT:
                motor.setPower(IntakeConstants.OUT_POWER);
                break;
            case OFF:
            default:
                motor.setPower(0);
                break;
        }
    }

    @Override
    public void stop() {
        state = State.OFF;
        if (motor != null) motor.setPower(0);
    }

    @Override
    public void addTelemetry(Telemetry t) {
        t.addData("Intake", isConnected() ? state : "NOT CONNECTED");
    }

    @Override
    public boolean isConnected() {
        return motor != null;
    }
}
