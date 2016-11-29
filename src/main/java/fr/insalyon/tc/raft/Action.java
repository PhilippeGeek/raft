package fr.insalyon.tc.raft;

import java.io.Serializable;
import java.util.UUID;

/**
 * Action that can be applied to a state machine.
 */
public class Action implements Serializable{

    /**
     * Identifier of this action
     */
    private final String id;

    /**
     * Parent of the current action in execution order
     */
    private Action parent;

    /**
     * Command of the action
     */
    private final String command;

    /**
     * Arguments to the command to execute
     */
    private final String[] args;

    /**
     * Constructor when the node is created
     * @param command The command to execute
     * @param args The arguments to pass to command
     */
    public Action(String command, String... args) {
        this.id = UUID.randomUUID().toString();
        this.command = command;
        this.args = args;
    }

    /**
     * Constructor when node is recieved with a given ID
     * @param id The unique id of the current node
     * @param command The command to execute
     * @param args The arguments to pass to command
     */
    public Action(String id, String command, String... args) {
        this.id = id;
        this.command = command;
        this.args = args;
    }

    /**
     * @return A unique identifier for that action
     */
    public String getId() {
        return id;
    }

    /**
     * @return The parent of current action (must be performed before)
     */
    public Action getParent() {
        return parent;
    }

    /**
     * Update the parent of current node.
     * @param parent New parent of the current node
     */
    public void setParent(Action parent) {
        this.parent = parent;
    }

    /**
     * @return Command that must be performed by this action
     */
    public String getCommand() {
        return command;
    }

    /**
     * @return Arguments of order
     */
    public String[] getArgs() {
        return args;
    }
}
