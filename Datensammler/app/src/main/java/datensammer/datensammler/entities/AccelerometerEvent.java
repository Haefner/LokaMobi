package datensammer.datensammler.entities;

import com.google.gson.annotations.Expose;

import androidx.annotation.NonNull;
import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import static androidx.room.ForeignKey.CASCADE;

@Entity(indices = {@Index("record_id")},foreignKeys = @ForeignKey(entity = Record.class,parentColumns = "id",childColumns = "record_id",onDelete = CASCADE))
public class AccelerometerEvent implements Event {
    @PrimaryKey(autoGenerate = true)
    public long id;
    @ColumnInfo(name="record_id")
    public long recordId;
    @Expose
    @NonNull
    public Float x;
    @Expose
    @NonNull
    public Float y;
    @Expose
    @NonNull
    public Float z;
    @Expose
    @NonNull
    public Long timestamp;

    public AccelerometerEvent() {
    }


    public AccelerometerEvent(long recordId, @NonNull Float x, @NonNull Float y, @NonNull Float z, @NonNull Long timestamp) {
        this.recordId = recordId;
        this.x = x;
        this.y = y;
        this.z = z;
        this.timestamp = timestamp;
    }

    @Ignore
    public AccelerometerEvent(@NonNull Float x, @NonNull Float y, @NonNull Float z, @NonNull Long timestamp) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.timestamp = timestamp;
    }

    @Override
    public long getId() {
        return id;
    }

    @Override
    public void setId(long id) {
        this.id = id;
    }

    @Override
    public long getrecordId() {
        return recordId;
    }

    @Override
    public void setrecordId(long recordId) {
        this.recordId = recordId;
    }

    @Override
    @NonNull
    public Long getTimestamp() {
        return timestamp;
    }

    @Override
    public void setTimestamp(@NonNull Long timestamp) {
        this.timestamp = timestamp;
    }

    @Override
    @NonNull
    public Float getX() {
        return x;
    }

    @Override
    public void setX(@NonNull Float x) {
        this.x = x;
    }

    @Override
    @NonNull
    public Float getY() {
        return y;
    }

    @Override
    public void setY(@NonNull Float y) {
        this.y = y;
    }

    @Override
    @NonNull
    public Float getZ() {
        return z;
    }

    @Override
    public void setZ(@NonNull Float z) {
        this.z = z;
    }
}