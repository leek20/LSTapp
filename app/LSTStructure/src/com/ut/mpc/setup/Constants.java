package com.ut.mpc.setup;

public class Constants {
	public enum SpatialType{GPS,Meters};
	public static SpatialType SPATIAL_TYPE = SpatialType.GPS;
	
	public static class CoverageWindow{
		public static float TOTAL_WEIGHT = 2;
		
		public static float SPACE_WEIGHT = 1;
		public static float SPACE_RADIUS = .009f;
		public static float SPACE_DECAY = SPACE_WEIGHT / (SPACE_RADIUS);
		
		public static float TEMPORAL_WEIGHT = TOTAL_WEIGHT - 1;
		public static float TEMPORAL_RADIUS = 1000 * 3600 * 4; //in ms, 4 hrs
		public static float TEMPORAL_DECAY = TEMPORAL_WEIGHT / (TEMPORAL_RADIUS);
		
		//TODO: add SPACE-TEMPORAL Trim so nearby doesn't need to trim if they are insignificant points
		
		public static int TRIM_THRESH = 10;
		
		//slope of temporal decay;   timerelevance =  timereference / ( decay * timestamp )
		//if this is less than 1, there will be an overflow of space weight (which may be ok)
		//public static float CURRENT_TIMESTAMP = 0f; //this is temporary for testing purposes only, eventually this will be a method call to current time
		//public static float REFERENCE_TIMESTAMP = 0f;
		
		public static float LONG_KM_EST = .009f;
		public static float LAT_KM_EST = .001f;
		
		public static boolean GRID_DEFAULT = true;
		public static float X_GRID_GRAN = (GRID_DEFAULT) ? SPACE_RADIUS / 2 : .001f; //allow fine tuning by setting grid default to off
		public static float Y_GRID_GRAN = (GRID_DEFAULT) ? SPACE_RADIUS / 2 : .001f; //allow fine tuning by setting grid default to off
		public static float T_GRID_GRAN = (GRID_DEFAULT) ? TEMPORAL_RADIUS / 2 : .01f;
		public static int OPT_LEVEL = 1;
		public static boolean PLOT = false;
		public static boolean NORMALIZE_PLOT = false; //generally won't see an effect of temporal decay with this set to true (will simply normalize scale)
	}
	
	public static class SmartInsert{
		public static int INS_THRESH = 80;	
	}
	
	public static boolean DEBUG_LEVEL1 = true;
	public static boolean DEBUG_LEVEL2 = true;
	public static boolean DEBUG_LEVEL3 = false;
	
	//level 1 for unit testing
	//level 2 for other testing
	//level 3 for performance benchmarking
	public static  void DebugPrint(String str, int level){
		if(level == 1 && DEBUG_LEVEL1){
			System.out.println(str);
		} else if(level == 2 && DEBUG_LEVEL2){
			System.out.println(str);
		} else if(level == 3 && DEBUG_LEVEL3){
			System.out.println(str);
		}
	}

	/*
	 * Defaults configured for the Crawded Mobi Data Set
	 * Distance is in meters
	 */
	public static  void setMobilityDefaults(){
		SPATIAL_TYPE = SpatialType.Meters;
		CoverageWindow.SPACE_WEIGHT = 100;
		CoverageWindow.SPACE_RADIUS = 30;
		CoverageWindow.GRID_DEFAULT = false;
		CoverageWindow.X_GRID_GRAN = (CoverageWindow.GRID_DEFAULT) ? CoverageWindow.SPACE_RADIUS / 10 : 5; 
		CoverageWindow.Y_GRID_GRAN = (CoverageWindow.GRID_DEFAULT) ? CoverageWindow.SPACE_RADIUS / 10 : 5; 	
		CoverageWindow.NORMALIZE_PLOT = false; 
		CoverageWindow.TEMPORAL_DECAY = 8;
		//CoverageWindow.CURRENT_TIMESTAMP = 0; 
		//CoverageWindow.REFERENCE_TIMESTAMP = 0;
		SmartInsert.INS_THRESH = 80;		
		DEBUG_LEVEL1 = true;
		DEBUG_LEVEL2 = true;
		DEBUG_LEVEL3 = false;
	}
	
	/*
	 * Defaults configured for the Cabspotting MObility Data Set
	 * 	Distance is in kilometers
	 */
	public static  void setCabsDefaults(){
		SPATIAL_TYPE = SpatialType.GPS;
		CoverageWindow.SPACE_WEIGHT = 100;
		CoverageWindow.SPACE_RADIUS = 1; //km	
		CoverageWindow.GRID_DEFAULT = false;
		CoverageWindow.X_GRID_GRAN = (CoverageWindow.GRID_DEFAULT) ? CoverageWindow.SPACE_RADIUS / 10 : .001f; 
		CoverageWindow.Y_GRID_GRAN = (CoverageWindow.GRID_DEFAULT) ? CoverageWindow.SPACE_RADIUS / 10 : .001f; 	
		CoverageWindow.NORMALIZE_PLOT = false; 
		CoverageWindow.TEMPORAL_DECAY = 3;
		//CoverageWindow.CURRENT_TIMESTAMP = 0; 
		//CoverageWindow.REFERENCE_TIMESTAMP = 0;
		SmartInsert.INS_THRESH = 80;		
		DEBUG_LEVEL1 = true;
		DEBUG_LEVEL2 = true;
		DEBUG_LEVEL3 = false;
	}
	
}
