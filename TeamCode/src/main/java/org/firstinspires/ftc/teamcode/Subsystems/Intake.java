package org.firstinspires.ftc.teamcode.Subsystems;

import com.qualcomm.robotcore.hardware.DcMotor;
import com.qualcomm.robotcore.hardware.HardwareMap;

public class Intake {
    private DcMotor intakeMotor;

    public Intake(HardwareMap h) {
        intakeMotor = h.get(DcMotor.class, "intakeMotor");
        intakeMotor.setDirection(DcMotor.Direction.FORWARD);
    }

    public void spinIn(double power) {
        intakeMotor.setPower(power);
    }
    public void spinOut(double power) {
        intakeMotor.setPower(-power);
    }
    public void stop() {
        intakeMotor.setPower(0);
    }
}