package org.firstinspires.ftc.teamcode.Constants;

import com.bylazar.configurables.annotations.Configurable;

import org.firstinspires.ftc.teamcode.Util.Alliance;

/**
 * The autonomous plan: positions and timings.
 *
 * All positions are written for PLAN_ALLIANCE, exactly as planned on paper.
 * The other alliance's positions are calculated (see Util/Alliance and RobotConstants.FIELD_SYMMETRY).
 *
 * Units: inches on Pedro's 144 x 144 field; headings in DEGREES (converted in code).
 * L and W are the robot's length and width (RobotConstants), so "L / 2" means
 * "the robot's center when its back (or front) touches the wall".
 *
 */
@Configurable
public class AutoConstants {

    /** Which alliance the paper plan was drawn for. The park spot is in the red box, so: RED. */
    public static Alliance PLAN_ALLIANCE = Alliance.RED;

    private static final double L = RobotConstants.ROBOT_LENGTH_IN;
    private static final double W = RobotConstants.ROBOT_WIDTH_IN;

    // ---------------------------------------------------------------- positions
    // Headings: your positions keep the robot's length along y everywhere, so 90 (facing +y)
    // is the starting guess. PLACEHOLDER: set each one to where the robot's FRONT really faces.
    // While the turret can't aim by itself, the robot's heading at each SHOOT spot must point
    // the shooter at the goal.

    /** Steps 1-2: start here, and shoot from here. */
    public static double START_X = 60;
    public static double START_Y = L / 2;
    public static double START_HEADING_DEG = 90;

    /** Step 3: first collect spot (corner). */
    public static double COLLECT_1_X = W / 2;
    public static double COLLECT_1_Y = L / 2;
    public static double COLLECT_1_HEADING_DEG = 90;

    /** Steps 5, 9: shooting spot #2 (used twice). */
    public static double SHOOT_2_X = 24 + W / 2;
    public static double SHOOT_2_Y = 144 - L / 2;
    public static double SHOOT_2_HEADING_DEG = 90;

    /** Step 7: second collect spot, reached along a curve. */
    public static double COLLECT_2_X = 48;
    public static double COLLECT_2_Y = 144 - L / 2;
    public static double COLLECT_2_HEADING_DEG = 90;
    /** The curve's reference (control) point: it pulls the curve toward it, but the robot doesn't pass through it. */
    public static double COLLECT_2_CONTROL_X = 36;
    public static double COLLECT_2_CONTROL_Y = 120;

    /** Step 11: park. */
    public static double PARK_X = W / 2;
    public static double PARK_Y = 96 + L / 2;
    public static double PARK_HEADING_DEG = 90;

    // ------------------------------------------------------------------ timings

    /** Flywheel speeds, in ticks per second. The two spots are different distances from the goal. */
    public static double SHOOT_1_TPS = 1400;   // from the start spot
    public static double SHOOT_2_TPS = 1400;   // from shooting spot #2

    /** How long to keep feeding once firing starts (long enough to empty the robot). */
    public static double FEED_SECONDS = 1.5;

    /** Steps 4 and 8: wait at each collect spot with the intake running. */
    public static double COLLECT_WAIT_SECONDS = 2.0;

    /**
     * Safety limits: if a step takes longer than this, move on anyway.
     * Without these, a flywheel that never reaches speed (or a path that never finishes)
     * would freeze the whole auto in one step.
     */
    public static double SPIN_UP_TIMEOUT_SECONDS = 2.0;
    public static double PATH_TIMEOUT_SECONDS = 5.0;
}
