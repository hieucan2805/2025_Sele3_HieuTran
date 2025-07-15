package com.auto.ht.utils;

import java.time.Duration;

public class Constants {
    public static final String PROPERTIES_FILE = "selenide.properties";

    // Base URL
    public static final String BASE_URL_VJ = "https://vietjetair.com";
    public static final String BASE_URL_LF = "https://leapfrog.com";
    public static final String BASE_URL_AGODA = "https://agoda.com";

    //Date Time Format
    public static final String TIME_FORMAT_CURRENT_DATE_TIME = "MM_dd_yyyy_HH_mm_ss";
    public static final String TIME_FORMAT_CURRENT_DATE = "dd, MMMM yyyy";

    //Time out
    public static final Duration MEDIUM_WAIT = Duration.ofSeconds(5);
    public static final Duration SHORT_WAIT = Duration.ofSeconds(5);
    public static final Duration VERY_SHORT_WAIT = Duration.ofSeconds(1);
}