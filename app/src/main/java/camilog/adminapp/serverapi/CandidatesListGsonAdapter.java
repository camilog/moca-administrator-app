package camilog.adminapp.serverapi;

import com.google.gson.Gson;

import java.util.ArrayList;

import camilog.adminapp.elections.Election;

/**
 * Created by stefano on 11-09-15.
 */
public class CandidatesListGsonAdapter {
    public String question;
    public int number_of_candidates;
    public ArrayList<Candidate> candidates;
    public CandidatesListGsonAdapter(Election election){
        question = election.getElectionName();
        number_of_candidates = election.getNumberOfCandidates();
        candidates = populateCandidates(election);
    }

    public String toJSON(){
        return new Gson().toJson(this);
    }

    private ArrayList<Candidate> populateCandidates(Election election){
        ArrayList<Candidate> result = new ArrayList<>();
        int id=1;
        for(camilog.adminapp.elections.Candidate candidate : election.getCandidates()){
            result.add(new Candidate(id++, candidate.getName()));
        }
        return result;
    }
    class Candidate{
        int id;
        String name;
        public Candidate(int id, String name){
            this.id = id; this.name = name;
        }
    }
}
