package com.ut.mpc.utils;

/**
 * Creates a range object commonly used for queries
 * The range can be conceptualized as 2 STPoints, one for minimums and
 * one for maximums.  In 3 dimensions, the range can be visualized as
 * a cube with the mins point in one of the upper corners and the maxs
 * point in one of the lower corners
 */
public class STRegion {
	private STPoint mins;
	private STPoint maxs;
	
	public STRegion(){}
	
	public STRegion(STPoint p1, STPoint p2){
		if(mins == null){
			mins = new STPoint(p1);
		}
		if(maxs == null){
			maxs = new STPoint(p1);
		}
		mins.updateMin(p1);
		mins.updateMin(p2);
		maxs.updateMax(p1);
		maxs.updateMax(p2);
	}
	
	/**
	 * Adds a point to the range, if the point does not contribute to the range's
	 * minimum or maximum values, it will not affect the range
	 * @param point point to add to the range
	 */
	public void addPoint(STPoint point){
		if(this.mins == null || this.maxs == null){
			this.mins = new STPoint(point);
			this.maxs = new STPoint(point);
		} else {
			this.mins.updateMin(point);
			this.maxs.updateMax(point);	
		}
	}
	
	/**
	 * Retrieves the range's minimum values
	 * @return STPoint with minimum values for each dimension
	 */
	public STPoint getMins(){
		return this.mins;
	}
	
	/**
	 * Retrieves the range's maximum values
	 * @return STPoint with maximum values for each dimension
	 */
	public STPoint getMaxs(){
		return this.maxs;
	}
	
	public String toString(){
		return this.mins.toString() + "|" +  this.maxs.toString();
	}

    public static STRegion fromString(String input){
        String[] splits = input.split("\\|");
        return new STRegion(STPoint.fromString(splits[0]), STPoint.fromString(splits[1]));
    }
}
