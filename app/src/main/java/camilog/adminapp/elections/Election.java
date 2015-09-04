package camilog.adminapp.elections;

import java.util.ArrayList;

/**
 * Created by stefano on 02-09-15.
 */
public class Election {
    private String _serverURL, _name;
    private int _noCandidates;
    private ArrayList<Candidate> _candidates;
    public Election(String name, String bbserver){
        _name = name;
        _serverURL = bbserver;
        _candidates = new ArrayList<>();
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
    public boolean hasCandidates(){
        return _noCandidates > 0;
    }
    public void setBBServer(String bbServerURL){_serverURL = bbServerURL;}
    public String getBBServer(){return _serverURL;}
    public int getNumberOfCandidates(){return _noCandidates;}
    public String getElectionName(){return _name;}
    public ArrayList<Candidate> getCandidates(){return _candidates;}
}
