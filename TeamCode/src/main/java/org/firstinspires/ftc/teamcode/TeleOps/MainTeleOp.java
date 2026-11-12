package org.firstinspires.ftc.teamcode.TeleOps;

import com.pedropathing.math.Pose;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.hardware.CRServo;

import org.firstinspires.ftc.teamcode.Constants.DriveConstants;
import org.firstinspires.ftc.teamcode.Constants.ShooterConstants;
import org.firstinspires.ftc.teamcode.Robot;
import org.firstinspires.ftc.teamcode.Subsystems.DyeRotor;
import org.firstinspires.ftc.teamcode.Subsystems.Hood;
import org.firstinspires.ftc.teamcode.Subsystems.Intake;
import org.firstinspires.ftc.teamcode.Subsystems.Turret;
import org.firstinspires.ftc.teamcode.Util.Alliance;
import org.firstinspires.ftc.teamcode.Util.PoseStorage;
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

            if (PoseStorage.valid) {
                robot.driveTrain.setPose(new Pose(
                        PoseStorage.xInches, PoseStorage.yInches, PoseStorage.headingRadians));
                PoseStorage.clear();
            } else {
                robot.driveTrain.setPose(alliance().pose(
                        DriveConstants.DEFAULT_START_X,
                        DriveConstants.DEFAULT_START_Y,
                        DriveConstants.DEFAULT_START_HEADING));
            }

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

        if (gamepad1.xWasPressed()) {
            robot.dyeRotor.toggle();
        }

        if (gamepad1.aWasPressed()) {
            robot.intake.toggleIn();
        }

        if (gamepad1.dpad_right) {
            robot.turret.setState(Turret.State.SPIN_CW);
        } else if (gamepad1.dpad_left) {
            robot.turret.setState(Turret.State.SPIN_CCW);
        } else {
            robot.turret.setState(Turret.State.OFF);
        }

        if (gamepad1.dpad_up) {
            robot.hood.setState(Hood.State.RAISE);
        } else if (gamepad1.dpad_down) {
            robot.hood.setState(Hood.State.LOWER);
        } else {
            robot.hood.setState(Hood.State.OFF);
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
