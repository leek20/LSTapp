package com.ut.mpc.utils;

import com.ut.mpc.setup.Constants;

public class GPSLib {
	private static float longitudeKM = .009f;
	private static float latitudeKM = .001f;

	/*
	 * Retrieves a space bound around a given point with given radius values using GPS estimation, if
	 * the depicted type is GPS type
	 * @param point - point around which to form the bound
	 * @param boundValues - point representing expansion values in each dimension.  These are 
	 * 		  raw values that need to be transferred to coordinate system before use
	 */
	public static STRegion getSpaceBoundQuick(STPoint point, STPoint boundValues, Constants.SpatialType type) throws LSTFilterException{
		STRegion region = new STRegion();
		
		float xOffset, yOffset, tOffset;
		if(type == Constants.SpatialType.GPS){
			xOffset = boundValues.getX();
			yOffset = boundValues.getY();
			tOffset = boundValues.getT();
		} else if(type == Constants.SpatialType.Meters){
			xOffset = boundValues.getX();
			yOffset = boundValues.getY();
			tOffset = boundValues.getT();
		} else {
			throw new LSTFilterException("Unrecognized coordinate type for GPSLib");
		}
		region.addPoint(new STPoint(point.getX() + xOffset,
									point.getY() + yOffset,
									point.getT() + tOffset));
		region.addPoint(new STPoint(point.getX() - xOffset,
									point.getY() - yOffset,
									point.getT() - tOffset));
		return region;
	}
	
