package com.example.jorge.guidin;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
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
import dialogs.DialogController;
import http.HttpServices;

import android.widget.Toast;


public class Login extends ActionBarActivity {

    private static String username;
    private static String password;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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
                new View.OnClickListener(){
                    public void onClick(View view){
                        EditText user = (EditText)findViewById(R.id.editTextUser);
                        String s = user.getText().toString();
                        if(s.equals("")){
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
                        }else{
                            /*Intent i = new Intent(getActivity(),Principal.class);
                            startActivity(i);*/
                            username = s;
                            password = ((EditText)findViewById(R.id.editTextPassword)).getText().toString();
                            login(username,password);
                        }
                    }
                }
        );
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
}
