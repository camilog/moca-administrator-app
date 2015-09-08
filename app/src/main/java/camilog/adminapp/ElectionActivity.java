package camilog.adminapp;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import camilog.adminapp.db.ElectionManager;
import camilog.adminapp.db.ElectionSqlHelper;
import camilog.adminapp.elections.Election;

/**
 * Created by stefano on 04-09-15.
 */
public class ElectionActivity extends Activity {
    private ElectionManager _electionManager;
    private Election _election;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.election_layout);
        initElectionManager();
        initializeElection();
        setUpElectionLayout();
    }

    private void initElectionManager(){
        _electionManager = new ElectionManager(getApplicationContext());
    }

    private void initializeElection(){
        long id = getIntent().getExtras().getLong(MainActivity.ELECTION_INFORMATION_ID);
        ElectionSqlHelper.ElectionCursor cursor = _electionManager.getElectionById(id);
        while(cursor.moveToNext()){
            _election = cursor.getElection();
        }
    }

    private void setUpElectionLayout(){
        setElectionName();
        addElectionCandidates();
    }

    private void setElectionName(){
        TextView tv = (TextView) findViewById(R.id.election_name_text);
        tv.setText( _election.getElectionName() );
    }

    private void addElectionCandidates(){
        //TODO:
    }
}
