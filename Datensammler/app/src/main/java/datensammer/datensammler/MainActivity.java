package datensammer.datensammler;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.StrictMode;
import android.provider.Settings;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;

import java.util.LinkedList;

public class MainActivity extends AppCompatActivity {


    SensorManager sensorManager;
    SensorEventListener sensorEventListener;
    LinkedList<Integer> frequenzbereich;

    LocationManager locationManagerGPS;
    LocationListener locationListenerGPS;

    LocationManager locationManagerNetwork;
    LocationListener locationListenerNetwork;



    /**
     * Angabe ob Messung des Accelerometer aktiv ist
     */
    Switch swchAc;
    TextView acValX;
    TextView acValY;
    TextView acValZ;
    Spinner acHz;
    Integer acFrequenz = 1000000;

    /**
     * Angabe ob Messung des Gyroscope aktiv ist
     */
    Switch swchGy;
    TextView gyValX;
    TextView gyValY;
    TextView gyValZ;
    Spinner gyHz;
    Integer gyFrequenz = 1000000;

    /**
     * Angabe ob Messung des Kompass aktiv ist
     */
    Switch swchCo;
    TextView coValX;
    TextView coValY;
    TextView coValZ;
    TextView degrees;
    Spinner coHz;
    Integer coFrequenz = 1000000;

    /**
     * Angabe ob Messung des GPS aktiv ist
     */
    Switch swchGPS;
    TextView tvLatitudeGPS;
    TextView tvLongitudeGPS;
    TextView tvAccuracyGPS;

    /**
     * Angabe ob Messung des NETWORK-Ortung aktiv ist
     */
    Switch swchNETWORK;
    TextView tvLatitudeNetwork;
    TextView tvLongitudeNetwork;
    TextView tvAccuracyNetwork;

    /**
     * Angabe ob die Daten aufgezeichnet werden
     */
    Switch swchRe;

    String androidId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpIDs();
        setUpFreuenzBereich();
        setUpSensorManager();
        // setUpLocationManager();
        setUpSwitch();

