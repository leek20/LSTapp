package com.example.user.lstapp;

/**
 * Created by user on 1/20/15.
 */
public class Constants {
    public static int map_radius = 1000;

    //alpha, red, green, blue
    public static int[][] outlineC = {
            {255, 3, 74, 98}, //blue
            {255, 60, 104, 0}, //green
            {255, 207, 117, 7},//orange
            {255, 186, 14, 18} //red
    };
    public static int[][] fillC = {
            {127, 43, 129, 157}, //blue
            {127, 83, 144, 0}, //green
            {127, 255, 159, 42},//orange
            {127, 251, 79, 83} //red
    };

    public static final int LOC_POLLING_INTERVAL = 1000 * 10; // ms
    public static final int LOC_FASTEST_INTERVAL = 1000; // ms

    public static final double highPoKThresh = .7;
    public static final double mediumPoKThresh = .45;
    public static final double stdPoKThresh = .2;
    public static final double lowPoKThresh = .05;

    public static final String highPoKLabel = "Expert Explorer";
    public static final String mediumPoKLabel = "Strong Tourist";
    public static final String stdPokLabel = "Standard Viewing";
    public static final String lowPoKLabel = "Quick Sighting";
    public static final String noPoKLabel = "Undiscovered";

    public static final String APP_TITLE = "MyTourist";
    public static final String APP_TITLE_TRACKING = APP_TITLE + " (tracking)";
}
