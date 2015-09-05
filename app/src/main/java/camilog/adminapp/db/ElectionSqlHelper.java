package camilog.adminapp.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.CursorWrapper;
import android.database.DatabaseErrorHandler;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

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

    public ElectionSqlHelper(Context context){
        super(context, DB_NAME, null, VERSION);
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

    public ElectionCursor queryAllElections(){
        Cursor cursor = getReadableDatabase().query(TABLE_ELECTIONS,null, null, null, null, null, null);
        return new ElectionCursor(cursor);
    }


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
            return election;
        }
    }
}
