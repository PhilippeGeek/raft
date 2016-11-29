package fr.insalyon.tc.raft;

import java.io.IOException;
import java.io.PrintStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;

/**
 * Server which list all existing nodes
 */
public class ClientTest {
    private static List<String> nodes = Collections.synchronizedList(new ArrayList<>());
    private static boolean running = true;

    public static void main(String... args){
        try {
            Socket s = new Socket("localhost", 4242);
            new PrintStream(s.getOutputStream()).println("127.0.0.1:5680");
            final Scanner scanner = new Scanner(s.getInputStream());
            while (scanner.hasNext()){
                System.out.println(scanner.nextLine());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
