package org.firstinspires.ftc.teamcode.Subsystems;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Constants.DriveConstants;
import org.firstinspires.ftc.teamcode.Constants.HardwareNames;
import org.firstinspires.ftc.teamcode.Util.CachedMotor;

/**
 * Robot-centric mecanum drive for TeleOp.
 *
 * Once Pedro is tuned (pedro/Constants.create), switch TeleOp to the Pedro follower's
 * teleop drive so you get field-centric driving and the same localization as auto —
 * that's what 23641, 772, 14450, 11138 and 31666 all ended up doing.
 */
public class DriveTrain implements Subsystem {
    private final CachedMotor fl, fr, bl, br;
    private final boolean connected;

    private double forward, strafe, turn;
    private double speedScale = 1.0;

    public DriveTrain(HardwareMap hw) {
        DcMotorEx flRaw = hw.tryGet(DcMotorEx.class, HardwareNames.FRONT_LEFT);
        DcMotorEx frRaw = hw.tryGet(DcMotorEx.class, HardwareNames.FRONT_RIGHT);
        DcMotorEx blRaw = hw.tryGet(DcMotorEx.class, HardwareNames.BACK_LEFT);
        DcMotorEx brRaw = hw.tryGet(DcMotorEx.class, HardwareNames.BACK_RIGHT);
        connected = flRaw != null && frRaw != null && blRaw != null && brRaw != null;

        if (connected) {
            double tol = DriveConstants.MOTOR_CACHE_TOLERANCE;
            fl = new CachedMotor(flRaw, tol);
            fr = new CachedMotor(frRaw, tol);
            bl = new CachedMotor(blRaw, tol);
            br = new CachedMotor(brRaw, tol);
            fl.setReversed(DriveConstants.REVERSE_FRONT_LEFT);
            fr.setReversed(DriveConstants.REVERSE_FRONT_RIGHT);
            bl.setReversed(DriveConstants.REVERSE_BACK_LEFT);
            br.setReversed(DriveConstants.REVERSE_BACK_RIGHT);
            fl.setBrake(true);
            fr.setBrake(true);
            bl.setBrake(true);
            br.setBrake(true);
        } else {
            fl = fr = bl = br = null;
        }
    }

    /**
     * Store what the driver wants. Nothing moves until periodic().
     * @param forward +1 = full forward  (pass -gamepad.left_stick_y; pushing the stick UP reads NEGATIVE)
     * @param strafe  +1 = full right
     * @param turn    +1 = full clockwise
     */
    public void drive(double forward, double strafe, double turn) {
        this.forward = deadband(forward);
        this.strafe = deadband(strafe);
        this.turn = deadband(turn);
    }

    public void setSlowMode(boolean slow) {
        speedScale = slow ? DriveConstants.SLOW_MODE_SCALE : 1.0;
    }

    @Override
    public void periodic() {
        if (!connected) return;

        // Standard mecanum mixing — all three axes at once, so the robot can drive diagonally and turn while moving.
        double flP = forward + strafe + turn;
        double frP = forward - strafe - turn;
        double blP = forward - strafe + turn;
        double brP = forward + strafe - turn;

        // If any wheel would exceed 1.0, scale ALL of them down equally so the robot keeps its direction.
        double max = Math.max(1.0, Math.max(Math.max(Math.abs(flP), Math.abs(frP)), Math.max(Math.abs(blP), Math.abs(brP))));

        fl.setPower(flP / max * speedScale);
        fr.setPower(frP / max * speedScale);
        bl.setPower(blP / max * speedScale);
        br.setPower(brP / max * speedScale);
    }

    @Override
    public void stop() {
        forward = strafe = turn = 0;
        if (!connected) return;
        fl.setPower(0);
        fr.setPower(0);
        bl.setPower(0);
        br.setPower(0);
    }

    @Override
    public void addTelemetry(Telemetry t) {
        t.addData("Drive", connected
                ? String.format("fwd %.2f  str %.2f  turn %.2f  x%.2f", forward, strafe, turn, speedScale)
                : "NOT CONNECTED");
    }

    @Override
    public boolean isConnected() {
        return connected;
    }

    private static double deadband(double v) {
        return Math.abs(v) < DriveConstants.STICK_DEADBAND ? 0 : v;
    }
}
