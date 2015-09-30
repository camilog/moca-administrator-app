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

    public Election(String name, String bbserver){
        _name = name;
        _bbServer = new BBServer(bbserver);
        _candidates = new ArrayList<>();
        _noCandidates = 0;
        _db_id = 0;
    }

    public Election(String name){
        this(name, "");
    }

    @Override
    public String toString(){
        return _name;
    }

    public void addCandidate(Candidate newCandidate){
        _candidates.add(newCandidate);
        _noCandidates++;
    }
    public void addListOfCandidates(List<Candidate> candidates){
        for(Candidate candidate: candidates){
            addCandidate(candidate);
        }
    }
    public void addListOfCandidatesByName(String[] candidatesNames){
        for(String name : candidatesNames){
            addCandidateByName(name);
        }
    }

    public void addCandidateByName(String name){
        addCandidate(new Candidate(name));
    }

    public ElectionResults getResults(){
        //TODO:
        return null;
    }

    public void uploadToBBServer(){
        _bbServer.uploadElectionCandidates(this);
    }
    public void multiplyBallots(){_bbServer.multiplyBallots(this);}
    public void obtainResults(){_bbServer.obtainResults(this);}

    /**
     * Returns true if election has candidates, or false if not
    */
    public boolean hasCandidates(){
        return _noCandidates > 0;
    }
    public void setBBServer(String bbServerURL){_bbServer.setAddress(bbServerURL);}
    public String getBBServer(){return _bbServer.getAddress();}
    public int getNumberOfCandidates(){return _noCandidates;}
    public String getElectionName(){return _name;}
    public ArrayList<Candidate> getCandidates(){return _candidates;}
    public void setDB_ID(long id){_db_id = id;}
    public long getDB_ID(){return _db_id;}
}
