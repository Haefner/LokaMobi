package datensammer.datensammler;

import androidx.fragment.app.FragmentActivity;
import datensammer.datensammler.entities.Location;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMarkerDragListener {

    private GoogleMap mMap;
    private List<Marker> routeMarkerList;
    private Button buttonStartStop;
    private Button buttonFix;
    private Button buttonEditMode;
    private Button buttonAddWp;
    private Button buttonResetRoute;
    private Button changeView;
    private boolean recordMode;
    private boolean editMode;
    private Marker cursorMarker;
    private LocationMessung locationMessung;
    private LocationProvider locationProvider;
    private SharedPreferences sharedPref;
    private SharedPreferences.Editor shPrfEditor;
    //Liste, in der zu den gesetzen Wegpunkten die Zeiten speichert.
    private List<android.location.Location> fixWaypoints= new ArrayList<>();
    //Zaeler der mitzahlt beim wievielten Wegpunkt man sich befindet
    int numberWegpunkt=0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        locationMessung = (LocationMessung) getIntent().getExtras().getSerializable("LocationMessung");
        Log.d("Extra",String.valueOf(locationMessung));

        routeMarkerList = new ArrayList<>();

        buttonStartStop = findViewById(R.id.buttonStart);
        buttonFix = findViewById(R.id.buttonFix);
        buttonEditMode = findViewById(R.id.buttonEditRoute);

        buttonAddWp = findViewById(R.id.buttonAddWp);
        buttonResetRoute = findViewById(R.id.buttonResetRoute);
        changeView = findViewById(R.id.buttonChangeView);

        buttonFix.setEnabled(recordMode);

         sharedPref = this.getPreferences(Context.MODE_PRIVATE);
         shPrfEditor = sharedPref.edit();
    }


    private void loadSavedRoute(){
        String jsonRouteList = sharedPref.getString("SavedRoute","");
        Log.d("List",jsonRouteList);
        if(jsonRouteList.equals("")){
            return;
        }else{
            ArrayList<LatLng> positionList;
            Gson gson = new Gson();
            Type listType = new TypeToken<ArrayList<LatLng>>() {}.getType();
            positionList = gson.fromJson(jsonRouteList,listType);

            for(LatLng position : positionList){
                Marker marker = mMap.addMarker(new MarkerOptions().position(position).title("WP "+String.valueOf(routeMarkerList.size()+1)).draggable(true));
                routeMarkerList.add(marker);
            }
        }

    }


    private  void saveRoute(){
        ArrayList<LatLng> positionList = new ArrayList<>();
        Gson gson = new Gson();
        Type listType = new TypeToken<ArrayList<LatLng>>() {}.getType();

        for(Marker marker : routeMarkerList){

            positionList.add(marker.getPosition());

        }
        String jsonList = gson.toJson(positionList,listType);
        Log.d("List",jsonList);
        shPrfEditor.putString("SavedRoute",jsonList);
        shPrfEditor.commit();
    }
    public void onButtonStartStopClick(View view) {

        if (routeMarkerList.isEmpty()) {
            Toast.makeText(this, "No Route selected.", Toast.LENGTH_SHORT).show();
            return;
        }

        recordMode = !recordMode;
        buttonFix.setEnabled(recordMode);
        buttonEditMode.setEnabled(!recordMode);

        if (recordMode) {
            //Button Start wurde gedrueckt
            buttonStartStop.setText("Stop");
            locationProvider = new LocationProvider(this,this,locationMessung);
            locationProvider.start();
        } else {
            //Button Stop wurde gedrueckt
            buttonStartStop.setText("Start");
            locationProvider.stop();
            //Setze den Wegpunkt der den Zeitpunkten zugeordnet werden soll zurück auf den ersten Wert der Liste.
            numberWegpunkt = 0;
            fixWaypoints.clear();

        }
    }

    /**
     * Jedes mal, wenn Fix geklickt wird, ordne dem Zeitstempel den nächsten Wegpunkt zu.
     * Ist die Liste abgearbeitet, dann fange wieder bei eins an
     * @param view
     */
    public void onButtonFixClick(View view){
        Log.d("Fix","Clicked");
        android.location.Location location = new android.location.Location("Messpunkt");
        location.setTime(System.currentTimeMillis());
        location.setLatitude(routeMarkerList.get(numberWegpunkt).getPosition().latitude);
        location.setLongitude(routeMarkerList.get(numberWegpunkt).getPosition().longitude);
        fixWaypoints.add(location);
        numberWegpunkt= numberWegpunkt + 1 ;
        if(numberWegpunkt==routeMarkerList.size())
        {
            //Alle Wegpunkte sind zugeordnet. Man muss erst stopp und start für die nächste Messung drücken
            buttonFix.setEnabled(false);
        }
    }

    public void onButtonEditRoute(View view){

        editMode = !editMode;
        buttonStartStop.setEnabled(!editMode);

        if(editMode){
            buttonEditMode.setText("Save Route");
            buttonResetRoute.setVisibility(View.VISIBLE);
            buttonAddWp.setVisibility(View.VISIBLE);
        }else{
            buttonEditMode.setText("Edit Route");
            buttonResetRoute.setVisibility(View.INVISIBLE);
            buttonAddWp.setVisibility(View.INVISIBLE);
            cursorMarker.remove();

            saveRoute();
        }



        if(editMode){
                cursorMarker = mMap.addMarker(new MarkerOptions().position(mMap.getCameraPosition().target).title("Cursor"));
                cursorMarker.setIcon(BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE));
                cursorMarker.setDraggable(true);
        }

    }


    public  void onButtonResetRouteClick(View view){

        routeMarkerList.forEach( marker -> marker.remove());
        routeMarkerList = new ArrayList<>();

    }

    public void onButtonAddWpClick(View view){
        Marker marker = mMap.addMarker(new MarkerOptions().position(cursorMarker.getPosition()).title("WP "+String.valueOf(routeMarkerList.size()+1)).draggable(true));
        routeMarkerList.add(marker);
    }

    public void onButtonChangeView(View view)
    {
        //Satellitenansicht
        if(mMap.getMapType() == GoogleMap.MAP_TYPE_HYBRID)
        {
            mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        }
        else{
        mMap.setMapType(GoogleMap.MAP_TYPE_HYBRID);}
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mMap.setOnMarkerDragListener(this);
        loadSavedRoute();

        //Zoome zur Hochschule Bochum
        LatLng myPosition= new LatLng(51.447561,7.270792);
        CameraUpdate yourLocation = CameraUpdateFactory.newLatLngZoom(myPosition, 19);
        mMap.animateCamera(yourLocation);

    }



    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
    }
}
