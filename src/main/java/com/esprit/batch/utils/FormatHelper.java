package com.esprit.batch.utils;

import java.util.concurrent.TimeUnit;

/**
 * @author Mohamed Khalfallah
 * @company  
*/
public class FormatHelper {

    /**
     * Private constructor to prevent instantiation.
    */
    private FormatHelper(){}

    /**
     * Formats a duration in milliseconds into a string in the format "Xm Ys Zms".
     *
     * @param millis the duration in milliseconds.
     * @return a formatted string representing the duration.
    */
    public static String formatDuration(long millis) {
        long minutes = TimeUnit.MILLISECONDS.toMinutes(millis);
        millis -= TimeUnit.MINUTES.toMillis(minutes);
        long seconds = TimeUnit.MILLISECONDS.toSeconds(millis);
        millis -= TimeUnit.SECONDS.toMillis(seconds);

        return String.format("%dm %ds %dms", minutes, seconds, millis);
    }
    
}
