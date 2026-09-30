package org.firstinspires.ftc.teamcode.RobotFunctions;

import java.util.function.BooleanSupplier;

public class Toggle {
    private boolean toggle;
    private final BooleanSupplier button;
    public Toggle(BooleanSupplier button) {
        this.button = button;
        toggle = false;
    }
    public boolean getToggle() {
        return toggle;
    }
    public void toggleIfPressed() {
        if (button.getAsBoolean()) {
            toggle = !toggle;
        }
    }
    public void thisNotThat(Runnable ifTrue, Runnable ifFalse) {
        if (toggle) {
            ifTrue.run();
        } else {
            ifFalse.run();
        }
    }
}
