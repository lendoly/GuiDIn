package com.example.jorge.guidin;

import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.client.ClientProtocolException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.example.jorge.guidin.dialogs.DialogController;
import com.example.jorge.guidin.http.HttpServices;


public class Registro extends ActionBarActivity {

    private boolean[] superables = {false,false,false,false};
    private int discapacidad;
    private TextToSpeech ttobj;
    final String vozInicial = "Esta en el registro, por favor si es usted una persona con discapacidad visual, diga si a continuación";
    private static final int VOICE_RECOGNITION_REQUEST_CODE = 0x100;
    private static final int REQUEST_CHECK_TTS = 0x1000;
    private String vozReconocida;
    private int origenVoz = 0;
    private TextView txtview;

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
        //speakText(vozInicial);
        //reconocimientoDeVoz();
    }

    public void speakText(String texto){
        Toast.makeText(getApplicationContext(), texto, Toast.LENGTH_LONG).show();
        ttobj.speak(texto, TextToSpeech.QUEUE_FLUSH, null);
    }


    public void reconocimientoDeVoz() {
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
        startActivityForResult(voiceIntent, VOICE_RECOGNITION_REQUEST_CODE);
    }


    public boolean hasVoicerec() {
        final PackageManager pm = getPackageManager();
        final List<ResolveInfo> activities = pm.queryIntentActivities(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
        return (activities.size() != 0);
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        //Reconocimiento de voz
        if (requestCode == VOICE_RECOGNITION_REQUEST_CODE && resultCode == RESULT_OK) {
            // Fill the list view with the strings the recognizer thought it
            // could have heard
            ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

            if (matches != null && matches.size() > 0) {
                vozReconocida = matches.get(0);
                if(origenVoz == 0) {
                    if(vozReconocida.equals("si")) {
                        origenVoz++;
                        reconocimientoDeVoz();
                    }
                }else if(origenVoz == 1) {
                        speakText("Diga su nombre");
                        origenVoz++;
                        reconocimientoDeVoz();
                }else if(origenVoz == 2) {
                    txtview =  (TextView)findViewById(R.id.registerName);
                    txtview.setText(vozReconocida);
                    speakText("Diga un nombre de usuario");
                    origenVoz++;
                    reconocimientoDeVoz();
                }else if(origenVoz == 3) {
                    txtview =  (TextView)findViewById(R.id.registerUser);
                    txtview.setText(vozReconocida);
                    speakText("Diga una contraseña");
                    origenVoz++;
                    reconocimientoDeVoz();
                }else if(origenVoz == 4) {
                    txtview =  (TextView)findViewById(R.id.registerPassword);
                    txtview.setText(vozReconocida);
                    speakText("Ahora le preguntaremos sobre los elementos que puede superar para ofrecer una mejor guía. Responda si o no");
                    speakText("¿Puede subir o bajar escaleras?");
                    origenVoz++;
                    reconocimientoDeVoz();
                }else if(origenVoz == 5) {
                    if(vozReconocida.equals("si")) {
                        txtview =  (TextView)findViewById(R.id.escaleras);
                        txtview.setSelected(true);
                    }
                    speakText("¿Puede pasar a traves de puertas?");
                    origenVoz++;
                    reconocimientoDeVoz();
                }else if(origenVoz == 6) {
                    if(vozReconocida.equals("si")) {
                        txtview = (TextView) findViewById(R.id.escaleras);
                        txtview.setSelected(true);
                    }
                    speakText("¿Puede subir en ascemsores?");
                    origenVoz++;
                    reconocimientoDeVoz();
                }else if(origenVoz == 7) {
                    if(vozReconocida.equals("si")) {
                        txtview =  (TextView)findViewById(R.id.escaleras);
                        txtview.setSelected(true);
                    }
                    speakText("¿Puede usar rampas?");
                    origenVoz++;
                    reconocimientoDeVoz();
                }else if(origenVoz == 8) {
                    if(vozReconocida.equals("si")) {
                        txtview =  (TextView)findViewById(R.id.escaleras);
                        txtview.setSelected(true);
                    }
                    txtview =  (TextView)findViewById(R.id.registerUser);
                    txtview.setText(vozReconocida);
                    discapacidad = 2;
                    if (comprobarDatos() == "")
                        register();
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
