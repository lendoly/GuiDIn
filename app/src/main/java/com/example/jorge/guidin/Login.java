package com.example.jorge.guidin;

import android.speech.tts.TextToSpeech;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import org.apache.http.client.ClientProtocolException;
import java.io.IOException;
import java.util.Locale;

import dialogs.DialogController;
import http.HttpServices;

import android.widget.Toast;


public class Login extends ActionBarActivity {

    private static String username;
    private static String password;
    private static String[] superables;
    private static String discapacidad;
    private TextToSpeech ttobj;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        ttobj=new TextToSpeech(getApplicationContext(),new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if(status != TextToSpeech.ERROR){
                    ttobj.setLanguage(new Locale("ES"));
                }
            }
        });

        speakText();

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
    }


    //reproduccion de voz
    @Override
    public void onPause(){
        if(ttobj !=null){
            ttobj.stop();
            ttobj.shutdown();
        }
        super.onPause();
    }

    public void speakText(){
        String toSpeak = "Bienvenido a GuiDIn, por favor si es usted una persona con discapacidad visual, pulse una tecla de volumen";
        Toast.makeText(getApplicationContext(), toSpeak,
                Toast.LENGTH_LONG).show();
        ttobj.speak(toSpeak, TextToSpeech.QUEUE_FLUSH, null);
    }

    /*contro de las teclas fisicas*/
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        switch(keyCode){
            case KeyEvent.KEYCODE_VOLUME_UP:
                speakText();
                //Toast.makeText(this, "Boton de Volumen Up presionado",Toast.LENGTH_SHORT).show();
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                onPause();
                //Toast.makeText(this, "Boton de Volumen Down presionado", Toast.LENGTH_LONG).show();
                return true;
        }
        return super.onKeyDown(keyCode,event);
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
                String[] discapacidadesYSuperables = discaSupe.split(";");
                superables = discapacidadesYSuperables[0].split(",");
                discapacidad = discapacidadesYSuperables[1];

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
            /*
            case R.id.search:
                //metodoSearch()
                info.setText("Se presionó Buscar");
                return true;
            case R.id.edit:
                //metodoEdit()
                info.setText("Se presionó Editar");
                return true;
            case R.id.delete:
                //metodoDelete()
                info.setText("Se presionó Eliminar");
                return true;
            case R.id.action_settings:
                //metodoSettings()
                info.setText("Se presionó Ajustes");
                return true;
                */
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


}
