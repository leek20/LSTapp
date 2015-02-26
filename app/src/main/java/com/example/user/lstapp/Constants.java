package com.example.user.lstapp;

/**
 * Created by user on 1/20/15.
 */
public class Constants {
    public static int map_radius = 1000;

    //alpha, red, green, blue

    //blue tint
    public static int[] l0_s = {255, 3, 74, 98}; //default stroke
    public static int[] l0_f = {127, 43, 129, 157}; //default fill

    //green tint
    public static int[] l1_s = {255, 60, 104, 0};
    public static int[] l1_f = {127, 83, 144, 0};

    //purple tint
    public static int[] l2_s = {255, 152, 28, 137};
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
