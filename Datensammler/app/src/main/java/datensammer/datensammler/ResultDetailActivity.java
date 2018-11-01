package datensammer.datensammler;


import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;

import java.util.Date;
import java.util.List;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import datensammer.datensammler.api.Repository;
import datensammer.datensammler.entities.AccelerometerEvent;
import datensammer.datensammler.entities.Location;
import datensammer.datensammler.entities.GyroscopeEvent;
import datensammer.datensammler.entities.MagnetometerEvent;
import datensammer.datensammler.entities.Record;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResultDetailActivity extends AppCompatActivity {
    Repository repository;
    Record record;
    List<AccelerometerEvent> accelerometerEventList;
    List<GyroscopeEvent> gyroscopeEventList;
    List<Location> locationList;
    List<MagnetometerEvent> magnetometerEventList;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        record = new Record();
        record.id = getIntent().getLongExtra("record_id", 0);
        record.name = getIntent().getStringExtra("record_name");
        record.timeStart = (Date) getIntent().getSerializableExtra("record_timeStart");
        record.timeEnd = (Date) getIntent().getSerializableExtra("record_timeEnd");
        repository = Repository.getInstance(getApplicationContext());

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_result_detail, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        Intent intent = null;
        switch (id) {
            case R.id.action_upload_data:
                uploadData();
                break;
        }
        return true;
    }

    private void uploadData(){


        repository.postRecord(record).enqueue(new Callback<Record>() {
            @Override
            public void onResponse(Call<Record> call, Response<Record> response) {
                long id  = response.body().id;
                uploadGyroscopeData(id);
                uploadAccelerometeData(id);
                uploadLocationData(id);
                uploadMagnetoMeterData(id);
            }

            @Override
            public void onFailure(Call<Record> call, Throwable t) {

            }
        });
    }


    private void uploadGyroscopeData(long id){
        gyroscopeEventList = repository.findGyroscopeEventsByRecord(record.id);

        repository.postGyroscopeData(gyroscopeEventList,id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });
    }

    private void uploadAccelerometeData(long id){
        accelerometerEventList = repository.findAccelerometerEventsByRecord(record.id);

        repository.postAccelerometerData(accelerometerEventList,id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });
    }


    private void uploadLocationData(long id){
        locationList = repository.findGpsLocationsByRecord(record.id);
        repository.postLocations(locationList,id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });
    }


    private void uploadMagnetoMeterData(long id){
        magnetometerEventList = repository.findMagnetometerEventByRecord(record.id);

        repository.postMagnetometerData(magnetometerEventList,id).enqueue(new Callback<Void>() {
            @Override
            public void onResponse(Call<Void> call, Response<Void> response) {

            }

            @Override
            public void onFailure(Call<Void> call, Throwable t) {

            }
        });
    }
}