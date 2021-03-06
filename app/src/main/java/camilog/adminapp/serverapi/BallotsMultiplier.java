package camilog.adminapp.serverapi;

import android.util.Log;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;

import camilog.adminapp.elections.Election;

/**
 * Created by stefano on 22-09-15.
 */
public class BallotsMultiplier extends AbstractBBServerTaskManager{
    public BallotsMultiplier(BBServer server){
        super(server);
    }

    /**
     * Starts a thread that downloads ballots from the Bulletin Board,
     * multiplies them and uploads the result to the Bulletin Board
     * @param election
     */
    public void multiplyAndUploadBallots(final Election election){
        startMultiplyBallotsThread(election);
    }

    //TODO: Revisar por qué esto no está funcionando
    private BigInteger multiplyBallotsThread(Election election) throws IOException, KeyNotFoundException{
        BallotsAllDocsResponse.BallotRowsResponse[] ballotRowsResponse = downloadBallotsAsRows();
        BigInteger authorityPublicKey = downloadAuthorityPublicKey();
        BigInteger result = BigInteger.ONE;
        try{
            for(BallotsAllDocsResponse.BallotRowsResponse row : ballotRowsResponse){
                BigInteger encryptedVoteValue = new BigInteger(row.value);
                encryptedVoteValue = encryptedVoteValue.mod(authorityPublicKey);
                result = result.multiply(encryptedVoteValue);
                result = result.mod(authorityPublicKey);
            }
        }catch(NullPointerException e){
            Log.e("jiji", "votos invalidos");
            System.exit(1);
        }
        Log.e("jiji", "termine de multiplicar, resultado = " + String.valueOf(result));
        return result;
    }

    private BallotsAllDocsResponse.BallotRowsResponse[] downloadBallotsAsRows() throws IOException{
        String response = _server.doJSONGETRequest(_server.getAddress() + "/" + _server.getBALLOTS_LIST_SUBDOMAIN() + "/" + _server.getALL_BALLOTS_VALUES_SUBDOMAIN());
        Gson gson = new Gson();
        BallotsAllDocsResponse ballotResponse = gson.fromJson(response, BallotsAllDocsResponse.class);
        BallotsAllDocsResponse.BallotRowsResponse[] rowsResponse = ballotResponse.rows;
        return rowsResponse;
    }

    private void uploadMultipliedBallots(BigInteger multipliedBallots) throws IOException {
        URL obj = new URL(_server.getAddress() + "/" + _server.getMULTIPLIED_BALLOTS_SUBDOMAIN());
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        String urlParameters = new MultipliedBallotsMessage(multipliedBallots).toJSON();
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(urlParameters);
        wr.flush();
        wr.close();
        con.getResponseCode();
    }

    private BigInteger getAuthorityPublicKeyById(String id) throws IOException{
        String response = _server.doJSONGETRequest(_server.getAddress() + "/" + _server.getAUTHORITY_PUBLIC_KEY_SUBDOMAIN() + "/" + id);
        Log.e("jiji", "recibiendo key = " + (new Gson()).fromJson(response, AuthorityPublicKeyAllDocsResponse.AuthorityPublicKeyParticularResponse.class).value_nsplusone);
        return new BigInteger((new Gson()).fromJson(response, AuthorityPublicKeyAllDocsResponse.AuthorityPublicKeyParticularResponse.class).value_nsplusone);
    }

    private BigInteger downloadAuthorityPublicKey() throws KeyNotFoundException, IOException{
        String response = _server.doJSONGETRequest(_server.getAddress() + "/" + _server.getAUTHORITY_PUBLIC_KEY_SUBDOMAIN() + "/" + _server.getALL_DOCS_SUBDOMAIN());
        Gson gson = new Gson();
        AuthorityPublicKeyAllDocsResponse allDocsResponse = gson.fromJson(response, AuthorityPublicKeyAllDocsResponse.class);
        AuthorityPublicKeyAllDocsResponse.AuthorityPublicKeyRowsResponse[] rowsResponse = allDocsResponse.rows;
        String publicKeyId = getFirstRowId(rowsResponse);
        BigInteger publicKey = getAuthorityPublicKeyById(publicKeyId);
        if(publicKey == null)throw new KeyNotFoundException();
        return publicKey;
    }

    private String getFirstRowId(AuthorityPublicKeyAllDocsResponse.AuthorityPublicKeyRowsResponse[] allDocsRowsResponses){
        return allDocsRowsResponses[0].id;
    }


    private void startMultiplyBallotsThread(final Election election){
        new Thread(){
            public void run(){
                try{
                    BigInteger multipliedBallots = multiplyBallotsThread(election);
                    uploadMultipliedBallots(multipliedBallots);
                }catch(Exception e){}
            }
        }.start();
    }

    private static class AuthorityPublicKeyAllDocsResponse{
        int total_rows;
        AuthorityPublicKeyRowsResponse[] rows;
        private class AuthorityPublicKeyRowsResponse{
            String id;
        }
        private class AuthorityPublicKeyParticularResponse{
            String value_nsplusone;
        }
    }
    private static class BallotsAllDocsResponse{
        int total_rows;
        BallotRowsResponse[] rows;
        private class BallotRowsResponse{
            String value;
        }
    }

    private static class MultipliedBallotsMessage{
        Parameters multiplied_ballots;
        private class Parameters{
            String value;
            public Parameters(String value){
                this.value = value;
            }
        }
        public MultipliedBallotsMessage(BigInteger multipliedBallots){
            multiplied_ballots = new Parameters(multipliedBallots.toString());
        }
        public String toJSON(){
            return new Gson().toJson(this);
        }
    }
    private class KeyNotFoundException extends Exception{}

}
