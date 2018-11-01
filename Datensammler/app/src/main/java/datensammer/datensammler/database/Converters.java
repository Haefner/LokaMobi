package datensammer.datensammler.database;

import java.util.Date;

import androidx.room.TypeConverter;
import datensammer.datensammler.entities.LocationType;

public class Converters {
    @TypeConverter
    public static Date fromTimestamp(Long value){
        return value == null? null : new Date(value);
    }

    @TypeConverter
    public static Long dateToTimestamp(Date date){
        return date == null ? null : date.getTime();
    }

    @TypeConverter
    public static String locationTypeToString(LocationType type) {return type.toString();}

    @TypeConverter
    public static LocationType stringToLocationType(String string){
        switch (string){
            case "gps":
                return LocationType.GPS;
            case "network":
                return LocationType.NETWORK;
            default:
                return null;
        }
    }
}
