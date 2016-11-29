package fr.insalyon.tc.raft;

import java.io.*;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.UUID;

/**
 * Represent a node of the system.
 */
public class Node {

    private static final int MASTER_SERVER_PORT = 4242;
    private static final String MASTER_SERVER_URL = "localhost";

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

    private int time = 0;
    private final int loopTime = (int) (Math.random()*150+150);

    /**
     * Construct a new Node to the system
     * @throws IOException Can not create node due to a network issue
     */
    @SuppressWarnings("WeakerAccess")
    public Node() throws IOException{
        id = UUID.randomUUID().toString();
        state = State.FOLLOWER;
        server = new ServerSocket(0,50, InetAddress.getLocalHost());
        registerNode();
        listenNewSocketConnections();
        startTimer();
        System.out.println("Started node on "+server.getInetAddress().getHostAddress()+":"+server.getLocalPort());
    }

    private void startTimer() {
        new Thread(()->{
            while (!server.isClosed()){
                time++;
                try {
                    Thread.sleep(1);
                } catch (InterruptedException e) {
                    throw new RuntimeException("Node Timer should never be halt!", e);
                }
                if(time>loopTime){
                    // Act timer exceed
                    Heartbeat heartbeat = new Heartbeat(this);
                    otherNodes.forEach((node)->node.sendHeartbeat(heartbeat));
                    // Reset the timer
                    time = 0;
                }
            }
        }).start();
    }

    private void sendHeartbeat(Heartbeat beat){
        if(isClientNode()){
            try {
                System.out.println("Send beat!");
                new ObjectOutputStream(socket.getOutputStream()).writeObject(beat);
            } catch (SocketException e) {
                if(e.getMessage().contains("Broken pipe")){
                    state = State.DOWN;
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void listenNewSocketConnections() {
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
                                new PrintStream(clientSocket.getOutputStream()).println(id);
                                System.out.println("New node has come "+hello[1]);
                                final Node node = new Node(hello[1], clientSocket);
                                otherNodes.add(node);
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
        while (getState() != State.DOWN){
            try {
                final InputStream inputStream = socket.getInputStream();
                final Object readObject = new ObjectInputStream(inputStream).readObject();
                System.out.println("Receive object! "+readObject);
            } catch (IOException e) {
                state = State.DOWN;
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

    private void registerNode(){
        try {
            Socket s = new Socket(Node.MASTER_SERVER_URL, Node.MASTER_SERVER_PORT);
            new PrintStream(s.getOutputStream()).println(getServerHost());
            final Scanner scanner = new Scanner(s.getInputStream());
            while (scanner.hasNext()){
                final String host = scanner.nextLine();
                if(host.contains(":") && !host.equals(getServerHost())){
                    final Node node = connectToNode(host);
                    if (node != null) {
                        otherNodes.add(node);
                        new Thread(node::listenNode).start();
                    }
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Node connectToNode(String host) {
        try{
            final Socket socket = new Socket(host.split(":")[0], Integer.parseInt(host.split(":")[1]));
            new PrintStream(socket.getOutputStream()).println("node "+id);
            String nodeId = new Scanner(socket.getInputStream()).nextLine();
            return new Node(nodeId, socket);
        } catch (IOException e) {
            System.err.println("Failed to connect to host " + host);
        }
        return null;
    }

    public String getServerHost() {
        return server.getInetAddress().getHostAddress()+":"+server.getLocalPort();
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

    public boolean isClientNode() {
        return server == null;
    }

    public enum State {
        FOLLOWER, CANDIDATE, DOWN, LEADER
    }
}
