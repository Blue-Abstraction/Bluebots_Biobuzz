package org.firstinspires.ftc.teamcode.Constants;

/**
 * Autonomous positions and timings. ALL POSITIONS ARE BLUE SIDE — Alliance mirrors them for red.
 * Inches on Pedro's 144 x 144 field; headings in DEGREES here (converted in code) because they're easier to read.
 *
 * PLACEHOLDERS: replace with the numbers from your paper plan of the BIOBUZZ field,
 * then adjust on the real field.
 *
 * TODO: add @Configurable once Panels is installed, to nudge positions live during practice.
 */
public class AutoConstants {
    // Where the robot is placed before the match
    public static double START_X = 56;
    public static double START_Y = 8;
    public static double START_HEADING_DEG = 90;

    // Where it shoots from
    public static double SHOOT_X = 56;
    public static double SHOOT_Y = 80;
    public static double SHOOT_HEADING_DEG = 135;

    // Where it ends up
    public static double PARK_X = 40;
    public static double PARK_Y = 30;
    public static double PARK_HEADING_DEG = 90;

    /** Flywheel speed for the auto shot, in ticks per second. */
    public static double SHOOT_TPS = 1400;

    /** How long to keep feeding once firing starts. */
    public static double FEED_SECONDS = 1.5;

    /**
     * Safety limits: if a step takes longer than this, move on anyway.
     * Without these, a flywheel that never reaches speed (or a path that never finishes)
     * would freeze the whole auto in one step.
     */
    public static double SPIN_UP_TIMEOUT_SECONDS = 2.0;
    public static double PATH_TIMEOUT_SECONDS = 5.0;
}
