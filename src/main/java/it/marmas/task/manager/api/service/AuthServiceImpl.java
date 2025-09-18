package it.marmas.task.manager.api.service;

import java.time.Instant;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import it.marmas.task.manager.api.auth.JwtUtil;
import it.marmas.task.manager.api.dto.AuthRequest;
import it.marmas.task.manager.api.dto.AuthResponse;
import it.marmas.task.manager.api.dto.AuthResponse.AuthError;
import it.marmas.task.manager.api.dto.GenericResponse;
import it.marmas.task.manager.api.dto.GenericResponse.ResponseError;
import it.marmas.task.manager.api.dto.RefreshTokenDto;
import it.marmas.task.manager.api.model.RefreshToken;
import it.marmas.task.manager.api.model.User;
import it.marmas.task.manager.api.repo.AuthRepository;
import it.marmas.task.manager.api.security.CustomUserDetails;
import it.marmas.task.manager.api.util.Utility;
import jakarta.transaction.Transactional;
@Service
public class AuthServiceImpl implements AuthService {

    @Value("${jwt.refresh_token_validity}")
    private String REFRESH_TOKEN_DURATION; // Refresh token validity in milliseconds

    @Autowired
    private JwtUtil jwtUtil; // Utility class for generating and validating JWTs

    @Autowired
    private AuthRepository authRepository; // Repository for RefreshToken persistence

    @Autowired
    private AuthenticationManager authenticationManager; // Spring authentication manager

    private Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);

    /**
     * Refresh the access token using a valid refresh token.
     * Implements rotating refresh tokens (old token removed, new token issued).
     * @param refreshTokenDto DTO containing the refresh token and device info
     * @return AuthResponse with new access & refresh tokens or error if expired/invalid
     */
    @Transactional
    public AuthResponse refresh(RefreshTokenDto refreshTokenDto) {
        String tk = Utility.seeTokenEnding(refreshTokenDto.getRefreshToken());
        AuthResponse authResponse = new AuthResponse();

        Optional<RefreshToken> opt = authRepository.findByToken(refreshTokenDto.getRefreshToken());
        RefreshToken oldToken = null;

        logger.info("refreshToken is " + tk);
        logger.info("checking if this token is present");

        if (opt.isPresent()) {
            oldToken = opt.get();
            logger.info("token present in db");
        } else {
            logger.info("token not present in db");
        }

        logger.info("check if it's expired");
        if (oldToken.getExpiry().isBefore(Instant.now())) {
            authRepository.remove(oldToken);
            logger.warn("refresh token expired");
            authResponse.setAuthError(new AuthError("login is necessary", 401));
            return authResponse;
        }

        logger.info("token is valid");
        User user = oldToken.getUser();
        CustomUserDetails customUserDetails = new CustomUserDetails(user);

        // Remove old token (rotating refresh token)
        logger.info("removing oldToken :" + Utility.seeTokenEnding(oldToken.getToken()));
        authRepository.remove(oldToken);

        // Generate new refresh token
        logger.info("generating new refresh token");
        String newRefreshTokenStr = jwtUtil.generateRefreshToken(customUserDetails);
        Instant newExpiry = Instant.now().plusSeconds(Long.parseLong(REFRESH_TOKEN_DURATION) / 1000);
        RefreshToken newToken = new RefreshToken(user, newRefreshTokenStr, newExpiry, refreshTokenDto.getDeviceInfo());
        authRepository.save(newToken);

        // Generate new access token
        logger.info("generating new accesstoken");
        String accessToken = jwtUtil.generateAccessToken(customUserDetails);

        return new AuthResponse(accessToken, newRefreshTokenStr, user.getId(), null);
    }

    /**
     * Logout a single refresh token (invalidate it).
     * @param refreshToken The refresh token to remove
     * @return GenericResponse indicating success or error
     */
    @Override
    @Transactional
    public GenericResponse<String> logout(String refreshToken) {
        GenericResponse<String> response = new GenericResponse<>();
        ResponseError error = new ResponseError();

        if (refreshToken == null) {
            error.setMessage(refreshToken);
            error.setCode(400);
            response.setError(error);
            return response;
        }

        Optional<RefreshToken> opt = authRepository.findByToken(refreshToken);
        if (!opt.isPresent()) {
            error.setMessage(refreshToken);
            error.setCode(500);
            response.setError(error);
            return response;
        } else {
            authRepository.remove(opt.get());
            logger.info("token has been removed");
            response.setContent("Logout effected");
            return response;
        }
    }

    /**
     * Logout all refresh tokens for the user (invalidate all sessions).
     * @param refreshToken DTO containing any valid refresh token to identify the user
     * @return GenericResponse indicating success or error
     */
    @Transactional
    public GenericResponse<String> logoutAll(RefreshTokenDto refreshToken) {
        GenericResponse<String> response = new GenericResponse<>();
        ResponseError error = new ResponseError();

        if (refreshToken == null) {
            error.setMessage("missing token to delete");
            error.setCode(400);
            response.setError(error);
            return response;
        }

        String username = jwtUtil.extractUsername(refreshToken.getRefreshToken());
        logger.info("username extracted : " + username);

        Optional<Integer> opt = authRepository.logoutAll(username);

        if (!opt.isPresent() || (opt.isPresent() && opt.get() <= 0)) {
            error.setMessage("error during log out");
            error.setCode(500);
            response.setError(error);
            return response;
        } else {
            logger.info("all tokens have been removed");
            response.setContent("Logout effected");
            return response;
        }
    }

    /**
     * Authenticate a user and generate access + refresh tokens.
     * @param request AuthRequest containing username, password, device info
     * @return AuthResponse with tokens or error if credentials invalid
     */
    @Override
    @Transactional
    public AuthResponse login(AuthRequest request) {
        AuthResponse authResponse = new AuthResponse();
        try {
            // Authenticate user credentials
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();

            // Generate tokens
            String accessToken = jwtUtil.generateAccessToken(userDetails);
            String refreshToken = jwtUtil.generateRefreshToken(userDetails);

            authResponse.setAccessToken(accessToken);
            authResponse.setRefreshToken(refreshToken);
            authResponse.setUserId(userDetails.getUser().getId());

            // Save refresh token in DB
            RefreshToken refToken = new RefreshToken();
            refToken.setToken(refreshToken);
            refToken.setUser(userDetails.getUser());
            refToken.setDeviceInfo(request.getDeviceInfo());
            refToken.setExpiry(jwtUtil.extractExpiration(refreshToken).toInstant());
            authRepository.save(refToken);

            logger.info("refresh token saved in db");
            return authResponse;

        } catch (BadCredentialsException ex) {
            authResponse.setAuthError(new AuthError("Credentials not valid", 401));
            return authResponse;
        }
    }
}
