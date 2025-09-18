package it.marmas.task.manager.api.service;

import java.util.Optional;

import it.marmas.task.manager.api.dto.PasswordResetTokenDto;

public interface  PasswordResetTokenService {

	String resetPassword(String newPassword, String token);
	
	Optional<PasswordResetTokenDto> insertToken(PasswordResetTokenDto passwordResetTokenDto);
	
	
 
 	
}
