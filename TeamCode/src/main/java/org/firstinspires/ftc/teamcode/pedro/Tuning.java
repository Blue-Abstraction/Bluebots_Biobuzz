package org.firstinspires.ftc.teamcode.pedro;

import com.pedropathing.algorithm.Foresight;
import com.pedropathing.revhub.drivetrains.Mecanum;
import com.pedropathing.revhub.localizers.OTOSLocalizer;
import com.pedropathing.tuning.autotune.Procedure;
import com.pedropathing.tuning.autotune.Tuner;

import org.firstinspires.ftc.teamcode.pedro.procedures.ForesightTuner;
import org.firstinspires.ftc.teamcode.pedro.procedures.MecanumTuner;
import org.firstinspires.ftc.teamcode.pedro.procedures.OTOSTuner;
import org.firstinspires.ftc.teamcode.pedro.procedures.Tests;

/**
 * The tuners for THIS robot (X-drive as Mecanum + OTOS), in the order to run them.
 * Paste each tuner's printed block into pedro/Constants.java before running the next one.
 */
public class Tuning {

    // 1. Motor directions. https://pedropathing.com/docs/pathing/tuning/drivetrain/mecanum
    @Tuner
    public static Procedure mecanumTuner() {
        return new MecanumTuner();
    }

    // 2. OTOS scalars and mounting offset. Keep the robot still when it starts: the OTOS calibrates its IMU.
    @Tuner
    public static Procedure otosTuner() {
        return new OTOSTuner();
    }

    // 3. Path-following gains. Needs steps 1 and 2 pasted into Constants first.
    @Tuner
    public static Procedure foresightTuner() {
        return new ForesightTuner(
                hardwareMap -> new OTOSLocalizer(hardwareMap, Constants.localizerConfig),
                hardwareMap -> new Mecanum(hardwareMap, Constants.drivetrainConfig));
    }

    // 4. Driving / Localization / Hold / Line / Curve tests. Needs step 3 pasted first.
    @Tuner
    public static Procedure tests() {
        return new Tests(
                hardwareMap -> new Mecanum(hardwareMap, Constants.drivetrainConfig),
                hardwareMap -> new OTOSLocalizer(hardwareMap, Constants.localizerConfig),
                () -> new Foresight(Constants.foresightConfig));
    }
}
