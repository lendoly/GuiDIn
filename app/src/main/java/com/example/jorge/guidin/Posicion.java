package com.example.jorge.guidin;

import android.os.Bundle;
import android.speech.tts.TextToSpeech;
import android.view.MenuItem;

import android.app.ProgressDialog;
import android.os.SystemClock;
import android.widget.RelativeLayout;

import com.example.jorge.guidin.dialogs.DialogController;
import com.example.jorge.guidin.http.HttpServices;
import com.example.jorge.guidin.posicion.Mapa;
import com.example.jorge.guidin.rutas.ListaCuadrantes;
import com.example.jorge.guidin.wps.ThreadDatos;
import com.example.jorge.guidin.wps.ThreadUbicacion;


public class Posicion extends PositionActivity {

private ThreadUbicacion thread;
private ProgressDialog pd;
private Thread comprobar;
private Mapa m;
private ListaCuadrantes cuadrantes;
private TextToSpeech ttobj;

@Override
public void onCreate(final Bundle savedInstanceState) {
    Menu.activeActivity = this;
    super.onCreate(savedInstanceState);


    /*requestWindowFeature(Window.FEATURE_NO_TITLE);
    getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);*/

    setContentView(R.layout.activity_posicion);
    RelativeLayout main = (RelativeLayout) findViewById(R.id.positionLayout);

    cuadrantes = new ListaCuadrantes();
    m = new Mapa(this);
    m.setListaCuadrantes(cuadrantes);;
    main.addView(m);
    pd = ProgressDialog.show(this, "", "Cargando datos...");
    comprobar = new Thread(){
        public void run(){
            comprobar();
        }
    };
    comprobar.start();
}

    @Override
    public void onPause(){
        super.onPause();
    }

    public void speakText(String texto){
        //Toast.makeText(getApplicationContext(), texto, Toast.LENGTH_LONG).show();
        ttobj.speak(texto, TextToSpeech.QUEUE_FLUSH, null, "bienvenida");
        while (ttobj.isSpeaking()){}
    }


    @Override
    public void onDestroy()
    {
        // Don't forget to shutdown!
        if (ttobj != null)
        {
            ttobj.stop();
            ttobj.shutdown();
        }
        super.onDestroy();
    }



    @Override
public boolean onOptionsItemSelected(MenuItem item) {
    // Handle action bar item clicks here. The action bar will
    // automatically handle clicks on the Home/Up button, so long
    // as you specify a parent activity in AndroidManifest.xml.
    int id = item.getItemId();

    return super.onOptionsItemSelected(item);
}

    private void comprobar(){
        if(Menu.thread.getEstado() == ThreadDatos.ESTADO_ERROR){
            pd.dismiss();
            DialogController.createInformDialog(this, "Ha ocurido un error mientras se recuperaban los datos.");
        }else if(Menu.thread.getEstado() == ThreadDatos.ESTADO_EJECUCION){
                    SystemClock.sleep(2000);
                    comprobar();
                }else if(Menu.thread.getEstado() == ThreadDatos.ESTADO_EXITO){
                    //Mapa m = new Mapa(this);
                    pd.dismiss();
                    //setContentView(m);
                    if(thread == null){
                        thread = new ThreadUbicacion(this);
                        thread.setCuadrantes(cuadrantes);
                        thread.run();
                    }
                }
        }

    public void refresh(double x, double y, double z, int c){
        m.setPosX(x);
        m.setPosY(y);
        m.setPosZ(z);
        m.setCuadrante(c);
        HttpServices service = new HttpServices();
        String lugar = service.getLugar(c);
        String discapacidad = Login.getDiscapacidad();
        if((discapacidad.equals("visual"))&&(!lugar.equals(""))){
            speakText("se encuentra cerca de " + lugar);
        }
    }

    public void pintarPosicion(){
        //TODO
    }

    public ListaCuadrantes getCuadrantes(){
        return cuadrantes;
    }

    public void setCuadrantes(ListaCuadrantes l){
        cuadrantes= l;
    }

    public Mapa getMapa(){
        return m;
    }

}