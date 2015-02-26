package com.ut.mpc.utils;

import com.ut.mpc.kdtree.KDTree;

import java.util.ArrayList;
import java.util.List;

public class KDTreeAdapter {
	public KDTree kdtree;
	
	public KDTreeAdapter(int dims){
        kdtree = new KDTree(dims);
	}
	
	public void insert(STPoint point){
        float[] rawPoint = new float[]{point.getX(), point.getY(), point.getT()};
        kdtree.insert(rawPoint, point);
	}

    public List<STPoint> range(STRegion region){
        STPoint mins = region.getMins();
        STPoint maxs = region.getMaxs();
        float[] minsRaw = new float[]{mins.getX(),mins.getY(),mins.getT()};
        float[] maxsRaw = new float[]{maxs.getX(),maxs.getY(),maxs.getT()};
        Object[] results = kdtree.range(minsRaw, maxsRaw);
        List<STPoint> points = new ArrayList<STPoint>();
        for(Object result : results){
            points.add((STPoint) result);
        }
        return points;
    }

    public static KDTreeAdapter makeBalancedTree(int dims, List<STPoint> points){
        KDTreeAdapter kdtree = new KDTreeAdapter(dims);
        int treeSize = points.size();
        kdtree.makeTreeRecurse(points,0,treeSize - 1, 0);
        return kdtree;
    }

    //int dim 0-x, 1-y, 2-t
    private void makeTreeRecurse(List<STPoint> points, int start, int end, int dim){
        if(start <= end){
            Quicksort.sort(points, start, end, dim);

            //find median and insert into new tree
            int median = ((end - start) / 2) + start;
            this.insert(points.get(median));

            int nextDim = (dim + 1) % 3;
            //recurse on left part of array
            makeTreeRecurse(points, start, median - 1, nextDim);

            //recurse on right part of array
            makeTreeRecurse(points, median + 1, end, nextDim);
        }
    }

}

