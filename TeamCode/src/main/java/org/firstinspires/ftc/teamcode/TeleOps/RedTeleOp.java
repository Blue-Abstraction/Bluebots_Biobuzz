package org.firstinspires.ftc.teamcode.TeleOps;

import com.qualcomm.robotcore.eventloop.opmode.TeleOp;

import org.firstinspires.ftc.teamcode.Util.Alliance;

@TeleOp(name = "Red TeleOp", group = "!Competition")
public class RedTeleOp extends MainTeleOp {
    @Override
    protected Alliance alliance() {
        return Alliance.RED;
    }
}
