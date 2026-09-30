package org.firstinspires.ftc.teamcode.Autonomous;

import com.pedropathing.control.PIDFCoefficients;
import com.pedropathing.control.PredictiveBrakingCoefficients;
import com.pedropathing.follower.Follower;
import com.pedropathing.follower.FollowerConstants;
import com.pedropathing.ftc.FollowerBuilder;
import com.pedropathing.ftc.drivetrains.MecanumConstants;
import com.pedropathing.ftc.localization.constants.PinpointConstants;
import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver;
import com.qualcomm.hardware.gobilda.GoBildaPinpointDriver.GoBildaOdometryPods;
import com.qualcomm.robotcore.hardware.DcMotorSimple;
import com.qualcomm.robotcore.hardware.HardwareMap;

import org.firstinspires.ftc.robotcore.external.navigation.DistanceUnit;

public class Constants {
    // TODO: fill these out
    private static double mass = 0; // kg
    // check out this image to find out how: https://pedropathing.com/docs/odometry-dark.png
    private static double forwardPodY = 0; // inches
    private static double strafePodX = 0; // inches

    /*
    TODO: To find the x-velocity:
    1. Make sure you have enough room. By default, the robot moves 48 inches forward,
    but this can be changed by navigating to the ForwardVelocityTuner class in Tuning.java.
    Typically larger numbers yield better results.
    2. Then, in the Tuning OpMode, under automatic, select and start Forward Velocity Tuner.
    The robot speed should ramp up until it reaches full power.
    It will continue moving until it has reached the set distance, then it will abruptly stop.
    3. Once the robot stops moving at maximum speed, one number will be displayed on telemetry:
    Velocity: The final velocity the robot achieved before stopping; this is what we want
    ---
    TODO: To find the y-velocity:
    1. Same set-up with the x-velocity. By default, the robot moves 48 inches to the left,
    but this can be changed by navigating to the LateralVelocityTuner class in Tuning.java.
    2. In the Tuning OpMode, under automatic, select and start Lateral Velocity Tuner.
    It does the same thing as the x-velocity tuner, but it moves left instead of forward.
    3. Record and change yVelocity to what's displayed in telemetry.
     */
    private static double xVelocity = 0;
    private static double yVelocity = 0;
    /*
    TODO: After finding the velocities, you should tune the headings next:
    1. Open Panels. And select the Tuning Opmode and then choose HeadingTuner on your Driver Hub or Driver Station,
    3. Ensure that the timer for autonomous OpModes is disabled.
    4. Run the run the HeadingTuner autonomous OpMode.
    5. Turn the robot left or right at varying amounts and observe how the robot turns back to its starting heading.
    6. Adjust the PIDF constants (coefficientsHeadingPIDF) in the Tuning-> Follower -> Constants
    section of Panels Configurables to ensure that the robot can accurately correct back to its starting position with minimal oscillations.
    7. If the robot has too many oscillations, lower P. If it turns slowly, increase P.
    8. Fill out the P, I, D, and F variables you tuned below.
     */
    private static double P = 0;
    private static double I = 0;
    private static double D = 0;
    private static double F = 0;

    /*
    TODO: We're using predictive braking for this template.
    Run the Tuning.java OpMode -> Automatic -> PredictiveBrakingTuner.
    This will give you values for kQuadratic and kLinear. Fill them out below.
     */
    private static double kLinear = 0;
    private static double kQuadratic = 0;

    /*
    TODO: adjust the kP value for predictive braking (around .05 - .3) as well. .1 is a good starting point.
    Run LineTest and adjust kP to your liking.
    kP changes are harder to notice. However, tune kP as high as possible
    to maximize holding strength and accuracy, without jittering the robot.
    If you want smoother or sooner deceleration, try experimenting with kP of 0.05 or lower
    and increasing the kQuadratic term, as this will act more like a motion profile.

    Also, lower the parametric end constraint to around .97 or .95. Lower values
    will make actions trigger earlier:
        path.setTValueConstraint(double set)
    Don't set it below .9, though.

    And you're done!
     */
    private static double kP = .1;

    // methods
    public static FollowerConstants followerConstants = new FollowerConstants()
            .mass(mass)
            .headingPIDFCoefficients(new PIDFCoefficients(P, I, D, F))
            .predictiveBrakingCoefficients(new PredictiveBrakingCoefficients(kP, kLinear, kQuadratic))
            .centripetalScaling(0);

    public static Follower createFollower(HardwareMap hardwareMap) {
        return new FollowerBuilder(followerConstants, hardwareMap)
                .pinpointLocalizer(localizerConstants)
                .mecanumDrivetrain(driveConstants)
                .build();
    }

    public static MecanumConstants driveConstants = new MecanumConstants()
            .maxPower(1)
            .rightFrontMotorName("FRW")
            .rightRearMotorName("BRW")
            .leftRearMotorName("BLW")
            .leftFrontMotorName("FLW")
            // TODO: change directions if needed
            .leftFrontMotorDirection(DcMotorSimple.Direction.REVERSE)
            .leftRearMotorDirection(DcMotorSimple.Direction.REVERSE)
            .rightFrontMotorDirection(DcMotorSimple.Direction.FORWARD)
            .rightRearMotorDirection(DcMotorSimple.Direction.FORWARD)
            .xVelocity(xVelocity)
            .yVelocity(yVelocity);

    public static PinpointConstants localizerConstants = new PinpointConstants()
            .forwardPodY(forwardPodY)
            .strafePodX(strafePodX)
            .distanceUnit(DistanceUnit.INCH)
            .hardwareMapName("pinpoint") // TODO: change if needed
            .encoderResolution(GoBildaOdometryPods.goBILDA_4_BAR_POD)
            /*
            TODO: To find the encoder direction:
            1. Select and run localization test under the localization folder in the tuning OpMode
            2. Move the robot forward. The x coordinate should increase.
            3. Move the robot left. The y coordinate should increase.
            4. If either of those does not happen, you must reverse the respective encoder.
             */
            .forwardEncoderDirection(GoBildaPinpointDriver.EncoderDirection.FORWARD)
            .strafeEncoderDirection(GoBildaPinpointDriver.EncoderDirection.FORWARD);
}