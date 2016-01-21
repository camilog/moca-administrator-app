package camilog.adminapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import camilog.adminapp.db.ElectionManager;
import camilog.adminapp.elections.Candidate;
import camilog.adminapp.elections.Election;
import camilog.adminapp.elections.ElectionHolder;
import camilog.adminapp.serverapi.BBServer;

/**
 * Created by stefano on 04-09-15.
 * Modified by diego on 19-01-16.
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
        setContentView(R.layout.new_election_layoutv2);
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

        //Added button that checks integrity of BB before creating a new election
        Button checkBBButton = (Button) findViewById(R.id.check_BB_button);
        checkBBButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(getApplicationContext(), "Checking BB...", Toast.LENGTH_SHORT).show();
                new Thread() {
                    public void run() {
                        checkBBButtonClick();
                    }
                }.start();
            }
        });
    }

    private void initTempCandidates() {
        _temporaryCandidates = new ArrayList<>();
    }

    private void initElectionManager() {
        _electionManager = new ElectionManager(getApplicationContext());
    }

    private void initElectionHolder() {
        _electionHolder = ElectionHolder.getElectionHolder(_electionManager);
    }

    private void addElectionToHolder(Election election) {
        _electionHolder.addElection(election);
    }

    private void saveElectionToDatabase(Election election) {
        _electionManager.insertElection(election);
    }

    private void saveAll(Election election) {
        addElectionToHolder(election);
        saveElectionToDatabase(election);
        this.finish();
    }

    private void addCandidateButtonClick() {
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

    private void addElectionButtonClick() {
        String bb_server = retrieveBBServer();
        String election_name = retrieveElectionName();
        Election election = new Election(election_name, bb_server);
        election.addListOfCandidates(_temporaryCandidates);
        saveAll(election);
    }

    private void checkBBButtonClick() {
        String bb_server = retrieveBBServer();
        BBServer server = new BBServer(bb_server);
        final boolean ok = getBBStatus(server);
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                changeUI(ok);
            }
        });
    }

    //Displays an error message if BB is not correct, otherwise it reveals options to create an election
    private void changeUI(boolean serverState) {
        if (!serverState) {
            Toast toast = Toast.makeText(getApplicationContext(), "BB not built correctly! Fix and try again.", Toast.LENGTH_LONG);
            toast.setGravity(Gravity.TOP, Gravity.CENTER_HORIZONTAL, 500);
            toast.show();
        } else {
            Toast toast = Toast.makeText(getApplicationContext(), "BB ok!", Toast.LENGTH_SHORT);
            toast.setGravity(Gravity.TOP, Gravity.CENTER_HORIZONTAL, 500);
            toast.show();
            RelativeLayout hidden = (RelativeLayout) findViewById(R.id.hidden_area);
            hidden.setVisibility(View.VISIBLE);
        }
    }

    //Creates boolean array that is filled according to the success of reaching each table of the BB
    private boolean getBBStatus(BBServer server){
        String baseAddress = server.getAddress()+"/";
        boolean bbOK = true;
        boolean[] pass = new boolean[8];

        try {
            pass[0] = checkUrl(new URL(baseAddress + server.getAUTHORITY_PUBLIC_KEY_SUBDOMAIN()));
            pass[1] = checkUrl(new URL(baseAddress + server.getBALLOTS_LIST_SUBDOMAIN()));
            pass[2] = checkUrl(new URL(baseAddress + server.getCANDIDATES_LIST_SUBDOMAIN()));
            pass[3] = checkUrl(new URL(baseAddress + server.getDUMMY_SHARE_SUBDOMAIN()));
            pass[4] = checkUrl(new URL(baseAddress + server.getELECTION_RESULT_SUBDOMAIN()));
            pass[5] = checkUrl(new URL(baseAddress + server.getMULTIPLIED_BALLOTS_SUBDOMAIN()));
            pass[6] = checkUrl(new URL(baseAddress + server.getPARTIAL_DECRYPTIONS_SUBDOMAIN()));
            pass[7] = checkUrl(new URL(baseAddress + "voters_public_keys"));

            for (int i = 0;i<pass.length;i++) {
                if (!pass[i]) {
                    bbOK = false;
                    break;
                }
            }

        } catch (MalformedURLException e) {
            bbOK = false;
            e.printStackTrace();
        }
        return bbOK;
    }

    //Tries to connect to a URL of the BB and assigns boolean value depending on success
    private boolean checkUrl(URL url) {
        boolean status = true;

        try {
            HttpURLConnection con = (HttpURLConnection) url.openConnection();
            if (con.getResponseCode() != 200) {
                status = false;
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return status;
    }

    private String retrieveBBServer() {
        EditText bb_edit = (EditText) findViewById(R.id.new_election_bb_server_edit);
        return bb_edit.getText().toString();
    }

    private String retrieveElectionName() {
        EditText election_name_edit = (EditText) findViewById(R.id.new_election_name_edit);
        return election_name_edit.getText().toString();
    }
}
