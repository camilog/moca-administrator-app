package camilog.adminapp.elections;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import camilog.adminapp.serverapi.BBServer;

/**
 * Created by stefano on 02-09-15.
 */
public class Election {
    private String _name;
    private int _noCandidates;
    private ArrayList<Candidate> _candidates;
    private long _db_id;
    private BBServer _bbServer;

    /**
     * Creates an election with an empty list of candidates,
     * with the given name and Bulletin Board server
     * @param name
     * @param bbserver
     */
    public Election(String name, String bbserver){
        _name = name;
        _bbServer = new BBServer(bbserver);
        _candidates = new ArrayList<>();
        _noCandidates = 0;
        _db_id = 0;
    }

    /**
     * Creates an election with an empty list of candidates,
     * with the given name
     * @param name
     */
    public Election(String name){
        this(name, "");
    }

    /**
     *
     * @return the Election name
     */
    @Override
    public String toString(){
        return _name;
    }

    /**
     * Adds a candidate to the election candidate container
     * @param newCandidate
     */
    public void addCandidate(Candidate newCandidate){
        _candidates.add(newCandidate);
        _noCandidates++;
    }

    /**
     * Adds a list of candidates to the election candidate container
     * @param candidates
     */
    public void addListOfCandidates(List<Candidate> candidates){
        for(Candidate candidate: candidates){
            addCandidate(candidate);
        }
    }

    /**
     * Adds a list of candidates to the election candidate container
     * given the candidate names only
     * @param candidatesNames
     */
    public void addListOfCandidatesByName(String[] candidatesNames){
        for(String name : candidatesNames){
            addCandidateByName(name);
        }
    }

    /**
     * Adds a candidate to the election candidate container
     * given it's name as a {@link String}
     * @param name
     */
    public void addCandidateByName(String name){
        addCandidate(new Candidate(name));
    }

    public ElectionResults getResults(){
        //TODO:
        return null;
    }

    /**
     * Makes a request to the bulletin board to upload it's candidates
     */
    public void uploadToBBServer(){
        _bbServer.uploadElectionCandidates(this);
    }

    /**
     * Performs the multiplication of the ballots stored in the bulletin board
     * and uploads the results back
     */
    public void multiplyBallots(){_bbServer.multiplyBallots(this);}

    /**
     * Downloads the election partial decryptions and multiplicated ballots
     * and uploads the results back to the bulletin board
     */
    public void obtainResults(){_bbServer.obtainResults(this);}

    /**
     * Returns true if election has candidates, or false if not
    */
    public boolean hasCandidates(){
        return _noCandidates > 0;
    }

    /**
     * Sets the Bulletin Board Server URL
     * @param bbServerURL
     */
    public void setBBServer(String bbServerURL){_bbServer.setAddress(bbServerURL);}

    /**
     *
     * @return the bulletin board server address
     */
    public String getBBServer(){return _bbServer.getAddress();}

    /**
     *
     * @return the amount of candidate objects stored within the elecion
     */
    public int getNumberOfCandidates(){return _noCandidates;}

    /**
     *
     * @return the Election name
     */
    public String getElectionName(){return _name;}

    /**
     *
     * @return the Candiadtes objects as an ArrayList
     */
    public ArrayList<Candidate> getCandidates(){return _candidates;}

    /**
     * Sets the id of the election within the database
     * @param id
     */
    public void setDB_ID(long id){_db_id = id;}

    /**
     *
     * @return the election id within the database
     */
    public long getDB_ID(){return _db_id;}
}
