package com.example.jorge.guidin;

import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.os.Build;
import android.widget.TextView;
import android.net.wifi.ScanResult;
import android.widget.Button;
import android.view.View.OnClickListener;


public class ScanResultActivity extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_result);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, new PlaceholderFragment())
                    .commit();
        }

        this.setContentView(R.layout.activity_scan_result);

        ScanResult resultado =  Wifis.resultados.get(Wifis.elementoSelec);

        TextView ssid = (TextView)findViewById(R.id.ssid);
        ssid.setText(resultado.SSID);

        TextView bssid = (TextView)findViewById(R.id.bssid);
        bssid.setText(resultado.BSSID);

        TextView frequency = (TextView)findViewById(R.id.frequency);
        frequency.setText("" + resultado.frequency);

        TextView level = (TextView)findViewById(R.id.level);
        level.setText("" + resultado.level);

        Button b = (Button)findViewById(R.id.ScanResVolver);
        b.setOnClickListener(new OnClickListener(){

            public void onClick(View arg0) {
                finish();
            }
        });
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_scan_result, menu);
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

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_scan_result, container, false);
            return rootView;
        }
    }
}
