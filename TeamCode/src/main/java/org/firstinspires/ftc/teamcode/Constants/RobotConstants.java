package org.firstinspires.ftc.teamcode.Constants;

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

    /** Field is 144 in square; used to mirror blue-side coordinates to red. */
    public static double FIELD_WIDTH_IN = 144.0;

    /** Turn the long telemetry list off at competition if loop times get slow. */
    public static boolean VERBOSE_TELEMETRY = true;
}
