package com.example.jorge.guidin;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBar;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;



public class Acelerometro extends ActionBarActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks, SensorEventListener {

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private float[] magnetic_angles;
    private float[] accelerations;
    private TextView accX;
    private TextView accY;
    private TextView accZ;
    private TextView giroX;
    private TextView giroY;
    private TextView giroZ;
    private float giroSobreX;
    private float giroSobreY;
    private float giroSobreZ;
    private TextView giroValido;
    private TextView accXNorm;
    private TextView accYNorm;
    private TextView accZNorm;
    private TextView accXMapa;
    private TextView accYMapa;
    private SensorManager mSensorManager;
    private boolean esGiroValido;
    private SensorEventListener giroscopio;

    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_acelerometro);

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                (DrawerLayout) findViewById(R.id.drawer_layout));

        accX = (TextView) findViewById(R.id.txtAccX);
        accY = (TextView) findViewById(R.id.txtAccY);
        accZ = (TextView) findViewById(R.id.txtAccZ);
        giroX = (TextView) findViewById(R.id.txtGiroX);
        giroY = (TextView) findViewById(R.id.txtGiroY);
        giroZ = (TextView) findViewById(R.id.txtGiroZ);
        giroValido = (TextView) findViewById(R.id.txtGiroValido);
        accXNorm = (TextView) findViewById(R.id.txtAccXSuelo);
        accYNorm = (TextView) findViewById(R.id.txtAccYSuelo);
        accZNorm = (TextView) findViewById(R.id.txtAccZSuelo);
        accXMapa = (TextView) findViewById(R.id.txtAccXMapa);
        accYMapa = (TextView) findViewById(R.id.txtAccYMapa);

        mSensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

        //Sensor s = mSensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER).get(0);
        Sensor s = mSensorManager.getSensorList(Sensor.TYPE_ACCELEROMETER).get(0);
        Sensor s3 = mSensorManager.getSensorList(Sensor.TYPE_MAGNETIC_FIELD).get(0);

        giroscopio = new SensorEventListener(){

            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }

            public void onSensorChanged(SensorEvent event) {
                magnetic_angles = event.values;
				/*giroX.setText("" + event.values[1]);
				giroY.setText("" + event.values[2]);
				giroZ.setText("" + event.values[0]);
				if(event.values[2]>40 && ((event.values[1] <40 || event.values[1]>-40) ||(event.values[1] <10 || event.values[1]>170))){
					giroValido.setText("SI");
				}else{
					giroValido.setText("NO");
				}*/
            }

        };

        mSensorManager.registerListener(this, s, SensorManager.SENSOR_DELAY_NORMAL);
        //mSensorManager.registerListener(giroscopio, s2, SensorManager.SENSOR_DELAY_NORMAL);
        mSensorManager.registerListener(giroscopio, s3, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction()
                .replace(R.id.container, PlaceholderFragment.newInstance(position + 1))
                .commit();
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_section1);
                break;
            case 2:
                mTitle = getString(R.string.title_section2);
                break;
            case 3:
                mTitle = getString(R.string.title_section3);
                break;
        }
    }

    public void restoreActionBar() {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setTitle(mTitle);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.acelerometro, menu);
            restoreActionBar();
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_acelerometro, container, false);
            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((Acelerometro) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }
    }


    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO Auto-generated method stub

    }

    public void onSensorChanged(SensorEvent event) {
        accelerations = event.values;
        accX.setText("" + event.values[0]);
        accY.setText("" + event.values[1]);
        accZ.setText("" + event.values[2]);

        if(magnetic_angles!=null){
            float[] R = new float[9];
            //float[] R2 = new float[9];
            float[] I = new float[9];
            float[] result = new float[3];
            //float aux;
            if(SensorManager.getRotationMatrix(R, I, accelerations, magnetic_angles)){
                //float inclination = (float)((SensorManager.getInclination(I)*180)/Math.PI);
                //accZNorm.setText("" + inclination);
                //SensorManager.remapCoordinateSystem(R, SensorManager.AXIS_X, SensorManager.AXIS_Y, R2);
//				float[] I2 = new float[9];
//				float[] R2 = new float[9];
                //SensorManager.remapCoordinateSystem(I, SensorManager.AXIS_X, SensorManager.AXIS_Z, I2);
                //SensorManager.remapCoordinateSystem(R, SensorManager.AXIS_X, SensorManager.AXIS_Z, R2);
                SensorManager.getOrientation(R, result);

                giroSobreX = (float) Math.toDegrees(result[1]);
                giroX.setText("" + giroSobreX);
                giroSobreY= (float) Math.toDegrees(result[2]);
                //giroSobreY = SensorManager.getInclination(I);
                giroY.setText("" + giroSobreY);
                giroSobreZ = (float) Math.toDegrees(result[0]);


                giroZ.setText("" + giroSobreZ);
            }

            if((giroSobreX>=-20 && giroSobreX<=20) && (giroSobreY<=-50 && giroSobreY>=-130)){
                esGiroValido = true;
                giroValido.setText("SI");
            }else{
                esGiroValido = false;
                giroValido.setText("NO");
            }

            //Calculo la aceleración en el eje X quitando lo correspondiente a la gravedad

            float newAngle = 0;

            newAngle = 90 + giroSobreY;

            double radians = Math.toRadians(newAngle);
            double accAux = 0;

            if(accelerations[0]>0){
                accAux = accelerations[0] - Math.cos(radians)* SensorManager.STANDARD_GRAVITY;
            }else{
                accAux = accelerations[0] + Math.cos(radians)* SensorManager.STANDARD_GRAVITY;
            }
            if(esGiroValido){
                accXNorm.setText("" + accAux);
            }else{
                accXNorm.setText("-");
            }


            //Calculo la aceleración en el eje Z combinando los giros de X e Y

            if(giroSobreY<=0 && giroSobreY>=-90){
                newAngle = -giroSobreY;
            }else{
                newAngle = 180 - Math.abs(giroSobreY);
            }
            double radiansY = Math.toRadians(newAngle);

            //	float aux2 = (float) Math.abs(Math.toRadians(giroSobreX));

            if(accelerations[2]>0){
                accAux = accelerations[2] - Math.cos(radiansY)* SensorManager.STANDARD_GRAVITY;

            }else{
                accAux = accelerations[2] +  Math.cos(radiansY)* SensorManager.STANDARD_GRAVITY;
            }
            accZNorm.setText("" + accAux);
            if(accAux>1){
                int pepe = 1;
                accYNorm.setText("" + pepe);
            }
            if(esGiroValido){

                //Redirijo el ángulo de Z por el giro del móvil en modo landscape
                giroSobreZ = giroSobreZ + 90;
                if(giroSobreZ > 180) giroSobreZ = giroSobreZ - 360;
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
                accXMapa.setText("" + accX);
                accYMapa.setText("" + accY);
            }else{
                accZNorm.setText("-");
            }

        }
    }

}
