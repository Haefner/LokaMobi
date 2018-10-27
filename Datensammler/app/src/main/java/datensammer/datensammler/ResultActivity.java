package datensammer.datensammler;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import androidx.appcompat.app.AppCompatActivity;
import datensammer.datensammler.api.Repository;
import datensammer.datensammler.daos.RecordDao;
import datensammer.datensammler.database.AppDatabase;
import datensammer.datensammler.database.Database;
import datensammer.datensammler.entities.Record;

public class ResultActivity extends AppCompatActivity implements AdapterView.OnItemClickListener {

    ListView recordListView;
    ArrayAdapter<Record> arrayAdapter;
    Repository repository;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);
        //setTitle(R.string.action_results);
        repository = Repository.getInstance(getApplicationContext());


        //Test Data
        /*Record measurement = new Record();
        measurement.sampleRate = 12;
        measurement.startDate = new Date();
        measurement.endDate = new Date((measurement.startDate.getTime()+(60L*1000L)));
        recordDao.insertRecord(measurement);
        recordDao.insertRecord(measurement);
        recordDao.insertRecord(measurement);
        recordDao.insertRecord(measurement);*/
        //Test Data

        recordListView =  findViewById(R.id.records);



        Executor executor = Executors.newSingleThreadExecutor();

        executor.execute(new Runnable() {
            @Override
            public void run() {
                arrayAdapter = new ArrayAdapter<>(ResultActivity.this,android.R.layout.simple_list_item_1,repository.getAllRecords());
                recordListView.setAdapter(arrayAdapter);

            }
        });
        recordListView.setOnItemClickListener(ResultActivity.this);


    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this, ResultDetailActivity.class);
        Record record = arrayAdapter.getItem(position);
        intent.putExtra("record_id",record.id);
        intent.putExtra("record_name",record.name);
        intent.putExtra("record_timeStart",record.timeStart);
        intent.putExtra("record_timeEnd",record.timeEnd);

        startActivity(intent);
    }
}