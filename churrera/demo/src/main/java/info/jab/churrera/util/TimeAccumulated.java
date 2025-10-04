package info.jab.churrera.util;

import java.time.Instant;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

/**
 * Utility class for time-related operations and formatting.
 */
public class TimeAccumulated {

    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm:ss");

    /**
     * Gets the current time formatted as HH:MM:SS.
     *
     * @return formatted current time string (e.g., "14:30:45")
     */
    public static String getCurrentTime() {
        LocalTime now = LocalTime.now();
        return now.format(TIME_FORMATTER);
    }

    private static final String UNKNOWN_TIME = "unknown";
    private static final String SECONDS_ONLY_FORMAT = "%ds";
    private static final String MINUTES_SECONDS_FORMAT = "%dm %ds";

    /**
     * Calculates and formats the elapsed time since execution started.
     *
     * @param startTime the start time in milliseconds
     * @return formatted time string (e.g., "2m 30s")
     */
    public static String getTimeFormatted(long startTime) {
        if (startTime == 0) {
            return UNKNOWN_TIME;
        }

        long elapsedSeconds = Instant.now().getEpochSecond() - (startTime / 1000);

        if (elapsedSeconds < 60) {
            return String.format(SECONDS_ONLY_FORMAT, elapsedSeconds);
        }

        long minutes = elapsedSeconds / 60;
        long seconds = elapsedSeconds % 60;
        return String.format(MINUTES_SECONDS_FORMAT, minutes, seconds);
    }
}
