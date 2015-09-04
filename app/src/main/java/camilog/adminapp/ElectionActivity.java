package camilog.adminapp;

import android.app.Activity;
import android.os.Bundle;
import android.widget.TextView;

/**
 * Created by stefano on 04-09-15.
 */
public class ElectionActivity extends Activity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.election_layout);
        initViews();
    }

    private void initViews(){
        TextView tv = (TextView) findViewById(R.id.election_name_text);
        tv.setText((String) (getIntent().getExtras().get(MainActivity.ELECTION_INFORMATION_NAME)));
    }
}
