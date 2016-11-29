package fr.insalyon.tc.raft;

import java.util.ArrayList;

/**
 * Represent a node of the system.
 */
public class Node {

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

    public Node(){
        state = State.FOLLOWER;
    }

    public State getState() {
        return state;
    }

    public enum State {
        FOLLOWER, CANDIDATE, LEADER
    }
}
