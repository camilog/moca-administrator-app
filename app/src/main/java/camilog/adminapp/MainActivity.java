package camilog.adminapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.util.ArrayList;

import camilog.adminapp.db.ElectionManager;
import camilog.adminapp.db.ElectionSqlHelper;
import camilog.adminapp.elections.Election;
import camilog.adminapp.elections.ElectionHolder;


public class MainActivity extends Activity {
    public static final String ELECTION_INFORMATION_NAME = "camilog.adminapp.ELECTION_INFORMATION_NAME";
    public static final String ELECTION_INFORMATION_ID = "camilog.adminapp.ELECTION_INFORMATION_ID";

    private ArrayList<Election> _elections;
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
        populateElections();
        createAndPopulateElectionHolder();
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

    private void populateElections(){
        _elections = new ArrayList<>();
        fillElectionsWithCursor(_electionManager.getAllElections());
        configureElectionsAdapter();
    }

    private void createAndPopulateElectionHolder(){
        _electionHolder = ElectionHolder.getElectionHolder();
        _electionHolder.addListOfElections(_elections);
    }

    private void configureElectionsAdapter(){
        ArrayAdapter<Election> arrayAdapter = new ArrayAdapter<Election>(this, android.R.layout.simple_list_item_1, _elections);
        _electionsListView.setAdapter(arrayAdapter);
    }

    private void fillElectionsWithCursor(ElectionSqlHelper.ElectionCursor cursor){
        while(cursor.moveToNext()){
            _elections.add(cursor.getElection());
        }
    }

    private void addOnClickListeners(){
        _electionsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                goToElection(((Election) (adapterView.getItemAtPosition(i))).getDB_ID());
            }
        });
    }

    private void initViews(){
        _electionsListView = (ListView) findViewById(R.id.elections_listview);
    }
}
