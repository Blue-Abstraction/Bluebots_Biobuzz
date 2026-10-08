package org.firstinspires.ftc.teamcode.Util;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

/**
 * A motor that skips pointless hardware writes.
 *
 * Every setPower() call is a message over the hub's bus and costs loop time.
 * If the new power is basically the same as the last one, we don't send it.
 * Zero is always sent so the robot can always stop. (Used by 23641, 772, 23511, 11138.)
 */
public class CachedMotor {
    private final DcMotorEx motor;
    private final double tolerance;
    private double lastPower = Double.NaN;

    public CachedMotor(DcMotorEx motor, double tolerance) {
        this.motor = motor;
        this.tolerance = tolerance;
    }

    public void setPower(double power) {
        power = Math.max(-1.0, Math.min(1.0, power));
        boolean firstWrite = Double.isNaN(lastPower);
        boolean bigChange = Math.abs(power - lastPower) > tolerance;
        boolean stopping = power == 0.0 && lastPower != 0.0;
        if (firstWrite || bigChange || stopping) {
            motor.setPower(power);
            lastPower = power;
        }
    }

    public void setReversed(boolean reversed) {
        motor.setDirection(reversed ? DcMotorSimple.Direction.REVERSE : DcMotorSimple.Direction.FORWARD);
    }

    public void setBrake(boolean brake) {
        motor.setZeroPowerBehavior(brake ? DcMotor.ZeroPowerBehavior.BRAKE : DcMotor.ZeroPowerBehavior.FLOAT);
    }

    /** Encoder speed in ticks per second (comes from the bulk-read cache, so it's cheap). */
    public double getVelocity() {
        return motor.getVelocity();
    }

    public DcMotorEx raw() {
        return motor;
    }
}
