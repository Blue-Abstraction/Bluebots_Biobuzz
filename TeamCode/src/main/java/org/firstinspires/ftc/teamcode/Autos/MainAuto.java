package org.firstinspires.ftc.teamcode.Autos;

import static com.pedropathing.api.Paths.curve;
import static com.pedropathing.api.Paths.line;

import com.pedropathing.math.Pose;
import com.pedropathing.paths.Path;
import com.qualcomm.robotcore.eventloop.opmode.OpMode;
import com.qualcomm.robotcore.util.ElapsedTime;

import org.firstinspires.ftc.teamcode.Constants.AutoConstants;
import org.firstinspires.ftc.teamcode.Robot;
import org.firstinspires.ftc.teamcode.Subsystems.DyeRotor;
import org.firstinspires.ftc.teamcode.Subsystems.Intake;
import org.firstinspires.ftc.teamcode.Util.Alliance;
import org.firstinspires.ftc.teamcode.Util.PoseStorage;

/**
 * Autonomous, written once. BlueAuto / RedAuto only choose the alliance.
 *
 * THE PLAN (numbers match the paper plan):
 *   1. start                          7. curve to collect #2, intaking
 *   2. shoot                          8. wait 2 s
 *   3. drive to collect #1, intaking  9. drive back to shooting spot #2
 *   4. wait 2 s                      10. shoot
 *   5. drive to shooting spot #2     11. park
 *   6. shoot
 *
 * Structure: a STATE MACHINE. Each Step is something we WAIT for. Every loop, the current
 * step's case asks "is my wait over?". If yes, it starts the next actions and calls goTo().
 * Mechanisms keep running in the background because robot.update() runs them every loop,
 * so the flywheel spins up WHILE the robot drives, and the intake runs WHILE it drives.
 */
public abstract class MainAuto extends OpMode {

    private enum Step {
        SHOOT_1_SPIN_UP, SHOOT_1_FEED,              // 2
        DRIVE_TO_COLLECT_1, COLLECT_1_WAIT,         // 3, 4
        DRIVE_TO_SHOOT_2, SHOOT_2_SPIN_UP, SHOOT_2_FEED,   // 5, 6
        DRIVE_TO_COLLECT_2, COLLECT_2_WAIT,         // 7, 8
        DRIVE_TO_SHOOT_3, SHOOT_3_SPIN_UP, SHOOT_3_FEED,   // 9, 10
        DRIVE_TO_PARK,                              // 11
        DONE
    }

    protected abstract Alliance alliance();

    private Robot robot;
    private Step step;
    private final ElapsedTime stepTimer = new ElapsedTime();

    // Built once in init(), converted for this alliance.
    private Pose start, collect1, shoot2, collect2Control, collect2, park;
    private Path toCollect1, toShoot2, toCollect2, backToShoot2, toPark;

    // ============================================================
    // INIT: runs once when INIT is pressed. Robot must be STILL: the OTOS calibrates.
    // ============================================================
    @Override
    public void init() {
        robot = new Robot(hardwareMap, alliance());

        // Positions from AutoConstants, converted to this alliance's side.
        start = plan(AutoConstants.START_X, AutoConstants.START_Y, AutoConstants.START_HEADING_DEG);
        collect1 = plan(AutoConstants.COLLECT_1_X, AutoConstants.COLLECT_1_Y, AutoConstants.COLLECT_1_HEADING_DEG);
        shoot2 = plan(AutoConstants.SHOOT_2_X, AutoConstants.SHOOT_2_Y, AutoConstants.SHOOT_2_HEADING_DEG);
        collect2Control = plan(AutoConstants.COLLECT_2_CONTROL_X, AutoConstants.COLLECT_2_CONTROL_Y, 0);
        collect2 = plan(AutoConstants.COLLECT_2_X, AutoConstants.COLLECT_2_Y, AutoConstants.COLLECT_2_HEADING_DEG);
        park = plan(AutoConstants.PARK_X, AutoConstants.PARK_Y, AutoConstants.PARK_HEADING_DEG);

        // Paths, built once. .linear(a, b) turns the robot smoothly from a's heading to b's
        // during the drive (if both headings are the same, it simply keeps that heading).
        toCollect1   = line(start, collect1).linear(start, collect1);                          // 3
        toShoot2     = line(collect1, shoot2).linear(collect1, shoot2);                        // 5
        toCollect2   = curve(shoot2, collect2Control, collect2).linear(shoot2, collect2);      // 7
        backToShoot2 = line(collect2, shoot2).linear(collect2, shoot2);                        // 9
        toPark       = line(shoot2, park).linear(shoot2, park);                                // 11

        // 1. Pedro must know where the robot was placed, or every path is off by that much.
        robot.driveTrain.setPose(start);
    }

    @Override
    public void init_loop() {
        telemetry.addData("Alliance", "%s (plan drawn for %s)", alliance(), AutoConstants.PLAN_ALLIANCE);
        telemetry.addData("Place robot at", "(%.1f, %.1f) facing %.0f deg",
                start.x(), start.y(), Math.toDegrees(start.heading()));
        telemetry.addLine("Keep the robot still during INIT.");
        robot.addTelemetry(telemetry);
        telemetry.update();
    }

    // ============================================================
    // START: runs once when PLAY is pressed.
    // ============================================================
    @Override
    public void start() {
        // 2. We shoot from the start spot, so the first thing is spinning up.
        robot.flywheel.setTarget(AutoConstants.SHOOT_1_TPS);
        goTo(Step.SHOOT_1_SPIN_UP);
    }

