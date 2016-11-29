package fr.insalyon.tc.raft;

import java.io.IOException;

/**
 * Start a new node to the graph
 */
public class Main {

    public static void main(String... args){
        try {
            new Node();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
