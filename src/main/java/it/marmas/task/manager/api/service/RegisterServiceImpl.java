package it.marmas.task.manager.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.marmas.task.manager.api.auth.JwtUtil;
import it.marmas.task.manager.api.dto.UserDto;
import it.marmas.task.manager.api.dto.UserValidationTokenDto;

@Service
public class RegisterServiceImpl implements RegisterService{
	@Autowired
	private UserService userService;
	@Autowired 
	private JwtUtil jwtUtil;
	@Autowired
	private EmailService emailService;
	@Autowired
	private UserValidationTokenService userValidationTokenService;
	
	
	@Override
	public void registerUser(UserDto registerRequest) {
    	
    	userService.insertUser(registerRequest);
    	String token = jwtUtil.generateTokenForValidateAccount(registerRequest.getEmail());
    	
     	
    	boolean emailSent=emailService.sendActivationAccountEmail(registerRequest.getEmail(),registerRequest.getUsername(), token);
    	
    	if(emailSent) {
    		UserValidationTokenDto tokenDto= new UserValidationTokenDto();
    		tokenDto.setEmail(registerRequest.getEmail());
    		tokenDto.setToken(token);
     		userValidationTokenService.insertToken(tokenDto);
     		
     		
    	}
    	
	}

}
