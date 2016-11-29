package fr.insalyon.tc.raft;

import java.io.Serializable;
import java.util.HashMap;
import java.util.UUID;

import fr.insalyon.tc.raft.Node;

import static com.sun.org.apache.xalan.internal.xsltc.compiler.util.Type.Node;

/**
 * Created by wgata on 29/11/16.
 */
public class Vote implements Serializable {

    /**
     * Identifier of this vote
     */
    private final String id = UUID.randomUUID().toString();

    private final String candidateId;

    /**
     * table de suivi des votes
     */
    private HashMap<String,VoteState> tableVote = new HashMap<>();

    public Vote(Node candidate) {
        if(candidate.getState() != fr.insalyon.tc.raft.Node.State.CANDIDATE){
            throw new IllegalArgumentException("Can not have a non candidate node");
        }
        candidateId = candidate.getId();
    }

    public void merge(Vote vote){
        if(!vote.id.equals(id)){
            throw new IllegalArgumentException("Votes has not the same id");
        }
        vote.tableVote.forEach((nodeId, voteChoose)->{
            if(tableVote.get(nodeId) == VoteState.WAITING){
                tableVote.remove(nodeId);
                tableVote.put(nodeId, voteChoose);
            }
        });
    }

    public VoteState getResult(){
        int granted = 0, refused = 0;
        for (VoteState state : tableVote.values()) {
            switch (state) {
                case WAITING:
                    return VoteState.WAITING;
                case GRANTED:
                    granted++;
                    break;
                case REFUSED:
                    refused++;
                    break;
            }
        }
        return granted>=refused?VoteState.GRANTED:VoteState.REFUSED;
    }

    public String getCandidateId() {
        return candidateId;
    }
}
