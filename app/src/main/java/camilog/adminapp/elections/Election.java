package camilog.adminapp.elections;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.json.JSONObject;

import java.util.ArrayList;

/**
 * Created by stefano on 02-09-15.
 */
public class Election {
    public static final String JSON_NAME = "name";
    public static final String JSON_CANDIDATES = "candidates";
    public static final String JSON_NUMBER_CANDIDATES = "number_candidates";
    public static final String JSON_BBSERVER = "bb_server";

    private String _serverURL, _name;
    private int _noCandidates;
    private ArrayList<Candidate> _candidates;
    public Election(String name, String bbserver){
        _name = name;
        _serverURL = bbserver;
        _candidates = new ArrayList<>();
        _noCandidates = 0;

    }

    public Election(String name){
        this(name, "");
    }

    @Override
    public String toString(){
        return _name;
    }

    public void addCandidate(Candidate newCandidate){
        //TODO
        try{
            _candidates.add(newCandidate);
        }catch(Exception e){

        }
        _noCandidates++;
    }

    public void addCandidateByName(String name){
        //TODO: instantiates a candidate and makes call to addCandidate
    }

    public ElectionResults getResults(){
        //TODO:
        return null;
    }

    public void countVotes(){
        //TODO:
    }

    public void uploadInformationToServer(){
        //TODO:
    }

    public String toJSON(){

        return new Gson().toJson(this);
    }

    public boolean hasCandidates(){
        return _noCandidates > 0;
    }
    public void setBBServer(String bbServerURL){_serverURL = bbServerURL;}
    public String getBBServer(){return _serverURL;}
    public int getNumberOfCandidates(){return _noCandidates;}
    public String getElectionName(){return _name;}
    public ArrayList<Candidate> getCandidates(){return _candidates;}
}
