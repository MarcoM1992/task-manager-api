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
	  private static final String PURPOSE_VALUE_VALIDATE_ACCOUNT="validate-account";
	private static final String PURPOSE = "purpose";
	@Autowired
	private UserValidationTokenRepository userValidationTokenRepository;
	@Autowired
	private UserService userService;
	@Autowired
	@Qualifier("userValidationMapper")
	private Mapper<UserValidationToken, UserValidationTokenDto>userValidationTokenMapper;
	@Value("${jwt.secret}")
	private String secretKey;
	
	@Transactional(readOnly=true)
	public List<UserValidationTokenDto> getAllTokenForUsername(String username){
		 Optional<List<UserValidationToken>>tokenList=userValidationTokenRepository.getAllTokenForUsername(username);
		 
		
		 if(tokenList.isPresent()) {
		 return tokenList.get().stream().map(userValidationTokenMapper::toDto).toList() ;
		 }
		 return null;
	}
	@Transactional
	public UserDto validateAccount(String token) {
		logger.info("searching if token exists :"+token);
		UserValidationToken validToken =userValidationTokenRepository.findByToken(token).orElseThrow((
				)-> new InvalidOneTimeTokenException("Token has not been found "));
	
		logger.info("token exists");
		
		logger.info("checking if token is expired");
		
		if(checkIfTokenExpiredOrNotValid(validToken)) {
			String errorMsg="Token expired or not valid";
			logger.error(errorMsg);
			throw new RuntimeException(errorMsg);
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
		
		logger.info("purpose is equal "+PURPOSE_VALUE_VALIDATE_ACCOUNT+" =="+(purpose.equals(PURPOSE_VALUE_VALIDATE_ACCOUNT)));
		if(!purpose.equals(PURPOSE_VALUE_VALIDATE_ACCOUNT)){
			throw new RuntimeException("Token purpose not valid");
		}
		
		String email= claims.getSubject();
		logger.info("email retrieved from claims : "+ email);
		
		logger.info("looking for user by email :"+email);
		User user = userService.getUserEntityByEmail(email).orElseThrow(()->new ElementNotFoundException("User not found with this email : "+email));
		
		user.setEnabled(true);
		UserDto dto=userService.updateUser(user);
		
		logger.info("user enabled");
		
		logger.info("disabling token in progress");
		
		disableToken(validToken);
		
		logger.info("token disabled");
		return dto;
		
		
	}
	private void disableToken(UserValidationToken token) {
		token.setEnabled(false);
		userValidationTokenRepository.updateToken(token);
	}
	
	private boolean checkIfTokenExpiredOrNotValid(UserValidationToken token) {
		LocalDateTime tokenExpiration= token.getExpiryDate();
		LocalDateTime currentTime=LocalDateTime.now(ZoneOffset.UTC);
		if(tokenExpiration.isBefore(currentTime)) {
			
			return true;
 		}
		return false;
		
	}
	
	
	@Override
	@Transactional
	public UserValidationTokenDto insertToken(UserValidationTokenDto dto) {
 			User u= userService.getUserEntityByEmail(dto.getEmail()).orElseThrow(()->new UserValidationException("Error retrieving email : "+ dto.getEmail()));
 			logger.info("user found by email "+dto.getEmail());
			
 			UserValidationToken userValidationToken= userValidationTokenMapper.toEntity(dto);
 			userValidationToken.setUser(u);
			LocalDateTime now =LocalDateTime.now(ZoneOffset.UTC);
	 		userValidationToken.setExpiryDate(now.plusMinutes(15));
	 		logger.info("insert token "+ userValidationToken);
 			 userValidationToken= userValidationTokenRepository.insertToken(userValidationToken).orElseThrow(()->new RuntimeException("Token not saved ")); 
 	 		logger.info("token saved : "+userValidationToken.getToken());

			  return userValidationTokenMapper.toDto(userValidationToken) ;
		
 		
 		
		 
	}

}
