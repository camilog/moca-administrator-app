package camilog.adminapp.serverapi;

import android.util.Log;
import android.widget.Toast;

import camilog.adminapp.elections.Election;

/**
 * Created by stefano on 02-09-15.
 */
public class BBServer {
    private String _serverAddress;
    public BBServer(String address){
        _serverAddress = address;
    }

    public void uploadElection(Election election){
        Log.i("jiji", "tratando de subir esto: " + new CandidatesListGsonAdapter(election).toJSON());
    }

    public void setAddress(String serverAddress){
        _serverAddress = serverAddress;
    }
    public String getAddress(){
        return _serverAddress;
    }
}
