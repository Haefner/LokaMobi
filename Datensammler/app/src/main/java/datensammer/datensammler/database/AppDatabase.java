package datensammer.datensammler.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;
import datensammer.datensammler.daos.RecordDao;
import datensammer.datensammler.daos.SensorEventDao;
import datensammer.datensammler.entities.AccelerometerEvent;
import datensammer.datensammler.entities.GpsLocation;
import datensammer.datensammler.entities.GyroscopeEvent;
import datensammer.datensammler.entities.Record;

@Database(entities = {AccelerometerEvent.class, Record.class, GyroscopeEvent.class, GpsLocation.class},version = 1,exportSchema = false)
@TypeConverters({Converters.class})

public abstract class AppDatabase extends RoomDatabase {
    public abstract RecordDao getRecordDao();
    public abstract SensorEventDao getSensorEventDao();
}