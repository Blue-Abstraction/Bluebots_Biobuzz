package org.firstinspires.ftc.teamcode.pedro;

import com.pedropathing.algorithm.Foresight;
import com.pedropathing.algorithm.ForesightConfig;
import com.pedropathing.follower.Follower;
import com.pedropathing.math.Pose;
import com.pedropathing.revhub.drivetrains.Mecanum;
import com.pedropathing.revhub.drivetrains.MecanumConfig;
import com.pedropathing.revhub.localizers.OTOSConfig;
import com.pedropathing.revhub.localizers.OTOSLocalizer;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;
import org.firstinspires.ftc.teamcode.Constants.HardwareNames;

/**
 * Pedro Pathing setup: X-drive (configured as Mecanum — same wheel math) + SparkFun OTOS + Foresight.
 *
 * TUNING DAY, in order (each tuner PRINTS a block — paste it over the matching block below):
 *   1. Mecanum tuner   -> drivetrainConfig
 *   2. OTOS tuner      -> localizerConfig
 *   3. Foresight tuner -> foresightConfig   (needs 1 and 2 pasted first)
 *   4. Run Tests (Driving, Localization, Hold, Line, Curve)
 *   5. Only then set TUNED = true.
 *
 * After pasting, change the quoted names the tuners print (e.g. "FLW") back to HardwareNames.*
 * so every config name still lives in one place.
 *
 * Until TUNED is true, create() returns null, so DriveTrain shows NOT CONNECTED instead of
 * driving with placeholder numbers.
 */
public class Constants {

    /** Flip to true only after all three blocks below are pasted in from the tuners and Tests pass. */
    public static final boolean TUNED = false;

    // ---- 1. Mecanum tuner (works for X-drive: answer "Forward" if the wheel pushes the robot toward its front) ----
    public static MecanumConfig drivetrainConfig = new MecanumConfig(c -> {
        c.frontLeftName.set(HardwareNames.FRONT_LEFT);
        c.frontRightName.set(HardwareNames.FRONT_RIGHT);
        c.backLeftName.set(HardwareNames.BACK_LEFT);
        c.backRightName.set(HardwareNames.BACK_RIGHT);
        c.frontLeftDirection.set(DcMotorSimple.Direction.FORWARD);   // PLACEHOLDER
        c.frontRightDirection.set(DcMotorSimple.Direction.FORWARD);  // PLACEHOLDER
        c.backLeftDirection.set(DcMotorSimple.Direction.FORWARD);    // PLACEHOLDER
        c.backRightDirection.set(DcMotorSimple.Direction.FORWARD);   // PLACEHOLDER
    });

    // ---- 2. OTOS tuner ----
    public static OTOSConfig localizerConfig = new OTOSConfig(c -> {
        c.name.set(HardwareNames.OTOS);
        c.linearScalar.set(1.0);           // PLACEHOLDER
        c.angularScalar.set(1.0);          // PLACEHOLDER
        c.offset.set(new Pose(0, 0));      // PLACEHOLDER: where the OTOS sits relative to the robot's center
        c.linearUnit.set(DistanceUnit.INCH);
    });

    // ---- 3. Foresight tuner ----
    // Empty until tuned. Pedro only complains about missing values when it actually uses them,
    // and nothing uses this before TUNED = true (except the Tests procedure, which you run after pasting).
    public static ForesightConfig foresightConfig = new ForesightConfig(c -> {
        // paste the Foresight tuner's block here
    });

    public static Follower create(HardwareMap hardwareMap) {
        if (!TUNED) return null;
        return new Follower(
                new OTOSLocalizer(hardwareMap, localizerConfig),
                new Mecanum(hardwareMap, drivetrainConfig),
                new Foresight(foresightConfig));
    }
}
