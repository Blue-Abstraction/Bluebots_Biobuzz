package org.firstinspires.ftc.teamcode.Util;

/**
 * Hands the robot's final position from autonomous to TeleOp.
 *
 * Static fields survive between OpModes (the app keeps running), so autonomous
 * writes here in stop() and TeleOp reads it in init(). That way field-centric
 * driving and auto-aim know where the robot is from the first second of TeleOp.
 * (23641 and 23511 used the SDK blackboard / a static END_POSE; 31666 used this exact pattern.)
 *
 * Plain doubles on purpose, so this compiles no matter which Pedro version you use.
 */
public final class PoseStorage {
    private PoseStorage() {}

    public static double xInches = 0;
    public static double yInches = 0;
    public static double headingRadians = 0;
    public static boolean valid = false;
    public static Alliance alliance = Alliance.BLUE;

    public static void save(double x, double y, double heading, Alliance a) {
        xInches = x;
        yInches = y;
        headingRadians = heading;
        alliance = a;
        valid = true;
    }

    /** Call after TeleOp has used it, so a stale pose isn't reused next match. */
    public static void clear() {
        valid = false;
    }
}
