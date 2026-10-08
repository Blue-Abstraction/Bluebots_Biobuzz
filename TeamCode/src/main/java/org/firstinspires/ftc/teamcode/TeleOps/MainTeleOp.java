package org.firstinspires.ftc.teamcode.TeleOps;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;

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

        if (gamepad2.right_trigger > 0.3) {
            robot.intake.setState(Intake.State.IN);
        } else if (gamepad2.left_trigger > 0.3) {
            robot.intake.setState(Intake.State.OUT);
        } else {
            robot.intake.setState(Intake.State.OFF);
        }

        // xWasPressed() is true for exactly ONE loop per press — no Toggle class or delay timer needed.
        if (gamepad2.bWasPressed()) {
            flywheelOn = !flywheelOn;
        }
        if (gamepad2.dpadUpWasPressed()) {
            flywheelTargetTps += ShooterConstants.TPS_STEP;
        }
        if (gamepad2.dpadDownWasPressed()) {
            flywheelTargetTps = Math.max(0, flywheelTargetTps - ShooterConstants.TPS_STEP);
        }
        robot.flywheel.setTarget(flywheelOn ? flywheelTargetTps : 0);

        if (gamepad2.aWasPressed()) {
            boolean override = gamepad2.right_bumper;
            if (robot.flywheel.atSpeed() || override || !robot.flywheel.isConnected()) {
                robot.feeder.push();
            } else {
                gamepad2.rumble(200); // tell the operator "not ready yet"
            }
        }

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
