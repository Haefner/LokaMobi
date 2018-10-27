package datensammer.datensammler.daos;

import androidx.room.Dao;
import androidx.room.Insert;
import datensammer.datensammler.entities.AccelerometerEvent;
import datensammer.datensammler.entities.GpsLocation;
import datensammer.datensammler.entities.GyroscopeEvent;

@Dao
public interface SensorEventDao {
    @Insert
    long insertAccelerometerEvent(AccelerometerEvent event);
    @Insert
    long insertGyroscopeEvent(GyroscopeEvent event);

    @Insert
    long insertGpsLocation(GpsLocation location);
}
