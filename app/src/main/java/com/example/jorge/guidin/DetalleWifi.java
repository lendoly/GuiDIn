package com.example.jorge.guidin;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.net.wifi.ScanResult;
import android.widget.TextView;
import android.widget.Button;
import android.view.View.OnClickListener;


public class DetalleWifi extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scan_result);

        ScanResult resultado =  Wifis.resultados.get(Wifis.elementoSelec);

        TextView ssid = (TextView)findViewById(R.id.TextViewSSID2);
        ssid.setText(resultado.SSID);

        TextView bssid = (TextView)findViewById(R.id.TextViewBSSID2);
        bssid.setText(resultado.BSSID);

        TextView frequency = (TextView)findViewById(R.id.TextViewFrecuencia2);
        frequency.setText("" + resultado.frequency);

        TextView level = (TextView)findViewById(R.id.TextViewPotencia2);
        level.setText("" + resultado.level);

        Button b = (Button)findViewById(R.id.buttonBack);
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


        return super.onOptionsItemSelected(item);
    }
}
