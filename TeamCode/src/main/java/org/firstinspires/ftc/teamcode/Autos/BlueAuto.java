package org.firstinspires.ftc.teamcode.Autos;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.Util.Alliance;

// preselectTeleOp: after auto ends, the Driver Station selects Blue TeleOp automatically.
@Autonomous(name = "Blue Auto", group = "!Competition", preselectTeleOp = "Blue TeleOp")
public class BlueAuto extends MainAuto {
    @Override
    protected Alliance alliance() {
        return Alliance.BLUE;
    }
}
