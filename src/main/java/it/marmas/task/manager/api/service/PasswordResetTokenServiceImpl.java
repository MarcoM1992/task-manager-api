package it.marmas.task.manager.api.service;

 import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.ott.InvalidOneTimeTokenException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import it.marmas.task.manager.api.dto.PasswordResetTokenDto;
import it.marmas.task.manager.api.exceptions.ElementNotFoundException;
import it.marmas.task.manager.api.mapper.Mapper;
import it.marmas.task.manager.api.model.PasswordResetToken;
import it.marmas.task.manager.api.model.User;
import it.marmas.task.manager.api.repo.PasswordResetTokenRepository; 

@Service
public class PasswordResetTokenServiceImpl implements PasswordResetTokenService {

    private static final Logger logger = LoggerFactory.getLogger(PasswordResetTokenServiceImpl.class);

    @Autowired
    private PasswordEncoder passwordEncoder; // For hashing passwords

    private String REQUEST_PASSWORD_RESET = "reset_password"; // Expected token purpose
    private static final String PURPOSE = "purpose"; // Claim key in JWT

    @Value("${jwt.secret}")
    private String secretKey; // Secret key used for JWT verification

    @Autowired
    private UserService userService; // Service to manage user entities

    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepo; // Repository for password reset tokens

    @Autowired
    @Qualifier("tokenMapper")
    Mapper<PasswordResetToken, PasswordResetTokenDto> tokenMapper; // Mapper for converting between DTOs and entities

    /**
     * Reset a user's password using a valid token.
     * @param newPassword the new password to set
     * @param token the one-time JWT token
     * @return a status message indicating success or error
     */
    @Transactional
    @Override
    public String resetPassword(String newPassword, String token) {
        logger.info("Searching if token exists: " + token);

        // Retrieve token entity or throw exception if not found
        PasswordResetToken resetToken = passwordResetTokenRepo.findByToken(token)
                .orElseThrow(() -> new InvalidOneTimeTokenException("Token has not been found"));

        logger.info("Token exists");

        // Check if token is expired or disabled
        if (checkIfTokenExpiredOrDisabled(resetToken)) {
            String errorMsg = "This token expired or is not valid";
            logger.error(errorMsg);
            return errorMsg;
        }

        logger.info("Token is not expired");

        // Parse JWT claims to validate token
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseClaimsJws(token)
                .getBody();

        String purpose = claims.get(PURPOSE, String.class);
        logger.info(PURPOSE + " : " + purpose);

        // Validate token purpose
        if (!REQUEST_PASSWORD_RESET.equals(purpose)) {
            throw new RuntimeException("Token purpose not valid");
        }

        String email = claims.getSubject();
        logger.info("Email retrieved from claims: " + email);

        // Retrieve the user by email
        User user = userService.getUserEntityByEmail(email)
                .orElseThrow(() -> new ElementNotFoundException("User not found with this email: " + email));

        // Update the user's password
        user.setPassword(passwordEncoder.encode(newPassword));
        userService.updateUser(user);
        logger.info("User password changed");

        // Disable the token after use
        disableToken(resetToken);
        logger.info("Token disabled");

        return "Password changed";
    }

    /**
     * Check if a token is expired or disabled.
     * @param token the token to check
     * @return true if expired or disabled, false otherwise
     */
    public boolean checkIfTokenExpiredOrDisabled(PasswordResetToken token) {
        LocalDateTime tokenExpiration = token.getExpiryDate();
        LocalDateTime currentTime = LocalDateTime.now(ZoneOffset.UTC);

        if (tokenExpiration.isBefore(currentTime) || !token.isEnabled()) {
            disableToken(token);
            logger.info("Expired token status updated");
            return true;
        }
        return false;
    }

    /**
     * Disable a token to prevent future use.
     * @param token the token to disable
     */
    private void disableToken(PasswordResetToken token) {
        token.setEnabled(false);
        passwordResetTokenRepo.updateToken(token);
    }

    /**
     * Insert a new password reset token into the database.
     * @param passwordResetTokenDto DTO containing token data
     * @return Optional DTO of the inserted token
     */
    @Override
    @Transactional
    public Optional<PasswordResetTokenDto> insertToken(PasswordResetTokenDto passwordResetTokenDto) {
        String email = passwordResetTokenDto.getEmail();
        User u = null;

        if (email != null) {
            u = userService.getUserEntityByEmail(email)
                    .orElseThrow(() -> new ElementNotFoundException("User with email " + email + " not found"));
            logger.info("User retrieved by email: " + email + " is: " + u);
        }

        // Map DTO to entity
        PasswordResetToken prt = tokenMapper.toEntity(passwordResetTokenDto);
        prt.setUser(u);

        logger.info("Password reset token entity created: " + prt);

        // Insert token into DB
        passwordResetTokenRepo.insertToken(prt);
        logger.info("Token inserted in DB");

        return Optional.of(tokenMapper.toDto(prt));
    }
}
