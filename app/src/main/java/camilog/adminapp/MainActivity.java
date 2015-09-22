package camilog.adminapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

import camilog.adminapp.db.ElectionManager;
import camilog.adminapp.db.ElectionSqlHelper;
import camilog.adminapp.elections.Election;
import camilog.adminapp.elections.ElectionHolder;


public class MainActivity extends Activity {
    public static final String ELECTION_INFORMATION_ID = "camilog.adminapp.ELECTION_INFORMATION_ID";
    private ListView _electionsListView;
    private ElectionManager _electionManager;
    private ElectionHolder _electionHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setElectionManager();
        initViews();
        addOnClickListeners();
        createAndPopulateElectionHolder();
        configureElectionsAdapter();
    }

    @Override
    protected void onResume() {
        super.onResume();
        ((ArrayAdapter<Election>)_electionsListView.getAdapter()).notifyDataSetChanged();
    }

    private void setElectionManager(){
        _electionManager = new ElectionManager(getApplicationContext());
    }

    private void goToElection(long electionId){
        Bundle electionInformation = new Bundle();
        electionInformation.putLong(ELECTION_INFORMATION_ID, electionId);
        Intent intent = new Intent(this, ElectionActivity.class);
        intent.putExtras(electionInformation);
        startActivity(intent);
    }

    private void createAndPopulateElectionHolder(){
        _electionHolder = ElectionHolder.getElectionHolder(_electionManager);
    }

    private void configureElectionsAdapter(){
        ArrayAdapter<Election> arrayAdapter = new ArrayAdapter<Election>(this, android.R.layout.simple_list_item_1, _electionHolder.getElectionsAsList());
        _electionsListView.setAdapter(arrayAdapter);
    }

    private void addOnClickListeners(){
        _electionsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                goToElection(((Election) (adapterView.getItemAtPosition(i))).getDB_ID());
            }
        });
        Button newElectionButton = (Button) findViewById(R.id.new_election_button);
        newElectionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startNewElectionActivity();
            }
        });
    }

    private void startNewElectionActivity(){
        Intent intent = new Intent(this, NewElectionActivity.class);
        startActivity(intent);
    }

    private void initViews(){
        _electionsListView = (ListView) findViewById(R.id.elections_listview);
    }
}
