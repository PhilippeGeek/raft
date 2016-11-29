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
public class ListServer {
    private static List<String> nodes = Collections.synchronizedList(new ArrayList<>());
    private static boolean running = true;

    public static void main(String... args){
        ServerSocket serverSocket=null;
        try {
            serverSocket = new ServerSocket(4242);
        } catch (IOException e) {
            System.exit(1);
        }
        while (running) {
            try {
                final Socket client = serverSocket.accept();
                new Thread(()->{
                    try {
                        // Get URL from client
                        String clientUrl = new Scanner(client.getInputStream()).nextLine();

                        // Append it to nodes
                        if(!nodes.contains(clientUrl)){
                            nodes.add(clientUrl);
                        }

                        // Generate database to send
                        StringBuilder list = new StringBuilder();
                        nodes.forEach((node)->list.append(node).append('\n'));

                        // Send the db
                        new PrintStream(client.getOutputStream()).print(list.toString());

                        // Close connection
                        client.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }).start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
