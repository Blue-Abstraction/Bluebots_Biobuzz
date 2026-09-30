package org.firstinspires.ftc.teamcode.TeleOps;
import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.Servo;

import org.firstinspires.ftc.teamcode.RobotFunctions.Toggle;

@TeleOp
public class Template extends LinearOpMode {
    private DcMotor FLW, FRW, BLW, BRW, flyWheel;
    private Servo servoR, servoL;
    private double power;
    private Toggle flyWheelToggle;

    @Override
    public void runOpMode() throws InterruptedException {
        FLW = hardwareMap.get(DcMotor.class, "FLW");
        FRW = hardwareMap.get(DcMotor.class, "FRW");
        BLW = hardwareMap.get(DcMotor.class, "BLW");
        BRW = hardwareMap.get(DcMotor.class, "BRW");

        flyWheel = hardwareMap.get(DcMotor.class, "FlywheelMotor");
        servoR = hardwareMap.get(Servo.class, "servor");
        servoL = hardwareMap.get(Servo.class, "servol");

        servoR.setDirection(Servo.Direction.FORWARD);
        servoL.setDirection(Servo.Direction.FORWARD);

        FLW.setDirection(DcMotorSimple.Direction.FORWARD);
        FRW.setDirection(DcMotorSimple.Direction.REVERSE);
        BLW.setDirection(DcMotorSimple.Direction.FORWARD);
        BRW.setDirection(DcMotorSimple.Direction.REVERSE);

        flyWheel.setDirection(DcMotorSimple.Direction.FORWARD);

        telemetry.addData("Status", "Initialized");
        telemetry.update();

        flyWheelToggle = new Toggle(() -> gamepad1.bWasPressed());
        power = .5;

        waitForStart();

        while (opModeIsActive()) {
            telemetry.addLine("Running");

            if (gamepad1.left_stick_y != 0) {
                FLW.setPower(gamepad1.left_stick_y);
                FRW.setPower(gamepad1.left_stick_y);
                BLW.setPower(gamepad1.left_stick_y);
                BRW.setPower(gamepad1.left_stick_y);
            } else if (gamepad1.right_stick_x != 0) {
                FLW.setPower(-gamepad1.right_stick_x);
                FRW.setPower(gamepad1.right_stick_x);
                BLW.setPower(-gamepad1.right_stick_x);
                BRW.setPower(gamepad1.right_stick_x);
            } else {
                FLW.setPower(0);
                FRW.setPower(0);
                BLW.setPower(0);
                BRW.setPower(0);
            }

            flyWheelToggle.toggleIfPressed();
            flyWheelToggle.thisNotThat(
                    () -> flyWheel.setPower(power),
                    () -> flyWheel.setPower(0));

            if (gamepad1.aWasPressed()) {
                new Thread(() -> {
                    servoR.setPosition(1);
                    servoL.setPosition(0);
                    sleep(750);
                    servoR.setPosition(0);
                    servoL.setPosition(1);
                }).start();
            }

            if (gamepad1.dpadUpWasPressed()) {
                power += 0.05;
            } else if (gamepad1.dpadDownWasPressed()) {
                power -= 0.05;
            }

            telemetry.addData("Fly Wheel Power", power);
            telemetry.update();
        }
    }
}