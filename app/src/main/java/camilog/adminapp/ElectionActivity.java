package camilog.adminapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import camilog.adminapp.db.ElectionManager;
import camilog.adminapp.elections.Candidate;
import camilog.adminapp.elections.Election;
import camilog.adminapp.elections.ElectionHolder;

/**
 * Created by stefano on 04-09-15.
 */
public class ElectionActivity extends Activity {
    public static final String ELECTION_INFORMATION_ID = "camilog.adminapp.ELECTION_INFORMATION_ID";

    private ElectionManager _electionManager;
    private Election _election;
    private ElectionHolder _electionHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.election_layout);
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
        _electionHolder = ElectionHolder.getElectionHolder();
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

    private void addOnClickListeners(){
        ImageButton configButton = (ImageButton) findViewById(R.id.settings_button);
        configButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startConfigurationActivity();
            }
        });
    }
}
