package org.firstinspires.ftc.teamcode.Util;

import com.qualcomm.robotcore.hardware.CRServo;

/**
 * CachedMotor's twin for continuous rotation servos.
 *
 * A CR servo's setPower() is a hardware write just like a motor's, so the same rule applies:
 * only send a new power when it actually changed, and always send a stop.
 */
public class CachedCRServo {
    private final CRServo servo;
    private final double tolerance;
    private double lastPower = Double.NaN;

    public CachedCRServo(CRServo servo, double tolerance) {
        this.servo = servo;
        this.tolerance = tolerance;
    }

    public void setPower(double power) {
        power = Math.max(-1.0, Math.min(1.0, power));
        boolean firstWrite = Double.isNaN(lastPower);
        boolean bigChange = Math.abs(power - lastPower) > tolerance;
        boolean stopping = power == 0.0 && lastPower != 0.0;
        if (firstWrite || bigChange || stopping) {
            servo.setPower(power);
            lastPower = power;
        }
    }

    public CRServo raw() {
        return servo;
    }
}