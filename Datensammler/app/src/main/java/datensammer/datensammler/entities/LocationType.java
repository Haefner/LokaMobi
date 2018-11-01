package datensammer.datensammler.entities;


public enum LocationType {
    NETWORK("network"),
    GPS("gps");

    private String name;
    LocationType(String name) {
        this.name = name;
    }

    @Override
    public String toString() {
        return name;
    }
}