	public static double spatialDistanceBetween(STPoint p1, STPoint p2, Constants.SpatialType type) throws LSTFilterException{
		if(type == Constants.SpatialType.GPS){
			double R = 6371;
			double lat1 = Math.toRadians(p1.getY());
			double lat2 = Math.toRadians(p2.getY());
			double long1 = Math.toRadians(p1.getX());
			double long2 = Math.toRadians(p2.getX());
			double d = Math.acos(Math.sin(lat1) * Math.sin(lat2) +
							     Math.cos(lat1) * Math.cos(lat2) *
							     Math.cos(long2 - long1)) * R;
			return d;
		} else if(type == Constants.SpatialType.Meters) {
			return Math.sqrt(Math.pow(p1.getX() - p2.getX(),2) + Math.pow(p1.getY() - p2.getY(),2));
			//return Math.pow(p1.getX() - p2.getX(),2) + Math.pow(p1.getY() - p2.getY(),2);
		} else {
			throw new LSTFilterException("Unrecognized coordinate type for GPSLib");
		}
	}
	
//	public static void main(String[] args){
//		GPSLib tester = new GPSLib();
//		double[] temp = getCoordFromDist(37.75134, -122.39488, 10, 180);
//		System.out.println(temp[0]);
//		System.out.println(temp[1]);
//	}
//	
//	/**
//	 * Returns a bounding region from the given set of min and max points.
//	 * Bounds set by SPACE_RADIUS
//	 * @param min - STPoint containing minimum values in all dimensions
//	 * @param max - STPoint containing maximum values in all dimensions
//	 * @return STRegion bound of the given min max points
//	 */
//	public static STRegion getRegionBound(STPoint min, STPoint max){
//		STPoint minExt = new STPoint();
//		STPoint maxExt = new STPoint();
//		if(Constants.SPATIAL_TYPE == Constants.SpatialType.GPS){
//			STPoint temp;
//			temp = GPSLib.getCoordFromDist(max.getX(), max.getY(), Constants.CoverageWindow.SPACE_RADIUS, 270);
//			maxExt.setY(temp.getY());
//			temp = GPSLib.getCoordFromDist(min.getX(), min.getY(), Constants.CoverageWindow.SPACE_RADIUS, 90);
//			minExt.setY(temp.getY());
//			temp = GPSLib.getCoordFromDist(min.getX(), min.getY(), Constants.CoverageWindow.SPACE_RADIUS, 180);
//			minExt.setX(temp.getX());
//			temp = GPSLib.getCoordFromDist(max.getX(), max.getY(), Constants.CoverageWindow.SPACE_RADIUS, 0);
//			maxExt.setX(temp.getX());
//		} else if(Constants.SPATIAL_TYPE == Constants.SpatialType.Meters){
//			minExt.setX(min.getX() - Constants.CoverageWindow.SPACE_RADIUS);
//			minExt.setY(min.getY() - Constants.CoverageWindow.SPACE_RADIUS);
//			maxExt.setX(max.getX() + Constants.CoverageWindow.SPACE_RADIUS);
//			maxExt.setY(max.getY() + Constants.CoverageWindow.SPACE_RADIUS);		
//		}
//		return new STRegion(minExt, maxExt);
//	}
//
//	public static STRegion getSpaceBoundQuick(STPoint min, STPoint max){
//		STPoint minExt = new STPoint();
//		STPoint maxExt = new STPoint();
//		STRegion region = null;
//		if(Constants.SPATIAL_TYPE == Constants.SpatialType.GPS){
//			//Possible idea - get accurate distance for first point, find what distance is and use that fixed value
//			float paddingX = Constants.CoverageWindow.LONG_KM_EST * Constants.CoverageWindow.SPACE_RADIUS;
//			float paddingY = Constants.CoverageWindow.LAT_KM_EST * Constants.CoverageWindow.SPACE_RADIUS;
//			minExt.setX(min.getX() - paddingX);
//			minExt.setY(min.getY() - paddingY);
//			maxExt.setX(max.getX() + paddingX);
//			maxExt.setY(max.getY() + paddingY);
//			region = new STRegion(minExt, maxExt);
//		} else if(Constants.SPATIAL_TYPE == Constants.SpatialType.Meters){
//			region = getRegionBound(min, max);
//		}
//		return region;
//	}
//	
//	//latitude in degrees
//	//longitude in degrees
//	//distance in km
//	//bearing in degrees
//	//formula from http://www.movable-type.co.uk/scripts/latlong.html
//	public static STPoint getCoordFromDist(float longitude, float latitude, float distance, float bearing){
//		double R = 6371;
//		double latRad = Math.toRadians(latitude);
//		double lonRad = Math.toRadians(longitude);
//		double brng = Math.toRadians(bearing);
//		double latResult = Math.asin( Math.sin(latRad)*Math.cos(distance/R) + 
//	              Math.cos(latRad)*Math.sin(distance/R)*Math.cos(brng) );
//		double lonResult = lonRad + Math.atan2(Math.sin(brng)*Math.sin(distance/R)*Math.cos(latResult), 
//	                     Math.cos(distance/R)-Math.sin(latRad)*Math.sin(latResult));
//		STPoint point = new STPoint();
//		point.setX((float) Math.toDegrees(lonResult));
//		point.setY((float) Math.toDegrees(latResult));
//		return point;
//	}
//	
//	//points in degrees (longitude,latitude)
//	//returns -1 if Constants.SpatialType is undefined
//	public static double getDistanceBetween(double[] p1, double[] p2){
//		if(Constants.SPATIAL_TYPE == Constants.SpatialType.GPS){
//			double R = 6371;
//			double lat1 = Math.toRadians(p1[0]);
//			double lat2 = Math.toRadians(p2[0]);
//			double long1 = Math.toRadians(p1[1]);
//			double long2 = Math.toRadians(p2[1]);
//			double d = Math.acos(Math.sin(lat1) * Math.sin(lat2) +
//							     Math.cos(lat1) * Math.cos(lat2) *
//							     Math.cos(long2 - long1)) * R;
//			return d;
//		} else if(Constants.SPATIAL_TYPE == Constants.SpatialType.Meters) {
//			return Math.sqrt(Math.pow(p1[0] - p2[0],2) + Math.pow(p1[1] - p2[1],2));
//		} else {
//			return -1;
//		}
//	}
}
