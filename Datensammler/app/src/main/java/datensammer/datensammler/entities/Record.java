package datensammer.datensammler.entities;


import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.Date;

import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity
public class Record {
    @Expose(serialize = false)
    @PrimaryKey(autoGenerate = true)
    public long id;
    @Expose
    public String name;
    @Expose
    @SerializedName("time_start")
    public Date timeStart;
    @Expose
    @SerializedName("time_end")
    public Date timeEnd;

    public Record() {}

    public Record(String name, Date timeStart, Date timeEnd) {
        this.id = id;
        this.timeStart = timeStart;
        this.timeEnd = timeEnd;
        this.name = name;
    }

    @Override
    public String toString() {
        return "id: "+id +" "+name;
    }
}
