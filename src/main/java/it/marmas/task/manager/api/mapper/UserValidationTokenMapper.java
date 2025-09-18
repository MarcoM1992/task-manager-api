package it.marmas.task.manager.api.mapper;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import it.marmas.task.manager.api.dto.UserValidationTokenDto;
import it.marmas.task.manager.api.model.User;
import it.marmas.task.manager.api.model.UserValidationToken;
import it.marmas.task.manager.api.util.Utility;

@Component("userValidationMapper")
public class UserValidationTokenMapper implements Mapper<UserValidationToken, UserValidationTokenDto>{
 

Logger logger = LoggerFactory.getLogger(getClass());

	@Override
	public UserValidationTokenDto toDto(UserValidationToken entity) {
	 UserValidationTokenDto dto = new UserValidationTokenDto();
	 User u= entity.getUser();
	 String token= entity.getToken();
	 LocalDateTime expiryDate=entity.getExpiryDate();
	 if (expiryDate!=null) {
		 logger.info("expiryDate "+Utility.formatLdl(expiryDate));
		 dto.setExpiryDate(expiryDate);
	 }
	 if(token!=null) {
		 logger.info("token "+token);
		dto.setToken(token);
	 }
	 if(u!=null && u.getEmail()!=null) {
		 logger.info("email "+u.getEmail());
		 dto.setEmail(u.getEmail());
	 }
	 dto.setEnabled(entity.isEnabled());
	 
	 return dto;
	}

	@Override
	public UserValidationToken toEntity(UserValidationTokenDto dto) {
 	 String token= dto.getToken();
	 LocalDateTime ldt = dto.getExpiryDate();
	 UserValidationToken entity=new UserValidationToken();
	 if(Utility.notNull(token) ) {
		 entity.setToken(token);
	 }
	 if(Utility.notNull(ldt)) {
		 entity.setExpiryDate(ldt); 
	}
	 entity.setEnabled(dto.isEnabled());
	return  entity;
	}

}
