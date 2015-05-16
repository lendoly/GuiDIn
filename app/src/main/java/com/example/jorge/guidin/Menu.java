package com.example.jorge.guidin;

import android.content.Intent;
import android.os.Bundle;
import android.app.Activity;
import android.content.Context;
import android.app.ListActivity;
import android.graphics.drawable.Drawable;
import android.speech.tts.TextToSpeech;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jorge.guidin.http.HttpServices;
import com.example.jorge.guidin.dialogs.DialogController;
import com.example.jorge.guidin.wps.ThreadDatos;

import java.util.ArrayList;
import java.util.List;
import java.io.IOException;

import org.apache.http.client.ClientProtocolException;

import android.view.KeyEvent;

import java.util.Locale;

import android.view.Gravity;


public class Menu extends ListActivity implements TextToSpeech.OnInitListener{

    public static Activity activeActivity = null;
    public static ThreadDatos thread;

    public final static int MENU_QUIT = 0;

    private String username;
    private String password;
    private String[] superables;
    private String discapacidad;
    private static final int MY_DATA_CHECK_CODE = 1234;
    private static final int REQUEST_CHECK_TTS = 0x1000;
    private boolean admin;
    private String vozInicial = "Pulse la tecla de subir volumen para indicar un destino, pulse la tecla de bajar el volumen para posicionarle en el mapa, o pulse atras para cerrar sesión.";