        //Kommunikation mit dem Server
        androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        /* GPS */
        if(ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }
    }

    private void setUpFreuenzBereich() {
        frequenzbereich = new LinkedList<>();
        frequenzbereich.add(1000000); //1 Sekunde
        frequenzbereich.add(250000); //viertel Selkunde
        frequenzbereich.add(100000); // 1/10 Sekunde
        frequenzbereich.add(10000); // 1/100 Sekunde

        ArrayAdapter<Integer> frequenzAdapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_item, frequenzbereich);
        frequenzAdapter.setDropDownViewResource(android.R.layout.simple_spinner_item);
        acHz.setAdapter(frequenzAdapter);
        gyHz.setAdapter(frequenzAdapter);
        coHz.setAdapter(frequenzAdapter);

        acHz.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                acFrequenz = Integer.valueOf(String.valueOf(acHz.getSelectedItem()));
                //pruefe ob Messung laeut falls ja, deaktiviere Listener und aktiviere sie neu
                if (swchAc.isChecked()) {
                    deaktivateListener(SensorTyp.ACCELEROMETER);
                    registerListener(SensorTyp.ACCELEROMETER, acFrequenz);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });


        gyHz.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                gyFrequenz = Integer.valueOf(String.valueOf(gyHz.getSelectedItem()));
                //pruefe ob Messung laeut falls ja, deaktiviere Listener und aktiviere sie neu
                if (swchGy.isChecked()) {
                    deaktivateListener(SensorTyp.GYROSCOPE);
                    registerListener(SensorTyp.GYROSCOPE, gyFrequenz);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        coHz.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                coFrequenz = Integer.valueOf(String.valueOf(coHz.getSelectedItem()));
                //pruefe ob Messung laeut falls ja, deaktiviere Listener und aktiviere sie neu
                if (swchCo.isChecked()) {
                    deaktivateListener(SensorTyp.COMPASS);
                    registerListener(SensorTyp.COMPASS, coFrequenz);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    private void setUpSwitch() {
        //Record: sollen die Daten aufgezeichnet werden
//        swchRe.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if (swchRe.isChecked()) {
//                    datenaufnahme.startRecordDate(androidId);
//                } else {
//                    datenaufnahme.stopRecordDate(androidId);
//                }
//            }
//        });

        //Auswertung welche Sensordaten gemessen werden sollen

        //Accelorometer
        swchAc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (swchAc.isChecked()) {
                    registerListener(SensorTyp.ACCELEROMETER, acFrequenz);
                } else {
                    deaktivateListener(SensorTyp.ACCELEROMETER);
                }
            }
        });
        //Gyroscope
        swchGy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (swchGy.isChecked()) {
                    registerListener(SensorTyp.GYROSCOPE, gyFrequenz);
                } else {
                    deaktivateListener(SensorTyp.GYROSCOPE);
                }
            }
        });
        //Compss
        swchCo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (swchCo.isChecked()) {
                    registerListener(SensorTyp.COMPASS, coFrequenz);
                } else {
                    deaktivateListener(SensorTyp.COMPASS);
                }
            }
        });
        //GPS
        swchGPS.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(swchGPS.isChecked()){
                    positionierungMitLocationManagerGPS();
                }else{
                    //Stop GPS-Tracking
                    locationManagerGPS.removeUpdates(locationListenerGPS);
                }
            }
        });
        //NETWORK
        swchNETWORK.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(swchNETWORK.isChecked()){
                    positionierungMitLocationManagerNetwork();
                }else{
                    //Stop NETWORK-Tracking
                    locationManagerNetwork.removeUpdates(locationListenerNetwork);
                }
            }
        });
    }

    /******************************************************************************************************************************
     *      GPS Code BEGIN
     */

    public void positionierungMitLocationManagerGPS(){
        locationManagerGPS = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListenerGPS = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                tvLatitudeGPS.setText("" + location.getLatitude());
                tvLongitudeGPS.setText("" + location.getLongitude());
                tvAccuracyGPS.setText("" + location.getAccuracy());
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);
            }
        };
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }
        locationManagerGPS.requestLocationUpdates(LocationManager.GPS_PROVIDER,
                500,          //minimum time interval between location updates, in milliseconds
                0,         //minimum distance between location updates, in meters
                locationListenerGPS);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (alleBerechtigungenErteilt(grantResults)) {
            //------------------starteApplikation();
        }else{
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }
    }

    private boolean alleBerechtigungenErteilt(int[] erteileBerechtigungen){
        for(int erteileBerechtigung : erteileBerechtigungen){
            if(erteileBerechtigung == PackageManager.PERMISSION_DENIED){
                return false;
            }
        }
        return true;
    }


    /**
     *      GPS Code END
    /******************************************************************************************************************************/

    /******************************************************************************************************************************
     *      NETWORK Code BEGIN
     */

    public void positionierungMitLocationManagerNetwork(){
        locationManagerNetwork = (LocationManager) getSystemService(LOCATION_SERVICE);
        locationListenerNetwork = new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                tvLatitudeNetwork.setText("" + location.getLatitude());
                tvLongitudeNetwork.setText("" + location.getLongitude());
                tvAccuracyNetwork.setText("" + location.getAccuracy());
            }

            @Override
            public void onStatusChanged(String provider, int status, Bundle extras) {

            }

            @Override
            public void onProviderEnabled(String provider) {

            }

            @Override
            public void onProviderDisabled(String provider) {
                Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                startActivity(intent);

            }
        };
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }

        ConnectivityManager connectivityManager = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() != NetworkInfo.State.CONNECTED &&
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() != NetworkInfo.State.CONNECTED) {
            Intent intentWifi = new Intent(Settings.ACTION_WIFI_SETTINGS);
            startActivity(intentWifi);
        }

        locationManagerNetwork.requestLocationUpdates(LocationManager.NETWORK_PROVIDER,
                500,          //minimum time interval between location updates, in milliseconds
                0,         //minimum distance between location updates, in meters
                locationListenerNetwork);
    }


    /**
     *      NETWORK Code END
     /******************************************************************************************************************************/

    private void setUpSensorManager() {
        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        sensorEventListener = new SensorEventListener() {
            @Override
            public void onAccuracyChanged(Sensor sensor, int accuracy) {

            }

            @Override
            public void onSensorChanged(SensorEvent event) {
                switch (event.sensor.getType()) {
                    //Beschleunigungssensor Drehmoment Winkelgeschwindigkeit
                    case Sensor.TYPE_GYROSCOPE:
                        gyValX.setText("" + event.values[0]);
                        gyValY.setText("" + event.values[1]);
                        gyValZ.setText("" + event.values[2]);
                        if (swchRe.isChecked()) {
                            //TODO Datenrecord
                            //  datenaufnahme.recordGyroscope(androidId, event.values[0], event.values[1], event.values[2]);
                        }
                        break;
                    //Bewegungssensor Liniar
                    case Sensor.TYPE_ACCELEROMETER:
                        acValX.setText("" + event.values[0]);
                        acValY.setText("" + event.values[1]);
                        acValZ.setText("" + event.values[2]);
                        if (swchRe.isChecked()) {
                            //TODO Datenrecord
                            //  datenaufnahme.recordAccelometer(androidId, event.values[0], event.values[1], event.values[2]);
                        }
                        break;
                    //Compass
                    case Sensor.TYPE_MAGNETIC_FIELD:
                        coValX.setText("" + event.values[0]);
                        coValY.setText("" + event.values[1]);
                        coValZ.setText("" + event.values[2]);
                        if (swchRe.isChecked()) {
                            //TODO Datenrecord
                            //    datenaufnahme.recordCompass(androidId, event.values[0], event.values[1], event.values[2]);
                        }
                        break;
                }

            }
        };

    }

    /**
     * Aktiviert das Aufzeichnen der Daten
     *
     * @param sensorTyp ACCELEROMETER, GYROSCOPE, LOCATION, LIGHT
     * @param time      Zeit in Microsekunden
     */
    private void registerListener(SensorTyp sensorTyp, int time) {
        if (sensorTyp == SensorTyp.ACCELEROMETER) {
            sensorManager.registerListener(sensorEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER), time);
        } else if (sensorTyp == SensorTyp.GYROSCOPE) {
            sensorManager.registerListener(sensorEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE), time);
         } else if (sensorTyp == SensorTyp.COMPASS) {
            sensorManager.registerListener(sensorEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD), time);
        }
