package org.firstinspires.ftc.teamcode.Autos;

import com.qualcomm.robotcore.eventloop.opmode.Autonomous;

import org.firstinspires.ftc.teamcode.Util.Alliance;

// preselectTeleOp: after auto ends, the Driver Station selects Red TeleOp automatically.
@Autonomous(name = "Red Auto", group = "!Competition", preselectTeleOp = "Red TeleOp")
public class RedAuto extends MainAuto {
    @Override
    protected Alliance alliance() {
        return Alliance.RED;
    }
}
