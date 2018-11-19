package datensammer.datensammler;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.media.MediaScannerConnection;
import android.os.Environment;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;

import java.io.File;

import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;
import java.util.List;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;



public class CsvExporter {


    private Activity activity;
    private Context context;
    public CsvExporter(Activity activity,Context context){
        this.context = context;
        this.activity= activity;
    }

    public void write(List<Record> recordList){
      //TODO: Umwandlung der Werte in String.
        StringBuffer stringBuffer = new StringBuffer();

        for(Record record : recordList){

            String interpolatedLat = String.valueOf(record.interpolated.latitude);
            String interpolatedLong = String.valueOf(record.interpolated.longitude);
            String locationLat = String.valueOf(record.location.getLatitude());
            String locationLong = String.valueOf(record.location.getLongitude());
            String error = String.valueOf(record.getErrorDistance());
            stringBuffer.append(record.interpolationType.toString()+","
                    +interpolatedLat+","
                    +interpolatedLong+","
                    +locationLat+","
                    +locationLong+","
                    +error);
        }


        writeFile(new Date().toString(),stringBuffer.toString());
    }


    private void writeFile(String filename, String string){


        checkPermissions();

        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS);
        File path = new File(storageDir,"/datensammler");
        if(!path.exists()){
            Log.d("File IO","Verzeichnis existiert nicht. Erstelle Verzeichnis...");
            if(!path.mkdirs()){
                Log.d("File IO","Erstellen von Verzeichnis fehlgeschlagen");
            }else
                Log.d("File IO","Verzeichnis erfolgreich erstellt.");
        }




        File file = new File(path,filename+".csv");

        try {
            FileWriter fileWriter = new FileWriter(file);
            fileWriter.write(string);
            fileWriter.flush();
            fileWriter.close();


            MediaScannerConnection.scanFile(context,
                    new String[] { file.toString() }, null,
                    (path1, uri) -> {
                        Log.i("ExternalStorage", "Scanned " + path1 + ":");
                        Log.i("ExternalStorage", "-> uri=" + uri);
                    });

        } catch (IOException e) {
            e.printStackTrace();
        }


    }


    private void checkPermissions(){

        if (ContextCompat.checkSelfPermission(context, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(activity,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE},1);


        }
    }
}
