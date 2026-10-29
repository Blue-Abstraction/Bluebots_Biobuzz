package org.firstinspires.ftc.teamcode.Subsystems;

import com.qualcomm.robotcore.hardware.DcMotorEx;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Constants.HardwareNames;
import org.firstinspires.ftc.teamcode.Constants.IntakeConstants;
import org.firstinspires.ftc.teamcode.Util.CachedMotor;

import java.util.EnumMap;
import java.util.Map;

/**
 * Four-sided intake driven by TWO motors: each motor spins one pair of opposite sides
 * (front + back on one, left + right on the other). ONE subsystem owns both.
 *
 * Because opposite sides share a motor, the smallest thing we can control separately is a PAIR,
 * not a side. TeleOp usually calls setState(IN / OUT / OFF) for both pairs at once;
 * setPair() is there for per-pair control (front/back intakes while left/right spits, say).
 */
public class Intake implements Subsystem {
    public enum State { OFF, IN, OUT }
    public enum Pair { FRONT_BACK, LEFT_RIGHT }

    private final Map<Pair, CachedMotor> motors = new EnumMap<>(Pair.class);
    private final Map<Pair, State> states = new EnumMap<>(Pair.class);
    private final Map<Pair, Double> velocities = new EnumMap<>(Pair.class);

    public Intake(HardwareMap hw) {
        for (Pair pair : Pair.values()) {
            DcMotorEx raw = hw.tryGet(DcMotorEx.class, nameFor(pair));
            if (raw != null) {
                CachedMotor motor = new CachedMotor(raw, IntakeConstants.CACHE_TOLERANCE);
                motor.setBrake(true); // rollers stop right away instead of coasting
                motors.put(pair, motor);
            }
            states.put(pair, State.OFF);
            velocities.put(pair, 0.0);
        }
    }

    // ----------------------------- Intentions -----------------------------

    /** Both pairs (all four sides) at once. */
    public void setState(State state) {
        for (Pair pair : Pair.values()) {
            states.put(pair, state);
        }
    }

    /** One pair only. */
    public void setPair(Pair pair, State state) {
        states.put(pair, state);
    }

    public State getState(Pair pair) {
        return states.get(pair);
    }

    /** Toggle button: if anything is intaking, turn everything off; otherwise intake on all sides. */
    public void toggleIn() {
        setState(states.containsValue(State.IN) ? State.OFF : State.IN);
    }

    /** Roller speed in encoder ticks/second (from the bulk read). Useful later for jam detection. */
    public double getVelocity(Pair pair) {
        return velocities.get(pair);
    }

    // ------------------------------- Loop -------------------------------

    @Override
    public void periodic() {
        for (Map.Entry<Pair, CachedMotor> e : motors.entrySet()) {
            Pair pair = e.getKey();
            CachedMotor motor = e.getValue();

            velocities.put(pair, motor.getVelocity()); // READ

            double power = powerFor(states.get(pair));  // COMPUTE
            // Direction applied here (not once at startup) so flipping a REVERSE_ flag live works.
            if (isReversed(pair)) power = -power;

            motor.setPower(power);                      // WRITE
        }
    }

    @Override
    public void stop() {
        setState(State.OFF);
        for (CachedMotor motor : motors.values()) {
            motor.setPower(0);
        }
    }

    @Override
    public void addTelemetry(Telemetry t) {
        for (Pair pair : Pair.values()) {
            t.addData("Intake " + pair, motors.containsKey(pair)
                    ? String.format("%s  (%.0f tps)", states.get(pair), velocities.get(pair))
                    : "NOT CONNECTED");
        }
    }

    /** True only if both motors are in the config. Telemetry shows which one is missing. */
    @Override
    public boolean isConnected() {
        return motors.size() == Pair.values().length;
    }

    // ------------------------------ Helpers ------------------------------

    private static double powerFor(State state) {
        switch (state) {
            case IN:  return IntakeConstants.IN_POWER;
            case OUT: return IntakeConstants.OUT_POWER;
            case OFF:
            default:  return 0;
        }
    }

    private static String nameFor(Pair pair) {
        return pair == Pair.FRONT_BACK ? HardwareNames.INTAKE_FRONT_BACK : HardwareNames.INTAKE_LEFT_RIGHT;
    }

    private static boolean isReversed(Pair pair) {
        return pair == Pair.FRONT_BACK ? IntakeConstants.REVERSE_FRONT_BACK : IntakeConstants.REVERSE_LEFT_RIGHT;
    }
}
