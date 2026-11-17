package org.firstinspires.ftc.teamcode.Testing;

import com.bylazar.telemetry.PanelsTelemetry;
import com.bylazar.telemetry.TelemetryManager;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Constants.ShooterConstants;
import org.firstinspires.ftc.teamcode.Robot;
import org.firstinspires.ftc.teamcode.Util.Alliance;

/**
 * One tuner OpMode per mechanism is something every top team had
 * (14450's FlywheelPIDTesting, 31666's FlywheelTuner, 23641's ShooterTuner).
 *
 * SETUP: laptop on the Control Hub's Wi-Fi, open http://192.168.43.1:8001
 *   - Configurables tab: edit ShooterConstants.kS / kV / kP while this runs (needs @Configurable).
 *   - Graph tab: plot flywheel_target_tps and flywheel_actual_tps together, and flywheel_error_tps.
 *
 * How to tune:
 *   1. Set kP = 0 and kS = 0.
 *   2. Pick a target. Raise kV until "actual" levels off just under "target".
 *   3. Raise kS if the wheel struggles to start at low targets.
 *   4. Add kP until it reaches target quickly and recovers fast after a shot (error snaps back to 0),
 *      without oscillating (error swinging above and below 0).
 *   5. Try several targets — one set of gains should work across the whole range.
 *   6. Copy the final kS / kV / kP into ShooterConstants.java: live changes reset when the app restarts.
 *
 * gamepad1: dpad up/down = target +/- step, A = toggle on/off, B = stop.
 */
@TeleOp(name = "Flywheel Tuner", group = "Testing")
public class FlywheelTuner extends OpMode {

    private Robot robot;
    private TelemetryManager panels;   // null if Panels isn't available; then we use the Driver Station only
    private double target = ShooterConstants.DEFAULT_TARGET_TPS;
    private boolean on = false;

    @Override
    public void init() {
        robot = new Robot(hardwareMap, Alliance.BLUE);

        // Telemetry must never take the robot down: if Panels fails, carry on without it.
        try {
            panels = PanelsTelemetry.INSTANCE.getTelemetry();
        } catch (RuntimeException e) {
            panels = null;
        }
    }

    @Override
    public void loop() {
        if (gamepad1.dpadUpWasPressed()) target += ShooterConstants.TPS_STEP;
        if (gamepad1.dpadDownWasPressed()) target = Math.max(0, target - ShooterConstants.TPS_STEP);
        if (gamepad1.aWasPressed()) on = !on;
        if (gamepad1.bWasPressed()) on = false;

        robot.flywheel.setTarget(on ? target : 0);
        robot.update();

        double targetTps = robot.flywheel.getTarget();
        double actualTps = robot.flywheel.getVelocity();

        // Driver Station lines
        telemetry.addData("kS / kV / kP", "%.4f / %.6f / %.6f",
                ShooterConstants.kS, ShooterConstants.kV, ShooterConstants.kP);
        telemetry.addData("Error (tps)", "%.0f", targetTps - actualTps);
        robot.flywheel.addTelemetry(telemetry);
        telemetry.addData("Loop", "%.1f ms", robot.getLoopTimeMs());

        publish(targetTps, actualTps);
    }

    /** Graph values to Panels, then show everything; exactly one telemetry update per loop either way. */
    private void publish(double targetTps, double actualTps) {
        if (panels != null) {
            try {
                panels.addData("flywheel_target_tps", targetTps);
                panels.addData("flywheel_actual_tps", actualTps);
                panels.addData("flywheel_error_tps", targetTps - actualTps);
                panels.update(telemetry);   // sends to Panels AND the Driver Station
                return;
            } catch (RuntimeException e) {
                panels = null;              // Panels broke mid-run: stop using it
            }
        }
        telemetry.update();                 // Driver Station only
    }

    @Override
    public void stop() {
        if (robot != null) robot.stop();
    }
}