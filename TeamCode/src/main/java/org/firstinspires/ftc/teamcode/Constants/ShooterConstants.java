package org.firstinspires.ftc.teamcode.Constants;

/**
 * Flywheel + feeder numbers. Placeholder values — tune them with
 * Testing/FlywheelTuner once the mechanism exists.
 *
 * TODO: add @Configurable once Panels is installed (see RobotConstants).
 */
public class ShooterConstants {
    // ---- Flywheel velocity control: power = kS*sign + kV*target + kP*error ----
    // Every DECODE top team with a flywheel used feedforward + a little P,
    // scaled by (nominal voltage / current voltage).
    /** Power needed just to overcome friction. */
    public static double kS = 0.0;
    /** Power per (encoder tick / second) of target speed. Tune this FIRST. */
    public static double kV = 0.0004;
    /** Extra power per tick/s of error. Tune after kV. */
    public static double kP = 0.0005;
    public static boolean REVERSED = false;

    /** "Close enough" window, in ticks per second, before we allow a shot. */
    public static double AT_SPEED_TOLERANCE_TPS = 40;

    /** Preset speeds in ticks per second. Later: replace with a distance -> speed lookup table. */
    public static double DEFAULT_TARGET_TPS = 1400;
    public static double TPS_STEP = 50;

    // ---- Feeder (the two servos the old Template pulsed with a Thread) ----
    public static double FEEDER_REST_RIGHT = 0.0;
    public static double FEEDER_REST_LEFT  = 1.0;
    public static double FEEDER_PUSH_RIGHT = 1.0;
    public static double FEEDER_PUSH_LEFT  = 0.0;
    /** How long the feeder stays pushed, in milliseconds. */
    public static double FEEDER_PUSH_MS = 750;
}
