package fr.insalyon.tc.raft;

/**
 * Represent a node of the system.
 */
public class Node {

    private State state;

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
