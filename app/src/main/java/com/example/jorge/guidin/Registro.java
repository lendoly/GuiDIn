package com.example.jorge.guidin;

import android.app.Activity;
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
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.client.ClientProtocolException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import com.example.jorge.guidin.dialogs.DialogController;
import com.example.jorge.guidin.http.HttpServices;


public class Registro extends ActionBarActivity implements TextToSpeech.OnInitListener{

    private boolean[] superables = {false,false,false,false};
    private int discapacidad;
    private String discapacidadStr;
    private TextToSpeech ttobj;
    final String vozInicial = "Está en el registro, a continuacion se le pedirán los datos para completar el registro";
    private static final int VOICE_RECOGNITION_REQUEST_CODE = 0x100;
    private static final int REQUEST_CHECK_TTS = 0x1000;
    private static final int MY_DATA_CHECK_CODE = 1234;
    private static final int VOICE_NOMBRE= 101;
    private static final int VOICE_USUARIO= 102;
    private static final int VOICE_PASS= 103;
    private static final int VOICE_ESCALERAS= 104;
    private static final int VOICE_ASCENSOR= 105;
    private static final int VOICE_PUERTA= 106;
    private static final int VOICE_RAMPA= 107;
    private String vozReconocida;
    private TextView txtview;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registro);
        discapacidadStr = Login.getDiscapacidad();

        // Fire off an intent to check if a TTS engine is installed
        Intent checkIntent = new Intent();
        checkIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);
        startActivityForResult(checkIntent, MY_DATA_CHECK_CODE);

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

    /**
     * Executed when a new TTS is instantiated. Some static text is spoken via TTS here.
     * @param i
     */
    public void onInit(int i)
    {
        if(discapacidadStr.equals("visual")) {
            ttobj.speak(vozInicial,
                    TextToSpeech.QUEUE_FLUSH,  // Drop all pending entries in the playback queue.
                    null, "bienvenida");
            while (ttobj.isSpeaking()){}
            speakText("Diga su nombre");
            reconocimientoDeVoz(VOICE_NOMBRE);
        }
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


    public boolean hasVoicerec() {
        final PackageManager pm = getPackageManager();
        final List<ResolveInfo> activities = pm.queryIntentActivities(new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH), 0);
        return (activities.size() != 0);
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        if (requestCode == MY_DATA_CHECK_CODE) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                // success, create the TTS instance
                ttobj = new TextToSpeech(this, this);
            } else {
                // missing data, install it
                Intent installIntent = new Intent();
                installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);
                startActivity(installIntent);
            }
        }

        //Reconocimiento de voz
        // Fill the list view with the strings the recognizer thought it
        // could have heard
        ArrayList<String> matches = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);

        if (matches != null && matches.size() > 0) {
            vozReconocida = matches.get(0);
            if (requestCode == VOICE_NOMBRE && resultCode == RESULT_OK) {
                if (matches != null && matches.size() > 0) {
                    txtview = (TextView) findViewById(R.id.registerName);
                    txtview.setText(vozReconocida);
                    speakText("Diga un nombre de usuario");
                    reconocimientoDeVoz(VOICE_USUARIO);
                }
            }
            if (requestCode == VOICE_USUARIO && resultCode == RESULT_OK) {
                if (matches != null && matches.size() > 0) {
                    txtview = (TextView) findViewById(R.id.registerUser);
                    txtview.setText(vozReconocida);
                    speakText("Diga una contraseña");
                    reconocimientoDeVoz(VOICE_PASS);
                }
            }
            if (requestCode == VOICE_PASS && resultCode == RESULT_OK) {
                if (matches != null && matches.size() > 0) {
                    txtview = (TextView) findViewById(R.id.registerPassword);
                    txtview.setText(vozReconocida);
                    speakText("Ahora le preguntaremos sobre los elementos que puede superar para ofrecer una mejor guía. Responda si o no.  ¿Puede subir o bajar escaleras?");
                    reconocimientoDeVoz(VOICE_ESCALERAS);
                }
            }
            if (requestCode == VOICE_ESCALERAS && resultCode == RESULT_OK) {
                if (matches != null && matches.size() > 0) {
                    if (vozReconocida.equals("Si")) {
                        txtview = (TextView) findViewById(R.id.escaleras);
                        txtview.setSelected(true);
                        superables[0] = true;
                    }
                    speakText("¿Puede usar ascensores?");
                    reconocimientoDeVoz(VOICE_ASCENSOR);
                }
            }
            if (requestCode == VOICE_ASCENSOR && resultCode == RESULT_OK) {
                if (matches != null && matches.size() > 0) {
                    if (vozReconocida.equals("Si")) {
                        txtview = (TextView) findViewById(R.id.ascensor);
                        txtview.setSelected(true);
                        superables[1] = true;
                    }
                    speakText("¿Puede pasar a traves de puertas?");
                    reconocimientoDeVoz(VOICE_PUERTA);
                }
            }
            if (requestCode == VOICE_PUERTA && resultCode == RESULT_OK) {
                if (matches != null && matches.size() > 0) {
                    if (vozReconocida.equals("Si")) {
                        txtview = (TextView) findViewById(R.id.puerta);
                        txtview.setSelected(true);
                        superables[2] = true;
                    }
                    speakText("¿Puede usar rampas?");
                    reconocimientoDeVoz(VOICE_RAMPA);
                }
            }
            if (requestCode == VOICE_RAMPA && resultCode == RESULT_OK) {
                if (matches != null && matches.size() > 0) {
                    if (vozReconocida.equals("Si")) {
                        txtview = (TextView) findViewById(R.id.rampa);
                        txtview.setSelected(true);
                        superables[3] = true;
                    }
                    discapacidad = 2;
                    if (comprobarDatos() == "") {
                        register();
                        speakText("Registro completado");
                        finish();
                    }
                }
            }
            super.onActivityResult(requestCode, resultCode, data);

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
        //en caso de que se seleccione que no hay discapacidad se asume que puede pasar por los superables
        if (discapacidad == 0){
            superables[0] = true;
            superables[1] = true;
            superables[2] = true;
            superables[3] = true;
            txtview = (TextView) findViewById(R.id.ascensor);
            txtview.setSelected(true);
            txtview = (TextView) findViewById(R.id.puerta);
            txtview.setSelected(true);
            txtview = (TextView) findViewById(R.id.rampa);
            txtview.setSelected(true);
            txtview = (TextView) findViewById(R.id.escaleras);
            txtview.setSelected(true);
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
