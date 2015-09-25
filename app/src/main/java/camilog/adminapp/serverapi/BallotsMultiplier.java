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
    public void multiplyAndUploadBallots(final Election election){
        startMultiplyBallotsThread(election);
    }

    private BigInteger multiplyBallotsThread(Election election) throws IOException, KeyNotFoundException{
        BallotsAllDocsResponse.BallotRowsResponse[] ballotRowsResponse = downloadBallotsAsRows();
        BigInteger authorityPublicKey = downloadAuthorityPublicKey();
        BigInteger result = BigInteger.ONE;
        for(BallotsAllDocsResponse.BallotRowsResponse row : ballotRowsResponse){
            BigInteger encryptedVoteValue = new BigInteger(row.value);
            encryptedVoteValue = encryptedVoteValue.mod(authorityPublicKey);
            result = result.multiply(encryptedVoteValue);
            result = result.mod(authorityPublicKey);
        }
        Log.i("jiji", "termine de multplicar, resultado = " + String.valueOf(result));
        return result;
    }

    private BallotsAllDocsResponse.BallotRowsResponse[] downloadBallotsAsRows() throws IOException{
        Log.i("jiji", "voy a intentar conectarme");
        Log.i("jiji", "adress = " + _server.getAddress() + "/" + _server.getBALLOTS_LIST_SUBDOMAIN() + "/" + _server.getALL_BALLOTS_VALUES_SUBDOMAIN());
        URL obj = new URL(_server.getAddress() + "/" + _server.getBALLOTS_LIST_SUBDOMAIN() + "/" + _server.getALL_BALLOTS_VALUES_SUBDOMAIN());
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Content-Type", "application/json");
        int code = con.getResponseCode();
        Log.i("jiji", "codigo conexion =  " + String.valueOf(code));
        String response = getResponseFromInputStream(con.getInputStream());
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
        String urlParameters = "{\"multiplied_ballot\":{\"value\":" + multipliedBallots.toString() + "}}";
        //TODO: preguntar camilo
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(urlParameters);
        wr.flush();
        wr.close();
        con.getResponseCode();
    }

    private BigInteger getEncryptedVoteValueById(String id) throws IOException{
        URL obj = new URL(_server.getAddress() + "/" + _server.getBALLOTS_LIST_SUBDOMAIN() + "/" + id);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Content-Type", "application/json");
        con.getResponseCode();
        String response = getResponseFromInputStream(con.getInputStream());
        return new BigInteger((new Gson()).fromJson(response, BallotsAllDocsResponse.BallotRowsResponse.class).value);
    }

    private BigInteger getAuthorityPublicKeyById(String id) throws IOException{
        URL obj = new URL(_server.getAddress() + "/" + _server.getAUTHORITY_PUBLIC_KEY_SUBDOMAIN() + "/" + id);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Content-Type", "application/json");
        con.getResponseCode();
        String response = getResponseFromInputStream(con.getInputStream());
        Log.i("jiji", "recibiendo key = " + (new Gson()).fromJson(response, AuthorityPublicKeyAllDocsResponse.AuthorityPublicKeyParticularResponse.class).value);
        return new BigInteger((new Gson()).fromJson(response, AuthorityPublicKeyAllDocsResponse.AuthorityPublicKeyParticularResponse.class).value);
    }

    private BigInteger downloadAuthorityPublicKey() throws KeyNotFoundException, IOException{
        BigInteger publicKey;
        URL obj = new URL(_server.getAddress() + "/" + _server.getAUTHORITY_PUBLIC_KEY_SUBDOMAIN() + "/" + _server.getALL_DOCS_SUBDOMAIN());
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Content-Type", "application/json");
        con.getResponseCode();
        String response = getResponseFromInputStream(con.getInputStream());
        Gson gson = new Gson();
        AuthorityPublicKeyAllDocsResponse allDocsResponse = gson.fromJson(response, AuthorityPublicKeyAllDocsResponse.class);
        AuthorityPublicKeyAllDocsResponse.AuthorityPublicKeyRowsResponse[] rowsResponse = allDocsResponse.rows;
        String publicKeyId = getFirstRowId(rowsResponse);
        publicKey = getAuthorityPublicKeyById(publicKeyId);
        if(publicKey == null)throw new KeyNotFoundException();
        return publicKey;
    }

    private String getFirstRowId(AuthorityPublicKeyAllDocsResponse.AuthorityPublicKeyRowsResponse[] allDocsRowsResponses){
        return allDocsRowsResponses[0].id;
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
        return response;
    }

    private void startMultiplyBallotsThread(final Election election){
        new Thread(){
            public void run(){
                try{
                    BigInteger multipliedBallots = multiplyBallotsThread(election);
                    //uploadMultipliedBallots(multipliedBallots);
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
            String value;
        }
    }
    private static class BallotsAllDocsResponse{
        int total_rows;
        BallotRowsResponse[] rows;
        private class BallotRowsResponse{
            String value;
        }
    }
    private class KeyNotFoundException extends Exception{}

}
