package camilog.adminapp;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;

import camilog.adminapp.db.ElectionManager;
import camilog.adminapp.elections.Candidate;
import camilog.adminapp.elections.Election;
import camilog.adminapp.elections.ElectionHolder;
import camilog.adminapp.serverapi.CandidatesListGsonAdapter;

/**
 * Created by stefano on 04-09-15.
 * Modified by diego on 21-01-16.
 */
public class ElectionActivity extends Activity {
    public static final String ELECTION_INFORMATION_ID = "camilog.adminapp.ELECTION_INFORMATION_ID";

    private ElectionManager _electionManager;
    private Election _election;
    private ElectionHolder _electionHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.election_layoutv3);
        try{
            initElectionManager();
            initElectionHolder();
            initializeElection();
            addOnClickListeners();
        }catch(ElectionHolder.ElectionNotFoundException e){
            Log.i("jiji", "Error loading elections: " + e.getMessage());
            System.exit(1);
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        setUpElectionLayout();
    }

    private void initElectionManager(){
        _electionManager = new ElectionManager(getApplicationContext());
    }

    private void initElectionHolder(){
        _electionHolder = ElectionHolder.getElectionHolder(_electionManager);
    }

    private void initializeElection() throws ElectionHolder.ElectionNotFoundException{
        long id = getIntent().getExtras().getLong(MainActivity.ELECTION_INFORMATION_ID);
        _election = _electionHolder.getElectionById(id);
    }

    private void setUpElectionLayout(){
        setElectionName();
        addElectionCandidates();
    }

    private void setElectionName(){
        TextView tv = (TextView) findViewById(R.id.election_name_text);
        tv.setText(_election.getElectionName());
    }

    private void addElectionCandidates(){
        ListView candidatesListView = (ListView)findViewById(R.id.new_candidates_listview);
        if(candidatesListView == null)System.exit(1);
        ArrayAdapter<Candidate> arrayAdapter = new ArrayAdapter<Candidate>(this, android.R.layout.simple_list_item_1, _election.getCandidates());
        candidatesListView.setAdapter(arrayAdapter);
    }

    private void startConfigurationActivity(){
        Intent intent = new Intent(this, ConfigurationActivity.class);
        Bundle electionInformation = new Bundle();
        electionInformation.putLong(ELECTION_INFORMATION_ID, _election.getDB_ID());
        intent.putExtras(electionInformation);
        startActivity(intent);
    }

    //Fetches the authority key and candidate list information from the apps directory to start the qr code creation
    //TODO:Implementar caso donde falten alguno de los dos documentos
    private void startQRActivity(){
        Context c = getApplicationContext();
        File publicKeyDir = c.getDir("publicAuthKey", Context.MODE_PRIVATE);
        File candidateListDir = c.getDir("candidateList",Context.MODE_PRIVATE);
        File publicKeyFile = new File (publicKeyDir, "publicAuthKey.key");
        File candidateListFile = new File(candidateListDir, "candidateList.json");

        String publicAuthKey = "";
        String candidateList = "";

        try{
            BufferedReader keyReader = new BufferedReader(new FileReader(publicKeyFile));
            BufferedReader jsonReader = new BufferedReader(new FileReader(candidateListFile));
            publicAuthKey = keyReader.readLine();
            candidateList = jsonReader.readLine();

        } catch (IOException e){
            e.printStackTrace();
        }

        //Added the '#' character to separate the candidate list from the key
        Intent intent = new Intent(ElectionActivity.this,ShowQRActivity.class);
        intent.putExtra("candidates",candidateList);
        intent.putExtra("publicKey",publicAuthKey);
        startActivity(intent);
    }


    private void addOnClickListeners(){
        ImageButton configButton = (ImageButton) findViewById(R.id.settings_button);
        configButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startConfigurationActivity();
            }
        });

        Button uploadButton = (Button) findViewById(R.id.upload_to_bb_button);
        uploadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Uploading election to bulletin board...", Toast.LENGTH_SHORT).show();
                _election.uploadToBBServer();
                String candidateList = new CandidatesListGsonAdapter(_election).toJSON();
                saveCandidateListCopy(candidateList);
            }
        });

        Button multiplyBallotsButton = (Button) findViewById(R.id.multiply_ballots_button);
        multiplyBallotsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                _election.multiplyBallots();
                Toast.makeText(getApplicationContext(), "Multiplying ballots...", Toast.LENGTH_SHORT).show();
            }
        });

        Button displayResultsButton = (Button) findViewById(R.id.display_results_button);
        displayResultsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                _election.obtainResults();
                Toast.makeText(getApplicationContext(), "Obtaining results...", Toast.LENGTH_SHORT).show();
            }
        });

        //Generate QR code with candidate list and public authority key
        ImageButton generateQRButton = (ImageButton) findViewById(R.id.qr_button);
        generateQRButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startQRActivity();
            }
        });
    }

    //Save a copy of the candidate list in the app directory to be used in the qr code generation
    private void saveCandidateListCopy(String candidateList){
        File candidateListDir = getApplicationContext().getDir("candidateList", Context.MODE_PRIVATE);
        File candidateListFile = new File(candidateListDir, "candidateList.json");

        try {
            if (candidateListFile.exists())
                candidateListFile.delete();

            candidateListFile.createNewFile();

            BufferedWriter writer = new BufferedWriter(new FileWriter(candidateListFile, true));
            writer.write(candidateList);
            writer.close();

            Toast toast = Toast.makeText(this, "Candidate List saved", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP, 25, 400);
            toast.show();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
