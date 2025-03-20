package edu.uob.data;

public class PathData {
    private String fromLocationName;
    private String toLocationName;

    public PathData(String fromLocationName, String toLocationName) {
        this.fromLocationName = fromLocationName;
        this.toLocationName = toLocationName;
    }

    public String getFromLocationName() {
        return this.fromLocationName;
    }

    public String getToLocationName() {
        return this.toLocationName;
    }
}