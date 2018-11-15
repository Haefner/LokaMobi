package datensammer.datensammler;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.StrictMode;
import android.provider.Settings;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import datensammer.datensammler.api.Repository;
import datensammer.datensammler.entities.AccelerometerEvent;
import datensammer.datensammler.entities.Location;
import datensammer.datensammler.entities.GyroscopeEvent;
import datensammer.datensammler.entities.LocationType;
import datensammer.datensammler.entities.MagnetometerEvent;
import datensammer.datensammler.entities.Record;

import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Date;
import java.util.LinkedList;

public class MainActivity extends AppCompatActivity {

    Repository repo;
    Button buttonMapsView;


    String androidId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setUpIDs();

        //Kommunikation mit dem Server
        androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


        repo = Repository.getInstance(getApplicationContext());

        buttonMapsView = findViewById(R.id.buttonMapView);
    }



    protected void setUpIDs() {

    }




    public void onButtonShowMapClick(View view){

        Intent intent = new Intent(this,MapsActivity.class);
        intent.putExtra("LocationMessung", LocationMessung.FUSED_LOCATION_PROVIDER_BALANCED_POWER);
        startActivity(intent);

    }


}
