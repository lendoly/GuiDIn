package com.example.jorge.guidin.wps;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.wifi.WifiManager;
import android.os.SystemClock;
import android.util.Log;
import android.widget.ImageView;

import com.example.jorge.guidin.Menu;
import com.example.jorge.guidin.PositionActivity;

import com.example.jorge.guidin.R;
import com.example.jorge.guidin.dialogs.DialogController;
import com.example.jorge.guidin.rutas.ListaCuadrantes;

public class ThreadUbicacion extends Thread{

    private WPS lista;
    private PositionActivity fatherActivity;
    private int estado;
    private ListaCuadrantes cuadrantes;
    private ParticleFilter filter;
    private Coordenada c;
    private long time;

    private SensorManager mSensorManager;
    public float[] magnetic_angles;
    public float[] accelerations;
    private boolean giroValido;

    /**
     * Mapa con las zonas permitidas en blanco
     */
    public static Bitmap mapaMascara;

    private final int ESTADO_PARADO = 0;
    private final int ESTADO_EJECUCION = 1;
    private final int ESTADO_ERROR = 2;

    /**
     * Constructora
     * @param activity la actividad Position que tiene el mapa pintado
     */
    public ThreadUbicacion(PositionActivity activity){
        fatherActivity = activity;
        this.setName("ThreadUbicacion");
        estado = ESTADO_EJECUCION;
        mSensorManager = (SensorManager) activity.getSystemService(Context.SENSOR_SERVICE);

        Sensor s = mSensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER).get(0);
        Sensor s2 = mSensorManager.getSensorList(Sensor.TYPE_MAGNETIC_FIELD).get(0);

        SensorEventListener magneticAnglesListener = new SensorEventListener(){

            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }

