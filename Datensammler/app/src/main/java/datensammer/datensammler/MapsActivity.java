package datensammer.datensammler;

import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

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
    private boolean recordMode;
    private boolean editMode;
    private Marker cursorMarker;
    private LocationMessung locationMessung;
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

        buttonFix.setEnabled(recordMode);
    }



    public void onButtonStartStopClick(View view){

        if(routeMarkerList.isEmpty()){
            Toast.makeText(this,"No Route selected.",Toast.LENGTH_SHORT).show();
            return;
        }

        recordMode = !recordMode;
        buttonFix.setEnabled(recordMode);
        buttonEditMode.setEnabled(!recordMode);

        if(recordMode){
            buttonStartStop.setText("Stop");
        }else{buttonStartStop.setText("Start");}
    }


    public void onButtonFixClick(View view){
        Log.d("Fix","Clicked");

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