    TextToSpeech ttobj;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Fire off an intent to check if a TTS engine is installed
        Intent checkIntent = new Intent();
        checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkIntent, MY_DATA_CHECK_CODE);

        if(thread == null){
            thread = createAndStartThread(); //Comienza a recuperar los datos de BD
        }
        username = Login.getUsername();
        password = Login.getPassword();
        superables = Login.getSuperables();
        discapacidad = Login.getDiscapacidad();
        admin = Login.isAdmin();

        IconifiedTextListAdapter adapter = new IconifiedTextListAdapter(this);
        IconAndText aux = new IconAndText(this, null, getString(R.string.ruta), true);
        adapter.addItem(aux);
        aux = new IconAndText(this, null, getString(R.string.posicion), true);
        adapter.addItem(aux);
        if (this.admin) {
            aux = new IconAndText(this, null, getString(R.string.wifis), true);
            adapter.addItem(aux);
            aux = new IconAndText(this, null, getString(R.string.pruebas), true);
            adapter.addItem(aux);

            aux = new IconAndText(this, null, getString(R.string.medir), true);
            adapter.addItem(aux);
        }
        aux = new IconAndText(this, null, getString(R.string.salir), true);
        adapter.addItem(aux);
        this.setListAdapter(adapter);
    }


    /**
     * Executed when a new TTS is instantiated. Some static text is spoken via TTS here.
     * @param i
     */
    public void onInit(int i)
    {
        if(discapacidad.equals("visual")) {
            ttobj.speak(vozInicial,
                    TextToSpeech.QUEUE_FLUSH,  // Drop all pending entries in the playback queue.
                    null);
        }
    }
    @Override
    public void onPause(){
        if(ttobj !=null){
            ttobj.stop();
            ttobj.shutdown();
        }
        super.onPause();
    }

    public void speakText(String texto){
        Toast.makeText(getApplicationContext(), texto, Toast.LENGTH_LONG).show();
        ttobj.speak(texto, TextToSpeech.QUEUE_FLUSH, null, "bienvenida");
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


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        //Reproducción de voz
        if (requestCode == MY_DATA_CHECK_CODE) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                // success, create the TTS instance
                ttobj = new TextToSpeech(this, this);
            } else {
                // missing data, install it
                Intent installIntent = new Intent();
                installIntent.setAction(
                        TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installIntent);
            }
        }


        if (requestCode == REQUEST_CHECK_TTS) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                // success, create the TTS instance
                //mTts = new TextToSpeech(this, this);
            } else {
                // missing data, install it
                Intent installIntent = new Intent();
                installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installIntent);
            }
        }
    }



    public ThreadDatos createAndStartThread(){
        ThreadDatos t = new ThreadDatos(ThreadDatos.ESTADO_EJECUCION);
        Thread.UncaughtExceptionHandler h = new Thread.UncaughtExceptionHandler() {
            public void uncaughtException(Thread th, Throwable ex) {
                Toast.makeText(getApplicationContext(), "Uncaught exception: " + ex, Toast.LENGTH_SHORT).show();
                System.out.println("Uncaught exception: " + ex);
            }
        };
        t.setUncaughtExceptionHandler(h);
        t.start();
        t.run();
        return t;
    }


    /*contro de las teclas fisicas*/
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Intent intent;
        switch(keyCode){
            case KeyEvent.KEYCODE_VOLUME_UP:
                intent = new Intent(this, IndicarDestino.class);
                startActivity(intent);
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                intent = new Intent(this, Posicion.class);
                startActivity(intent);
                return true;
            case KeyEvent.KEYCODE_BACK:
                logout(username, password);
                finish();
                return true;
        }
        return super.onKeyDown(keyCode, event);
    }


    public class IconAndText extends LinearLayout {

        private boolean isSelectable;
        private ImageView mIcon;
        private TextView mText;

        public IconAndText(Context context, Drawable icon, String text, boolean isSelectable) {
            super(context);
            this.setOrientation(HORIZONTAL);
            this.isSelectable = isSelectable;
            mIcon = new ImageView(context);
            mIcon.setImageDrawable(icon);

            mIcon.setPadding(0, 2, 5, 0);


            addView(mIcon,  new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));

            mText = new TextView(context);
            mText.setText(text);
            mText.setEms(24);
            mText.setGravity(Gravity.CENTER);
            mText.setMinimumHeight(300);
            mText.setTextSize(26);
            //  mText.setBackgroundColor(android.R.color.white);
            addView(mText, new LinearLayout.LayoutParams(
                    LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
        }

        public boolean isSelectable(){
            return isSelectable;
        }

        public ImageView getIcon(){
            return mIcon;
        }

        public String getText(){
            return (String) mText.getText();
        }

        public void setText(String text){
            mText.setText(text);
        }

        public void setIcon(Drawable bullet){
            mIcon.setImageDrawable(bullet);
        }
    }

    public class IconifiedTextListAdapter extends BaseAdapter {


        private Context mContext;

        private List<IconAndText> mItems = new ArrayList<IconAndText>();

        public IconifiedTextListAdapter(Context context) {
            mContext = context;
        }

        public void addItem(IconAndText it) { mItems.add(it); }

        public void setListItems(List<IconAndText> lit) { mItems = lit; }

        public int getCount() { return mItems.size(); }

        public Object getItem(int position) { return mItems.get(position); }

        public boolean areAllItemsSelectable() { return false; }

        public boolean isSelectable(int position) {
            try{
                return mItems.get(position).isSelectable();
            }catch (IndexOutOfBoundsException aioobe){
                return false;
            }
        }

        public long getItemId(int position) {
            return position;
        }

        /** @param convertView The old view to overwrite, if one is passed
         * @returns a IconifiedTextView that holds wraps around an IconifiedText */
        public View getView(int position, View convertView, ViewGroup parent) {
            IconAndText btv;
            if (convertView == null) {
                btv = new IconAndText(mContext, mItems.get(position).getIcon().getDrawable(), mItems.get(position).getText(), mItems.get(position).isSelectable());
            } else {
                btv = (IconAndText) convertView;
                btv.setText(mItems.get(position).getText());
                btv.setIcon(mItems.get(position).getIcon().getDrawable());
            }
            return btv;
        }
    }

    @Override
    protected void onListItemClick (ListView l, View v, int position, long id){
        super.onListItemClick(l,v,position,id);
        if(this.admin){
            if(position==0){
                Intent intent = new Intent(this, IndicarDestino.class);
                startActivity(intent);
            }else if(position == 1){
                Intent intent = new Intent(this, Posicion.class);
                startActivity(intent);
            }else if(position == 2){
                Intent intent = new Intent(this, Wifis.class);
                startActivity(intent);
            }else if (position == 3){
                Intent intent = new Intent(this, Acelerometro.class);
                startActivity(intent);
            }else if (position == 4){
                Intent intent = new Intent(this, Medir.class);
                startActivity(intent);
            }else if (position == 5){
                logout(username, password);
                finish();
            }
        }
        else{
            if(position==0){
                Intent intent = new Intent(this, IndicarDestino.class);
                startActivity(intent);
            }else if(position == 1){
                Intent intent = new Intent(this, Posicion.class);
                startActivity(intent);
            }else if (position == 2) {
                logout(username, password);
                finish();
            }
        }


    }

    public void logout(String user, String password){
        HttpServices service = new HttpServices();
        try {
            if(service.logout(user, password).equals("")){

            }else{
                DialogController.createInformDialog(this, "Error en el logout");
            }
        } catch (ClientProtocolException e) {
            e.printStackTrace();
            DialogController.createInformDialog(this, "Excepción: " + e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            DialogController.createInformDialog(this, "Excepción: " + e.getMessage());
        }
    }


    public String getUsername(){
        return username;
    }

    public String getPassword(){
        return password;
    }

    public void setUsername(String u){
        username = u;
    }

    public void setPassword(String p){
        password = p;
    }

    public String[] getSuperables() {
        return superables;
    }

    public void setSuperables(String[] superables) {
        this.superables = superables;
    }

    public String getDiscapacidad() {
        return discapacidad;
    }

    public void setDiscapacidad(String discapacidad) {
        this.discapacidad = discapacidad;
    }

}