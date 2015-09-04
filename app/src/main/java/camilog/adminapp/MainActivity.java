package camilog.adminapp;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import java.util.ArrayList;

import camilog.adminapp.elections.Election;


public class MainActivity extends Activity {
    public static final String ELECTION_INFORMATION_NAME = "camilog.adminapp.ELECTION_INFORMATION_NAME";
    private ArrayList<Election> _elections;
    private ListView candidatesListView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        addOnClickListeners();
        _elections = new ArrayList<>();
        _elections.add(new Election("Color favorito?")); // TEMPORAL!!
        _elections.add(new Election("Animal favorito?"));
        //TODO: Las elecciones tienen que ser obtenidas y agregadas desde una base de datos
        ArrayAdapter<Election> arrayAdapter = new ArrayAdapter<Election>(this, android.R.layout.simple_list_item_1, _elections);
        candidatesListView.setAdapter(arrayAdapter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void goToElection(Election election){
        Bundle electionInformation = new Bundle();
        electionInformation.putString(ELECTION_INFORMATION_NAME, election.getElectionName());
        Intent intent = new Intent(this, ElectionActivity.class);
        intent.putExtras(electionInformation);
        startActivity(intent);
    }

    private void addOnClickListeners(){
        if(candidatesListView == null)return;
        candidatesListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                goToElection( (Election) (adapterView.getItemAtPosition(i)));
            }
        });
    }

    private void initViews(){
        candidatesListView = (ListView) findViewById(R.id.elections_listview);
        if(candidatesListView == null)System.exit(0);
    }
}
