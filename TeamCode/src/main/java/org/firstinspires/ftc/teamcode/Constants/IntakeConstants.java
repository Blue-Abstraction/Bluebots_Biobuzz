package org.firstinspires.ftc.teamcode.Constants;

// TODO: add @Configurable once Panels is installed (see RobotConstants).
public class IntakeConstants {
    /** Motor power for intaking and for spitting out. */
    public static double IN_POWER = 1.0;
    public static double OUT_POWER = -0.8;

    /**
     * Set to true for a pair that spits when told IN. PLACEHOLDERS: run the intake IN
     * and flip whichever pair goes the wrong way. Read every loop, so Panels can flip them live.
     */
    public static boolean REVERSE_FRONT_BACK = false;
    public static boolean REVERSE_LEFT_RIGHT = false;

    /** Only send a new power if it changed by more than this. */
    public static double CACHE_TOLERANCE = 0.01;
}
