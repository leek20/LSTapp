package com.ut.mpc.utils;

import java.util.List;

/*
 * SpatioTemporal Data Storage Interface
 * 
 */
public interface STStorage {
	/**
	 * Gets the size of the storage by number of data points
	 * @return integer value size of the number of points in the structure
	 */
	public int getSize();
	
	/**
	 * Inserts a new point into the STStorage
	 * @param point - point to insert into the structure
	 */
	public void insert(STPoint point);
	
	/**
	 * Queries for a range of points within the structure
	 * @param range bounds for the range query
	 * @return list of points found within the region queried for
	 */
	public List<STPoint> range(STRegion range);
	
	/**
	 * Returns the n nearest neighbors from a given needle point
	 * @param needle position to check for neighbors
	 * @param n number of neighbors to retrieve
	 * @return list of n nearest neighbors
	 */
	public List<STPoint> nearestNeighbor(STPoint needle, int n);
	
	/**
	 * Internally calls nearest neighbor and performs a range query on timestamps alone
	 * @param p1 First query point, starting point for sequence
	 * @param p2 Second query point, ending point for sequence
	 * @return List of points in order they appear in the sequence
	 */
	public List<STPoint> getSequence(STPoint start, STPoint end);
	
	/**
	 * Returns the bounding region around all data points in the structure
	 * @return STRegion - region with mins and maxs set to bounds of points in structure
	 */
	public STRegion getBoundingBox();

    /**
     * Deletes all data points within the structure
     */
    public void clear();
}
