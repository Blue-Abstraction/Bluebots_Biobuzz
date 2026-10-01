package org.firstinspires.ftc.teamcode.TeleOps;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.DcMotorSimple;

@TeleOp
public class TeachingDrivingOpMode extends LinearOpMode {

    private DcMotor FLW, FRW, BLW, BRW;

    @Override
    public void runOpMode() throws InterruptedException {
        FLW = hardwareMap.get(DcMotor.class, "FLW");
        FRW = hardwareMap.get(DcMotor.class, "FRW");
        BLW = hardwareMap.get(DcMotor.class, "BLW");
        BRW = hardwareMap.get(DcMotor.class, "BRW");

        FLW.setDirection(DcMotorSimple.Direction.FORWARD);
        BLW.setDirection(DcMotorSimple.Direction.FORWARD);
        FRW.setDirection(DcMotorSimple.Direction.REVERSE);
        BRW.setDirection(DcMotorSimple.Direction.REVERSE);

        waitForStart();

        while(opModeIsActive()) {
            double y = gamepad1.left_stick_y;
            double x = gamepad1.left_stick_x;

            if (Math.abs(y) >= 0.3) {
                FLW.setPower(y);
                FRW.setPower(y);
                BLW.setPower(y);
                BRW.setPower(y);
            }
            if (Math.abs(x) >= 0.3) {
                FLW.setPower(x);
                FRW.setPower(-x);
                BLW.setPower(-x);
                BRW.setPower(x);
            }

            telemetry.addData("Left stick x position is at", x); // Left stick x position is at: 1.0
            telemetry.addData("Left stick y position is at", y); // Left stick y position is at: 1.0
            telemetry.update();
        }
    }
}
