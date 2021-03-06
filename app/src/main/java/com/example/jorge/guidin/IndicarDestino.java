package com.example.jorge.guidin;

import android.content.Context;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.MenuItem;


import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.ExecutionException;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.wifi.WifiManager;
import android.os.SystemClock;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.speech.tts.TextToSpeech.OnInitListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.view.inputmethod.InputMethodManager;

import com.example.jorge.guidin.rutas.ListaCuadrantes;
import com.example.jorge.guidin.sockets.Client;
import com.example.jorge.guidin.wps.Coordenada;
import com.example.jorge.guidin.wps.WPS;
import com.example.jorge.guidin.wps.WPSException;



public class IndicarDestino extends ActionBarActivity implements OnInitListener, OnClickListener,SensorEventListener{

    private TextToSpeech ttobj;
    private static final int VOICE_RECOGNITION_REQUEST_CODE = 0x100;
    private static final int VOICE_DESTINO= 101;
    private static final int REQUEST_CHECK_TTS = 0x1000;
    private static final int MY_DATA_CHECK_CODE = 1234;

    private int posIni = -1;
    private float orientacion = 999;

    private ListaCuadrantes cuadrantes;
    private WifiManager manager;
    private SensorEventListener giroscopio;
    private SensorManager mSensorManager;
    private float[] magnetic_angles;
    private boolean activaWifi;

    private boolean wifiActivado;
    private boolean destinoInsertado = false;

    Button btBuscar,btVoz;
    private TextView mResult2;
    private EditText mResult1;

