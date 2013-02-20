package com.thinkaurelius.titan.core.attribute;

import com.google.common.base.Preconditions;

/**
 * (c) Matthias Broecheler (me@matthiasb.com)
 */

public class Geoshape {

    public enum Type {
        POINT, BOX, CIRCLE, POLYGON;
    }

    //coordinates[0] = latitudes, coordinates[1] = longitudes
    private final float[][] coordinates;

    private Geoshape(final float[][] coordinates) {
        Preconditions.checkArgument(coordinates!=null && coordinates.length==2);
        Preconditions.checkArgument(coordinates[0].length==coordinates[1].length && coordinates[0].length>0);
        for (int i=0;i<coordinates[0].length;i++) {
            if (Float.isNaN(coordinates[0][i])) Preconditions.checkArgument(i==1 && coordinates.length==2 && coordinates[1][i]>0);
            else Preconditions.checkArgument(isValidCoordinate(coordinates[0][i],coordinates[1][i]));
        }
        this.coordinates=coordinates;
    }

    public Type getType() {
        if (coordinates[0].length==1) return Type.POINT;
        else if (coordinates[0].length>2) return Type.POLYGON;
        else { //coordinates[0].length==2
            if (Float.isNaN(coordinates[0][1])) return Type.CIRCLE;
            else return Type.BOX;
        }
    }

    public int size() {
        switch(getType()) {
            case POINT: return 1;
            case CIRCLE: return 1;
            case BOX: return 2;
            case POLYGON: return coordinates[0].length;
            default: throw new IllegalStateException("Unrecognized type: " + getType());
        }
    }

    public Point getPoint(int position) {
        if (position<0 || position>=size()) throw new ArrayIndexOutOfBoundsException("Invalid position: " + position);
        return new Point(coordinates[0][position],coordinates[1][position]);
    }

    public Point getPoint() {
        Preconditions.checkArgument(size()==1,"Shape does not have a single point");
        return getPoint(0);
    }

    public float getRadius() {
        Preconditions.checkArgument(getType()==Type.CIRCLE,"This shape is not a circle");
        return coordinates[1][1];
    }

    public boolean intersect(Geoshape other) {

    }

    public boolean within(Geoshape outer) {

    }

    public boolean disjoint(Geoshape other) {

    }

    private GeoObject convert() {
        //TODO: convert to object accepted by spatial4j
    }


    public static final Geoshape point(final float latitude, final float longitude) {
        Preconditions.checkArgument(isValidCoordinate(latitude,longitude),"Invalid coordinate provided");
        return new Geoshape(new float[][]{ new float[]{latitude}, new float[]{longitude}});
    }

    public static final Geoshape circle(final float latitude, final float longitude, final float radiusInMeter) {
        Preconditions.checkArgument(isValidCoordinate(latitude,longitude),"Invalid coordinate provided");
        Preconditions.checkArgument(radiusInMeter>0,"Invalid radius provided");
        return new Geoshape(new float[][]{ new float[]{latitude, Float.NaN}, new float[]{longitude, radiusInMeter}});
    }

    public static final Geoshape box(final float northWestLatitude, final float northWestLongitude,
                                     final float southEastLatitude, final float southEastLongitude) {
        Preconditions.checkArgument(isValidCoordinate(northWestLatitude,northWestLongitude),"Invalid north-west coordinate provided");
        Preconditions.checkArgument(isValidCoordinate(southEastLatitude,southEastLongitude),"Invalid south-east coordinate provided");
        return new Geoshape(new float[][]{ new float[]{northWestLatitude, southEastLatitude}, new float[]{northWestLongitude, southEastLongitude}});
    }

    public static final boolean isValidCoordinate(final float latitude, final float longitude) {
        return latitude>=-90.0 && latitude<=90.0 && longitude>=-180.0 && longitude<=180.0;
    }

    public static final class Point {

        private final float longitude;
        private final float latitide;

        public Point(float longitude, float latitide) {
            this.longitude = longitude;
            this.latitide = latitide;
        }

        public float getLongitude() {
            return longitude;
        }

        public float getLatitide() {
            return latitide;
        }
    }


}