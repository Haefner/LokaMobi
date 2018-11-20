package datensammer.datensammler;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnSuccessListener;

import java.util.ArrayList;
import java.util.List;

import static com.google.android.gms.location.LocationServices.getFusedLocationProviderClient;

import androidx.core.app.ActivityCompat;


public class LocationProvider {


    private List<Record> recordList;
    LocationMessung m_enumLM;
    Context m_context;
    Activity m_activity;

    /* LocationManager */
    LocationManager locationManager;
    LocationListener locationListener;

    /* FusedLocationProviderClient */
    public LocationRequest locationRequest;
    LocationCallback locationCallback;


    public LocationProvider(Context context, Activity activity, LocationMessung enumLocationMessung) {
        m_context = context;
        m_activity = activity;
        m_enumLM = enumLocationMessung;
        recordList = new ArrayList<>();
    }

    public void stop() {
        /* stop LocationManager */
        if (locationManager != null) {
            locationManager.removeUpdates(locationListener);
        }

        /* stop FusedLocationProviderClient */
        if (getFusedLocationProviderClient(m_activity) != null && locationCallback != null) {
            getFusedLocationProviderClient(m_activity).removeLocationUpdates(locationCallback);
        }
    }


    public void start() {

        Log.d("LocationProvider", "Start.........................");

        if (m_enumLM == LocationMessung.GPS_LOCATION_PROVIDER ||
                m_enumLM == LocationMessung.NETZWERK_LOCATION_PROVIDER) {
            Log.d("locationManager", "positionierungMitLocationManager.........................");

            positionierungMitLocationManager();

        } else if (m_enumLM == LocationMessung.FUSED_LOCATION_PROVIDER_BALANCED_POWER ||
                m_enumLM == LocationMessung.FUSED_LOCATION_PROVIDER_HIGH_ACCUANCY ||
                m_enumLM == LocationMessung.FUSED_LOCATION_PROVIDER_LOW_POWER ||
                m_enumLM == LocationMessung.FUSED_LOCATION_PROVIDER_NO_POWER) {
            Log.d("FusedLocationProviderClient", "positionierungMitFusedLocationProviderClient--------------------------------");
            postionierungMitFusedLocationProviderClient();
        } else {
            Toast.makeText(m_context, "No LocationProvider selected.", Toast.LENGTH_SHORT).show();
        }

    }


    public void positionierungMitLocationManager() {
        if (ActivityCompat.checkSelfPermission(m_context.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(m_activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }

        locationManager = (LocationManager) m_context.getSystemService(Context.LOCATION_SERVICE);
        locationListener = new LocationListener() {
            /* Variable for first location data */
            boolean isFirstLocation = true;

            @Override
            public void onLocationChanged(Location location) {
                if (isFirstLocation) {
                    Toast.makeText(m_context, "First Location Data received.", Toast.LENGTH_SHORT).show();
                    isFirstLocation = false;
                }
                //set locationVariable
                Log.d("locationManager", "....................Längengrad: " + location.getLongitude());

                /***********************************************************************************/

                recordList.add(new Record(null,InterpolationType.INTERPOLATED_POINT,location,RecordType.AUTO));
                /*********************************************************************************/

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
                m_activity.startActivity(intent);
            }
        };
        //if (ActivityCompat.checkSelfPermission(m_context.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(m_context.getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        //    ActivityCompat.requestPermissions(m_activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        //}


        if (m_enumLM == LocationMessung.GPS_LOCATION_PROVIDER) {
            Log.d("locationManager", "GPS.........................");
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 0, locationListener);
        } else if (m_enumLM == LocationMessung.NETZWERK_LOCATION_PROVIDER) {
            Log.d("locationManager", "NETZWERK.........................");
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, locationListener);
        }
    }

    public void postionierungMitFusedLocationProviderClient() {
        locationRequest = new LocationRequest();
        locationRequest.setInterval(1000);
        locationRequest.setMaxWaitTime(1000);

        switch (m_enumLM) {
            case FUSED_LOCATION_PROVIDER_HIGH_ACCUANCY:
                locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
                Log.d("FusedLocationProviderClient", "HIGH_ACCUANCY--------------------------------");
                break;
            case FUSED_LOCATION_PROVIDER_BALANCED_POWER:
                locationRequest.setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY);
                Log.d("FusedLocationProviderClient", "BALANCED_POWER_ACCURACY--------------------------------");
                break;
            case FUSED_LOCATION_PROVIDER_LOW_POWER:
                locationRequest.setPriority(LocationRequest.PRIORITY_LOW_POWER);
                Log.d("FusedLocationProviderClient", "LOW_POWER--------------------------------");
                break;
            case FUSED_LOCATION_PROVIDER_NO_POWER:
                locationRequest.setPriority(LocationRequest.PRIORITY_NO_POWER);
                Log.d("FusedLocationProviderClient", "NO_POWER--------------------------------");
                break;
        }

        if (ActivityCompat.checkSelfPermission(m_context.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(m_context.getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(m_activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }

        getFusedLocationProviderClient(m_activity).requestLocationUpdates(locationRequest, locationCallback = new LocationCallback() {
            /* Variable for first location data */
            boolean isFirstLocation = true;

            public void onLocationResult(LocationResult locationResult) {
                if (isFirstLocation) {
                    Toast.makeText(m_context, "First Location Data received.", Toast.LENGTH_SHORT).show();
                    isFirstLocation = false;
                }
                //onLocatioChanged(locationResult.getLastLocation());
                Log.d("FusedLocationProviderClient", "------------------- Längengrad: " + locationResult.getLastLocation().getLongitude());

                /***********************************************************************************/
                recordList.add(new Record(null,InterpolationType.INTERPOLATED_POINT,locationResult.getLastLocation(),RecordType.AUTO));
                /*********************************************************************************/

            }
        }, Looper.myLooper());


    }


    private void onLocatioChanged(Location location) {

    }

    public List<Record> getRecordList() {
        return recordList;
    }


    public void fix(LatLng wp) {
        Location location;
        if (ActivityCompat.checkSelfPermission(m_context.getApplicationContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(m_context.getApplicationContext(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(m_activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 0);
        }

        if (m_enumLM == LocationMessung.GPS_LOCATION_PROVIDER ||
                m_enumLM == LocationMessung.NETZWERK_LOCATION_PROVIDER) {

            location  = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            recordList.add(new Record(wp,InterpolationType.WAYPOINT,location,RecordType.FIX));
            Log.d("fix point:", String.valueOf(recordList.get(0).location.getLatitude()));
            return;

        }
            getFusedLocationProviderClient(m_context).getLastLocation().addOnSuccessListener(m_activity, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    recordList.add(new Record(wp,InterpolationType.WAYPOINT,location,RecordType.FIX));
                }
            });



    }
}

