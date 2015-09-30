package camilog.adminapp.db;

import android.content.Context;

import camilog.adminapp.elections.Election;

/**
 * Created by stefano on 04-09-15.
 */
public class ElectionManager {
    private ElectionSqlHelper _helper;
    private Context _context;

    public ElectionManager(Context context){
        _context = context;
        _helper = ElectionSqlHelper.getElectionSqlHelper(context);
    }

    /**
     *
     * @return ElectionCursor pointing at the beggining of an "all elections" response
     */
    public ElectionSqlHelper.ElectionCursor getAllElections(){
        return _helper.queryAllElections();
    }

    /**
     *
     * @return ElectionCursor pointing at the beggining of an "election by id" response
     */
    public ElectionSqlHelper.ElectionCursor getElectionById(long id){
        return _helper.queryElectionById(id);
    }

    /**
     * Inserts election e into internal database
     * @param e the election to be inserted
     */
    public void insertElection(Election e){
        _helper.insertElection(e);
    }

    /**
     * Updates election e inside internal database
     * @param e
     */
    public void updateElection(Election e){
        _helper.updateElection(e);
    }
}
