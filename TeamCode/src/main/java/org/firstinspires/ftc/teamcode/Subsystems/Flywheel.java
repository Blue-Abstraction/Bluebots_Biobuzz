package org.firstinspires.ftc.teamcode.Subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Constants.HardwareNames;
import org.firstinspires.ftc.teamcode.Constants.RobotConstants;
import org.firstinspires.ftc.teamcode.Constants.ShooterConstants;
import org.firstinspires.ftc.teamcode.Util.CachedMotor;

import java.util.function.DoubleSupplier;

/**
 * Flywheel with real speed control instead of a fixed power.
 *
 * The old Template ran the flywheel at setPower(0.5). That gives a different speed
 * every time the battery drains, so shots land differently at minute 1 vs minute 2.
 * Every DECODE top team with a shooter did this instead:
 *
 *   power = (kS * sign + kV * target + kP * error) * (12 V / battery V)
 *
 * kV does most of the work (feedforward), kP cleans up the rest, and the voltage
 * term keeps it consistent as the battery sags.
 */
public class Flywheel implements Subsystem {
    private final CachedMotor motor;
    private final DoubleSupplier batteryVoltage;

    private double targetTps = 0;   // ticks per second
    private double currentTps = 0;
    private double lastPower = 0;

    public Flywheel(HardwareMap hw, DoubleSupplier batteryVoltage) {
        this.batteryVoltage = batteryVoltage;
        DcMotorEx raw = hw.tryGet(DcMotorEx.class, HardwareNames.FLYWHEEL);
        if (raw != null) {
            // We do our own control math, so tell the hub not to run its built-in velocity PID.
            raw.setMode(DcMotor.RunMode.RUN_WITHOUT_ENCODER);
            motor = new CachedMotor(raw, 0.005);
            motor.setReversed(ShooterConstants.REVERSED);
            motor.setBrake(false); // let it coast down
        } else {
            motor = null;
        }
    }

    /** @param ticksPerSecond target wheel speed; 0 = off. */
    public void setTarget(double ticksPerSecond) {
        targetTps = Math.max(0, ticksPerSecond);
    }

    public double getTarget() {
        return targetTps;
    }

    public double getVelocity() {
        return currentTps;
    }

    /** True when spun up close enough to shoot. Gate the feeder on this. */
    public boolean atSpeed() {
        return targetTps > 0 && Math.abs(targetTps - currentTps) < ShooterConstants.AT_SPEED_TOLERANCE_TPS;
    }

    @Override
    public void periodic() {
        if (motor == null) return;
        currentTps = motor.getVelocity();

        if (targetTps <= 0) {
            lastPower = 0;
            motor.setPower(0);
            return;
        }

        double error = targetTps - currentTps;
        double power = ShooterConstants.kS
                + ShooterConstants.kV * targetTps
                + ShooterConstants.kP * error;

        double volts = batteryVoltage.getAsDouble();
        if (volts > 1.0) {
            power *= RobotConstants.NOMINAL_VOLTAGE / volts;
        }

        // A flywheel should never be driven backwards to slow down — just let it coast.
        lastPower = Math.max(0, Math.min(1, power));
        motor.setPower(lastPower);
    }

    @Override
    public void stop() {
        targetTps = 0;
        lastPower = 0;
        if (motor != null) motor.setPower(0);
    }

    @Override
    public void addTelemetry(Telemetry t) {
        if (!isConnected()) {
            t.addData("Flywheel", "NOT CONNECTED");
            return;
        }
        t.addData("Flywheel target (tps)", "%.0f", targetTps);
        t.addData("Flywheel actual (tps)", "%.0f", currentTps);
        t.addData("Flywheel power", "%.2f", lastPower);
        t.addData("Flywheel ready", atSpeed());
    }

    @Override
    public boolean isConnected() {
        return motor != null;
    }
}
