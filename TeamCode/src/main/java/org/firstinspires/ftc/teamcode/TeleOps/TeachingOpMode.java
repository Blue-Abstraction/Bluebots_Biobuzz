package org.firstinspires.ftc.teamcode.TeleOps;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Gamepad;
import com.qualcomm.robotcore.hardware.Servo;

@TeleOp
public class TeachingOpMode extends LinearOpMode {

    DcMotor leftMotor;
    DcMotor intake;
    Servo servo;
    Gamepad gamepad1;
    boolean intakeRunning;

    @Override
    public void runOpMode() throws InterruptedException {

        // Initialize hardware
        leftMotor = hardwareMap.get(DcMotor.class, "leftMotor");
        servo = hardwareMap.get(Servo.class, "servo");
        intake = hardwareMap.get(DcMotor.class, "intake");
        gamepad1 = new Gamepad();
        intakeRunning = false;

        waitForStart();

        while (opModeIsActive()) {
            telemetry.addData("Status", "Running");
            telemetry.addData("Testing",true);

            if(gamepad1.circle) {
                //This sets the motor to full power
                leftMotor.setPower(1);
            }else{
                leftMotor.setPower(0);
            }


            if(gamepad1.right_trigger > 0.3) {
                //This sets the servo to the middle position
                servo.setPosition(.5);
            }else{
                servo.setPosition(0);
            }

            if(gamepad1.left_stick_y > 0){
                //move robot forward
            }else if(gamepad1.left_stick_y < 0){
                //move robot backward
            }else{
                //stop robot
            }

            if(gamepad1.triangleWasPressed()){
                intakeRunning = !intakeRunning;
            }

            if(intakeRunning){
                intake.setPower(1);
            }else{
                intake.setPower(0);
            }


            telemetry.update();
        }
    }
}