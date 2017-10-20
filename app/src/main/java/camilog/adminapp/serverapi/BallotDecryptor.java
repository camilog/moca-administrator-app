package camilog.adminapp.serverapi;

import android.util.Log;

import com.google.gson.Gson;

import java.io.DataOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.SecureRandom;
import java.util.ArrayList;

import camilog.adminapp.elections.Election;
import camilog.adminapp.elections.ElectionResults;
import paillierp.PaillierThreshold;
import paillierp.PartialDecryption;
import paillierp.key.PaillierPrivateThresholdKey;

/**
 * Created by stefano on 29-09-15.
 */
public class BallotDecryptor extends AbstractBBServerTaskManager {
    public BallotDecryptor(BBServer server){
        super(server);
    }

    /**
     * Starts a thread which tries to retrieve the election results
     * and uploads them to the Bulletin Board
     * @param election
     */
    public void obtainResults(final Election election){
        startObtainResultsThread(election);
    }

    private void startObtainResultsThread(final Election election){
        new Thread(){
            @Override
            public void run(){
                try{
                    PrivateKey dummyShareKey = downloadDummyShare();
                    BigInteger multipliedBallots = downloadMultipliedBallots();
                    ArrayList<PartialDecryption> partialDecryptions = downloadPartialDecryptions();
                    PaillierThreshold dummyShare = getDummyShare(dummyShareKey);
                    PartialDecryption dummySharePartialDecryption = dummyShare.decrypt(multipliedBallots);
                    partialDecryptions.add(dummySharePartialDecryption);
                    PartialDecryption[] partialDecryptionsArray = ArrayListToArray(partialDecryptions);
                    BigInteger sumOfVotes = dummyShare.combineShares(partialDecryptionsArray);
                    Log.e("jiji", "sum of votes : " + sumOfVotes.toString() );
                    ElectionResults finalResult = new ElectionResults(sumOfVotes, 6);// TODO: ERROR, HARDCODEADO!! CAMBIAR!!
                    int i=1;
                    for(int votes : finalResult.getResultsByCandidate()){
                        Log.e("jiji", "candidate " + String.valueOf(i++) + ", votes : " + String.valueOf(votes));
                    }
                    updateElectionResults(finalResult);
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        }.start();
    }

    //TODO:Que el json de los resultados se suba también al server correspondiente de resultados
    private void updateElectionResults(final ElectionResults results) throws IOException{
        URL obj = new URL(_server.getAddress() + "/" + _server.getELECTION_RESULT_SUBDOMAIN());
        Log.e("jiji", "address : " + _server.getAddress() + "/" + _server.getCANDIDATES_LIST_SUBDOMAIN());
        HttpURLConnection con = (HttpURLConnection) obj.openConnection();
        con.setRequestMethod("POST");
        con.setRequestProperty("Content-Type", "application/json");
        String urlParameters = new ResultsGsonAdapter(results).toJSON();
        Log.e("jiji", "quiero subir " + urlParameters);
        con.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(con.getOutputStream());
        wr.writeBytes(urlParameters);
        wr.flush();
        wr.close();
        int code = con.getResponseCode();
        Log.e("jiji", "code : , " + String.valueOf(code));

    }

    private PartialDecryption[] ArrayListToArray(ArrayList<PartialDecryption> partialDecryptions){
        PartialDecryption[] result = new PartialDecryption[partialDecryptions.size()];
        for(int i=0;i<result.length;i++){
            result[i] = partialDecryptions.get(i);
        }
        return result;
    }

    private PaillierThreshold getDummyShare(PrivateKey dummySharePrivateKey){
        return new PaillierThreshold(new PaillierPrivateThresholdKey(dummySharePrivateKey.n,
                Integer.parseInt(dummySharePrivateKey.l.toString()), Integer.parseInt(dummySharePrivateKey.w.toString()),
                dummySharePrivateKey.v, dummySharePrivateKey.vi, dummySharePrivateKey.si, Integer.parseInt(dummySharePrivateKey.i.toString()),
                new SecureRandom().nextLong()));
    }

    private BigInteger downloadMultipliedBallots() throws IOException{
        String response = _server.doJSONGETRequest(_server.getAddress() + "/"  + _server.getMULTIPLIED_BALLOTS_SUBDOMAIN() + "/" + _server.getALL_DOCS_SUBDOMAIN());
        AllDocsResponse allDocsResponse = new Gson().fromJson(response, AllDocsResponse.class);
        String id = allDocsResponse.rows[0].id;
        return downloadMultipliedBallotsById(id);
    }

    private BigInteger downloadMultipliedBallotsById(String id) throws IOException{
        String response = _server.doJSONGETRequest(_server.getAddress() + "/" + _server.getMULTIPLIED_BALLOTS_SUBDOMAIN() + "/" + id);
        MultipliedBallotsResponse multipliedBallotsResponse = new Gson().fromJson(response, MultipliedBallotsResponse.class);
        return new BigInteger(multipliedBallotsResponse.multiplied_ballots.value);
    }

    private ArrayList<PartialDecryption> downloadPartialDecryptions() throws IOException{
        ArrayList<PartialDecryption> partialDecryptions = new ArrayList<>();
        String response = _server.doJSONGETRequest(_server.getAddress() + "/" + _server.getPARTIAL_DECRYPTIONS_SUBDOMAIN() + "/" + _server.getALL_DOCS_SUBDOMAIN());
        AllDocsResponse allDocsResponse = new Gson().fromJson(response, AllDocsResponse.class);
        for(AllDocsResponse.ParticularResponse row : allDocsResponse.rows){
            partialDecryptions.add(downloadPartialDecryptionById(row.id));
        }
        return partialDecryptions;
    }

    private PartialDecryption downloadPartialDecryptionById(String id) throws IOException{
        String response = _server.doJSONGETRequest(_server.getAddress() + "/" + _server.getPARTIAL_DECRYPTIONS_SUBDOMAIN() + "/" + id);
        PartialDecryptionResponse partialDecryptionResponse = new Gson().fromJson(response, PartialDecryptionResponse.class);
        return new PartialDecryption(new BigInteger(partialDecryptionResponse.decrypted_value), partialDecryptionResponse.auth_id);
    }


    private PrivateKey downloadDummyShare() throws IOException {
        String response = _server.doJSONGETRequest(_server.getAddress() + "/" + _server.getDUMMY_SHARE_SUBDOMAIN() + "/" + _server.getALL_DOCS_SUBDOMAIN());
        AllDocsResponse allDocsResponse = new Gson().fromJson(response, AllDocsResponse.class);
        AllDocsResponse.ParticularResponse[] rowsResponse = allDocsResponse.rows;
        String id = rowsResponse[0].id; // dummyShare ID
        Log.e("jiji", "dummy share id : " + id);
        return downloadDummyShareById(id);
    }

    private PrivateKey downloadDummyShareById(String id) throws IOException{
        String response = _server.doJSONGETRequest(_server.getAddress() + "/" + _server.getDUMMY_SHARE_SUBDOMAIN() + "/" + id);
        PrivateKey dummySharePrivateKey = new Gson().fromJson(response, PrivateKey.class);
        String log = String.format("obtuve private key: %s", dummySharePrivateKey.toString());
        Log.e("jiji", log);
        return dummySharePrivateKey;
    }

    private static class AllDocsResponse{
        int total_rows;
        ParticularResponse[] rows;
        private class ParticularResponse{
            String id;
        }
    }

    private static class MultipliedBallotsResponse{
        MultipliedBallots multiplied_ballots;
        private class MultipliedBallots{
            String value;
        }
    }

    private static class PartialDecryptionResponse{
        int auth_id;
        String decrypted_value;

        @Override
        public String toString(){
            return String.format("auth_id : %d, \n decrypted_value : %s", auth_id, decrypted_value);
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
        @Override
        public String toString(){
            return String.format("\n\t n = %s,\n\t l = %s,\n\t w = %s,\n\tv = %s, \n\tsi = %s, \n\ti = %s", n,l,w,v,si,i);
        }
    }
}
