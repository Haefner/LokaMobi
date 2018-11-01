package datensammer.datensammler.daos;

import java.util.List;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;
import datensammer.datensammler.entities.AccelerometerEvent;
import datensammer.datensammler.entities.Location;
import datensammer.datensammler.entities.GyroscopeEvent;
import datensammer.datensammler.entities.MagnetometerEvent;
import datensammer.datensammler.entities.Record;

@Dao
public interface RecordDao {
    @Insert
    long insertRecord(Record record);

    @Update
    int updateRecords(Record... mesasurement);

    @Delete
    int deleteRecords(Record record);

    @Query("SELECT * FROM  record")
    List<Record> getAllRecords();

    @Query("SELECT * FROM record WHERE id = :recordId")
    Record getRecordById(long recordId);

    @Query("SELECT * FROM accelerometerEvent INNER JOIN record ON record.id = accelerometerEvent.record_id WHERE  accelerometerEvent.record_id = :record")
    List<AccelerometerEvent> findAccelerometerEventsByRecord(long record);

    @Query("SELECT * FROM gyroscopeEvent INNER JOIN record ON record.id = gyroscopeEvent.record_id WHERE  gyroscopeEvent.record_id = :record")
    List<GyroscopeEvent> findGyroscopeEventsByRecord(long record);

    @Query("SELECT * FROM Location INNER JOIN record ON record_id = Location.record_id WHERE Location.record_id = :record")
    List<Location> findGpsLocationsByRecord(long record);

    @Query("SELECT * FROM magnetometerevent INNER JOIN record ON record_id = magnetometerevent.record_id WHERE magnetometerevent.record_id = :record")
    List<MagnetometerEvent> findMagnetometerEventsByRecord(long record);
}