    // ============================================================
    // LOOP: check the current step, then update the whole robot.
    // ============================================================
    @Override
    public void loop() {
        switch (step) {

            // ---------- 2. shoot (from the start spot) ----------
            case SHOOT_1_SPIN_UP:
                if (flywheelReady()) {
                    startFeeding();
                    goTo(Step.SHOOT_1_FEED);
                }
                break;

            case SHOOT_1_FEED:
                if (feedDone()) {
                    stopShooting();
                    robot.intake.setState(Intake.State.IN);      // 3. intake WHILE driving
                    driveTo(toCollect1, Step.DRIVE_TO_COLLECT_1);
                }
                break;

            // ---------- 3-4. collect #1 ----------
            case DRIVE_TO_COLLECT_1:
                if (pathDone()) {
                    goTo(Step.COLLECT_1_WAIT);                   // intake keeps running
                }
                break;

            case COLLECT_1_WAIT:
                if (stepTimer.seconds() > AutoConstants.COLLECT_WAIT_SECONDS) {
                    robot.intake.setState(Intake.State.OFF);
                    robot.flywheel.setTarget(AutoConstants.SHOOT_2_TPS);   // spin up WHILE driving
                    driveTo(toShoot2, Step.DRIVE_TO_SHOOT_2);    // 5.
                }
                break;

            // ---------- 5-6. shoot from spot #2 ----------
            case DRIVE_TO_SHOOT_2:
                if (pathDone()) {
                    goTo(Step.SHOOT_2_SPIN_UP);
                }
                break;

            case SHOOT_2_SPIN_UP:
                if (flywheelReady()) {
                    startFeeding();
                    goTo(Step.SHOOT_2_FEED);
                }
                break;

            case SHOOT_2_FEED:
                if (feedDone()) {
                    stopShooting();
                    robot.intake.setState(Intake.State.IN);      // 7. intake WHILE driving
                    driveTo(toCollect2, Step.DRIVE_TO_COLLECT_2);
                }
                break;

            // ---------- 7-8. collect #2 (curve) ----------
            case DRIVE_TO_COLLECT_2:
                if (pathDone()) {
                    goTo(Step.COLLECT_2_WAIT);
                }
                break;

            case COLLECT_2_WAIT:
                if (stepTimer.seconds() > AutoConstants.COLLECT_WAIT_SECONDS) {
                    robot.intake.setState(Intake.State.OFF);
                    robot.flywheel.setTarget(AutoConstants.SHOOT_2_TPS);   // spin up WHILE driving
                    driveTo(backToShoot2, Step.DRIVE_TO_SHOOT_3); // 9.
                }
                break;

            // ---------- 9-10. shoot from spot #2 again ----------
            case DRIVE_TO_SHOOT_3:
                if (pathDone()) {
                    goTo(Step.SHOOT_3_SPIN_UP);
                }
                break;

            case SHOOT_3_SPIN_UP:
                if (flywheelReady()) {
                    startFeeding();
                    goTo(Step.SHOOT_3_FEED);
                }
                break;

            case SHOOT_3_FEED:
                if (feedDone()) {
                    stopShooting();
                    driveTo(toPark, Step.DRIVE_TO_PARK);         // 11.
                }
                break;

            // ---------- 11. park ----------
            case DRIVE_TO_PARK:
                if (pathDone()) {
                    goTo(Step.DONE);
                }
                break;

            case DONE:
            default:
                break;   // Pedro holds the robot at the park spot
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
        // Hand the final position to TeleOp, but only if Pedro was actually tracking it.
        if (robot.driveTrain.isConnected()) {
            Pose p = robot.driveTrain.getPose();
            PoseStorage.save(p.x(), p.y(), p.heading(), alliance());
        }
        robot.stop();
    }

    // ------------------------------ Helpers ------------------------------

    /** A position from the paper plan, converted to this alliance's side. */
    private Pose plan(double x, double y, double headingDeg) {
        return alliance().fromPlan(AutoConstants.PLAN_ALLIANCE, new Pose(x, y, Math.toRadians(headingDeg)));
    }

    private void goTo(Step next) {
        step = next;
        stepTimer.reset();   // every step gets a fresh timer
    }

    /** Start a path and move to the step that waits for it. */
    private void driveTo(Path path, Step next) {
        robot.driveTrain.followPath(path);
        goTo(next);
    }

    /** A path step is done when Pedro finishes it, or when it has taken too long. */
    private boolean pathDone() {
        return !robot.driveTrain.isBusy()
                || stepTimer.seconds() > AutoConstants.PATH_TIMEOUT_SECONDS;
    }

    /** Ready to shoot: flywheel at speed, or it's taken too long (shoot anyway). */
    private boolean flywheelReady() {
        return robot.flywheel.atSpeed()
                || stepTimer.seconds() > AutoConstants.SPIN_UP_TIMEOUT_SECONDS;
    }

    private boolean feedDone() {
        return stepTimer.seconds() > AutoConstants.FEED_SECONDS;
    }

    /** ASSUMPTION: the dye rotor is what pushes balls into the flywheel. Change these two if not. */
    private void startFeeding() {
        robot.dyeRotor.setState(DyeRotor.State.SPIN);
    }

    private void stopFeeding() {
        robot.dyeRotor.setState(DyeRotor.State.OFF);
    }

    /** Done firing: stop feeding and let the flywheel coast down until the next shot. */
    private void stopShooting() {
        stopFeeding();
        robot.flywheel.setTarget(0);
    }
}
