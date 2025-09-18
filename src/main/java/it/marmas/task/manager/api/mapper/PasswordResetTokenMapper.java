package it.marmas.task.manager.api.mapper;

import java.time.LocalDateTime;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import it.marmas.task.manager.api.dto.PasswordResetTokenDto;
import it.marmas.task.manager.api.model.PasswordResetToken;

@Component("tokenMapper")
public class PasswordResetTokenMapper implements Mapper<PasswordResetToken,PasswordResetTokenDto> {
public Logger logger  = LoggerFactory.getLogger(getClass());
	@Override
	public PasswordResetTokenDto toDto(PasswordResetToken entity) {
		
		LocalDateTime expiryDate=entity.getExpiryDate();
		String email=null;
		if(entity.getUser()!=null) {
			email= entity.getUser().getEmail();
		}
		String token= entity.getToken();
		 
		 PasswordResetTokenDto dto  = new PasswordResetTokenDto();
		 dto.setEnabled(entity.isEnabled());
		 if(expiryDate!=null)
			 dto.setExpiryDate(expiryDate);
		 if(email!=null)
			 dto.setEmail(email);
		if(token!=null) {
			dto.setToken(token);
		}
		return dto;
		 
	}

	@Override
	public PasswordResetToken toEntity(PasswordResetTokenDto dto) {
		PasswordResetToken entity=new PasswordResetToken();
		LocalDateTime expiryDate=dto.getExpiryDate();
 		String token= dto.getToken();
		
		 if(expiryDate!=null)
			 entity.setExpiryDate(expiryDate);
		 if(token!=null) {
			entity.setToken(token);
		}
		 entity.setEnabled(dto.isEnabled());
		 return entity;
	}

}
