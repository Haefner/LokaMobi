package datensammer.datensammler;

import androidx.fragment.app.FragmentActivity;
import datensammer.datensammler.entities.ArtDesCircle;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
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
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
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
    private Button buttonAuswertung;
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
        buttonAuswertung = findViewById(R.id.buttonAuswertung);

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

    /*
     * Androids Back-Button
     */
    @Override
    public void onBackPressed(){
        if(locationProvider != null){
            locationProvider.stop();
        }
        super.onBackPressed();
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
            buttonAuswertung.setVisibility(View.INVISIBLE);
            entferneAuswertung();
        } else {
            //Button Stop wurde gedrueckt

            //Ergebnisse in CSV speichern.
            CsvExporter csvExporter = new CsvExporter(this,this);


            buttonStartStop.setText("Start");
            buttonAuswertung.setVisibility(View.VISIBLE);
            locationProvider.stop();
            interpolatePoints(locationProvider.getRecordList());
            List<Record> test = locationProvider.getRecordList();
//-------------            csvExporter.write(locationProvider.getRecordList());

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

        locationProvider.fix(routeMarkerList.get(numberWegpunkt).getPosition());
        numberWegpunkt= numberWegpunkt + 1 ;
        if(numberWegpunkt==routeMarkerList.size())
        {
            //Alle Wegpunkte sind zugeordnet. Man muss erst stopp und start für die nächste Messung drücken
            buttonFix.setEnabled(false);
            onButtonStartStopClick(view);
        }
    }

    public void onButtonEditRoute(View view){

        editMode = !editMode;
        buttonStartStop.setEnabled(!editMode);

        if(editMode){
            //Modus Bearbeiten
            buttonEditMode.setText("Save Route");
            buttonResetRoute.setVisibility(View.VISIBLE);
            buttonAddWp.setVisibility(View.VISIBLE);
            entferneAuswertung();
            buttonAuswertung.setVisibility(View.INVISIBLE);
        }else{
            //Modus speichern
            buttonEditMode.setText("Edit Route");
            buttonResetRoute.setVisibility(View.INVISIBLE);
            buttonAddWp.setVisibility(View.INVISIBLE);
            cursorMarker.remove();
            buttonAuswertung.setVisibility(View.INVISIBLE);

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


    private boolean auswertungAnzeigenOn =false;
    //Speicher alle Kreise um sie später wieder löschen zu können
    List<Circle> mapCircle=new ArrayList<>();
    public void onButtonAuswertung(View view)
    {

        List<Record> recordList = locationProvider.getRecordList();
        //Auswertung anzeigen
        if (auswertungAnzeigenOn == false) {
           for(Record record : recordList){
               switch(record.interpolationType){
                   case INTERPOLATED_POINT:
                       visuelleAuswertung(record.interpolated,ArtDesCircle.interpoliert,mMap);
                       visuelleAuswertung(new LatLng(record.location.getLatitude(),record.location.getLongitude()),ArtDesCircle.messpunkt,mMap);
                       break;
                   default:
                       visuelleAuswertung(new LatLng(record.location.getLatitude(),record.location.getLongitude()),ArtDesCircle.messpunkt,mMap);
               }
           }
            auswertungAnzeigenOn =true;
        }
        else{
            entferneAuswertung();
            auswertungAnzeigenOn =false;
        }
    }

    private void entferneAuswertung()
    {
        //Entferne alle Kreise
        for(Circle circle:mapCircle)
        {circle.remove();}
    }

    private void visuelleAuswertung(LatLng latLong, ArtDesCircle artDesCircle, GoogleMap mMap)
    {

        CircleOptions options = new CircleOptions();
        if (artDesCircle == ArtDesCircle.interpoliert) {
            options.fillColor(Color.RED);
            options.strokeColor(Color.RED);
        }
        if (artDesCircle == ArtDesCircle.messpunkt) {
            options.fillColor(Color.DKGRAY);
            options.strokeColor(Color.DKGRAY);
        }
        options.radius(1);
        options.center(latLong);

        mapCircle.add(mMap.addCircle(options));
    }

    List<LatLng> interpolatePointsLocation(LatLng latLng1,  LatLng latLng2, long t1, long t2){
        List<LatLng> interpoliertenKoordinatenList = new ArrayList<>();
        double stepTimeinms = 1000.0;
        double deltaTime;
        double newLongitude;
        double newLatitude;

        double deltaLongitude = latLng2.longitude - latLng1.longitude;
        double deltaLatitude = latLng2.latitude - latLng1.latitude;

        double gesamtTime = t2 - t1;
        double currentTimeforLoop = t1 + stepTimeinms;



        while (currentTimeforLoop < t2){

            deltaTime = (currentTimeforLoop - t1)/gesamtTime;
            newLongitude =  latLng1.longitude + deltaLongitude*deltaTime;
            newLatitude =  latLng1.latitude + deltaLatitude*deltaTime;
            LatLng newLatLng = new LatLng(newLatitude,newLongitude);

            interpoliertenKoordinatenList.add(newLatLng);
            currentTimeforLoop = currentTimeforLoop + stepTimeinms;
            Log.d("test", String.valueOf(currentTimeforLoop));
        }
        return interpoliertenKoordinatenList;
    }

    void interpolatePoints(List<Record> recordList){

        int positionVonMarkerInRecordlist[] = new int[routeMarkerList.size()];
        int j = 0;
        for (int i = 0; i < recordList.size(); i++) {
            if (recordList.get(i).recordType == RecordType.FIX) {
                positionVonMarkerInRecordlist[j] = i;
                j++;
            }
        }
        double newLongitude;
        double newLatitude;

        for(int l = 0; l<routeMarkerList.size()-1;l++){

            int anzInterpolatePoints = positionVonMarkerInRecordlist[l+1] - positionVonMarkerInRecordlist[l+0] - 1;
            double deltaLongitude = routeMarkerList.get(l+1).getPosition().longitude -  routeMarkerList.get(l+0).getPosition().longitude;
            double deltaLatitude =  routeMarkerList.get(l+1).getPosition().latitude -  routeMarkerList.get(l+0).getPosition().latitude;
            float stepSize = 1/anzInterpolatePoints;

            for(int k = 0; k<anzInterpolatePoints; k++){
                stepSize = (k+1)*stepSize;
                newLongitude =  recordList.get(positionVonMarkerInRecordlist[l+0]).location.getLongitude() + deltaLongitude*stepSize;
                newLatitude =  recordList.get(positionVonMarkerInRecordlist[l+0]).location.getLatitude() + deltaLatitude*stepSize;
                LatLng newLatLng = new LatLng(newLatitude,newLongitude);
                recordList.get(positionVonMarkerInRecordlist[l+0]+k+1).interpolated = newLatLng;
            }
        }
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
