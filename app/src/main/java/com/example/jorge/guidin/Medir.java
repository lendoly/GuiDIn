package com.example.jorge.guidin;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.SystemClock;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.net.UnknownHostException;
import java.util.List;

import org.apache.http.client.ClientProtocolException;

import com.example.jorge.guidin.dialogs.DialogController;
import com.example.jorge.guidin.http.HttpServices;
import com.example.jorge.guidin.wps.WPS;
import com.example.jorge.guidin.wps.WPSException;




public class Medir extends ActionBarActivity {

    private WPS wps;
    public static List<ScanResult> resultados;
    public static int elementoSelec;
    public final static int MENU_REFRESH = 1;
    public final static int MENU_INSERT = 2;
    private Double xActual, yActual, zActual, xUltima, yUltima, zUltima;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medir);

        if(wps==null) wps = new WPS(this.getApplicationContext());

        xActual=yActual=zActual=xUltima=yUltima=zUltima=0.0;
        actualizarInfo();

        Button aceptar = (Button) findViewById(R.id.aceptarMedir);
        Button moreX = (Button)findViewById(R.id.xMore);
        Button moreY = (Button)findViewById(R.id.yMore);
        Button moreZ = (Button)findViewById(R.id.zMore);
        Button lessX = (Button)findViewById(R.id.xLess);
        Button lessY = (Button)findViewById(R.id.yLess);
        Button lessZ = (Button)findViewById(R.id.zLess);



        aceptar.setOnClickListener(new android.view.View.OnClickListener(){
            public void onClick(View arg0) {
                refresh();
                HttpServices service = new HttpServices();
                try {
                    if(service.ping()){


                        boolean error = false;
                        for(int i = 0; i < resultados.size(); i++){
                            try {
                                service.insertCoorDatabase(xActual, yActual, zActual, resultados.get(i).BSSID, resultados.get(i).level);
                            } catch (ClientProtocolException e) {
                                e.printStackTrace();
                                error = true;
                            } catch (IOException e) {
                                e.printStackTrace();
                                error = true;
                            }
                        }
                        if(error){
                            DialogController.createInformDialog(getContext(), "Algunos datos no se han podido insertar.");
                        }else{
                            xUltima=xActual;
                            yUltima=yActual;
                            zUltima=zActual;
                            actualizarInfo();
                            DialogController.createInformDialog(getContext(), "Todos los datos han sido insertados correctamente.");
                        }


                    }else{
                        DialogController.createInformDialog(getContext(), "Servidor inaccesible.");
                    }
                } catch (UnknownHostException e) {
                    e.printStackTrace();
                    DialogController.createInformDialog(getContext(), "Servidor inaccesible.");
                } catch (IOException e) {
                    e.printStackTrace();
                    DialogController.createInformDialog(getContext(), "Servidor inaccesible.");
                }
            }

        });

        moreX.setOnClickListener(new android.view.View.OnClickListener(){
            public void onClick(View arg0) {
                xActual=xActual+0.5;
                actualizarInfo();
            }
        });
        moreY.setOnClickListener(new android.view.View.OnClickListener(){
            public void onClick(View arg0) {
                yActual=yActual+0.5;
                actualizarInfo();
            }
        });
        moreZ.setOnClickListener(new android.view.View.OnClickListener(){
            public void onClick(View arg0) {
                zActual=zActual+0.5;
                actualizarInfo();
            }
        });
        lessX.setOnClickListener(new android.view.View.OnClickListener(){
            public void onClick(View arg0) {
                xActual=xActual-0.5;
                actualizarInfo();
            }
        });
        lessY.setOnClickListener(new android.view.View.OnClickListener(){
            public void onClick(View arg0) {
                yActual=yActual-0.5;
                actualizarInfo();
            }
        });
        lessZ.setOnClickListener(new android.view.View.OnClickListener(){
            public void onClick(View arg0) {
                zActual=zActual-0.5;
                actualizarInfo();
            }
        });

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_medir, menu);
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

    private void actualizarInfo(){
        TextView tvX= (TextView) findViewById(R.id.textViewX);
        TextView tvY= (TextView) findViewById(R.id.textViewY);
        TextView tvZ= (TextView) findViewById(R.id.textViewZ);
        tvX.setText("X: actual("+xActual.toString()+") última ("+xUltima.toString()+")");
        tvY.setText("Y: actual("+yActual.toString()+") última ("+yUltima.toString()+")");
        tvZ.setText("Z: actual("+zActual.toString()+") última ("+zUltima.toString()+")");
    }



    private void refresh(){
        try {
            resultados = wps.scanAndShow();
            String[] valores = new String[resultados.size()];

            for(int i = 0; i < resultados.size(); i++){
                valores[i] = resultados.get(i).SSID + " (" + resultados.get(i).BSSID + ")";
            }


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
                        DialogController.createInformDialog(getContext(), "Se ha superado el tiempo de espera de activación de la antena WIFI.");
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


}
