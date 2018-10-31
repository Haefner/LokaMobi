package datensammer.datensammler.entities;

import com.google.gson.annotations.Expose;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.Ignore;
import androidx.room.Index;
import androidx.room.PrimaryKey;

import static androidx.room.ForeignKey.CASCADE;

    @Entity(indices = {@Index("record_id")},foreignKeys = @ForeignKey(entity = Record.class,parentColumns = "id",childColumns = "record_id",onDelete = CASCADE))
    public class GpsLocation {
        @PrimaryKey(autoGenerate = true)
        public long id;
        @ColumnInfo(name="record_id")
        public long recordId;
        @Expose
        public float bearing;
        @Expose
        public double latitude;
        @Expose
        public double longitude;
        @Expose
        public double altitude;
        @Expose
        public float speed;
        @Expose
        public long timestamp;


        public GpsLocation(long recordId, double latitude, double longitude, double altitude, float bearing, float speed, long timestamp) {
            this.recordId = recordId;
            this.bearing = bearing;
            this.latitude = latitude;
            this.longitude = longitude;
            this.altitude = altitude;
            this.speed = speed;
            this.timestamp = timestamp;
        }

        @Ignore
        public GpsLocation(double longitude, double latitude, double altitude, float bearing, float speed, long timestamp) {
            this.longitude = longitude;
            this.latitude = latitude;
            this.altitude = altitude;
            this.bearing = bearing;
            this.speed = speed;
            this.timestamp = timestamp;
        }

    }


