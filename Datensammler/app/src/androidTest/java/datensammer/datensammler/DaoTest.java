package datensammer.datensammler;


import android.content.Context;


import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Date;
import java.util.List;

import androidx.room.Room;
import androidx.test.InstrumentationRegistry;
import androidx.test.runner.AndroidJUnit4;
import datensammer.datensammler.daos.RecordDao;
import datensammer.datensammler.daos.SensorEventDao;
import datensammer.datensammler.database.AppDatabase;
import datensammer.datensammler.entities.AccelerometerEvent;
import datensammer.datensammler.entities.Record;


@RunWith(AndroidJUnit4.class)
public class DaoTest {
    private RecordDao mRecordDao;
    private SensorEventDao mSensorEventDao;
    private AppDatabase mDb;

    @Before
    public void createDb(){
        Context context = InstrumentationRegistry.getTargetContext();
        mDb = Room.inMemoryDatabaseBuilder(context,AppDatabase.class).build();
        mRecordDao = mDb.getRecordDao();
        mSensorEventDao = mDb.getSensorEventDao();
    }

    @After
    public void closeDb(){
        mDb.close();
    }

    @Test
    public void writeRecordAndRetrieve(){
        Record Record = new Record();
        Record result;
        Record.id =1;
        Record.timeStart = new Date();
        Record.timeEnd = new Date((Record.timeStart.getTime()+60L)*1000L);
        long id =  mRecordDao.insertRecord(Record);

        result = mRecordDao.getRecordById(id);
        assertNotNull(result);
        assertEquals(result.id,Record.id);
    }

    @Test
    public void writeAccelerometerEventAndRetrieveByRecord(){

        Record Record = new Record();
        Record.timeStart = new Date();
        Record.timeEnd = new Date((Record.timeStart.getTime()+60L)*1000L);
        long id =  mRecordDao.insertRecord(Record);

        mRecordDao.insertRecord(Record);

        AccelerometerEvent accelerometerEvent = new AccelerometerEvent();
        accelerometerEvent.timestamp = 545454L;
        accelerometerEvent.x = 2.0f;
        accelerometerEvent.y =4.99f;
        accelerometerEvent.z = 9.81f;

        accelerometerEvent.recordId = id;

        long eventId =  mSensorEventDao.insertAccelerometerEvent(accelerometerEvent);
        List<AccelerometerEvent> events =  mRecordDao.findAccelerometerEventsByRecord(id);

        assertTrue("List must not be empty.",events.size() > 0);
        assertEquals(events.get(0).id , eventId);


    }

    @Test
    public void insertRecordsAndGetAll(){
        Record Record = new Record();
        Record.timeStart = new Date();
        Record.timeEnd = new Date((Record.timeStart.getTime()+60L)*1000L);
        mRecordDao.insertRecord(Record);
        mRecordDao.insertRecord(Record);
        mRecordDao.insertRecord(Record);
        mRecordDao.insertRecord(Record);

        List<Record> Records = mRecordDao.getAllRecords();
        assertTrue("List must not be empty.",Records.size() >0);
    }
}
