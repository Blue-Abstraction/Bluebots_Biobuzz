package Clients;
/*
    copy and paste the following into the terminal in Android Studio to run this class:
        cd TeamCode/src/main/java/Clients
        javac VariableControlClient.java
        java VariableControlClient
*/

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.Scanner;

public class VariableControlClient {
    private static Socket socket;
    private static Scanner read;
    private static DataOutputStream out;
    private static DataInputStream in;

    private static final String IP = "192.168.43.1";

    public static void main(String[] args) throws IOException, UnknownHostException {
        // connect to the phone
        socket = new Socket(IP, 1234); // 1234 is the port
        System.out.println("Connected to the phone!");

        // init streams and stuff
        read = new Scanner(System.in);
        out = new DataOutputStream(socket.getOutputStream());
        in = new DataInputStream(socket.getInputStream());

        while (true) {
            String output = read.nextLine();

            out.writeUTF(output);
            out.flush();

            String input = in.readUTF();
            System.out.println(input);
        }
    }
}
