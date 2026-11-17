package org.firstinspires.ftc.teamcode.Util;

import com.pedropathing.math.Pose;

import org.firstinspires.ftc.teamcode.Constants.DriveConstants;
import org.firstinspires.ftc.teamcode.Constants.RobotConstants;

/**
 * Write every position ONCE for one alliance; the other alliance's version is calculated.
 *
 * How the two sides relate depends on the field (RobotConstants.FIELD_SYMMETRY):
 *   ROTATE: the other side is the same spot turned 180 degrees around the field center:
 *           (x, y, heading) -> (144 - x, 144 - y, heading + 180 deg)
 *   MIRROR: the other side is a left/right mirror image across x = 72:
 *           (x, y, heading) -> (144 - x, y, 180 deg - heading)
 *
 * CHECK which one BIOBUZZ is against the official field drawing in the game manual.
 */
public enum Alliance {
    BLUE,
    RED;

    /** How the field's two halves relate. Set in RobotConstants.FIELD_SYMMETRY. */
    public enum Symmetry { ROTATE, MIRROR }

    public boolean isRed() {
        return this == RED;
    }

    public Alliance other() {
        return isRed() ? BLUE : RED;
    }

    /** The same spot on the other alliance's side. Flipping twice gets you back where you started. */
    public static Pose flip(Pose p) {
        double size = RobotConstants.FIELD_WIDTH_IN;
        if (RobotConstants.FIELD_SYMMETRY == Symmetry.ROTATE) {
            return new Pose(size - p.x(), size - p.y(), p.heading() + Math.PI);
        } else {
            return new Pose(size - p.x(), p.y(), Math.PI - p.heading());
        }
    }

    /**
     * A pose written for {@code writtenFor}, converted to THIS alliance.
     * Used by autos, whose plans may be drawn for either side.
     */
    public Pose fromPlan(Alliance writtenFor, Pose planned) {
        return this == writtenFor ? planned : flip(planned);
    }

    /** A BLUE-side pose, converted to this alliance. (DriveConstants positions are written for blue.) */
    public Pose pose(double blueX, double blueY, double blueHeading) {
        return fromPlan(BLUE, new Pose(blueX, blueY, blueHeading));
    }

    public double x(double blueX) {
        return pose(blueX, 0, 0).x();
    }

    public double y(double blueY) {
        return pose(0, blueY, 0).y();
    }

    /** Heading in radians. */
    public double heading(double blueHeading) {
        return pose(0, 0, blueHeading).heading();
    }

    /** Heading (radians) the robot has when pointing straight away from this alliance's drivers. */
    public double driverForwardHeading() {
        return isRed() ? DriveConstants.RED_DRIVER_FORWARD_HEADING : DriveConstants.BLUE_DRIVER_FORWARD_HEADING;
    }
}
