package it.marmas.task.manager.api.service;

import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.ott.InvalidOneTimeTokenException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import it.marmas.task.manager.api.dto.UserDto;
import it.marmas.task.manager.api.dto.UserValidationTokenDto;
import it.marmas.task.manager.api.exceptions.ElementNotFoundException;
import it.marmas.task.manager.api.exceptions.UserValidationException;
import it.marmas.task.manager.api.mapper.Mapper;
import it.marmas.task.manager.api.model.User;
import it.marmas.task.manager.api.model.UserValidationToken;
import it.marmas.task.manager.api.repo.UserValidationTokenRepository; 
@Service
public class UserValidationTokenServiceImpl implements UserValidationTokenService {

    private static final Logger logger = LoggerFactory.getLogger(UserValidationTokenServiceImpl.class);

    private static final String PURPOSE_VALUE_VALIDATE_ACCOUNT = "validate-account";
    private static final String PURPOSE = "purpose";

    @Autowired
    private UserValidationTokenRepository userValidationTokenRepository;

    @Autowired
    private UserService userService;

    @Autowired
    @Qualifier("userValidationMapper")
    private Mapper<UserValidationToken, UserValidationTokenDto> userValidationTokenMapper;

    @Value("${jwt.secret}")
    private String secretKey;

    // ===================== LIST TOKENS =====================
    @Transactional(readOnly = true)
    public List<UserValidationTokenDto> getAllTokenForUsername(String username) {
        Optional<List<UserValidationToken>> tokenList =
                userValidationTokenRepository.getAllTokenForUsername(username);

        return tokenList
                .map(list -> list.stream().map(userValidationTokenMapper::toDto).toList())
                .orElse(null); // Could return empty list instead of null for safer handling
    }

    // ===================== VALIDATE ACCOUNT =====================
    @Transactional
    public UserDto validateAccount(String token) {
        logger.info("Searching token: {}", token);

        UserValidationToken validToken = userValidationTokenRepository.findByToken(token)
                .orElseThrow(() -> new InvalidOneTimeTokenException("Token not found"));

        logger.info("Token exists. Checking expiration...");

        if (checkIfTokenExpiredOrNotValid(validToken)) {
            String errorMsg = "Token expired or not valid";
            logger.error(errorMsg);
            throw new RuntimeException(errorMsg);
        }

        logger.info("Token is valid. Parsing JWT claims...");
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)))
                .build()
                .parseClaimsJws(token)
                .getBody();

        String purpose = claims.get(PURPOSE, String.class);
        logger.info("Token purpose: {}", purpose);

        if (!PURPOSE_VALUE_VALIDATE_ACCOUNT.equals(purpose)) {
            throw new RuntimeException("Token purpose not valid");
        }

        String email = claims.getSubject();
        logger.info("Email from token: {}", email);

        User user = userService.getUserEntityByEmail(email)
                .orElseThrow(() -> new ElementNotFoundException("User not found with this email: " + email));

        // Enable user account
        user.setEnabled(true);
        UserDto dto = userService.updateUser(user);
        logger.info("User enabled");

        // Disable token after use
        disableToken(validToken);
        logger.info("Token disabled");

        return dto;
    }

    // ===================== TOKEN UTILS =====================
    private void disableToken(UserValidationToken token) {
        token.setEnabled(false);
        userValidationTokenRepository.updateToken(token);
    }

    private boolean checkIfTokenExpiredOrNotValid(UserValidationToken token) {
        LocalDateTime tokenExpiration = token.getExpiryDate();
        LocalDateTime currentTime = LocalDateTime.now(ZoneOffset.UTC);
        return tokenExpiration.isBefore(currentTime);
    }

    // ===================== INSERT TOKEN =====================
    @Override
    @Transactional
    public UserValidationTokenDto insertToken(UserValidationTokenDto dto) {
        // Ensure user exists
        User u = userService.getUserEntityByEmail(dto.getEmail())
                .orElseThrow(() -> new UserValidationException("User not found: " + dto.getEmail()));
        logger.info("User found by email {}", dto.getEmail());

        UserValidationToken tokenEntity = userValidationTokenMapper.toEntity(dto);
        tokenEntity.setUser(u);

        // Set token expiry (15 minutes from now)
        LocalDateTime now = LocalDateTime.now(ZoneOffset.UTC);
        tokenEntity.setExpiryDate(now.plusMinutes(15));

        logger.info("Inserting token: {}", tokenEntity);

        tokenEntity = userValidationTokenRepository.insertToken(tokenEntity)
                .orElseThrow(() -> new RuntimeException("Token not saved"));

        logger.info("Token saved: {}", tokenEntity.getToken());
        return userValidationTokenMapper.toDto(tokenEntity);
    }
}
