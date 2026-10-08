package org.firstinspires.ftc.teamcode.Testing;

import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Constants.ShooterConstants;
import org.firstinspires.ftc.teamcode.Robot;
import org.firstinspires.ftc.teamcode.Util.Alliance;

/**
 * One tuner OpMode per mechanism is something every top team had
 * (14450's FlywheelPIDTesting, 31666's FlywheelTuner, 23641's ShooterTuner).
 *
 * How to tune (best with Panels installed so you can graph and edit live):
 *   1. Set kP = 0 and kS = 0.
 *   2. Pick a target. Raise kV until "actual" levels off just under "target".
 *   3. Raise kS if the wheel struggles to start at low targets.
 *   4. Add kP until it reaches target quickly and recovers fast after a shot, without oscillating.
 *   5. Try several targets — one set of gains should work across the whole range.
 *
 * gamepad1: dpad up/down = target +/- step, A = toggle on/off, B = stop.
 */
@TeleOp(name = "Flywheel Tuner", group = "Testing")
public class FlywheelTuner extends OpMode {
    private Robot robot;
    private double target = ShooterConstants.DEFAULT_TARGET_TPS;
    private boolean on = false;

    @Override
    public void init() {
        robot = new Robot(hardwareMap, Alliance.BLUE);
    }

    @Override
    public void loop() {
        if (gamepad1.dpadUpWasPressed()) target += ShooterConstants.TPS_STEP;
        if (gamepad1.dpadDownWasPressed()) target = Math.max(0, target - ShooterConstants.TPS_STEP);
        if (gamepad1.aWasPressed()) on = !on;
        if (gamepad1.bWasPressed()) on = false;

        robot.flywheel.setTarget(on ? target : 0);
        robot.update();

        telemetry.addData("kS / kV / kP", "%.4f / %.6f / %.6f",
                ShooterConstants.kS, ShooterConstants.kV, ShooterConstants.kP);
        telemetry.addData("Error (tps)", "%.0f", robot.flywheel.getTarget() - robot.flywheel.getVelocity());
        robot.flywheel.addTelemetry(telemetry);
        telemetry.addData("Loop", "%.1f ms", robot.getLoopTimeMs());
        telemetry.update();
    }

    @Override
    public void stop() {
        if (robot != null) robot.stop();
    }
}
