package com.example.jorge.guidin;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.ActionBarActivity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.jorge.guidin.dialogs.DialogController;
import com.example.jorge.guidin.http.HttpServices;

import org.apache.http.client.ClientProtocolException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class Login extends ActionBarActivity implements TextToSpeech.OnInitListener{

    private static String username;
    private static String password;
    private static String[] superables;
    private static String discapacidad;
    private static boolean admin;
    private TextToSpeech ttobj;
    private static final int VOICE_RECOGNITION_REQUEST_CODE = 0x100;
    private static final int VOICE_USUARIO= 101;
    private static final int VOICE_PASSWORD= 201;
    private static final int REQUEST_CHECK_TTS = 0x1000;
    private String vozReconocida;
    private static final int MY_DATA_CHECK_CODE = 1234;

    final String bienvenida = "Bienvenido a GuiDIn, por favor si es usted una persona con discapacidad visual, pulse la tecla de volumen arriba";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Fire off an intent to check if a TTS engine is installed
        Intent checkIntent = new Intent();
        checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkIntent, MY_DATA_CHECK_CODE);


        Button registrar = (Button)findViewById(R.id.buttonRegister);

        registrar.setOnClickListener(
                new View.OnClickListener(){
                    public void onClick(View view){
                        Intent i = new Intent(getActivity(),Registro.class);
                        startActivity(i);
                    }
                }
        );

        Button entrar = (Button)findViewById(R.id.buttonLogin);

        entrar.setOnClickListener(
                new View.OnClickListener() {
                    public void onClick(View view) {
                        EditText user = (EditText) findViewById(R.id.editTextUser);
                        String s = user.getText().toString();
                        if (s.equals("")) {
                            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                            builder.setMessage("Debes rellenar el campo \"Usuario\"");

                            builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            });

                            builder.setCancelable(false);
                            AlertDialog alert = builder.create();
                            alert.show();
                        } else {
                            /*Intent i = new Intent(getActivity(),Principal.class);
                            startActivity(i);*/
                            username = s;
                            password = ((EditText) findViewById(R.id.editTextPassword)).getText().toString();
                            login(username, password);
                            recuperarDiscapacidad(username);
                        }
                    }
                }
        );

        superables = new String[4];
    }

    //reproduccion de voz
    /**
     * Executed when a new TTS is instantiated. Some static text is spoken via TTS here.
     * @param i
     */
    public void onInit(int i)
    {
        ttobj.speak(bienvenida,
                TextToSpeech.QUEUE_FLUSH,  // Drop all pending entries in the playback queue.
                null,"bienvenida");
    }


    @Override
    public void onPause(){
//        if(ttobj !=null){
//            ttobj.stop();
//            ttobj.shutdown();
//        }
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
        // Don't forget to shutdown!
        if (ttobj != null)
        {
            ttobj.stop();
            ttobj.shutdown();
        }
        super.onDestroy();
    }

    /*control de las teclas fisicas*/
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        switch(keyCode){
            case KeyEvent.KEYCODE_VOLUME_UP:
                setDiscapacidad("visual");
                ttobj.speak("Diga registro para registrarse, o diga entrar para entrar en la aplicación", TextToSpeech.QUEUE_FLUSH, null,"opciones");
                while (ttobj.isSpeaking()){}

                reconocimientoDeVoz(VOICE_RECOGNITION_REQUEST_CODE);



                //Toast.makeText(this, "Boton de Volumen Up presionado",Toast.LENGTH_SHORT).show();
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:

                //Toast.makeText(this, "Boton de Volumen Down presionado", Toast.LENGTH_LONG).show();
                return true;
        }
        return super.onKeyDown(keyCode,event);
    }


    public void reconocimientoDeVoz(int codigo) {
        if(!hasVoicerec()) {
            Toast.makeText(this, "Este terminal no tiene instalado el soporte de reconocimiento de voz", Toast.LENGTH_LONG).show();
            return;
        }
        final Intent voiceIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        voiceIntent.putExtra(RecognizerIntent.EXTRA_PROMPT,"Hable ahora");
        voiceIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        startActivityForResult(voiceIntent, codigo);
    }


    public boolean hasVoicerec() {
        final PackageManager pm = getPackageManager();
        final List<ResolveInfo> activities = pm.queryIntentActivities(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
        return (activities.size() != 0);
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == MY_DATA_CHECK_CODE)
        {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS)
            {
                // success, create the TTS instance
                ttobj = new TextToSpeech(this,this);
            }
            else
            {
                // missing data, install it
                Intent installIntent = new Intent();
                installIntent.setAction(
                        TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installIntent);
            }
        }

        //Reconocimiento de voz
        if (requestCode == VOICE_RECOGNITION_REQUEST_CODE && resultCode == RESULT_OK) {
            // Fill the list view with the strings the recognizer thought it
            // could have heard
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

            // TODO hacer lo que sea con las cadenas
            if (matches != null && matches.size() > 0) {
                vozReconocida = matches.get(0);
                if(vozReconocida.equals("registro")) {
                    discapacidad = "visual";
                    Intent i = new Intent(getActivity(), Registro.class);
                    startActivity(i);
                }else if(vozReconocida.equals("entrar")) {
                    ttobj.speak("Diga el nombre de usuario", TextToSpeech.QUEUE_FLUSH, null,"opciones");
                    pedirUsuarioPorVoz();
                    pedirPasswordPorVoz();
                    TextView usuario =  (TextView)findViewById(R.id.editTextUser);
                    username = usuario.getText().toString().toLowerCase();
                    TextView contraseña =  (TextView)findViewById(R.id.editTextUser);
                    password = contraseña.getText().toString().toLowerCase();
                    Toast.makeText(this,username + "," + password,Toast.LENGTH_LONG).show();
                    login(username, password);
                    recuperarDiscapacidad(username);
                }
            }
            super.onActivityResult(requestCode, resultCode, data);
        }

        //Reproducción de voz
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
        if (requestCode == VOICE_USUARIO && resultCode == RESULT_OK) {
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (matches != null && matches.size() > 0) {
                username = matches.get(0);
                TextView usuario =  (TextView)findViewById(R.id.editTextUser);
                usuario.setText(username);
            }
        }

        if (requestCode == VOICE_PASSWORD && resultCode == RESULT_OK) {
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (matches != null && matches.size() > 0) {
                password = matches.get(0);
                TextView contraseña =  (TextView)findViewById(R.id.editTextPassword);
                contraseña.setText(password);
            }
        }


    }

    public void pedirUsuarioPorVoz(){
        ttobj = new TextToSpeech(this,this);

        speakText("Diga el nombre de usuario");


        while (ttobj.isSpeaking()){}

        reconocimientoDeVoz(VOICE_USUARIO);
        try{
            Thread.sleep(3000);
        }catch (InterruptedException e){}
        //TextView usuario =  (TextView)findViewById(R.id.editTextUser);
        //usuario.setText(vozReconocida);
        //username = vozReconocida;
    }

    public void pedirPasswordPorVoz(){

        ttobj = new TextToSpeech(this,this);
        speakText("Diga su contraseña:");


        reconocimientoDeVoz(VOICE_PASSWORD);
        try{
            Thread.sleep(3000);
        }catch (InterruptedException e){}
//        TextView contraseña =  (TextView)findViewById(R.id.editTextPassword);
//        contraseña.setText(vozReconocida);
//        password = vozReconocida;

    }



    public void login(String user, String password) {
        HttpServices service = new HttpServices();
        try {
            String result = service.login(user, password);
            if(result.equals("")){
                Intent i = new Intent(this, com.example.jorge.guidin.Menu.class);
                startActivity(i);
            }else if(result.equals("Servidor inaccesible")){
                DialogController.createInformDialog(this, "Error en el servidor");
            }else{
                //controlar si es por voz, pedir otra vez los credenciales por voz
                DialogController.createInformDialog(this, "Error en el login");
            }

        } catch (ClientProtocolException e) {
            e.printStackTrace();
            DialogController.createInformDialog(this, "Excepción: " + e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            DialogController.createInformDialog(this, "Excepción: " + e.getMessage());
        }
    }

    private void recuperarDiscapacidad(String user){
        HttpServices service = new HttpServices();
        try {
            String discaSupe = service.recuperarDiscapacidad(user);
            if(discaSupe.equals("")) {
                DialogController.createInformDialog(this, "Usuario no registrado");
            }else if (discaSupe.equals("Servidor inaccesible")) {
                    DialogController.createInformDialog(this, "Error en el servidor");
            }else{
                //rellenar la discapacidad y los superables
                String[] discapacidadesSuperablesAdmin = discaSupe.split(";");

                superables = discapacidadesSuperablesAdmin[0].split(",");
                discapacidad = discapacidadesSuperablesAdmin[1];
                String ad = discapacidadesSuperablesAdmin[2];
                if (ad.equals("1"))
                    admin = true;
                else
                    admin = false;

            }


        } catch (ClientProtocolException e) {
            e.printStackTrace();
            DialogController.createInformDialog(this, "Excepción: " + e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            DialogController.createInformDialog(this, "Excepción: " + e.getMessage());
        }


    }




    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.Salir:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    public Activity getActivity(){
        return this;
    }



    public static String getUsername() {
        return username;
    }

    @SuppressWarnings("static-access")
    public void setUsername(String username) {
        this.username = username;
    }

    @SuppressWarnings("static-access")
    public static String getPassword() {
        return password;
    }

    @SuppressWarnings("static-access")
    public void setPassword(String password) {
        this.password = password;
    }

    @SuppressWarnings("static-access")
    public void setSuperables(String[] superables) {
        this.superables = superables;
    }

    @SuppressWarnings("static-access")
    public static String[] getSuperables() {
        return superables;
    }

    @SuppressWarnings("static-access")
    public void setDiscapacidad(String discapacidad) {
        this.discapacidad = discapacidad;
    }

    @SuppressWarnings("static-access")
    public static String getDiscapacidad() {
        return discapacidad;
    }

    @SuppressWarnings("static-access")
    public static boolean isAdmin() {
        return admin;
    }

    @SuppressWarnings("static-access")
    public void setAdmin(boolean admin) {
        this.admin = admin;
    }

}
