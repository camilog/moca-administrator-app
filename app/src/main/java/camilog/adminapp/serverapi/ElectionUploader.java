package camilog.adminapp.serverapi;

import android.util.Log;

import java.io.DataOutputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import camilog.adminapp.elections.Election;

/**
 * Created by stefano on 22-09-15.
 */
public class ElectionUploader extends AbstractBBServerTaskManager {
    public ElectionUploader(BBServer server){
        super(server);
    }

    private void startUploadThread(Election election) throws IOException {
        URL obj = new URL(_server.getAddress() + "/" + _server.getCANDIDATES_LIST_SUBDOMAIN());
        Log.e("jiji", "address : " + _server.getAddress() + "/" + _server.getCANDIDATES_LIST_SUBDOMAIN());
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        String urlParameters = new CandidatesListGsonAdapter(election).toJSON();
        Log.e("jiji", "quiero subir " + urlParameters);
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(urlParameters);
        wr.flush();
        wr.close();
        int code = con.getResponseCode();
        Log.e("jiji", "code : , " + String.valueOf(code));
        //uploadToResultsServer(_server, election);
    }

    //TODO: Falta probar esto cuando se tenga definido dónde se van a subir los resultados
    private void uploadToResultsServer(BBServer server, Election e) throws IOException{
        URL res = new URL(server.getResultAddress());
        HttpURLConnection conn = (HttpURLConnection) res.openConnection();
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");
        String urlParam = new CandidatesListGsonAdapter(e).toJSON();
        conn.setDoOutput(true);
        DataOutputStream dos = new DataOutputStream(conn.getOutputStream());
        dos.writeBytes(urlParam);
        dos.flush();
        dos.close();
        int code = conn.getResponseCode();
        Log.e("jiji", "results code : , " + String.valueOf(code));
    }

    /**
     * Starts a thread that tries to upload the candidates of the election to the
     * Bulletin Board
     * @param election
     */
    public void uploadCandidates(final Election election){
        new Thread() {
            public void run() {
                try {
                    startUploadThread(election);
                } catch (Exception e) {}
            }
        }.start();
    }
}
