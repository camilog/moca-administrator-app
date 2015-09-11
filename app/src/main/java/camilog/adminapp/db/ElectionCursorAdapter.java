package camilog.adminapp.db;

import android.content.Context;
import android.database.Cursor;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.TextView;

import camilog.adminapp.elections.Election;

/**
 * Created by stefano on 04-09-15.
 */
public class ElectionCursorAdapter extends CursorAdapter {
    private ElectionSqlHelper.ElectionCursor _electionCursor;
    public ElectionCursorAdapter(Context context, ElectionSqlHelper.ElectionCursor cursor){
        super(context, cursor, 0);
        _electionCursor = cursor;
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        LayoutInflater inflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        return inflater.inflate(android.R.layout.simple_list_item_1, parent, false);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        Election election = _electionCursor.getElection();
        TextView textView = (TextView)view;
        textView.setText(election.getElectionName());
    }
}
