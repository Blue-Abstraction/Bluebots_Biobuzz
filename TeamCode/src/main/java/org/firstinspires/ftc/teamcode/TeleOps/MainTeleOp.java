package org.firstinspires.ftc.teamcode.TeleOps;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.CRServo;

import org.firstinspires.ftc.teamcode.Constants.ShooterConstants;
import org.firstinspires.ftc.teamcode.Robot;
import org.firstinspires.ftc.teamcode.Subsystems.Intake;
import org.firstinspires.ftc.teamcode.Util.Alliance;
import org.firstinspires.ftc.teamcode.Util.PoseStorage;

/**
 * The competition TeleOp, written the way the top teams write theirs:
 *
 *   - It's an iterative OpMode (init / loop / stop), not a LinearOpMode with while(),
 *     so there's no way to accidentally block the loop with sleep().
 *   - The OpMode only maps buttons to INTENTIONS. All motor math lives in the subsystems.
 *   - One robot.update() per loop does the bulk read + all control + all writes.
 *   - Blue and Red are tiny subclasses (BlueTeleOp / RedTeleOp) that only pass the alliance.
 *
 * Controls (change freely):
 *   gamepad1  left stick = drive/strafe, right stick X = turn, left bumper (hold) = slow mode
 *   gamepad2  right trigger = intake in, left trigger = intake out
 *             B = flywheel on/off, dpad up/down = flywheel speed, A = feed one (only when at speed)
 *             right bumper + A = feed even if not at speed (override)
 */
public abstract class MainTeleOp extends OpMode {
    protected Robot robot;
    private boolean flywheelOn = false;
    private double flywheelTargetTps = ShooterConstants.DEFAULT_TARGET_TPS;

    /** Subclasses say which alliance they are. */
    protected abstract Alliance alliance();

    @Override
    public void init() {
        robot = new Robot(hardwareMap, alliance());

        if (PoseStorage.valid) {
            telemetry.addData("Start pose from auto", "(%.1f, %.1f, %.0f°)",
                    PoseStorage.xInches, PoseStorage.yInches, Math.toDegrees(PoseStorage.headingRadians));
            // TODO: once Pedro is set up: follower.setStartingPose(new Pose(x, y, heading));
            PoseStorage.clear();
        }
        telemetry.addLine("Initialized. Anything marked NOT CONNECTED is missing from the config.");
        robot.addTelemetry(telemetry);
        telemetry.update();
    }

    @Override
    public void loop() {
        // ---------- 1. Driver inputs -> intentions ----------
        robot.driveTrain.drive(
                -gamepad1.left_stick_y,   // stick UP reads negative, so flip it
                gamepad1.left_stick_x,
                gamepad1.right_stick_x);
        robot.driveTrain.setSlowMode(gamepad1.left_bumper);

        // ---------- 2. One update: read sensors, run control, write hardware ----------
        robot.update();

        // ---------- 3. Telemetry ----------
        telemetry.addData("Flywheel preset (tps)", "%.0f  %s", flywheelTargetTps, flywheelOn ? "ON" : "off");
        robot.addTelemetry(telemetry);
        telemetry.update();
    }

    @Override
    public void stop() {
        if (robot != null) robot.stop();
    }
}
