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
public class PasswordResetTokenServiceImpl  implements PasswordResetTokenService{

	private static final Logger logger= LoggerFactory.getLogger(PasswordResetTokenServiceImpl.class);
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	private String REQUEST_PASSWORD_RESET="reset_password";
 	private static final String PURPOSE="purpose";
	@Value("${jwt.secret}")
	private String secretKey;
	@Autowired
	private UserService userService;
  
	@Autowired 
	private PasswordResetTokenRepository passwordResetTokenRepo;
	
	@Autowired
	@Qualifier("tokenMapper")
	Mapper<PasswordResetToken,PasswordResetTokenDto> tokenMapper;

   
	@Transactional
	@Override
	public String resetPassword(String newPassword, String token) {
 		logger.info("searching if token exists: "+token);
		PasswordResetToken resetToken =passwordResetTokenRepo.findByToken(token).orElseThrow(()-> new InvalidOneTimeTokenException("Token has not been found "));
 		
		logger.info("token exists");
		
 		
		logger.info("checking if token is expired");
		
		 if(checkIfTokenExpiredOrDisabled(resetToken)) {
				String errorMsg="this Token expired or not valid";
				logger.error(errorMsg);
				return errorMsg;
		 }
		
		 logger.info("token not expired");
		 
		 logger.info("parsing claims");
		 
		Claims claims =Jwts.parserBuilder()
				.setSigningKey(Keys.hmacShaKeyFor(secretKey.getBytes(StandardCharsets.UTF_8)))
				.build()
				.parseClaimsJws(token)
				.getBody();
		
		String purpose= claims.get(PURPOSE,String.class);
		
		logger.info(PURPOSE + " : "+purpose);
		
		logger.info("purpose is equal "+REQUEST_PASSWORD_RESET+" =="+(purpose.equals(REQUEST_PASSWORD_RESET)));
		if(!purpose.equals(REQUEST_PASSWORD_RESET)){
			throw new RuntimeException("Token purpose not valid");
		}
		
		String email= claims.getSubject();
		logger.info("email retrieved from claims : "+ email);
		
		logger.info("looking for user by email :"+email);
		User user = userService.getUserEntityByEmail(email).orElseThrow(()->new ElementNotFoundException("User not found with this email : "+email));
		
 		
		user.setPassword(passwordEncoder.encode(newPassword));
	    
		userService.updateUser(user);
		logger.info("user password changed");
 		disableToken(resetToken);
		logger.info("token disabled ");
		return "password changed ";
		 
	}

	public boolean checkIfTokenExpiredOrDisabled(PasswordResetToken token) {
		LocalDateTime tokenExpiration= token.getExpiryDate();
		LocalDateTime currentTime=LocalDateTime.now(ZoneOffset.UTC);
		if(tokenExpiration.isBefore(currentTime)||!token.isEnabled()) {
		
			disableToken(token);
			logger.info("expired token status updated");
			return true;
		 
		}
		return false;
		
	}
 	
	private void disableToken(PasswordResetToken token) {
		token.setEnabled(false);
		passwordResetTokenRepo.updateToken(token);
	}

	@Override
	@Transactional
	public Optional<PasswordResetTokenDto> insertToken(PasswordResetTokenDto passwordResetTokenDto) {
		
		String email = passwordResetTokenDto.getEmail();
		User u=null;
		if(email!=null) {
			u= userService.getUserEntityByEmail(email).orElseThrow(()->new ElementNotFoundException("user with email "+ email + " not found"));
			logger.info("user retrieved by email :"+email+" is : "+u);
 		}
	
		PasswordResetToken prt= tokenMapper.toEntity(passwordResetTokenDto) ;
		
		prt.setUser(u);
		logger.info(prt+"");
		
		logger.info("password reset token entity created "+ prt);
		passwordResetTokenRepo.insertToken(prt);
		logger.info("token inserted in db");

		return Optional.of(tokenMapper.toDto(prt));
	}

}
