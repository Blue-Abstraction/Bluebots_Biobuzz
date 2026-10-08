package org.firstinspires.ftc.teamcode;

import com.qualcomm.hardware.lynx.LynxModule;
import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.VoltageSensor;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Constants.RobotConstants;
import org.firstinspires.ftc.teamcode.Subsystems.DriveTrain;
import org.firstinspires.ftc.teamcode.Subsystems.Feeder;
import org.firstinspires.ftc.teamcode.Subsystems.Flywheel;
import org.firstinspires.ftc.teamcode.Subsystems.Intake;
import org.firstinspires.ftc.teamcode.Subsystems.Subsystem;
import org.firstinspires.ftc.teamcode.Util.Alliance;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * The whole robot in one object. Every OpMode — TeleOp, auto, tuners — builds one of these
 * and calls update() once per loop. All 8 top-team repos we studied had this class
 * (23641 CurrentRobot, 772 RootSubsystem, 16750 Robot, 23511 Robot, 11138 Robot, ...).
 *
 * update() does the same three things every loop, in this order:
 *   1. READ    - clear the bulk-read cache so every sensor/encoder read this loop is fresh
 *                but only costs ONE trip to each hub (huge loop-time win)
 *   2. THINK   - each subsystem runs its control math
 *   3. WRITE   - each subsystem sends its outputs (skipping writes that didn't change)
 *
 * Adding a mechanism = write a class that implements Subsystem, then one register() line below.
 */
public class Robot {
    public final Alliance alliance;

    public final DriveTrain driveTrain;
    public final Intake intake;
    public final Flywheel flywheel;
    public final Feeder feeder;

    private final List<Subsystem> subsystems = new ArrayList<>();
    private final List<LynxModule> hubs;
    private final VoltageSensor voltageSensor;

    private final ElapsedTime voltageTimer = new ElapsedTime();
    private final ElapsedTime loopTimer = new ElapsedTime();
    private double voltage = 12.0;
    private double loopTimeMs = 0;

    public Robot(HardwareMap hardwareMap, Alliance alliance) {
        this.alliance = alliance;

        // MANUAL bulk caching: we decide when the cache refreshes (start of each loop).
        hubs = hardwareMap.getAll(LynxModule.class);
        for (LynxModule hub : hubs) {
            hub.setBulkCachingMode(LynxModule.BulkCachingMode.MANUAL);
        }

        Iterator<VoltageSensor> sensors = hardwareMap.voltageSensor.iterator();
        voltageSensor = sensors.hasNext() ? sensors.next() : null;
        readVoltage();

        driveTrain = register(new DriveTrain(hardwareMap));
        intake     = register(new Intake(hardwareMap));
        flywheel   = register(new Flywheel(hardwareMap, this::getVoltage));
        feeder     = register(new Feeder(hardwareMap));
    }

    private <T extends Subsystem> T register(T subsystem) {
        subsystems.add(subsystem);
        return subsystem;
    }

    /** Call exactly once per loop, after you've set what each subsystem should do. */
    public void update() {
        loopTimeMs = loopTimer.milliseconds();
        loopTimer.reset();

        for (LynxModule hub : hubs) {
            hub.clearBulkCache();
        }

        // Reading the battery is a slow, separate call — only refresh it a few times a second.
        if (voltageTimer.seconds() >= RobotConstants.VOLTAGE_REFRESH_SECONDS) {
            readVoltage();
        }

        for (Subsystem s : subsystems) {
            s.periodic();
        }
    }

    public void stop() {
        for (Subsystem s : subsystems) {
            s.stop();
        }
    }

    public void addTelemetry(Telemetry t) {
        // Loop time is the #1 health number. Under ~20 ms is good; over ~40 ms you'll feel it.
        t.addData("Loop", "%.1f ms (%.0f Hz)", loopTimeMs, loopTimeMs > 0 ? 1000.0 / loopTimeMs : 0);
        t.addData("Battery", "%.2f V", voltage);
        t.addData("Alliance", alliance);
        if (RobotConstants.VERBOSE_TELEMETRY) {
            for (Subsystem s : subsystems) {
                s.addTelemetry(t);
            }
        }
    }

    public double getVoltage() {
        return voltage;
    }

    public double getLoopTimeMs() {
        return loopTimeMs;
    }

    private void readVoltage() {
        voltageTimer.reset();
        if (voltageSensor == null) return;
        double v = voltageSensor.getVoltage();
        if (v > 1.0) voltage = v; // ignore garbage readings
    }
}
