package it.marmas.task.manager.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import it.marmas.task.manager.api.auth.JwtUtil;
import it.marmas.task.manager.api.dto.UserDto;
import it.marmas.task.manager.api.dto.UserValidationTokenDto;
@Service
public class RegisterServiceImpl implements RegisterService {

    @Autowired
    private UserService userService; // Service to manage users

    @Autowired 
    private JwtUtil jwtUtil; // Utility for generating JWT tokens

    @Autowired
    private EmailService emailService; // Service for sending emails

    @Autowired
    private UserValidationTokenService userValidationTokenService; // Service for storing validation tokens

    /**
     * Register a new user and send an activation email.
     * @param registerRequest DTO containing user registration data (username, email, password, etc.)
     */
    @Override
    public void registerUser(UserDto registerRequest) {
        // Insert the user into the database
        userService.insertUser(registerRequest);

        // Generate a validation token for account activation
        String token = jwtUtil.generateTokenForValidateAccount(registerRequest.getEmail());

        // Send activation email to the user
        boolean emailSent = emailService.sendActivationAccountEmail(
                registerRequest.getEmail(), 
                registerRequest.getUsername(), 
                token
        );

        // If email is successfully sent, store the validation token in the database
        if (emailSent) {
            UserValidationTokenDto tokenDto = new UserValidationTokenDto();
            tokenDto.setEmail(registerRequest.getEmail());
            tokenDto.setToken(token);

            userValidationTokenService.insertToken(tokenDto);
        }
    }
}
