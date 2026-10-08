package org.firstinspires.ftc.teamcode.Constants;

// TODO: add @Configurable once Panels is installed (see RobotConstants).
public class DriveConstants {
    /** Stick values smaller than this are treated as zero (stops drift from worn sticks). */
    public static double STICK_DEADBAND = 0.05;

    /** Multiplier applied when the driver holds "slow mode". */
    public static double SLOW_MODE_SCALE = 0.35;

    /** Only send a new power to a motor if it changed by more than this. */
    public static double MOTOR_CACHE_TOLERANCE = 0.01;

    /** Set to true for any motor that spins the wrong way. */
    public static boolean REVERSE_FRONT_LEFT  = false;
    public static boolean REVERSE_FRONT_RIGHT = true;
    public static boolean REVERSE_BACK_LEFT   = false;
    public static boolean REVERSE_BACK_RIGHT  = true;
}
