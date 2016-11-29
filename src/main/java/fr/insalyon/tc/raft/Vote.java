package fr.insalyon.tc.raft;

import java.io.Serializable;
import java.util.HashMap;
import fr.insalyon.tc.raft.Node;

import static com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type.Node;

/**
 * Created by wgata on 29/11/16.
 */
public class Vote implements Serializable {

    /**
     * Identifier of this vote
     */
    private String id;

    /**
     * période courante du candidat
     */
    private int currentTerm;

    /**
     * indice de la dernière entrée du log du candidat.
     */
    private int LastLogIndex;

    /**
     * période de la dernière entrée du log du candidat
     */
    private int lastLogTerm;

    /**
     * vrai si le candidat est choisi
     */
    private boolean voteGranted;

    /**
     * table de suivi des votes
     */
    private HashMap<Node,Vote> tableVote = new HashMap<Node,Vote>();

public Vote() {
    this.id = id;
    this.voteGranted = voteGranted;
}



}
