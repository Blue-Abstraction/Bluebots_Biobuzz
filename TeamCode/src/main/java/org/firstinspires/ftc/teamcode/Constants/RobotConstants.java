package org.firstinspires.ftc.teamcode.Constants;

import org.firstinspires.ftc.teamcode.Util.Alliance;

/**
 * Robot-wide numbers. Units go in the variable name or a comment — always.
 *
 * TUNING LIVE: every top team made these editable while the robot runs
 * (FTC Dashboard @Config or Panels @Configurable). Once Panels is installed,
 * put @Configurable above each constants class and keep the fields
 * "public static" and NOT final — then you can change them from a laptop
 * without re-deploying.
 */
public class RobotConstants {
    /** Nominal battery voltage that feedforward gains were tuned at. */
    public static double NOMINAL_VOLTAGE = 12.0;

    /** Reading the battery voltage is slow, so refresh it only this often. (23511 does this.) */
    public static double VOLTAGE_REFRESH_SECONDS = 0.2;

    /** Field is 144 in square. */
    public static double FIELD_WIDTH_IN = 144.0;

    /**
     * How the two alliances' halves of the field relate (see Util/Alliance).
     * ROTATE is based on the field diagram from the auto plan (red box top-left, blue box bottom-right).
     * CONFIRM against the official BIOBUZZ field drawing.
     */
    public static Alliance.Symmetry FIELD_SYMMETRY = Alliance.Symmetry.ROTATE;

    /**
     * Robot size, in inches, measured to the outside of the frame/bumpers.
     * LENGTH = front to back, WIDTH = side to side.
     * PLACEHOLDERS (18 in is the starting-size limit): measure the real robot.
     * Auto positions against walls use half of these, so get them right.
     */
    public static double ROBOT_LENGTH_IN = 18.0;
    public static double ROBOT_WIDTH_IN = 18.0;

    /** Turn the long telemetry list off at competition if loop times get slow. */
    public static boolean VERBOSE_TELEMETRY = true;
}
