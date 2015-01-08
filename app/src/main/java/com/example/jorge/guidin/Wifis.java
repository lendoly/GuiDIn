package com.example.jorge.guidin;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ListActivity;
import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.SystemClock;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

import wps.WPS;
import wps.WPSException;
import dialogs.DialogController;


public class Wifis extends ListActivity {

    private WPS wps;
    public static List<ScanResult> resultados;
    public static int elementoSelec;
    public final static int MENU_REFRESH = 1;
    public final static int MENU_INSERT = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wifis);
        cargarDatos();
    }


    private void cargarDatos(){
        if(wps==null){
            wps = new WPS(this.getApplicationContext());
        }
        refresh();
    }

    private void refresh(){
        try {
            resultados = wps.scanAndShow();
            String[] valores = new String[resultados.size()];

            for(int i = 0; i < resultados.size(); i++){
                valores[i] = resultados.get(i).SSID + " (" + resultados.get(i).BSSID + ")";
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<String>(this,
                    android.R.layout.simple_list_item_1, valores);
            this.setListAdapter(adapter);

        } catch (WPSException e) {
            e.printStackTrace();
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage(e.getMessage());
            builder.setPositiveButton("Activar", new android.content.DialogInterface.OnClickListener(){

                public void onClick(DialogInterface arg0, int arg1) {
                    WifiManager manager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
                    manager.setWifiEnabled(true);

                    int i = 0;
                    boolean b = false;
                    while(!b && i < 10){
                        b = manager.isWifiEnabled();
                        i++;
                        try {
                            SystemClock.sleep(1000);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                    if(manager.isWifiEnabled()){
                        refresh();
                    }else{
                        DialogController.createInformDialog(getContext(), "Se ha superado el tiempo de espera de activaci�n de la antena WIFI.");
                    }
                }

            });
            builder.setNegativeButton("Salir", new android.content.DialogInterface.OnClickListener(){

                public void onClick(DialogInterface dialog, int which) {
                    finish();
                }

            });
            builder.setCancelable(false);
            AlertDialog alert = builder.create();
            alert.show();
        }

    }

    public Activity getContext(){
        return this;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_wifis, menu);
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
}
