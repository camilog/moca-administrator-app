package camilog.adminapp.serverapi;

import com.google.gson.Gson;

import java.io.IOException;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by stefano on 29-09-15.
 */
public class BallotDecryptor extends AbstractBBServerTaskManager {
    public BallotDecryptor(BBServer server){
        super(server);
    }

    private PrivateKey downloadDummyShare() throws IOException {
        URL obj = new URL(_server.getAddress() + "/" + _server.getDUMMY_SHARE_SUBDOMAIN() + "/" + _server.getALL_DOCS_SUBDOMAIN());
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Content-Type", "application/json");
        int code = con.getResponseCode();
        String response = BBServer.getResponseFromInputStream(con.getInputStream());
        AllDocsResponse allDocsResponse = new Gson().fromJson(response, AllDocsResponse.class);
        AllDocsResponse.ParticularResponse[] rowsResponse = allDocsResponse.rows;
        String id = rowsResponse[0].id; // dummyShare ID
        return downloadDummyShareById(id);
    }

    private PrivateKey downloadDummyShareById(String id) throws IOException{
        URL obj = new URL(_server.getAddress() + "/" + _server.getDUMMY_SHARE_SUBDOMAIN() + "/" + id);
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("GET");
        con.setRequestProperty("Content-Type", "application/json");
        int code = con.getResponseCode();
        String response = BBServer.getResponseFromInputStream(con.getInputStream());
        PrivateKey dummySharePrivateKey = new Gson().fromJson(response, PrivateKey.class);
        return dummySharePrivateKey;
    }

    private static class AllDocsResponse{
        int total_rows;
        ParticularResponse[] rows;
        private class ParticularResponse{
            String id;
        }
    }

    private static class PrivateKey{
        BigInteger n, l, w, v, si, i;
        BigInteger[] vi;
        public PrivateKey(BigInteger[][] values) {
            vi = values[1];
            n = values[0][0];
            l = values[0][1];
            w = values[0][2];
            v = values[0][3];
            si = values[0][4];
            i = values[0][5];
        }
    }
}
