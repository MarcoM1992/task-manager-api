package it.marmas.task.manager.api.util;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import it.marmas.task.manager.api.exceptions.AuthorizationException;
public class UtilityUser {

    private static Logger logger = LoggerFactory.getLogger(UtilityUser.class);

    /**
     * Checks if the current user is authorized to perform an operation.
     * 
     * @param currentUser The username of the user performing the action
     * @param nameRetrieved The username of the resource owner
     * @param currentAuthority List of roles assigned to the current user
     * @throws AuthorizationException if the user is not the owner and not an admin
     */
    public static void checkAuthorization(String currentUser, String nameRetrieved, List<String> currentAuthority) {
        boolean isSameUser = currentUser.equalsIgnoreCase(nameRetrieved);
        logger.info("Is the same user: " + isSameUser);

        // Check if the user has admin privileges
        boolean isAdmin = currentAuthority.stream()
                .anyMatch(x -> x.equalsIgnoreCase("ADMIN") || x.equalsIgnoreCase("ROLE_ADMIN"));
        logger.info("Is Admin: " + isAdmin);

        // If the user is neither the owner nor an admin, throw exception
        if (!isSameUser && !isAdmin) {
            String errorMsg = "User " + currentUser + " is not allowed to execute this operation. Contact the administration.";
            logger.warn(errorMsg);
            throw new AuthorizationException(errorMsg);
        }
    }

    /**
     * Validates if a string is a numeric ID.
     * 
     * @param id the string to validate
     * @return true if the string is not null and contains only digits
     */
    public static boolean idIsValid(String id) {
        boolean idNotNull = id != null;
        boolean idIsDigit = Arrays.stream(id.split(""))
                                  .allMatch(x -> Character.isDigit(x.charAt(0)));

        logger.info("ID not null: " + idNotNull);
        logger.info("ID is digit: " + idIsDigit);

        return idNotNull && idIsDigit;
    }

    /**
     * Converts a UTC LocalDateTime to a user's timezone.
     * 
     * @param createdAtUtc the original UTC datetime
     * @param zoneId the target timezone ID (e.g., "Europe/London")
     * @return LocalDateTime adjusted to the user's timezone
     */
    public static LocalDateTime convertTimeWithUserSetting(LocalDateTime createdAtUtc, String zoneId) {
        return createdAtUtc
                .atOffset(ZoneOffset.UTC)            // Interpret as UTC
                .atZoneSameInstant(ZoneId.of(zoneId)) // Convert to target timezone
                .toLocalDateTime();                  // Return LocalDateTime in target zone
    }
}
