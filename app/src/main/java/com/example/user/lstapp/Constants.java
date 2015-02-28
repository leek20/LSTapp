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
            {255, 152, 28, 137} //purple
    };
    public static int[][] fillC = {
            {127, 43, 129, 157}, //blue
            {127, 83, 144, 0}, //green
            {127, 188, 88, 176} //purple
    };

    public static int[] l2_f = {127, 188, 88, 176};

    public static double l0_cuttoff = .25;
    public static double l1_cuttoff = .75;

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
}
