package it.marmas.task.manager.api.util;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAccessor;
import java.util.Date;
public class Utility {

    // Default timezone used for formatting and conversions
    private static final String DEFAUL_ZONE_ID = "UTC";

    /**
     * Checks if an object is not null.
     * 
     * @param obj the object to check
     * @return true if the object is not null
     */
    public static boolean notNull(Object obj) {
        return obj != null;
    }

    /**
     * Converts a LocalDateTime to java.util.Date using the system default timezone.
     * 
     * @param localDateTime the LocalDateTime to convert
     * @return a Date object, or null if input is null
     */
    public static Date convertToDate(LocalDateTime localDateTime) {
        if (localDateTime == null) return null;
        return Date.from(localDateTime.atZone(ZoneId.systemDefault()).toInstant());
    }

    /**
     * Converts a java.util.Date to LocalDateTime using the system default timezone.
     * 
     * @param date the Date to convert
     * @return a LocalDateTime, or null if input is null
     */
    public static LocalDateTime convertToLocalDateTime(Date date) {
        if (date == null) return null;
        return date.toInstant()
                   .atZone(ZoneId.systemDefault())
                   .toLocalDateTime();
    }

    /**
     * Formats a java.util.Date into a string: yyyy/MM/dd/HH:mm:ss
     * 
     * @param date the Date to format
     * @return formatted string
     */
    public static String formatDate(Date date) {
        LocalDateTime ldt = date.toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd/HH:mm:ss");
        return formatter.format(ldt);
    }

    /**
     * Formats a TemporalAccessor (Instant, LocalDateTime, etc.) using the default timezone.
     * 
     * @param ldt the temporal object
     * @return formatted string
     */
    public static String formatLdl(TemporalAccessor ldt) {
        ZoneId zone = ZoneId.of(DEFAUL_ZONE_ID);
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd/HH:mm:ss").withZone(zone);
        return formatter.format(ldt);
    }

    /**
     * Formats a TemporalAccessor using a specified timezone.
     * 
     * @param ldt the temporal object
     * @param zone the target timezone
     * @return formatted string
     */
    public static String formatLdl(TemporalAccessor ldt, ZoneId zone) {
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd/HH:mm:ss").withZone(zone);
        return formatter.format(ldt);
    }

    /**
     * Converts an OffsetDateTime to LocalDateTime in UTC.
     * 
     * @param odt the OffsetDateTime to convert
     * @return LocalDateTime in UTC
     */
    public static LocalDateTime odtToLdt(OffsetDateTime odt) {
        return odt.withOffsetSameInstant(ZoneOffset.UTC).toLocalDateTime();
    }

    /**
     * Converts a UTC LocalDateTime to an OffsetDateTime in a user's timezone.
     * 
     * @param utcDateTime the UTC LocalDateTime
     * @param userZone the target user's timezone; if null, defaults to UTC
     * @return OffsetDateTime in the user's timezone
     */
    public static OffsetDateTime ldtToOdt(LocalDateTime utcDateTime, ZoneId userZone) {
        if (utcDateTime == null) return null;
        if (userZone == null) userZone = ZoneId.of(DEFAUL_ZONE_ID);

        OffsetDateTime utcOffset = utcDateTime.atOffset(ZoneOffset.UTC);
        return utcOffset.atZoneSameInstant(userZone).toOffsetDateTime();
    }

    /**
     * Converts a LocalDateTime in a specific zone to an Instant.
     * 
     * @param localDateTime the LocalDateTime to convert
     * @param zoneId the zone ID for conversion
     * @return Instant representing the same moment in time
     */
    public static Instant localDateTimeToInstant(LocalDateTime localDateTime, String zoneId) {
        return localDateTime.atZone(ZoneId.of(zoneId)).toInstant();
    }

    /**
     * Converts an Instant to LocalDateTime using a specific timezone.
     * 
     * @param instant the Instant to convert
     * @param zoneId the target timezone; if null, defaults to UTC
     * @return LocalDateTime in the specified timezone
     */
    public static LocalDateTime instantToLocalDateTime(Instant instant, ZoneId zoneId) {
        ZoneId zone = zoneId == null ? ZoneId.of(DEFAUL_ZONE_ID) : zoneId;
        return instant.atZone(zone).toLocalDateTime();
    }

    /**
     * Returns the last 20 characters of a token string.
     * 
     * @param accessToken the token string
     * @return last 20 characters
     */
    public static String seeTokenEnding(String accessToken) {
        return accessToken.substring(accessToken.length() - 20);
    }
}
