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

    /**
     *
     * @param electionManager
     * @return an instance of the ElectionHolder object
     */
    public static ElectionHolder getElectionHolder(ElectionManager electionManager){
        if(uniqueInstance == null){
            uniqueInstance = new ElectionHolder(electionManager);
        }
        return uniqueInstance;
    }

    /**
     * Retrieves the election associated to an id
     * @param id
     * @return the {@link Election} object
     * @throws ElectionNotFoundException
     */
    public Election getElectionById(long id) throws ElectionNotFoundException {
        Election election;
        if((election = _idToElection.get(id)) == null)throw new ElectionNotFoundException("Election not found with ID " + id);
        return election;
    }

    /**
     * Adds an {@link Election} to the {@link ElectionHolder}
     * @param election
     */
    public void addElection(Election election){
        _electionsArrayList.add(election);
        _idToElection.put(election.getDB_ID(), election);
    }

    /**
     * Removes en election from the holder
     * @param election
     */
    public void removeElection(Election election){
        _idToElection.remove(election);
        _electionsArrayList.remove(election);
    }

    /**
     *
     * @return a {@link Collection} with every election contained in the {@link ElectionHolder}
     */
    public Collection<Election> getAllElections(){
        return _idToElection.values();
    }

    /**
     *
     * @return an {@link ArrayList} containing all the elections in the holder
     */
    public ArrayList<Election> getElectionsAsList(){
        return _electionsArrayList;
    }

    public class ElectionNotFoundException extends Exception{
        public ElectionNotFoundException(String s){super(s);}
    }

}

