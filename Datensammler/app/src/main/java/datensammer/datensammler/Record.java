package datensammer.datensammler;

import android.location.Location;

import com.google.android.gms.maps.model.LatLng;




public class Record {

    public Record(LatLng interpolated, InterpolationType interpolationType, Location location, RecordType recordType) {
        this.interpolated = interpolated;
        this.interpolationType = interpolationType;
        this.location = location;
        this.recordType = recordType;
    }


    public Record(Location location, RecordType recordType) {
        this.location = location;
        this.recordType = recordType;
    }

    /**
     * Der Interpolierte Punkt der aus der Location berechnet wird.
     */
    public LatLng interpolated;
    public InterpolationType interpolationType;
    /**
     * Der Punkt der vom Location Provider gemessen wird.
     */
    public Location location;
    public RecordType recordType;
    private float errorDistance;


    public float getErrorDistance() {

        Location locationInterpolated = new Location("");
        locationInterpolated.setLatitude(interpolated.latitude);
        locationInterpolated.setLongitude(interpolated.longitude);

        errorDistance = location.distanceTo(locationInterpolated);
        return errorDistance;
    }
}
