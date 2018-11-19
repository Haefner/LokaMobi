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

    public LatLng interpolated;
    public InterpolationType interpolationType;
    public Location location;
    public RecordType recordType;
    private float errorDistance;


    public float getErrorDistance() {

        Location locationInterpolated = new Location("");
        locationInterpolated.setLatitude(interpolated.latitude);
        locationInterpolated.setLongitude(interpolated.longitude);

        errorDistance = location.distanceTo(location);
        return errorDistance;
    }
}
