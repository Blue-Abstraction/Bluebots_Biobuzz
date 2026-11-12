package org.firstinspires.ftc.teamcode.Util;

import com.pedropathing.math.Pose;

import org.firstinspires.ftc.teamcode.Constants.DriveConstants;
import org.firstinspires.ftc.teamcode.Constants.RobotConstants;

/**
 * Write every position ONCE for blue, then mirror it for red.
 *
 * The mirror below assumes the field is mirrored left/right (across x = 72).
 * CHECK THIS against the BIOBUZZ field drawing and Pedro's coordinate system
 * before trusting it — if the field is rotationally symmetric instead, change x/y/heading.
 */
public enum Alliance {
    BLUE,
    RED;

    public boolean isRed() {
        return this == RED;
    }

    public double x(double blueX) {
        return isRed() ? RobotConstants.FIELD_WIDTH_IN - blueX : blueX;
    }

    public double y(double blueY) {
        return blueY;
    }

    /** Heading in radians. */
    public double heading(double blueHeading) {
        return isRed() ? Math.PI - blueHeading : blueHeading;
    }

    /** A blue-side pose, mirrored if this is red. */
    public Pose pose(double blueX, double blueY, double blueHeading) {
        return new Pose(x(blueX), y(blueY), heading(blueHeading));
    }

    /** Heading (radians) the robot has when pointing straight away from this alliance's drivers. */
    public double driverForwardHeading() {
        return isRed() ? DriveConstants.RED_DRIVER_FORWARD_HEADING : DriveConstants.BLUE_DRIVER_FORWARD_HEADING;
    }
}
