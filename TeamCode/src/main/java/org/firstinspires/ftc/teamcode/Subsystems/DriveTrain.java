package org.firstinspires.ftc.teamcode.Subsystems;

import static com.pedropathing.api.Paths.line;

import com.pedropathing.controllers.PIDController;
import com.pedropathing.drivetrain.DrivePowers;
import com.pedropathing.follower.Follower;
import com.pedropathing.follower.ManualDrive;
import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Constants.DriveConstants;
import org.firstinspires.ftc.teamcode.Util.Alliance;
import org.firstinspires.ftc.teamcode.pedro.Constants;

/**
 * The drivetrain AFTER Pedro Pathing is tuned (Pedro 3.x).
 *
 * What changed from the first version:
 *   - Pedro's Follower now owns the four drive motors (its Mecanum class does the mixing,
 *     normalization and write-caching we used to do here) AND knows where the robot is.
 *   - This class no longer touches motors. It decides what to ASK the Follower for:
 *     manual driving, field-centric driving, holding position, heading lock, or a path.
 *   - follower.update() runs here, once per loop, so TeleOp and auto both get it from robot.update().
 *
 * The public TeleOp methods (drive, setSlowMode) keep the same meaning as before,
 * so MainTeleOp's driving lines don't change.
 */
public class DriveTrain implements Subsystem {
    private final Follower follower;     // null until pedro/Constants.create() is filled in
    private final String setupError;
    private final Alliance alliance;
    private final PIDController headingPid =
            new PIDController(DriveConstants.HEADING_kP, 0, DriveConstants.HEADING_kD);

    // Driver intent, in OUR convention: +strafe = right, +turn = clockwise (same as the old DriveTrain)
    private double forward, strafe, turn;
    private double speedScale = 1.0;
    private boolean fieldCentric = true;
    private boolean driverInControl = false;  // false in auto, and while a goTo() path runs
    private Double lockedHeading = null;      // radians; null = the driver controls turning

    public DriveTrain(HardwareMap hw, Alliance alliance) {
        this.alliance = alliance;
        Follower f = null;
        String err = null;
        try {
            f = Constants.create(hw);
            if (f == null) err = "pedro/Constants.create() isn't filled in yet";
        } catch (RuntimeException e) {
            // Pedro uses hardwareMap.get(), which throws if a drive motor or the odometry
            // device is missing from the config. Report it instead of crashing the OpMode.
            err = e.getMessage();
        }
        follower = f;
        setupError = err;
    }

    // =============================== TeleOp ===============================

    /**
     * Same as before: +forward = forward, +strafeRight = right, +turnClockwise = clockwise.
     * In field-centric mode, "forward" means "away from the drivers" no matter where the robot faces.
     */
    public void drive(double forward, double strafeRight, double turnClockwise) {
        this.forward = deadband(forward);
        this.strafe = deadband(strafeRight);
        this.turn = deadband(turnClockwise);

        boolean sticksMoving = this.forward != 0 || this.strafe != 0 || this.turn != 0;
        // Sticks always win. With the sticks released, a running goTo() path is allowed to finish.
        if (sticksMoving || !isFollowingPath()) {
            driverInControl = true;
        }
    }

    public void setSlowMode(boolean slow) {
        speedScale = slow ? DriveConstants.SLOW_MODE_SCALE : 1.0;
    }

    public void setFieldCentric(boolean on) {
        fieldCentric = on;
    }

    public boolean isFieldCentric() {
        return fieldCentric;
    }

    /** Keep the robot pointed at this heading (radians) while the driver translates. Call every loop to track a target. */
    public void lockHeading(double headingRadians) {
        lockedHeading = headingRadians;
    }

    public void unlockHeading() {
        lockedHeading = null;
        headingPid.reset();
    }

    /**
     * Driver points the robot straight away from the driver station and presses a button:
     * heading is re-zeroed so field-centric "forward" is correct again. Fixes heading drift mid-match.
     */
    public void resetHeading() {
        if (follower != null) follower.setHeading(alliance.driverForwardHeading());
    }

    /** Drive itself to a spot on the field (a scoring position, say). Moving a stick cancels it. */
    public void goTo(Pose target) {
        if (follower == null) return;
        Pose start = follower.pose();
        follower.follow(line(start, target).linear(start, target));
        driverInControl = false;
    }

    // ======================= Position (TeleOp + auto) =======================

    public void setPose(Pose pose) {
        if (follower != null) follower.setPose(pose);
    }

    public Pose getPose() {
        return follower == null ? Pose.zero() : follower.pose();
    }

    /** Autonomous: run a path. robot.update() keeps it going. */
    public void followPath(Path path) {
        if (follower == null) return;
        follower.follow(path);
        driverInControl = false;
    }

    /** True while a path is running (auto waits on this). */
    public boolean isBusy() {
        return follower != null && follower.isBusy();
    }

    /** For anything Pedro offers that this class doesn't wrap yet. */
    public Follower follower() {
        return follower;
    }

    private boolean isFollowingPath() {
        return follower != null && follower.following();
    }

    // =============================== Loop ===============================

    @Override
    public void periodic() {
        if (follower == null) return;

        if (driverInControl) {
            // Pedro's convention is +strafe = LEFT and +turn = COUNTER-clockwise: the opposite of ours.
            // Converting here, in ONE place, means TeleOp never has to think about it.
            DrivePowers powers = new DrivePowers(
                    forward * speedScale,
                    -strafe * speedScale,
                    -turn * speedScale);

            if (lockedHeading != null) {
                headingPid.kP = DriveConstants.HEADING_kP;   // re-read so live tuning works
                headingPid.kD = DriveConstants.HEADING_kD;
                powers = ManualDrive.headingLock(follower, headingPid, powers, lockedHeading);
            }

            if (fieldCentric) {
                powers = ManualDrive.fieldCentric(powers, follower.pose().heading(),
                        -alliance.driverForwardHeading());
            }

            if (DriveConstants.HOLD_WHEN_IDLE) {
                ManualDrive.driveOrHold(follower, powers);  // sticks released + robot stopped -> hold position
            } else {
                follower.manual(powers);
            }
        }

        // Exactly once per loop: update odometry, then drive in whatever mode the follower is in
        // (manual, hold, or following a path).
        follower.update();
    }

    @Override
    public void stop() {
        forward = strafe = turn = 0;
        lockedHeading = null;
        if (follower != null) {
            follower.stop();
            follower.drivetrain.stop();
        }
    }

    @Override
    public void addTelemetry(Telemetry t) {
        if (follower == null) {
            t.addData("Drive", "NOT CONNECTED: " + setupError);
            return;
        }
        Pose p = follower.pose();
        t.addData("Pose", "(%.1f, %.1f) in, %.0f deg", p.x(), p.y(), Math.toDegrees(p.heading()));
        t.addData("Drive", "%s, %s%s",
                follower.mode(),
                fieldCentric ? "field-centric" : "robot-centric",
                lockedHeading != null ? ", heading locked" : "");
    }

    @Override
    public boolean isConnected() {
        return follower != null;
    }

    private static double deadband(double v) {
        return Math.abs(v) < DriveConstants.STICK_DEADBAND ? 0 : v;
    }
}
