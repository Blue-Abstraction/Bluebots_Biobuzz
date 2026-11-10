package org.firstinspires.ftc.teamcode.Subsystems;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Constants.DyeRotorConstants;
import org.firstinspires.ftc.teamcode.Constants.HardwareNames;
import org.firstinspires.ftc.teamcode.Constants.RobotConstants;
import org.firstinspires.ftc.teamcode.Util.CachedMotor;

import java.util.function.DoubleSupplier;

public class DyeRotor implements Subsystem {

    public enum State { SPIN, OFF }
    private State state = State.OFF;
    private final CachedMotor motor;
    private final DoubleSupplier batteryVoltage;
    private double velocity = 0;
    private double power = 0;

    public DyeRotor(HardwareMap hw, DoubleSupplier batteryVoltage) {
        this.batteryVoltage = batteryVoltage;
        DcMotorEx raw = hw.tryGet(DcMotorEx.class, HardwareNames.DYE_ROTOR);
        if (raw != null) {
            motor = new CachedMotor(raw, .01);
            motor.setBrake(true);
            motor.setReversed(DyeRotorConstants.REVERSED);
        } else {
            motor = null;
        }
    }

    public State getState() {
        return this.state;
    }
    public void setState(State state) {
        this.state = state;
    }
    public void toggle() {
        state = (state == State.OFF) ? State.SPIN : State.OFF;
    }

    @Override
    public void periodic() {
        if (motor == null) return;

        velocity = motor.getVelocity();

        switch (state) {
            case SPIN:
                power = DyeRotorConstants.SPIN_POWER;
                break;
            case OFF:
            default:
                power = 0;
                break;
        }
        double volts = batteryVoltage.getAsDouble();
        if (power != 0 && volts > 1.0) {
            power *= RobotConstants.NOMINAL_VOLTAGE / volts;
        }

        motor.setPower(power);
    }

    @Override
    public void stop() {
        setState(State.OFF);
        power = 0;
        if (motor != null) motor.setPower(0);
    }

    @Override
    public void addTelemetry(Telemetry t) {
        if (!isConnected()) {
            t.addData("DyeRotor", "NOT CONNECTED");
            return;
        }
        t.addData("DyeRotor", "%s  power %.2f  (%.0f tps)", state, power, velocity);
    }

    @Override
    public boolean isConnected() {
        return motor != null;
    }
}
