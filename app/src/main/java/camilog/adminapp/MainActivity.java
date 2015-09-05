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


public class MainActivity extends Activity {
    public static final String ELECTION_INFORMATION_NAME = "camilog.adminapp.ELECTION_INFORMATION_NAME";
    private ArrayList<Election> _elections;
    private ListView candidatesListView;
    private ElectionManager electionManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setElectionManager();
        initViews();
        addOnClickListeners();
        populateElections();

        //TODO: MUY IMPORTANTE: NECESITO ASOCIARLE LA ID A CADA ELECTION EN RUNTIME PARA PODER PEDIR LOS CANDIDATOS!! puedo ocupar la misma id que genera la DB
        //
    }

    private void setElectionManager(){
        electionManager = new ElectionManager(this);
    }

    private void goToElection(Election election){
        Bundle electionInformation = new Bundle();
        electionInformation.putString(ELECTION_INFORMATION_NAME, election.getElectionName());
        Intent intent = new Intent(this, ElectionActivity.class);
        intent.putExtras(electionInformation);
        startActivity(intent);
    }

    private void populateElections(){
        _elections = new ArrayList<>();
        ElectionSqlHelper.ElectionCursor cursor = electionManager.getAllElections();
        fillElectionsWithCursor(cursor);
        ArrayAdapter<Election> arrayAdapter = new ArrayAdapter<Election>(this, android.R.layout.simple_list_item_1, _elections);
        candidatesListView.setAdapter(arrayAdapter);
    }

    private void fillElectionsWithCursor(ElectionSqlHelper.ElectionCursor cursor){
        while(cursor.moveToNext()){
            _elections.add(cursor.getElection());
        }
    }

    private void addOnClickListeners(){
        candidatesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                goToElection( (Election) (adapterView.getItemAtPosition(i)));
            }
        });
    }

    private void initViews(){
        candidatesListView = (ListView) findViewById(R.id.elections_listview);
    }
}
