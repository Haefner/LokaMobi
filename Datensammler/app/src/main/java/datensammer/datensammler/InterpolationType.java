package datensammer.datensammler;

public enum InterpolationType {
    /**
     * Der händisch gesetzte Wegpunkt
     */
    WAYPOINT,
    /**
     * Der berechnete Wegpunkt. Dieser liegt zwischen zwei Waypoints.
     */
    INTERPOLATED_POINT
}
