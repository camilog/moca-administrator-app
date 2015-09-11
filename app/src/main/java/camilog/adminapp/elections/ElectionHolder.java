package camilog.adminapp.elections;

import android.util.Log;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;

import camilog.adminapp.db.ElectionManager;
import camilog.adminapp.db.ElectionSqlHelper;

/**
 * Created by stefano on 09-09-15.
 */
public class ElectionHolder {
    private static HashMap<Long, Election> _idToElection;
    private static ArrayList<Election> _electionsArrayList;
    private static ElectionHolder uniqueInstance = null;
    private ElectionManager _electionManager;
    private ElectionHolder(ElectionManager electionManager){
        _idToElection = new HashMap<>();
        _electionsArrayList = new ArrayList<>();
        _electionManager = electionManager;
        retrieveElections();
    }

    private void retrieveElections(){
        ElectionSqlHelper.ElectionCursor cursor = _electionManager.getAllElections();
        while(cursor.moveToNext()){
            addElection(cursor.getElection());
        }
    }
    public static ElectionHolder getElectionHolder(ElectionManager electionManager){
        if(uniqueInstance == null){
            uniqueInstance = new ElectionHolder(electionManager);
        }
        return uniqueInstance;
    }

    public Election getElectionById(long id) throws ElectionNotFoundException {
        Election election;
        if((election = _idToElection.get(id)) == null)throw new ElectionNotFoundException("Election not found with ID " + id);
        return election;
    }
    public void addElection(Election election){
        _electionsArrayList.add(election);
        _idToElection.put(election.getDB_ID(), election);
    }

    public Collection<Election> getAllElections(){
        return _idToElection.values();
    }

    public ArrayList<Election> getElectionsAsList(){
        return _electionsArrayList;
    }

    public class ElectionNotFoundException extends Exception{
        public ElectionNotFoundException(String s){super(s);}
    }

}

