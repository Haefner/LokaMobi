package datensammer.datensammler.database;

import android.content.Context;

import androidx.room.Room;

public class Database {
    private static AppDatabase db;
    private Database(){}

    public static AppDatabase getInstance(Context context){
        if(db == null){
            db =  Room.databaseBuilder(context,AppDatabase.class,"datensammlerdb").allowMainThreadQueries().build();
        }
        return db;

    }
}
