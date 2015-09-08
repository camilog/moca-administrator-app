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

    public ElectionSqlHelper.ElectionCursor getAllElections(){
        return _helper.queryAllElections();
    }

    public ElectionSqlHelper.ElectionCursor getElectionById(long id){
        return _helper.queryElectionById(id);
    }

    public void insertElection(Election e){
        _helper.insertElection(e);
    }
}
