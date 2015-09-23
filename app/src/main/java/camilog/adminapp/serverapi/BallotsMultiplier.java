package camilog.adminapp.serverapi;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.Key;

import camilog.adminapp.elections.Election;

/**
 * Created by stefano on 22-09-15.
 */
public class BallotsMultiplier extends AbstractBBServerTaskManager{
    public BallotsMultiplier(BBServer server){
        super(server);
    }
    public void multiplyBallots(final Election election){
        startMultiplyBallotsThread(election);
    }

    private void multiplyBallotsThread(Election election) throws IOException{
        URL obj = new URL(_server.getAddress() + "/" + _server.getBALLOTS_LIST_SUBDOMAIN() + "/" + _server.getALL_DOCS_SUBDOMAIN());
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Content-Type", "application/json");
        con.getResponseCode();
        String response = getResponseFromInputStream(con.getInputStream());
        //TODO:
        Gson gson = new Gson();
        BallotResponse ballotResponse = gson.fromJson(response, BallotResponse.class);
        BallotResponse.BallotRowsResponse[] rowsResponse = ballotResponse.rows;
        BigInteger result = BigInteger.ONE;
        for(int i=0;i<ballotResponse.total_rows;i++){
            int rowId = rowsResponse[i].id;
            BigInteger encryptedVoteValue = getEncryptedVoteValueById(rowId);
            //TODO: authority public key??
            //TODO: tomar módulo todo el rato o no?
        }
    }

    private void uploadMultipliedBallots(BigInteger multipliedBallots) throws IOException {
        // Set the URL where to POST the ballot
        URL obj = new URL(_server.getAddress() + "/" + _server.getMULTIPLIED_BALLOTS_SUBDOMAIN());
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        String urlParameters = "{\"multiplied_ballot\":{\"value\":" + multipliedBallots.toString() + "}}";
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(urlParameters);
        wr.flush();
        wr.close();
        con.getResponseCode();
    }

    private BigInteger getEncryptedVoteValueById(int id) throws IOException{
        URL obj = new URL(_server.getAddress() + "/" + _server.getBALLOTS_LIST_SUBDOMAIN() + "/" + String.valueOf(id));
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Content-Type", "application/json");
        con.getResponseCode();
        String response = getResponseFromInputStream(con.getInputStream());
        return new BigInteger((new Gson()).fromJson(response, BallotResponse.ParticularBallotResponse.class).encrypted_vote);
    }

    private BigInteger downloadAuthorityPublicKey() throws KeyNotFoundException, IOException{
        BigInteger publicKey = null;
        URL obj = new URL(_server.getAddress() + "/" + _server.getAUTHORITY_PUBLIC_KEY_SUBDOMAIN());
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Content-Type", "application/json");
        con.getResponseCode();
        String response = getResponseFromInputStream(con.getInputStream());
        Gson gson = new Gson();
        //TODO: terminar de bajar la publickey
        if(publicKey == null)throw new KeyNotFoundException();
        return publicKey;
    }

    private String getResponseFromInputStream(InputStream inputStream) throws IOException{
        String response;
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        String inputLine;
        StringBuilder builder = new StringBuilder();
        while ((inputLine = bufferedReader.readLine()) != null)
            builder.append(inputLine);
        response = builder.toString();
        bufferedReader.close();
        if(response == null)throw new IOException();
        return response;
    }

    private void startMultiplyBallotsThread(final Election election){
        new Thread(){
            public void run(){
                try{
                    multiplyBallotsThread(election);
                }catch(Exception e){}
            }
        }.start();
    }

    private static class BallotResponse{
        int total_rows;
        BallotRowsResponse[] rows;
        private static class BallotRowsResponse{
            int id;
        }
        private static class ParticularBallotResponse{
            String encrypted_vote;
        }
    }
    private class KeyNotFoundException extends Exception{}

}
