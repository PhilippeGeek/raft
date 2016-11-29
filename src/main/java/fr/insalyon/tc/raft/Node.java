package fr.insalyon.tc.raft;

import java.io.IOError;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.UUID;

/**
 * Represent a node of the system.
 */
public class Node {

    /**
     * Identifier for that node
     */
    private final String id;

    /**
     * Socket to join that node
     */
    private Socket socket;

    /**
     * Server of that node.
     * Used only if Node is on current system.
     */
    private ServerSocket server;

    /**
     * Current state of this node.
     */
    private State state;

    /**
     * All linked nodes
     */
    private ArrayList<Node> otherNodes = new ArrayList<>();

    /**
     * The current leader node in the network
     */
    private Node leader;

    /**
     * List known actions by this node
     */
    private HashMap<String, Action> setOfKnownActions = new HashMap<>();

    /**
     * Latest Action computed by the current node in its state machine
     */
    private Action lastAppliedAction;

    /**
     * Latest Action known on the network by that node
     */
    private Action lastKnownAction;

    public Node() throws IOException{
        id = UUID.randomUUID().toString();
        state = State.FOLLOWER;
        server = new ServerSocket();
        System.out.println("Started node on "+server.getInetAddress().getHostAddress()+":"+server.getLocalPort());
        new Thread(() -> {
            //noinspection InfiniteLoopStatement
            while (true){
                try {
                    final Socket clientSocket = server.accept();
                    String[] hello = new Scanner(clientSocket.getInputStream()).nextLine().split(" ");
                    if(hello.length <= 1){
                        clientSocket.close();
                    } else {
                        switch (hello[0]){
                            case "client":
                                break;
                            case "node":
                                System.out.println("New node has come "+hello[1]);
                                final Node node = new Node(hello[1], clientSocket);
                                new Thread(node::listenNode).start();
                        }
                    }
                } catch (IOException e) {
                    System.err.println("Error on new socket connection");
                    e.printStackTrace(System.err);
                }
            }
        }).start();
    }

    private void listenNode() {
        while (true){
            try {
                final InputStream inputStream = socket.getInputStream();
                final Object readObject = new ObjectInputStream(inputStream).readObject();
                System.out.println("Receive object! "+readObject);
            } catch (IOException e) {
                System.err.println("Error on socket connection");
                e.printStackTrace(System.err);
            } catch (ClassNotFoundException e) {
                System.err.println("Class can not be found!");
                try {
                    socket.close();
                } catch (IOException ignored) {}
                return;
            }
        }
    }

    private Node(String id, Socket socket){
        this.id = id;
        this.socket = socket;
    }

    public State getState() {
        return state;
    }

    public String getId() {
        return id;
    }

    public enum State {
        FOLLOWER, CANDIDATE, LEADER
    }
}
