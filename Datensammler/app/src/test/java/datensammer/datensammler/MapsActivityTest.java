package datensammer.datensammler;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.junit.Test;

import java.util.List;


import android.location.Location;
import android.util.Log;

import static org.junit.Assert.*;

public class MapsActivityTest {

    @Test
    public void interpolatePointsLocation() {
        MapsActivity activity = new MapsActivity();
        Location location1 = new Location("r");
        Location location2 = new Location("loc2");
        LatLng lat1 = new LatLng(51.448344,7.271675);
        LatLng lat2 = new LatLng(51.447996,7.270618);
        location1.setLongitude(7.271675);
        location1.setLatitude(51.448344);
        location2.setLongitude(7.270618);
        location2.setLatitude(51.447996);
        System.out.println("hi oliver");

        List<LatLng> test;
        test = activity.interpolatePointsLocation(lat1,lat2,10000, 30000);
        if(test.isEmpty())
        {
            System.out.println("failed");
        }
        for(int i=0; i<test.size();i++){
            System.out.println(String.valueOf(test.get(i).latitude));
        }
        for(int i=0; i<test.size();i++){
            System.out.println(String.valueOf(test.get(i).longitude));
        }
    }
}