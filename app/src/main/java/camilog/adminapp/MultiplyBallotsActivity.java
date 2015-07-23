package camilog.adminapp;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import com.google.gson.Gson;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;


public class MultiplyBallotsActivity extends Activity {

    private static String bbServer = "http://cjgomez.duckdns.org:3000/ballots";
    private static String authPublicKeyServer = "http://cjgomez.duckdns.org:3000/authority_public_keys";
    private static String multBallotsServer = "http://cjgomez.duckdns.org:3000/multiplied_ballots";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_multiply_ballots);

        new Thread() {
            public void run() {
                try {
                    downloadBallots();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }.start();
    }

    private void downloadBallots() throws IOException {

        URL obj = new URL(bbServer);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        con.setRequestMethod("GET");
        con.setRequestProperty("Content-Type", "application/json");
        con.getResponseCode();

        // Receive the response
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        String jsonString = response.toString();
        Gson gson = new Gson();
        Ballot[] ballots = gson.fromJson(jsonString, Ballot[].class);
        BigInteger[] encryptedVotes = new BigInteger[ballots.length];

        int i = 0;
        for (Ballot ballot : ballots) {
            encryptedVotes[i] = new BigInteger(ballot.enc);
            i++;
        }

        BigInteger multipliedBallots = multiplyBallots(encryptedVotes);
        uploadMultipliedBallots(multipliedBallots);

    }

    private void uploadMultipliedBallots(BigInteger multipliedBallots) throws IOException {
        // Set the URL where to POST the ballot
        URL obj = new URL(multBallotsServer);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        //add request header
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");

        // Create JSON with the parameters
        String urlParameters = "{\"multiplied_ballot\":{\"value\":" + multipliedBallots.toString() + "}}";

        // Send post request
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(urlParameters);
        wr.flush();
        wr.close();
        con.getResponseCode();
    }

    private BigInteger multiplyBallots(BigInteger[] encryptedVotes) throws IOException {
        BigInteger multipliedBallots = BigInteger.ONE;

        for (BigInteger encryptedVote : encryptedVotes)
            multipliedBallots = multipliedBallots.multiply(encryptedVote);

        BigInteger nsPlusOne = downloadAuthorityPublicKey();
        return multipliedBallots.mod(nsPlusOne);
    }

    private BigInteger downloadAuthorityPublicKey() throws IOException {
        BigInteger authPublicKey;

        URL obj = new URL(authPublicKeyServer);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();

        con.setRequestMethod("GET");
        con.setRequestProperty("Content-Type", "application/json");
        con.getResponseCode();

        // Receive the response
        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer response = new StringBuffer();
        while ((inputLine = in.readLine()) != null) {
            response.append(inputLine);
        }
        in.close();

        String jsonString = response.toString();
        Gson gson = new Gson();
        AuthorityPublicKey[] authorityPublicKeys = gson.fromJson(jsonString, AuthorityPublicKey[].class);

        authPublicKey = new BigInteger(authorityPublicKeys[0].key);

        return authPublicKey.add(BigInteger.ONE);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_multiply_ballots, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
