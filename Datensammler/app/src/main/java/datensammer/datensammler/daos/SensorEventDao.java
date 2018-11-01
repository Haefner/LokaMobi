package datensammer.datensammler.daos;

import androidx.room.Dao;
import androidx.room.Insert;
import datensammer.datensammler.entities.AccelerometerEvent;
import datensammer.datensammler.entities.Location;
import datensammer.datensammler.entities.GyroscopeEvent;
import datensammer.datensammler.entities.MagnetometerEvent;

@Dao
public interface SensorEventDao {
    @Insert
    long insertAccelerometerEvent(AccelerometerEvent event);
    @Insert
    long insertGyroscopeEvent(GyroscopeEvent event);

    @Insert
    long insertMagnetometerEvent(MagnetometerEvent event);

    @Insert
    long insertGpsLocation(Location location);
}
