package org.firstinspires.ftc.teamcode.NetworkTesting;

import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;

public abstract class VariableControlServer extends LinearOpMode {
    protected Map<String, Object> components = new HashMap<>(); // name, component
    protected ServerSocket serverSocket;
    protected Socket socket;
    protected DataOutputStream out;
    protected DataInputStream in;

    public void runOpMode() {
        try {
            serverSocket = new ServerSocket(1234);
            socket = serverSocket.accept();
            out = new DataOutputStream(socket.getOutputStream());
            in = new DataInputStream(socket.getInputStream());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        while (opModeIsActive()) {
            telemetry.addData("Connection is running. Client IP is", socket.getInetAddress());

            // reads input from the client
            String input;
            try {
                input = in.readUTF();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            // inputs should be formatted like this: [variable name] set [type] [value]
            String[] words = input.trim().split("\\s+");

            if (words[1].equalsIgnoreCase("create")) {
                components.put(words[0], getComponentClass(words[2]).cast(words[3]));
            } else if (words[1].equalsIgnoreCase("change")) {
                if (components.containsKey(words[0])) {
                    components.put(words[0], getComponentClass(words[2]).cast(words[3]));
                }
            }

            writeOpModeInHere();

            telemetry.update();

        }
    }
    private Class<?> getComponentClass(String type) {
        switch (type.toLowerCase()) {
            case "long": return Long.class;
            case "double": return Double.class;
            case "boolean": return Boolean.class;
            case "string": return String.class;
            case "int": return Integer.class;
            case "float": return Float.class;
            default: return null;
        }
    }

    protected void run(Runnable task, String name) {
        if (components.get(name) != null) {
            task.run();
        } else {
            telemetry.addData("Can't use " + name, "Variable not found. Did you initialize it?");
        }
    }
    protected abstract void writeOpModeInHere();
}