//        } else if (sensorTyp == SensorTyp.LOCATION) {
//            //Pruefe ob Berechtigung fuer GPS vorliegt
//            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//                swchLo.setChecked(false);
//                swchLoState = false;
//                // Meldung anzeigen, dass die Berechtiung nicht vorhanden ist, und erteilt werden muss
//                ActivityCompat.requestPermissions(Datensammlung.this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 1);
//                return;
//            }
//            //Pruefe ob GPS aktiviert ist
//            if (!locationManagerGPS
//                    .isProviderEnabled(LocationManager.GPS_PROVIDER)) {
//                swchLo.setChecked(false);
//                swchLoState = false;
//                startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
//                return;
//            }
//            //minDistance Angabe in Meter
//            locationManagerGPS.requestLocationUpdates(LocationManager.GPS_PROVIDER, time, 0.001f, locationListenerGPS);
//
//        }
        else {
            throw new RuntimeException("SensorTyp is not defined");
        }
    }

    private void deaktivateListener(SensorTyp sensorTyp) {
        if (sensorTyp == SensorTyp.ACCELEROMETER) {
            sensorManager.unregisterListener(sensorEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER));
        } else if (sensorTyp == SensorTyp.GYROSCOPE) {
            sensorManager.unregisterListener(sensorEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE));
        } else if (sensorTyp == SensorTyp.COMPASS) {
            sensorManager.unregisterListener(sensorEventListener, sensorManager.getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD));
//        } else if (sensorTyp == SensorTyp.LOCATION) {
//            locationManagerGPS.removeUpdates(locationListenerGPS);
        } else {
            throw new RuntimeException("SensorTyp is not defined");
        }
    }


    protected void setUpIDs() {
        //ID's for Accelerometer
        swchAc = findViewById(R.id.swchAc);
        acValX = findViewById(R.id.acValX);
        acValY = findViewById(R.id.acValY);
        acValZ = findViewById(R.id.acValZ);
        acHz = findViewById(R.id.acHz);

        //ID's for Gyroscope
        swchGy = findViewById(R.id.swchGy);
        gyValX = findViewById(R.id.gyValX);
        gyValY = findViewById(R.id.gyValY);
        gyValZ = findViewById(R.id.gyValZ);
        gyHz = findViewById(R.id.gyHz);

        //ID's for Compass
        swchCo = findViewById(R.id.swchCo);
        coValX = findViewById(R.id.coValX);
        coValY = findViewById(R.id.coValY);
        coValZ = findViewById(R.id.coValZ);
        coHz = findViewById(R.id.coHz);

        //ID's for GPS
        swchGPS = findViewById(R.id.swchGPS);
        tvLatitudeGPS = findViewById(R.id.tvLatitudeGPS);
        tvLongitudeGPS = findViewById(R.id.tvLongitudeGPS);
        tvAccuracyGPS = findViewById(R.id.tvAccuracyGPS);

        //ID's for NETWORK
        swchNETWORK = findViewById(R.id.swchNetwork);
        tvLatitudeNetwork = findViewById(R.id.tvLatitudeNetwork);
        tvLongitudeNetwork = findViewById(R.id.tvLongitudeNetwork);
        tvAccuracyNetwork = findViewById(R.id.tvAccuracyNetwork);

        //Sonstiges
        //Record
        swchRe = findViewById(R.id.swchRe);

    }


}
