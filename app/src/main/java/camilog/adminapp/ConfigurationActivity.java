package camilog.adminapp;

import android.app.Activity;
import android.content.ContentValues;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import camilog.adminapp.db.ElectionManager;
import camilog.adminapp.elections.Election;
import camilog.adminapp.elections.ElectionHolder;

/**
 * Created by stefano on 04-09-15.
 */
public class ConfigurationActivity extends Activity {
    private ElectionHolder _electionHolder;
    private Election _election;
    private ElectionManager _electionManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.configuration_layout);
        try{
            initElectionHolder();
            initElection();
            initElectionManager();
            setupLayout();
            addOnClickListeners();
        }catch(ElectionHolder.ElectionNotFoundException e){
            Log.i("jiji", "Error loading elections: " + e.getMessage());
            System.exit(1);
        }
    }

    private void setupLayout(){
        TextView tv = (TextView) findViewById(R.id.bb_server_edit);
        tv.setText(_election.getBBServer());

    }
    private void initElection() throws ElectionHolder.ElectionNotFoundException{
        _election = _electionHolder.getElectionById(getIntent().getExtras().getLong(ElectionActivity.ELECTION_INFORMATION_ID));
    }

    private void initElectionHolder(){
        _electionHolder = ElectionHolder.getElectionHolder();
    }
    private void initElectionManager(){
        _electionManager = new ElectionManager(getApplicationContext());
    }

    private void saveElectionToDb(){
        _electionManager.updateElection(_election);
    }

    private void addOnClickListeners(){
        Button saveButton = (Button) findViewById(R.id.save_button);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveElectionToDb();
            }
        });
    }
}
