package org.firstinspires.ftc.teamcode.Util;

import org.firstinspires.ftc.teamcode.Constants.RobotConstants;

/**
 * Write every autonomous path ONCE for blue, then mirror it for red.
 * Every top team with public code did this instead of keeping two copies of each auto
 * (23641 Flipper, 772 MirrorHelper, 11138 PoseMirror, 14450 AllianceColor).
 *
 * The mirror line below assumes the field is mirrored left/right (across x = 72).
 * CHECK THIS against the BIOBUZZ field drawing and your Pedro coordinate system
 * before trusting it — if the field is rotationally symmetric instead, change mirrorX/Y/heading.
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
}
