package it.marmas.task.manager.api.controller;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import it.marmas.task.manager.api.auth.JwtUtil;
import it.marmas.task.manager.api.dto.AuthRequest;
import it.marmas.task.manager.api.dto.PasswordResetTokenDto;
import it.marmas.task.manager.api.dto.RefreshTokenDto;
import it.marmas.task.manager.api.dto.RequestPasswordDto;
import it.marmas.task.manager.api.dto.UserDto;
import it.marmas.task.manager.api.exceptions.UserAlredyPresentException;
import it.marmas.task.manager.api.model.User;
import it.marmas.task.manager.api.service.AuthService;
import it.marmas.task.manager.api.service.EmailService;
import it.marmas.task.manager.api.service.PasswordResetTokenService;
import it.marmas.task.manager.api.service.RegisterService;
import it.marmas.task.manager.api.service.UserService;
import it.marmas.task.manager.api.service.UserValidationTokenService;

@Controller
@RequestMapping("/auth")
public class AuthController {

  	private   Logger logger = LoggerFactory.getLogger(AuthController.class);
 	@Autowired
 	private RegisterService registerService;
	@Autowired
	private UserValidationTokenService userValidationTokenService;
	@Autowired
    private  PasswordResetTokenService passwordResetTokenService;
	@Autowired
	private JwtUtil jwtUtil;
	@Autowired
      private   UserService userService;
	@Autowired
    private  EmailService emailService;
	@Autowired
	private AuthService authService;
	
	private static final String msgError="MSG_ERROR";

   
   @ResponseBody
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
	  
	 try {
            return ResponseEntity.ok(authService.login(request));

        } catch (Exception ex) {
       	 logger.error(ex.getMessage());
 		return ResponseEntity.status(HttpStatusCode.valueOf(500)).body("Read error in backend and custom it");         }
    }
   @ResponseBody
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserDto registerRequest){
    	try {
    	registerService.registerUser(registerRequest);

    	}catch(UserAlredyPresentException e) {
    		
        	return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
    	}
    	return ResponseEntity.ok("Activate Account by email");
    }
   
   
   
   @ResponseBody
     @PostMapping("/request-password-reset")
    public ResponseEntity<?> requestPasswordReset(@RequestBody String email ){
       try {
    	   Optional<User> user = userService.getUserEntityByEmail(email);
    	    if (user.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }
    	   
    	    PasswordResetTokenDto resetToken = jwtUtil.generateTokenForResetPassword(email);
	    	resetToken.setEmail(email);
	    	String token=resetToken.getToken();
    	    boolean emailSent=  emailService.sendResetPasswordEmail(email,token,user.get().getUsername());
    	    if(emailSent) {
    	    	   return ResponseEntity.ok(passwordResetTokenService.insertToken(resetToken));
    	    }else {
    	    	throw new Exception("there was an error in sending the email");
    	    }
    	    

       }catch(Exception e) {
    	   logger.error(e.getMessage());
    	   return ResponseEntity.internalServerError().body(e.getMessage());
       }
    }
   
   @GetMapping("/change_password")
   public String showForm(Model model,@RequestParam String token) {
	   model.addAttribute("token",token);
	   model.addAttribute(msgError,false);
	   return "password_reset_form";
	   
   }
   
      @PostMapping("/reset_password")
    public ResponseEntity<?> resetPassword(@ModelAttribute RequestPasswordDto dto){
     	try {
    		return ResponseEntity.ok(passwordResetTokenService.resetPassword(dto.getNewPassword(), dto.getToken()));
    	}catch(Exception e) {
    	 	   logger.error(e.getMessage());
        	   return ResponseEntity.internalServerError().body(e.getMessage());    	}
    }
   
   
   @ResponseBody
     @PostMapping("/reset_password_api")
    public ResponseEntity<?> resetPasswordApi(@RequestBody RequestPasswordDto dto){
     	try {
     		logger.info(dto.getNewPassword());
    		return ResponseEntity.ok(passwordResetTokenService.resetPassword(dto.getNewPassword(), dto.getToken()));
    	}catch(Exception e) {
    	 	   logger.error(e.getMessage());
        	   return ResponseEntity.internalServerError().body(e.getMessage());    	}
    }

 @GetMapping("/activate_account")
public String activateAccount(@RequestParam() String token,Model model){
 try {
	  UserDto dto=userValidationTokenService.validateAccount(token);
	  model.addAttribute("user",dto);
	  return "/activation-success";
 }catch (Exception e) {
	 logger.error(e.getMessage());
	   return null; 
 }
 
}

 @PostMapping("/refresh")
 public ResponseEntity<?> refresh(@RequestBody RefreshTokenDto request) {	
	 try {
     return ResponseEntity.ok( authService.refresh(request));
	 }catch (Exception e) {
		
		 logger.error(e.getMessage());
	return ResponseEntity.status(HttpStatusCode.valueOf(500)).body("Read error in backend and custom it");
	}
 }
 @PreAuthorize("hasAnyRole('USER','ADMIN')")
 @PostMapping("/logout")
 public ResponseEntity<?>logout(@RequestBody String refreshToken){
	 try {
	 return ResponseEntity.ok(authService.logout(refreshToken));
	 }catch(Exception e) {
		 logger.error(e.getMessage());
		return ResponseEntity.status(HttpStatusCode.valueOf(500)).body("Read error in backend and custom it");
	 }
	 
 }
 @PreAuthorize("hasAnyRole('USER','ADMIN')")
 @PostMapping("/logoutAll")
 public ResponseEntity<?>logoutAll(@RequestBody RefreshTokenDto refreshToken){
	 try {
	 return ResponseEntity.ok(authService.logoutAll(refreshToken));
	 }catch(Exception e) {
		 logger.error(e.getMessage());
		return ResponseEntity.status(HttpStatusCode.valueOf(500)).body("Read error in backend and custom it");
	 }
	 
 }
    
    

}