    //Atributo para obtener la ruta que me devuelva el servidor
    private String ruta;
    private int contador;
    private String siguientePaso;
    private int cuadranteClave;
    private int cuadranteActual;
    private String listaCuadrantesString;
    ArrayList<String> listaCuadrantes;
    private Timer timerCalcularRuta;
    private boolean timerActivado;
    private String []superables;
    private String list_supererables;
    private String discapacidad;
    final String inicio = "Ahora diga el destino al que quiere llegar";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_indicar_destino);

        Menu.activeActivity = this;

        // Fire off an intent to check if a TTS engine is installed
        Intent checkIntent = new Intent();
        checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkIntent, MY_DATA_CHECK_CODE);

        mResult1 = (EditText) findViewById(R.id.editText1);
        mResult2 = (TextView) findViewById(R.id.textView2);

        cuadrantes = new ListaCuadrantes();
        manager = (WifiManager) getSystemService(WIFI_SERVICE);
        wifiActivado = manager.isWifiEnabled();
        timerActivado = false;

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        Sensor s = mSensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER).get(0);
        Sensor s2 = mSensorManager.getSensorList(Sensor.TYPE_MAGNETIC_FIELD).get(0);
        giroscopio = new SensorEventListener() {

            public void onAccuracyChanged(Sensor sensor, int accuracy) {
            }

            public void onSensorChanged(SensorEvent event) {
                magnetic_angles = event.values;
            }

        };

        mSensorManager.registerListener(this, s, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(giroscopio, s2, SensorManager.SENSOR_DELAY_NORMAL);

        checkTTS();

        superables = Login.getSuperables();
        list_supererables="";
        for(int i = 0; i < superables.length; i++){
            if(i == 0)
                list_supererables += superables[i];
            else
                list_supererables += "," + superables[i];
        }

        discapacidad = Login.getDiscapacidad();
        cuadranteClave = -1;
        contador = 0;

        btVoz = (Button) findViewById(R.id.bVoz);

        btBuscar = (Button) findViewById(R.id.bBuscar);
        btBuscar.setOnClickListener(new OnClickListener() {

            public void onClick(View v) {
                destinoInsertado = true;
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(btBuscar.getWindowToken (), 0);
                consultarRutaServidor();
                calculaRuta();
            }

        });
    }

    public void onInit(int i) {
        if(discapacidad.equals("visual")) {
            speakText(inicio);
            reconocimientoDeVoz(VOICE_DESTINO);
        }
    }


    @Override
    public void onPause(){
        super.onPause();
    }

    public void speakText(String texto){
        //Toast.makeText(getApplicationContext(), texto, Toast.LENGTH_LONG).show();
        ttobj.speak(texto, TextToSpeech.QUEUE_FLUSH, null,"bienvenida");
        while (ttobj.isSpeaking()){}
    }

    @Override
    public void onDestroy()
    {
        mSensorManager.unregisterListener(giroscopio);
        mSensorManager.unregisterListener(this);
        super.onDestroy();
        // Don't forget to shutdown!
        if (ttobj != null)
        {
            ttobj.stop();
            ttobj.shutdown();
        }
        super.onDestroy();
    }

    public void reconocimientoDeVoz(int codigo) {
        if(!hasVoicerec()) {
            Toast.makeText(this, "Este terminal no tiene instalado el soporte de reconocimiento de voz", Toast.LENGTH_LONG).show();
            return;
        }
        final Intent voiceIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        voiceIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
        final String miPackage = getClass().getPackage().getName();
        voiceIntent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, miPackage);
        voiceIntent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Hable ahora");
        voiceIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        voiceIntent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
        startActivityForResult(voiceIntent, codigo);
    }


    public void onButtonClick(View v) {
        if(!hasVoicerec()) {
            Toast.makeText(this, "Este terminal no tiene instalado el soporte de reconocimiento de voz", Toast.LENGTH_LONG).show();
            return;
        }
        final Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        final String miPackage = getClass().getPackage().getName();
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, miPackage);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Habla ahora");
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 1);
        startActivityForResult(intent, VOICE_RECOGNITION_REQUEST_CODE);
    }

    public boolean hasVoicerec() {
        final PackageManager pm = getPackageManager();
        final List<ResolveInfo> activities = pm.queryIntentActivities(
                new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
        return (activities.size() != 0);
    }

    /** Llamada a la funcion Text To Speech*/
    private void checkTTS() {
        Intent checkIntent = new Intent();
        checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkIntent, REQUEST_CHECK_TTS);
    }



    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        //Reconocimiento de voz
        if (requestCode == MY_DATA_CHECK_CODE){
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS){
                // success, create the TTS instance
                ttobj = new TextToSpeech(this,this);
            }
            else{
                // missing data, install it
                Intent installIntent = new Intent();
                installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installIntent);
            }
        }

        ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

        //Reproducción de voz
        if (requestCode == REQUEST_CHECK_TTS) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                // success, create the TTS instance
                //ttobj = new TextToSpeech(this, this);
            } else {
                // missing data, install it
                Intent installIntent = new Intent();
                installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installIntent);
            }
        }
        if ((requestCode == VOICE_DESTINO) && (resultCode == RESULT_OK)) {
            //reproducir voz con la ruta
            mResult1.setText(matches.get(0));
            destinoInsertado = true;
            consultarRutaServidor();
            calculaRuta();

        }
    }

    public void onClick(View arg0) {
        // mEditText = (EditText) findViewById(R.id.editText1);
        // mEditText.setText(String.valueOf(posIni));
    }


    public void onSensorChanged(SensorEvent event) {}

    private void activateWifi(){

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("El dispositivo WiFi est� desactivado.");

        builder.setPositiveButton("Activar", new android.content.DialogInterface.OnClickListener(){
            public void onClick(DialogInterface arg0, int arg1) {
                activaWifi = true;
                wifiActivado = true;
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

    public int getPosIni() {
        return posIni;
    }

    public void setPosIni(int posIni) {
        this.posIni = posIni;
    }

    public float getOrientacion() {
        return orientacion;
    }

    public void setOrientacion(float orientacion) {
        this.orientacion = orientacion;
    }

    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO Auto-generated method stub
    }

    public void calculaRuta(){

        if (destinoInsertado){
            if (!wifiActivado){
                activateWifi();
            }
            if(!timerActivado){
                timerActivado=true;
                TimerTask timerTask = new TimerTask()
                {
                    public void run()
                    {
                        calculaRuta();
                    }
                };
                timerCalcularRuta = new Timer();
                timerCalcularRuta.scheduleAtFixedRate(timerTask, 0, 5000);
            }

            consultarRutaServidor();
            if (estaEnLaRuta(cuadranteActual)){
                if (esPuntoClave(cuadranteActual)){
                    actualizarSiguientePaso();
                }
            }
            else {
                consultarRutaServidor();
                return;
            }
            return;
        }
    }

    private void actualizarSiguientePaso() {
        consultarRutaServidor();
    }


    private boolean esPuntoClave(int cuadranteActual) {
        return cuadranteClave==cuadranteActual;
    }


    public Coordenada obtenerCoordenadaActual(){

        WPS lista = new WPS(this);
        try {
            Coordenada c = Menu.thread.getDatabase().calculatePosition(lista.scanAndShow());
            return c;
        } catch (WPSException e) {
            Log.e("ThreadUbicacion", e.getMessage());
            e.printStackTrace();
            return null;
        }
    }


    public void consultarRutaServidor(){

        String origenX=String.valueOf(obtenerCoordenadaActual().getX());
        String origenY=String.valueOf(obtenerCoordenadaActual().getY());
        String origenZ=String.valueOf(obtenerCoordenadaActual().getZ());
        String actualX=String.valueOf(obtenerCoordenadaActual().getX());
        String actualY=String.valueOf(obtenerCoordenadaActual().getY());
        String actualZ=String.valueOf(obtenerCoordenadaActual().getZ());
        String destino = mResult1.getText().toString();//"aula 13";

        String anguloOrientacion = String.valueOf(orientacion);

        String []datos = {origenX,origenY,origenZ, destino, anguloOrientacion,actualX,actualY,actualZ,list_supererables,discapacidad};
        Client c = new Client();
        c.execute(datos);
        try {
            c.get();
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (ExecutionException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }


        listaCuadrantesString = c.getCuadrantes();
        listaCuadrantes = new ArrayList<String>(Arrays.asList(listaCuadrantesString.split(" ")));
        int cuadranteClaveAux = c.getCuadranteClave();
        cuadranteActual=Integer.parseInt(listaCuadrantes.get(1));
        String rutaAux = c.getRuta();
        ArrayList<String> rutaList;
        rutaList =  new ArrayList<String>(Arrays.asList(rutaAux.split("\\.")));
        if (!rutaAux.equals("Recorrido finalizado.")) {
            if ((cuadranteClave == cuadranteActual) || (cuadranteClave == -1)) {
                if (cuadranteClave == -1) {
                    cuadranteClave = cuadranteClaveAux;
                    ruta = rutaAux;
                    if (discapacidad.equals("visual")) {
                        speakText(rutaList.get(0));
                    } else {
                        mResult2.setText(rutaList.get(0));
                    }
                } else {
                    if (discapacidad.equals("visual")) {
                        speakText(rutaList.get(1));
                    } else {
                        mResult2.setText(rutaList.get(1));
                    }
                    ruta = rutaAux;
                    rutaList =  new ArrayList<String>(Arrays.asList(rutaAux.split("\\.")));
                    cuadranteClave = cuadranteClaveAux;
                }
            }else{
                if(contador == 5) {
                    contador = 0;
                    try {
                        final ArrayList<String> finalRutaArray = rutaList;
                        //Utilizado para poder escribir en el view desde otros hilos
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                if (discapacidad.equals("visual")) {
                                    speakText(finalRutaArray.get(0));
                                } else {
                                    mResult2.setText(finalRutaArray.get(0));
                                }
                            }
                        });
                    }catch(Exception e){
                        e.printStackTrace();
                        //System.out.print(e);
                    }
                }
                contador++;
            }

        }else {
            if (discapacidad.equals("visual")) {
                speakText("Ha llegado a su destino");
            } else {
                mResult2.setText("Ha llegado a su destino");
            }
            finish();
        }

    }

    private boolean estaEnLaRuta(int cuadranteActual) {
        return listaCuadrantes.contains(String.valueOf(cuadranteActual));
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
