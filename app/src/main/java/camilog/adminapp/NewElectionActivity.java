package camilog.adminapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

import camilog.adminapp.db.ElectionManager;
import camilog.adminapp.elections.Candidate;
import camilog.adminapp.elections.Election;
import camilog.adminapp.elections.ElectionHolder;

/**
 * Created by stefano on 04-09-15.
 */
public class NewElectionActivity extends Activity {
    private ArrayList<Candidate> _temporaryCandidates;
    private ElectionManager _electionManager;
    private ElectionHolder _electionHolder;
    private final String addCandidateTitle = "Add candidate";
    private final String addCandidateNameMessage = "Candidate name";
    private ArrayAdapter<Candidate> candidatesAdapter;
    private ListView candidatesListView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_election_layout);
        initElectionManager();
        initElectionHolder();
        initTempCandidates();
        configureListViewAdapter();
        addOnClickListeners();
    }

    private void configureListViewAdapter(){
        candidatesListView = (ListView) findViewById(R.id.new_candidates_listview);
        candidatesAdapter = new ArrayAdapter<Candidate>(this, android.R.layout.simple_list_item_1, _temporaryCandidates);
        candidatesListView.setAdapter(candidatesAdapter);
    }

    private void addOnClickListeners(){
        Button addCandidateButton = (Button) findViewById(R.id.new_election_add_candidate_button);
        addCandidateButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addCandidateButtonClick();
            }
        });
        Button addElectionButton = (Button) findViewById(R.id.create_election_button);
        addElectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addElectionButtonClick();
            }
        });
    }

    private void initTempCandidates(){
        _temporaryCandidates = new ArrayList<>();
    }
    private void initElectionManager(){
        _electionManager = new ElectionManager(getApplicationContext());
    }
    private void initElectionHolder(){
        _electionHolder = ElectionHolder.getElectionHolder(_electionManager);
    }
    private void addElectionToHolder(Election election){
        _electionHolder.addElection(election);
    }
    private void saveElectionToDatabase(Election election){
        _electionManager.insertElection(election);
    }
    private void saveAll(Election election){
        addElectionToHolder(election);
        saveElectionToDatabase(election);
        this.finish();
    }

    private void addCandidateButtonClick(){
        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        final EditText candidateNameInput = new EditText(this);
        alert.setTitle(addCandidateTitle);
        alert.setMessage(addCandidateNameMessage);
        alert.setView(candidateNameInput);
        alert.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
            }
        });
        alert.setPositiveButton("Add", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                _temporaryCandidates.add(new Candidate(candidateNameInput.getText().toString()));
                candidatesAdapter.notifyDataSetChanged();
            }
        });
        alert.show();
    }

    private void addElectionButtonClick(){
        String bb_server = retrieveBBServer();
        String election_name = retrieveElectionName();
        Election election = new Election(election_name, bb_server);
        election.addListOfCandidates(_temporaryCandidates);
        saveAll(election);
    }

    private String retrieveBBServer(){
        EditText bb_edit = (EditText) findViewById(R.id.new_election_bb_server_edit);
        return bb_edit.getText().toString();
    }

    private String retrieveElectionName(){
        EditText election_name_edit = (EditText) findViewById(R.id.new_election_name_edit);
        return election_name_edit.getText().toString();
    }
}
