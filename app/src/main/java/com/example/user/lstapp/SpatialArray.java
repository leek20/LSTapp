package com.example.user.lstapp;

import com.ut.mpc.utils.STPoint;
import com.ut.mpc.utils.STStorage;
import com.ut.mpc.utils.STRegion;

import java.util.ArrayList;
import java.util.List;

public class SpatialArray implements STStorage {
    public int size = 0;
    public List<STPoint> points = new ArrayList<STPoint>();

    @Override
    public int getSize() {
        return this.size;
    }

    @Override
    public void insert(STPoint point) {
        this.points.add(point);
        this.size++;
    }

    @Override
    public List<STPoint> range(STRegion range) {
        List<STPoint> results = new ArrayList<STPoint>();
        STPoint mins = range.getMins();
        STPoint maxs = range.getMaxs();
        for(STPoint point : this.points){
            if(point.getX() >= mins.getX() && point.getX() <= maxs.getX() &&
                    point.getY() >= mins.getY() && point.getY() <= maxs.getY() &&
                    point.getT() >= mins.getT() && point.getT() <= maxs.getT()){
                results.add(point);
            }
        }
        return results;
    }

    @Override
    public List<STPoint> nearestNeighbor(STPoint needle, int n) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public List<STPoint> getSequence(STPoint start, STPoint end) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void clear() {
        this.points = new ArrayList<STPoint>();
    }

    @Override
    public STRegion getBoundingBox(){
        STPoint min = new STPoint(Float.MIN_VALUE,Float.MIN_VALUE, Float.MIN_VALUE);
        STPoint max = new STPoint(Float.MAX_VALUE,Float.MAX_VALUE,Float.MAX_VALUE);
        List<STPoint> allPoints = this.range(new STRegion(min,max));
        STPoint minBounds = new STPoint();
        STPoint maxBounds = new STPoint();
        for(STPoint point : allPoints){
            minBounds.updateMin(point);
            maxBounds.updateMax(point);
        }
        return new STRegion(minBounds,maxBounds);
    }
}