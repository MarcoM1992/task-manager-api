package it.marmas.task.manager.api.service;

import java.util.List;

import it.marmas.task.manager.api.dto.UserDto;
import it.marmas.task.manager.api.dto.UserValidationTokenDto;

public interface UserValidationTokenService   {
	public UserDto validateAccount(String token);

	public List<UserValidationTokenDto> getAllTokenForUsername(String username);

	public UserValidationTokenDto insertToken(UserValidationTokenDto dto);
	
 }
