package com.example.jorge.guidin;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import org.apache.http.client.ClientProtocolException;

import java.io.IOException;

import dialogs.DialogController;
import http.HttpServices;


public class Registro extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);

        Button cancelar = (Button)findViewById(R.id.buttonCancel);

        cancelar.setOnClickListener(
                new View.OnClickListener(){
                    public void onClick(View view){
                        finish();
                    }
                }
        );

        Button aceptar = (Button)findViewById(R.id.buttonAccept);
        aceptar.setOnClickListener(
                new View.OnClickListener(){
                    public void onClick(View view){
                        String s = comprobarDatos();
                        if(s.equals("")){

                            register();
                            finish();

                        }else{
                            DialogController.createInformDialog(getActivity(), s);
                        }
                    }
                }
        );
    }


    public String comprobarDatos() {
        EditText text = (EditText)findViewById(R.id.registerName);
        String s = text.getText().toString();
        if(s.equals("")){
            return "Debes completar el campo \"Nombre\"";
        }
        text = (EditText)findViewById(R.id.registerSurname);
        if(s.equals("")){
            return "Debes completar el campo \"Apellidos\"";
        }
        text = (EditText)findViewById(R.id.registerUser);
        if(s.equals("")){
            return "Debes completar el campo \"Usuario\"";
        }
        text = (EditText)findViewById(R.id.registerPassword);
        if(s.equals("")){
            return "Debes completar el campo \"Password\"";
        }
        return "";
    }


    private void register(){
        HttpServices service = new HttpServices();
        try {
            String s = service.register(((EditText)findViewById(R.id.registerUser)).getText().toString(),
                    ((EditText)findViewById(R.id.registerPassword)).getText().toString(),
                    ((EditText)findViewById(R.id.registerName)).getText().toString(),
                    ((EditText)findViewById(R.id.registerSurname)).getText().toString());

        } catch (ClientProtocolException e) {
            e.printStackTrace();
            DialogController.createInformDialog(this, "Excepción: " + e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            DialogController.createInformDialog(this, "Excepción: " + e.getMessage());
        }
    }

    public Activity getActivity(){
        return this;
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_registro, menu);
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
