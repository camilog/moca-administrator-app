package camilog.adminapp.serverapi;

import com.google.gson.Gson;

import camilog.adminapp.elections.ElectionResults;

/**
 * Created by stefano on 02-10-15.
 */
public class ResultsGsonAdapter {
    CandidateInformation[] result;
    public ResultsGsonAdapter(final ElectionResults results){
        int i=1;
        int[] votes = results.getResultsByCandidate();
        result = new CandidateInformation[votes.length];
        for(int j=0;j<result.length;j++){
            result[j] = new CandidateInformation(String.valueOf(i++), votes[j]);
        }
    }

    public String toJSON(){
        return new Gson().toJson(this);
    }

    private class CandidateInformation{
        String candidate_id;
        int votes;
        public CandidateInformation(String candidate_id, int votes){
            this.candidate_id = candidate_id;
            this.votes = votes;
        }
    }
}
