package camilog.adminapp.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import camilog.adminapp.MainActivity;
import camilog.adminapp.elections.Candidate;
import camilog.adminapp.elections.Election;

/**
 * Created by stefano on 04-09-15.
 */
public class ElectionSqlHelper extends SQLiteOpenHelper {
    private static final String DB_NAME = "adminapp.sqlite";
    private static final int VERSION = 3;

    private static final String TABLE_ELECTIONS = "elections";
    private static final String COLUMN_ELECTION_NAME = "name";
    private static final String COLUMN_CANDIDATES = "candidates";
    private static final String COLUMN_BBSERVER = "server";
    private static final String COLUMN_ID = "_id";

    private static ElectionSqlHelper uniqueInstance = null;

    private ElectionSqlHelper(Context context){
        super(context, DB_NAME, null, VERSION);
    }

    /**
     *
     * @param context
     * @return an instance of an ElectionSqlHelper
     */
    public static ElectionSqlHelper getElectionSqlHelper(Context context){
        if(uniqueInstance==null){
            uniqueInstance = new ElectionSqlHelper(context);
        }
        return uniqueInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String sqlQuery = String.format("CREATE TABLE %s (_id INTEGER PRIMARY KEY, %s TEXT, %s TEXT, %s TEXT)" , TABLE_ELECTIONS, COLUMN_ELECTION_NAME, COLUMN_CANDIDATES, COLUMN_BBSERVER);
        db.execSQL(sqlQuery);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {}
    /**
     *
     * @param election : the election to insert
     * @return the row id where the insertion ocurred
     */
    public long insertElection(Election election){
        ContentValues values = new ContentValues();
        values.put(COLUMN_ELECTION_NAME, election.getElectionName());
        values.put(COLUMN_CANDIDATES, getElectionCandidatesAsCsv(election));
        values.put(COLUMN_BBSERVER, election.getBBServer());
        return getWritableDatabase().insert(TABLE_ELECTIONS, null, values);
    }

    /**
     *
     * @param election
     * @return a String associated
     * to the election formatted as "candidate1,...,candidaten", for example, "dog,cat,chicken"
     */
    private String getElectionCandidatesAsCsv(Election election){
        ArrayList<Candidate> candidates = election.getCandidates();
        StringBuilder stringBuilder = new StringBuilder();
        boolean firstElement = true;
        for(Candidate candidate : candidates){
            String candidateName = candidate.getName();
            if(firstElement){
                stringBuilder.append(candidateName);
                firstElement = false;
            }else{
                stringBuilder.append(",");
                stringBuilder.append(candidateName);
            }
        }
        return stringBuilder.toString();
    }

    /**
     *
     * @return a Cursor which goes through every Election
     */
    public ElectionCursor queryAllElections(){
        Cursor cursor = getReadableDatabase().query(TABLE_ELECTIONS,null, null, null, null, null, null);
        return new ElectionCursor(cursor);
    }

    /**
     *
     * @param id
     * @return a Cursor pointing to a specific Election
     */
    public ElectionCursor queryElectionById(long id){
        Cursor cursor = getReadableDatabase().query(TABLE_ELECTIONS,null, "_id = ?", new String[]{String.valueOf(id)}, null,null,null);
        return new ElectionCursor(cursor);
    }

    /**
     * Updates the election in the internal database
     * @param e
     */
    public void updateElection(Election e){
        ContentValues cv = new ContentValues();
        cv.put(COLUMN_BBSERVER, e.getBBServer());
        cv.put(COLUMN_CANDIDATES, getElectionCandidatesAsCsv(e));
        getWritableDatabase().update(TABLE_ELECTIONS, cv, "_id = " + e.getDB_ID(), null);
    }


    /**
     * Cursor wrapper for elections
     */
    public static class ElectionCursor extends CursorWrapper{
        public ElectionCursor(Cursor cursor){
            super(cursor);
        }
        public Election getElection(){
            if(isBeforeFirst() || isAfterLast())return null;
            Election election;
            String electionName = getString(getColumnIndex(COLUMN_ELECTION_NAME));
            String bb_server = getString(getColumnIndex(COLUMN_BBSERVER));
            String[] candidatesNames = getString(getColumnIndex(COLUMN_CANDIDATES)).split(",");
            election = new Election(electionName, bb_server);
            election.addListOfCandidatesByName(candidatesNames);
            election.setDB_ID(getInt(getColumnIndex(COLUMN_ID)));
            return election;
        }
    }
}
