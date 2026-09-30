package org.firstinspires.ftc.teamcode.NetworkTesting;

import com.qualcomm.robotcore.eventloop.opmode.Disabled;
import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.Servo;

@Disabled
public class ExampleVariableControlClass extends VariableControlServer {

    private DcMotor motor;
    private Servo servo;

    @Override
    protected void writeOpModeInHere() { // you DON'T need the loop! this method is inserted into the loop in the parent class
        run(() -> motor.setPower((double) components.get("motorPower")), "motorPower");
        run(() -> servo.setPosition((double) components.get("servoPosition")), "servoPosition");
    }

    @Override
    public void runOpMode() {
        motor = hardwareMap.get(DcMotor.class, "motor");
        servo = hardwareMap.get(Servo.class, "servo");
        waitForStart();
        super.runOpMode();
    }
}