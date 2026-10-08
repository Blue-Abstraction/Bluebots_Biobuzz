package org.firstinspires.ftc.teamcode.Constants;

/**
 * Every name in the Driver Station robot configuration lives HERE and only here.
 * If a wire moves or a device gets renamed, you change one line instead of hunting
 * through every OpMode. (Pattern used by 16750 Technototes, 11138, 31666.)
 *
 * These match the names your existing Template / TeachingDrivingOpMode already used.
 */
public final class HardwareNames {
    private HardwareNames() {}

    // Drivetrain
    public static final String FRONT_LEFT  = "FLW";
    public static final String FRONT_RIGHT = "FRW";
    public static final String BACK_LEFT   = "BLW";
    public static final String BACK_RIGHT  = "BRW";

    // Mechanisms
    public static final String INTAKE   = "intakeMotor";
    public static final String FLYWHEEL = "FlywheelMotor";
    public static final String FEEDER_RIGHT = "servor";
    public static final String FEEDER_LEFT  = "servol";
}
