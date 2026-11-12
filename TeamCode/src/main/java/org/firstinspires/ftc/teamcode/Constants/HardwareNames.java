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
    // Intake: two motors, each spinning a pair of opposite sides
    public static final String INTAKE_FRONT_BACK = "intakeFrontBack";
    public static final String INTAKE_LEFT_RIGHT = "intakeLeftRight";

    public static final String FLYWHEEL = "flywheel";
    public static final String DYE_ROTOR = "dyeRotor";

    public static final String TURRET = "turret";

    public static final String HOOD = "hood";

    public static final String OTOS = "otos";
}
