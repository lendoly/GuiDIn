package com.example.jorge.guidin;

import android.app.Activity;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RadioButton;

import org.apache.http.client.ClientProtocolException;

import java.io.IOException;

import com.example.jorge.guidin.dialogs.DialogController;
import com.example.jorge.guidin.http.HttpServices;


public class Registro extends ActionBarActivity {

    private boolean[] superables = {false,false,false,false};
    private int discapacidad;

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
        String list_supererables="";
        if (superables[0])
            list_supererables += "escalera";
        if (superables[1])
            if (list_supererables=="")
                list_supererables += "ascensor";
            else
                list_supererables+= ",ascensor";
        if (superables[2])
            if (list_supererables=="")
                list_supererables += "puerta";
            else
                list_supererables += ",puerta";
        if (superables[3])
            if (list_supererables=="")
                list_supererables += "rampa";
            else
                list_supererables += ",rampa";
        try {
            String s = service.register(((EditText)findViewById(R.id.registerUser)).getText().toString(),
                    ((EditText)findViewById(R.id.registerPassword)).getText().toString(),
                    ((EditText)findViewById(R.id.registerName)).getText().toString(),
                    list_supererables, discapacidad);

        } catch (ClientProtocolException e) {
            e.printStackTrace();
            DialogController.createInformDialog(this, "Excepción: " + e.getMessage());
        } catch (IOException e) {
            e.printStackTrace();
            DialogController.createInformDialog(this, "Excepción: " + e.getMessage());
        }
    }

    public void onCheckboxClicked(View view) {
        // Is the view now checked?
        boolean checked = ((CheckBox) view).isChecked();

        // Check which checkbox was clicked
        switch(view.getId()) {
            case R.id.escaleras:
                if (checked)
                    superables[0] = true;
                else
                    superables[0] = false;
                break;
            case R.id.ascensor:
                if (checked)
                    superables[1] = true;
                else
                    superables[1] = false;
                break;
            case R.id.puerta:
                if (checked)
                    superables[2] = true;
                else
                    superables[2] = false;
                break;
            case R.id.rampa:
                if (checked)
                    superables[3] = true;
                else
                    superables[3] = false;
                break;
            // TODO: Veggie sandwich
        }
    }


    public void onRadioButtonClicked(View view) {
        // Is the button now checked?
        boolean checked = ((RadioButton) view).isChecked();

        // Check which radio button was clicked
        switch(view.getId()) {
            case R.id.radioVisual:
                if (checked)
                    discapacidad = 2;

                    break;
            case R.id.radioAuditiva:
                if (checked)
                    discapacidad = 1;
                    break;
            case R.id.radioNinguna:
                if (checked)
                    discapacidad = 0;
                break;
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
