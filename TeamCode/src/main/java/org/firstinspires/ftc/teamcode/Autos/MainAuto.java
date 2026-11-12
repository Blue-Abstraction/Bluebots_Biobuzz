package org.firstinspires.ftc.teamcode.Autos;

import static com.pedropathing.api.Paths.line;

import com.pedropathing.math.Pose;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Constants.AutoConstants;
import org.firstinspires.ftc.teamcode.Robot;
import org.firstinspires.ftc.teamcode.Util.Alliance;
import org.firstinspires.ftc.teamcode.Util.PoseStorage;

/**
 * Autonomous, written once. BlueAuto / RedAuto only choose the alliance; Alliance mirrors every position.
 *
 * Structure: a STATE MACHINE. Each step starts something, then every loop checks
 * "is this step done?" (or "has it taken too long?") before moving to the next one.
 * Nothing ever waits or sleeps, so robot.update() keeps running Pedro and every subsystem.
 *
 * Current plan (PLACEHOLDER — replace with your paper plan):
 *   drive to the shooting spot -> spin up -> feed for a moment -> drive to park
 */
public abstract class MainAuto extends OpMode {

    private enum Step { DRIVE_TO_SHOOT, SPIN_UP, FEED, DRIVE_TO_PARK, DONE }

    protected abstract Alliance alliance();

    private Robot robot;
    private Step step;
    private final ElapsedTime stepTimer = new ElapsedTime();

    // Built in init() from AutoConstants (blue side), mirrored automatically for red.
    private Pose start, shootSpot, park;

    // ============================================================
    // INIT: runs once when INIT is pressed. Robot must be STILL: the OTOS calibrates.
    // ============================================================
    @Override
    public void init() {
        robot = new Robot(hardwareMap, alliance());

        Alliance a = alliance();
        start = a.pose(AutoConstants.START_X, AutoConstants.START_Y,
                Math.toRadians(AutoConstants.START_HEADING_DEG));
        shootSpot = a.pose(AutoConstants.SHOOT_X, AutoConstants.SHOOT_Y,
                Math.toRadians(AutoConstants.SHOOT_HEADING_DEG));
        park = a.pose(AutoConstants.PARK_X, AutoConstants.PARK_Y,
                Math.toRadians(AutoConstants.PARK_HEADING_DEG));

        // Pedro must know where the robot was placed, or every path is off by that much.
        robot.driveTrain.setPose(start);
    }

    @Override
    public void init_loop() {
        telemetry.addData("Alliance", alliance());
        telemetry.addLine("Place the robot at the start spot and keep it still.");
        robot.addTelemetry(telemetry);   // shows NOT CONNECTED for anything missing
        telemetry.update();
    }

    // ============================================================
    // START: runs once when PLAY is pressed. Kick off the first step.
    // ============================================================
    @Override
    public void start() {
        robot.driveTrain.followPath(line(start, shootSpot).linear(start, shootSpot));
        goTo(Step.DRIVE_TO_SHOOT);
    }

    // ============================================================
    // LOOP: check the current step, then update the whole robot.
    // ============================================================
    @Override
    public void loop() {
        switch (step) {
            case DRIVE_TO_SHOOT:
                if (pathDone()) {
                    robot.flywheel.setTarget(AutoConstants.SHOOT_TPS);
                    goTo(Step.SPIN_UP);
                }
                break;

            case SPIN_UP:
                if (robot.flywheel.atSpeed()
                        || stepTimer.seconds() > AutoConstants.SPIN_UP_TIMEOUT_SECONDS) {
                    startFeeding();
                    goTo(Step.FEED);
                }
                break;

            case FEED:
                if (stepTimer.seconds() > AutoConstants.FEED_SECONDS) {
                    stopFeeding();
                    robot.flywheel.setTarget(0);
                    robot.driveTrain.followPath(line(shootSpot, park).linear(shootSpot, park));
                    goTo(Step.DRIVE_TO_PARK);
                }
                break;

            case DRIVE_TO_PARK:
                if (pathDone()) {
                    goTo(Step.DONE);
                }
                break;

            case DONE:
            default:
                break;
        }

        // One call: bulk read -> Pedro + every subsystem -> hardware writes.
        robot.update();

        telemetry.addData("Step", "%s  (%.1f s)", step, stepTimer.seconds());
        robot.addTelemetry(telemetry);
        telemetry.update();
    }

    // ============================================================
    // STOP: runs when the 30 seconds end (or STOP is pressed).
    // ============================================================
    @Override
    public void stop() {
        if (robot == null) return;
        // Hand the final position to TeleOp — but only if Pedro was actually tracking it.
        if (robot.driveTrain.isConnected()) {
            Pose p = robot.driveTrain.getPose();
            PoseStorage.save(p.x(), p.y(), p.heading(), alliance());
        }
        robot.stop();
    }

    // ------------------------------ Helpers ------------------------------

    private void goTo(Step next) {
        step = next;
        stepTimer.reset();   // every step gets a fresh timer
    }

    /** A path step is done when Pedro finishes it, or when it has taken too long. */
    private boolean pathDone() {
        return !robot.driveTrain.isBusy()
                || stepTimer.seconds() > AutoConstants.PATH_TIMEOUT_SECONDS;
    }

    /** TODO: start whatever pushes balls into the flywheel (transfer / dye rotor). */
    private void startFeeding() {
        // e.g. robot.dyeRotor.setState(DyeRotor.State.SPIN);
    }

    /** TODO: stop it again. */
    private void stopFeeding() {
        // e.g. robot.dyeRotor.setState(DyeRotor.State.OFF);
    }
}
