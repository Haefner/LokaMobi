package datensammer.datensammler.entities;

import androidx.annotation.NonNull;

public interface Event {
     long getId();



     void setId(long id);
     long getrecordId();
     void setrecordId(long recordId);

    @NonNull
     Long getTimestamp();

     void setTimestamp(@NonNull Long timestamp);

    @NonNull
     Float getX();

     void setX(@NonNull Float x);

    @NonNull
     Float getY();

     void setY(@NonNull Float y);

    @NonNull
     Float getZ();

     void setZ(@NonNull Float z);
}
