package org.firstinspires.ftc.teamcode.Subsystems;

import org.firstinspires.ftc.robotcore.external.Telemetry;

/**
 * The contract every mechanism follows. This is the single most common pattern
 * across the DECODE repos we studied:
 *
 *   - OpModes only say WHAT they want   (intake.setState(IN), flywheel.setTarget(1400))
 *   - periodic() decides HOW, once per loop (runs the PID, writes the motor)
 *   - nothing ever blocks: no sleep(), no while-loops waiting on hardware, no Threads
 *
 * Robot.update() calls periodic() on every registered subsystem, every loop.
 */
public interface Subsystem {
    /** Called once per loop, AFTER the bulk read. Do your control math and hardware writes here. */
    void periodic();

    /** Safe state: zero power. Called when the OpMode stops. */
    void stop();

    /** Add this mechanism's key numbers to telemetry. */
    void addTelemetry(Telemetry telemetry);

    /** False if the hardware isn't in the config yet. The subsystem then does nothing instead of crashing. */
    boolean isConnected();
}
