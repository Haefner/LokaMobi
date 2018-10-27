package datensammer.datensammler.api;

import java.util.List;

import datensammer.datensammler.entities.AccelerometerEvent;
import datensammer.datensammler.entities.GpsLocation;
import datensammer.datensammler.entities.GyroscopeEvent;
import datensammer.datensammler.entities.Record;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface RestService {
    @POST("records")
    Call<Record> addRecord(@Body Record record);

    @POST("records/{record_id}/locations")
    Call<Void> addLocations(@Body List<GpsLocation> locations, @Path("record_id") long record_id);

    @POST("records/{record_id}/gyroscope")
    Call<Void> addGyroscopeData(@Body List<GyroscopeEvent> gyroscopeData, @Path("record_id") long record_id);

    @POST("records/{record_id}/accelerometer")
    Call<Void> addAccelerometerData(@Body List<AccelerometerEvent> accelerometerData, @Path("record_id") long record_id);
}
