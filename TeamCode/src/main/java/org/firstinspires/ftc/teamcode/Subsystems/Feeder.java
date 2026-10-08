package org.firstinspires.ftc.teamcode.Subsystems;

import com.qualcomm.robotcore.hardware.HardwareMap;
import com.qualcomm.robotcore.hardware.Servo;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.robotcore.external.Telemetry;
import org.firstinspires.ftc.teamcode.Constants.HardwareNames;
import org.firstinspires.ftc.teamcode.Constants.ShooterConstants;

/**
 * The two-servo "push" the old Template did with:
 *
 *     new Thread(() -> { push; sleep(750); retract; }).start();
 *
 * That works on the bench but causes real problems on a robot: the thread keeps
 * running after you press STOP, two quick presses start two threads that fight,
 * and nothing else knows whether a push is in progress.
 *
 * Top teams do timed actions with a timer + state instead. periodic() checks the clock
 * every loop; nothing ever sleeps. Same behavior, none of the problems.
 */
public class Feeder implements Subsystem {
    private enum State { RESTING, PUSHING }

    private final Servo right, left;
    private final ElapsedTime timer = new ElapsedTime();
    private State state = State.RESTING;
    private boolean needsWrite = true;

    public Feeder(HardwareMap hw) {
        right = hw.tryGet(Servo.class, HardwareNames.FEEDER_RIGHT);
        left = hw.tryGet(Servo.class, HardwareNames.FEEDER_LEFT);
    }

    /** Start one push. Ignored if a push is already happening. */
    public void push() {
        if (state == State.RESTING) {
            state = State.PUSHING;
            timer.reset();
            needsWrite = true;
        }
    }

    public boolean isBusy() {
        return state == State.PUSHING;
    }

    @Override
    public void periodic() {
        if (!isConnected()) return;

        if (state == State.PUSHING && timer.milliseconds() >= ShooterConstants.FEEDER_PUSH_MS) {
            state = State.RESTING;
            needsWrite = true;
        }

        // Servos only get a new command when the state changes.
        if (needsWrite) {
            if (state == State.PUSHING) {
                right.setPosition(ShooterConstants.FEEDER_PUSH_RIGHT);
                left.setPosition(ShooterConstants.FEEDER_PUSH_LEFT);
            } else {
                right.setPosition(ShooterConstants.FEEDER_REST_RIGHT);
                left.setPosition(ShooterConstants.FEEDER_REST_LEFT);
            }
            needsWrite = false;
        }
    }

    @Override
    public void stop() {
        state = State.RESTING;
        needsWrite = true;
        periodic(); // write the rest position right now
    }

    @Override
    public void addTelemetry(Telemetry t) {
        t.addData("Feeder", isConnected() ? state : "NOT CONNECTED");
    }

    @Override
    public boolean isConnected() {
        return right != null && left != null;
    }
}
