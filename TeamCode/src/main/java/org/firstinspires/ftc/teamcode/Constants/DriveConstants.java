package org.firstinspires.ftc.teamcode.Constants;

/**
 * Drive settings once Pedro is tuned.
 *
 * GONE from the old version: motor directions and the write-cache tolerance.
 * Those now live in Pedro's MecanumConfig in pedro/Constants.java (the Mecanum tuner prints it for you).
 *
 * TODO: add @Configurable once Panels is installed (see RobotConstants).
 */
public class DriveConstants {
    /** Stick values smaller than this are treated as zero. */
    public static double STICK_DEADBAND = 0.05;

    /** Multiplier applied when the driver holds "slow mode". */
    public static double SLOW_MODE_SCALE = 0.35;

    /**
     * Field-centric: the heading (radians, Pedro field coordinates) the robot has when it points
     * straight AWAY from your drivers. PLACEHOLDERS — measure on the BIOBUZZ field: point the robot
     * away from the driver station and read "Pose" heading on telemetry. If the two alliances' drivers
     * stand on opposite walls, red will be blue + 180 degrees.
     */
    public static double BLUE_DRIVER_FORWARD_HEADING = Math.toRadians(90);
    public static double RED_DRIVER_FORWARD_HEADING  = Math.toRadians(90);

    /** Heading lock: turn power per radian of error, and damping on turn speed. Start here, tune live. */
    public static double HEADING_kP = 1.0;
    public static double HEADING_kD = 0.05;

    /** When the sticks are released and the robot stops, actively hold position (resists being pushed). */
    public static boolean HOLD_WHEN_IDLE = true;

    /** Where TeleOp assumes the robot starts if no auto ran (practice). Blue side; red is mirrored. PLACEHOLDER. */
    public static double DEFAULT_START_X = 72;
    public static double DEFAULT_START_Y = 72;
    public static double DEFAULT_START_HEADING = Math.toRadians(90);

    /** What heading lock aims at (e.g. a goal). Blue side; red is mirrored. PLACEHOLDER until you know the BIOBUZZ field. */
    public static double BLUE_TARGET_X = 12;
    public static double BLUE_TARGET_Y = 132;
}
