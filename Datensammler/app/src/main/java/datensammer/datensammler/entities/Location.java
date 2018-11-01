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
    public class Location {
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
        @Expose
        public LocationType locationType;


        public Location(long recordId, double latitude, double longitude, double altitude, float bearing, float speed, long timestamp,LocationType locationType) {
            this.recordId = recordId;
            this.bearing = bearing;
            this.latitude = latitude;
            this.longitude = longitude;
            this.altitude = altitude;
            this.speed = speed;
            this.timestamp = timestamp;
            this.locationType = locationType;
        }

        @Ignore
        public Location(double longitude, double latitude, double altitude, float bearing, float speed, long timestamp,LocationType locationType ) {
            this.longitude = longitude;
            this.latitude = latitude;
            this.altitude = altitude;
            this.bearing = bearing;
            this.speed = speed;
            this.timestamp = timestamp;
            this.locationType = locationType;

        }

    }


