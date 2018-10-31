package datensammer.datensammler.api;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.util.List;
import java.util.concurrent.TimeUnit;

import datensammer.datensammler.daos.RecordDao;
import datensammer.datensammler.daos.SensorEventDao;
import datensammer.datensammler.database.AppDatabase;
import datensammer.datensammler.database.Database;
import datensammer.datensammler.entities.AccelerometerEvent;
import datensammer.datensammler.entities.GpsLocation;
import datensammer.datensammler.entities.GyroscopeEvent;
import datensammer.datensammler.entities.Record;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class Repository {
    private RestService service;
    private static Repository instance;
    private RecordDao recordDao;
    private SensorEventDao sensorEventDao;
    private AppDatabase mDb;


    private Repository(Context context){

        //Logger für Debugging initialisieren.
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()
                .readTimeout(3,TimeUnit.MINUTES)
                .writeTimeout(3,TimeUnit.MINUTES)
                .addInterceptor(interceptor)
                .build();

        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
                .excludeFieldsWithoutExposeAnnotation()
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://lokamotion.de/api/v1/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                .client(client)
                .build();


        service = retrofit.create(RestService.class);

        mDb = Database.getInstance(context);
        recordDao = mDb.getRecordDao();
        sensorEventDao = mDb.getSensorEventDao();

    }

    public static Repository getInstance(Context context){
        if(instance == null){
            instance = new Repository(context);
        }
        return instance;
    }

    public  Call<Record> postRecord(Record record){

        return service.addRecord(record);
    }


    public  Call<Void> postAccelerometerData(List<AccelerometerEvent> acclereometerData, long record_id){

        return service.addAccelerometerData(acclereometerData,record_id);
    }


    public  Call<Void> postGyroscopeData(List<GyroscopeEvent> gyroscopeData, long record_id){

        return service.addGyroscopeData(gyroscopeData,record_id);
    }

    public Call<Void> postLocations(List<GpsLocation> locations, long record_id){

        return service.addLocations(locations,record_id);
    }

    public long addRecord(Record record){
        return  recordDao.insertRecord(record);
    }

    public void addGpsLocation(GpsLocation location){
        sensorEventDao.insertGpsLocation(location);
    }

    public void addAccelerometerEvent(AccelerometerEvent event){
        sensorEventDao.insertAccelerometerEvent(event);
    }


    public void addGyroscopeEvent(GyroscopeEvent event){
        sensorEventDao.insertGyroscopeEvent(event);
    }



    public List<Record> getAllRecords(){
        return recordDao.getAllRecords();
    }

    public List<AccelerometerEvent> findAccelerometerEventsByRecord(long record){
        return recordDao.findAccelerometerEventsByRecord(record);
    }

   public List<GyroscopeEvent> findGyroscopeEventsByRecord(long record){
        return recordDao.findGyroscopeEventsByRecord(record);
   }

    public List<GpsLocation> findGpsLocationsByRecord(long record){
        return recordDao.findGpsLocationsByRecord(record);
    }
}
