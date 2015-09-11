package camilog.adminapp.serverapi;

import android.util.Log;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import camilog.adminapp.elections.Election;

/**
 * Created by stefano on 02-09-15.
 */
public class BBServer {
    private final String CANDIDATES_LIST_SUBDOMAIN = "candidates_list";

    private String _serverAddress;
    public BBServer(String address){
        _serverAddress = address;
    }

    public void uploadElection(final Election election){
        new Thread() {
            public void run() {
                try {
                    startUploadThread(election);
                } catch (Exception e) {}
            }
        }.start();
    }

    public void setAddress(String serverAddress){
        _serverAddress = serverAddress;
    }
    public String getAddress(){
        return _serverAddress;
    }

    private void startUploadThread(Election election) throws IOException{
        URL obj = new URL(_serverAddress + "/" + CANDIDATES_LIST_SUBDOMAIN);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        String urlParameters = new CandidatesListGsonAdapter(election).toJSON();
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(urlParameters);
        wr.flush();
        wr.close();
        con.getResponseCode();
    }
}
