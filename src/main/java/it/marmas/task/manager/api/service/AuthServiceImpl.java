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
 	private  String REFRESH_TOKEN_DURATION;
	
	@Autowired
	private JwtUtil jwtUtil;
	
	@Autowired
	private AuthRepository authRepository;
 
	@Autowired
    private   AuthenticationManager authenticationManager;
	
	private Logger logger = LoggerFactory.getLogger(AuthServiceImpl.class);
    @Transactional
    public AuthResponse refresh(RefreshTokenDto refreshTokenDto) {
    	
    	String tk = Utility.seeTokenEnding(refreshTokenDto.getRefreshToken());
    	 
    	AuthResponse authResponse= new AuthResponse();
        Optional<RefreshToken> opt = authRepository.findByToken(refreshTokenDto.getRefreshToken());
        RefreshToken oldToken=null;
        logger.info("refreshToken is "+tk);
    	logger.info("checking if this token is present");
    	
        if(opt.isPresent()) {
        	
        	oldToken=opt.get();
        	logger.info("token present in db");
        	
        	
         }else {
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
        CustomUserDetails customUserDetails=new CustomUserDetails(user);
        // rimuovo il token vecchio (rotating refresh token)
        logger.info("removing oldToken :" +Utility.seeTokenEnding(oldToken.getToken()));
        authRepository.remove(oldToken);

        // genero nuovo refresh token
        logger.info("generating new refresh token");
        String newRefreshTokenStr = jwtUtil.generateRefreshToken(customUserDetails);
        logger.info("nuovo refresh token = "+Utility.seeTokenEnding(newRefreshTokenStr));
        Instant newExpiry = Instant.now().plusSeconds(Long.parseLong(REFRESH_TOKEN_DURATION)/1000);
        RefreshToken newToken = new RefreshToken(user, newRefreshTokenStr, newExpiry, refreshTokenDto.getDeviceInfo());
        logger.info("saving refresh token ");
        authRepository.save(newToken);

        logger.info("generating new accesstoken");
        String accessToken = jwtUtil.generateAccessToken(customUserDetails);
        
        return new AuthResponse(accessToken, newRefreshTokenStr,user.getId(),null);
    }

	@Override
	@Transactional
	public GenericResponse<String> logout(String refreshToken) {
		GenericResponse<String> response =new GenericResponse<>();
		GenericResponse.ResponseError error=new ResponseError();
		if(refreshToken==null) {
			error.setMessage(refreshToken);
			error.setCode(400);
 			response.setError(error);
			return response;
		}
		Optional<RefreshToken>opt=authRepository.findByToken(refreshToken);
		if(!opt.isPresent()) {
 			error.setMessage(refreshToken);
			error.setCode(500);
			response.setError(error);
			return response;
		}else {
			authRepository.remove(opt.get());
			String msgLog="token has been removed";
			
			logger.info(msgLog);
			String msgResp="Logout effected";
			response.setContent(msgResp);
			return response;
		}
		 
	}
	@Transactional
	public GenericResponse<String> logoutAll(RefreshTokenDto refreshToken){
		GenericResponse<String> response =new GenericResponse<>();
		GenericResponse.ResponseError error=new ResponseError();
		if(refreshToken==null) {
			error.setMessage("missing token to delete");
			error.setCode(400);
 			response.setError(error);
			return response;
		}
		String username=jwtUtil.extractUsername(refreshToken.getRefreshToken());
		logger.info("username extracted : "+ username);
 		Optional<Integer>opt=authRepository.logoutAll(username);
 		
		if(!opt.isPresent()||(opt.isPresent()&&opt.get()<=0)) {
 			error.setMessage("error during log out");
			error.setCode(500);
			response.setError(error);
			return response;
		}else {
			String msgLog="all tokens have been removed";
			logger.info(msgLog);
			String msgResp="Logout effected";
			response.setContent(msgResp);
			return response;
		}
		 
	}

	@Override
	@Transactional
	public AuthResponse login(AuthRequest request) {
		AuthResponse authResponse=new AuthResponse();
        try {
            Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
            );

            CustomUserDetails userDetails = (CustomUserDetails) authentication.getPrincipal();
            String accessToken = jwtUtil.generateAccessToken(userDetails);
            String refreshToken=jwtUtil.generateRefreshToken(userDetails);
            logger.info(Utility.seeTokenEnding(accessToken));
            authResponse.setAccessToken(accessToken);
            authResponse.setRefreshToken(refreshToken);
            authResponse.setUserId(userDetails.getUser().getId());
            RefreshToken refToken =new RefreshToken();
            
            refToken.setToken(refreshToken);
            refToken.setUser(userDetails.getUser());
            refToken.setDeviceInfo(request.getDeviceInfo());
            refToken.setExpiry(jwtUtil.extractExpiration(refreshToken).toInstant());
            
            authRepository.save(refToken);
            logger.info("refresh token saved in db");
            return authResponse;

        } catch (BadCredentialsException ex) {
        	String message="Credentials not valid";
        	AuthResponse.AuthError error= new AuthError(message, 401);
         	authResponse.setAuthError(error);
         	return authResponse;
         }
	}

 

}