            public void onSensorChanged(SensorEvent event) {
                magnetic_angles = event.values;
            }

        };

        SensorEventListener accelerationsListener = new SensorEventListener(){

            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }

            public void onSensorChanged(SensorEvent event) {
                accelerations = event.values;
            }

        };

        mSensorManager.registerListener(accelerationsListener, s, SensorManager.SENSOR_DELAY_GAME);
        mSensorManager.registerListener(magneticAnglesListener, s2, SensorManager.SENSOR_DELAY_GAME);

        ImageView img = new ImageView(activity);
        img.setImageResource(R.drawable.mapamascara);
        mapaMascara = BitmapFactory.decodeResource(img.getResources(), R.drawable.mapamascara);
    }

    public void run(){
        lista = new WPS(fatherActivity);
        //cuadrantes = new ListaCuadrantes();
        //fatherActivity.setCuadrantes(cuadrantes);
        WifiManager manager = (WifiManager) fatherActivity.getSystemService(Context.WIFI_SERVICE);
//		int numMedidasIncorrectas = 0;
        while(estado != ESTADO_ERROR){
            if(estado == ESTADO_EJECUCION){

                if(!manager.isWifiEnabled()){
                    estado = ESTADO_PARADO;
                    activateWifi();
                }else{

                    try {
                        c = Menu.thread.getDatabase().calculatePosition(lista.scanAndShow());
                    } catch (WPSException e) {
                        Log.e("ThreadUbicacion", e.getMessage());
                        e.printStackTrace();
                        estado = ESTADO_PARADO;
                        activateWifi();

                    }
                    int id = cuadrantes.numCuadrante(c.getX(), c.getY());


                    if(Menu.activeActivity == fatherActivity){
                        fatherActivity.refresh(c.getX(),c.getY(),c.getZ(),id);
                    }
                }
            }
        }

        DialogController.createInformDialog(fatherActivity, "Se ha superado el tiempo de espera de activación de la antena WIFI. Se cancela el posicionamiento.");
    }

    /**
     * Pregunta al usuario si quiere activar el WiFi
     */
    private void activateWifi(){

        AlertDialog.Builder builder = new AlertDialog.Builder(fatherActivity);
        builder.setMessage("El dispositivo WiFi está desactivado.");
        builder.setPositiveButton("Activar", new android.content.DialogInterface.OnClickListener(){

            public void onClick(DialogInterface arg0, int arg1) {
                WifiManager manager = (WifiManager) fatherActivity.getSystemService(Context.WIFI_SERVICE);
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
                    estado = ESTADO_EJECUCION;
                }else{
                    estado = ESTADO_ERROR;
                }
            }

        });

    /*builder.setCancelable(false);
    AlertDialog alert = builder.create();
    alert.show();*/
    }

    public ListaCuadrantes getCuadrantes(){
        return cuadrantes;
    }

    public void setCuadrantes(ListaCuadrantes l){
        cuadrantes = l;
    }

    /**
     * Estima la posición del móvil con el filtro de partículas
     * @throws Exception
     */
    public Coordenada stimation(Coordenada coordK) throws Exception{

        if(magnetic_angles!=null && accelerations!=null){
            float[] R = new float[9];
            float[] I = new float[9];
            float[] result = new float[3];
            //float aux;
            if(SensorManager.getRotationMatrix(R, I, accelerations, magnetic_angles)){
                //float inclination = (float)((SensorManager.getInclination(I)*180)/Math.PI);
                //accZNorm.setText("" + inclination);
                //SensorManager.remapCoordinateSystem(R, SensorManager.AXIS_X, SensorManager.AXIS_Y, R2);
                SensorManager.getOrientation(R, result);
                float giroSobreX = (float) Math.toDegrees(result[1]);
                float giroSobreY= (float) Math.toDegrees(result[2]);
                float giroSobreZ = (float) Math.toDegrees(result[0]);

                //Calculo si la posición del móvil es válida

                if((giroSobreX>=-20 && giroSobreX<=20) && (giroSobreY<=-50 && giroSobreY>=-130)){
                    setGiroValido(true);
                    float newAngle;

                    //Calculo la aceleración en el eje Z combinando los giros de X e Y

                    if(giroSobreY<=0 && giroSobreY>=-90){
                        newAngle = -giroSobreY;
                    }else{
                        newAngle = 180 - Math.abs(giroSobreY);
                    }
                    double radiansY = Math.toRadians(newAngle);
                    double accAux;
                    if(accelerations[2]>0){
                        accAux = accelerations[2] - Math.cos(radiansY)* SensorManager.STANDARD_GRAVITY;

                    }else{
                        accAux = accelerations[2] +  Math.cos(radiansY)* SensorManager.STANDARD_GRAVITY;
                    }

                    //Redirijo el ángulo de Z por el giro del móvil en modo landscape
                    giroSobreZ = giroSobreZ - 90;
                    if(giroSobreZ < -180) giroSobreZ = giroSobreZ + 360;
                    //En el caso de que la aceleración sea negativa invierto su dirección
                    if(accAux<0){
                        if(giroSobreZ<0){
                            giroSobreZ = giroSobreZ + 180;
                        }else {
                            giroSobreZ = giroSobreZ - 180;
                        }
                        accAux = -accAux;
                    }

                    //Calculo las aceleraciones en los ejes X e Y del mapa

                /*boolean accXPositiva;
                boolean accYPositiva;*/
                    double accX;
                    double accY;
                    if(giroSobreZ>=-180 && giroSobreZ<=-90){
                        newAngle = giroSobreZ + 180;
                    /*accXPositiva = true;
                    accYPositiva = false;*/
                        accX = accAux * Math.sin(Math.toRadians(newAngle));
                        accY = -(accAux * Math.cos(Math.toRadians(newAngle)));
                    }else if(giroSobreZ>-90 && giroSobreZ <=0){
                        newAngle = giroSobreZ + 90;
                    /*accXPositiva = true;
                    accYPositiva = true;*/
                        accX = accAux * Math.cos(Math.toRadians(newAngle));
                        accY = accAux * Math.sin(Math.toRadians(newAngle));
                    }else if(giroSobreZ>0 && giroSobreZ <=90){
                        newAngle = giroSobreZ;
                    /*accXPositiva = false;
                    accYPositiva = true;*/
                        accY = accAux * Math.cos(Math.toRadians(newAngle));
                        accX = -(accAux * Math.sin(Math.toRadians(newAngle)));
                    }else{
                        newAngle = giroSobreZ - 90;
                    /*accXPositiva = false;
                    accYPositiva = false;*/
                        accX = -(accAux * Math.cos(Math.toRadians(newAngle)));
                        accY = -(accAux * Math.sin(Math.toRadians(newAngle)));
                    }
                    time = System.currentTimeMillis()/1000;
                    filter.prediction(accX, accY, time);
                    filter.correction(coordK);
                    filter.particleUpdate();
                    filter.resampling();
                    Coordenada c2 = filter.stimation(c);
                    return c2;
                }else{
                    setGiroValido(false);
                    throw new Exception("Giro no válido.");
                }
            }else{
                throw new Exception("Error con los sensores.");
            }
        }else{
            throw new Exception("Error con los sensores.");
        }
    }

    public void setGiroValido(boolean giroValido) {
        this.giroValido = giroValido;
    }

    public boolean isGiroValido() {
        return giroValido;
    }

    public Coordenada getC(){
        return c;
    }

}