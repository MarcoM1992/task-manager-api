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
@RequestMapping("/auth") // Base path for all authentication endpoints
public class AuthController {

    private Logger logger = LoggerFactory.getLogger(AuthController.class);

    @Autowired
    private RegisterService registerService;

    @Autowired
    private UserValidationTokenService userValidationTokenService;

    @Autowired
    private PasswordResetTokenService passwordResetTokenService;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    @Autowired
    private AuthService authService;

    private static final String msgError = "MSG_ERROR";

    /**
     * Login endpoint.
     * Receives username/password in AuthRequest and returns JWT tokens if valid.
     */
    @ResponseBody
    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        try {
            return ResponseEntity.ok(authService.login(request));
        } catch (Exception ex) {
            logger.error(ex.getMessage());
            return ResponseEntity.status(HttpStatusCode.valueOf(500))
                                 .body("Backend read error or custom exception");
        }
    }

    /**
     * Register a new user.
     * Returns conflict status if the user already exists.
     */
    @ResponseBody
    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody UserDto registerRequest) {
        try {
            registerService.registerUser(registerRequest);
        } catch (UserAlredyPresentException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(e.getMessage());
        }
        return ResponseEntity.ok("Activate Account via email");
    }

    /**
     * Request a password reset token.
     * Sends an email with the token if user exists.
     */
    @ResponseBody
    @PostMapping("/request-password-reset")
    public ResponseEntity<?> requestPasswordReset(@RequestBody String email) {
        try {
            Optional<User> user = userService.getUserEntityByEmail(email);
            if (user.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("User not found");
            }

            // Generate reset token and send via email
            PasswordResetTokenDto resetToken = jwtUtil.generateTokenForResetPassword(email);
            resetToken.setEmail(email);
            String token = resetToken.getToken();
            boolean emailSent = emailService.sendResetPasswordEmail(email, token, user.get().getUsername());
            if (emailSent) {
                return ResponseEntity.ok(passwordResetTokenService.insertToken(resetToken));
            } else {
                throw new Exception("Error sending email");
            }

        } catch (Exception e) {
            logger.error(e.getMessage());
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    /**
     * Display password reset form.
     * Used when user clicks the link in email.
     */
    @GetMapping("/change_password")
    public String showForm(Model model, @RequestParam String token) {
        model.addAttribute("token", token);
        model.addAttribute(msgError, false);
        return "password_reset_form"; // Return the HTML view
    }

    /**
     * Reset password via form submission (HTML form).
     */
    @PostMapping("/reset_password")
    public ResponseEntity<?> resetPassword(@ModelAttribute RequestPasswordDto dto) {
        try {
            return ResponseEntity.ok(passwordResetTokenService.resetPassword(dto.getNewPassword(), dto.getToken()));
        } catch (Exception e) {
            logger.error(e.getMessage());
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    /**
     * Reset password via API call (JSON request).
     */
    @ResponseBody
    @PostMapping("/reset_password_api")
    public ResponseEntity<?> resetPasswordApi(@RequestBody RequestPasswordDto dto) {
        try {
            logger.info(dto.getNewPassword());
            return ResponseEntity.ok(passwordResetTokenService.resetPassword(dto.getNewPassword(), dto.getToken()));
        } catch (Exception e) {
            logger.error(e.getMessage());
            return ResponseEntity.internalServerError().body(e.getMessage());
        }
    }

    /**
     * Activate account endpoint.
     * Validates the token and returns success page if valid.
     */
    @GetMapping("/activate_account")
    public String activateAccount(@RequestParam String token, Model model) {
        try {
            UserDto dto = userValidationTokenService.validateAccount(token);
            model.addAttribute("user", dto);
            return "/activation-success"; // HTML success page
        } catch (Exception e) {
            logger.error(e.getMessage());
            return null;
        }
    }

    /**
     * Refresh JWT token using refresh token.
     */
    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@RequestBody RefreshTokenDto request) {
        try {
            return ResponseEntity.ok(authService.refresh(request));
        } catch (Exception e) {
            logger.error(e.getMessage());
            return ResponseEntity.status(HttpStatusCode.valueOf(500))
                                 .body("Backend read error or custom exception");
        }
    }

    /**
     * Logout a user by invalidating a refresh token.
     * Accessible to USER and ADMIN roles.
     */
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @PostMapping("/logout")
    public ResponseEntity<?> logout(@RequestBody String refreshToken) {
        try {
            return ResponseEntity.ok(authService.logout(refreshToken));
        } catch (Exception e) {
            logger.error(e.getMessage());
            return ResponseEntity.status(HttpStatusCode.valueOf(500))
                                 .body("Backend read error or custom exception");
        }
    }

    /**
     * Logout all sessions for a user by invalidating all refresh tokens.
     * Accessible to USER and ADMIN roles.
     */
    @PreAuthorize("hasAnyRole('USER','ADMIN')")
    @PostMapping("/logoutAll")
    public ResponseEntity<?> logoutAll(@RequestBody RefreshTokenDto refreshToken) {
        try {
            return ResponseEntity.ok(authService.logoutAll(refreshToken));
        } catch (Exception e) {
            logger.error(e.getMessage());
            return ResponseEntity.status(HttpStatusCode.valueOf(500))
                                 .body("Backend read error or custom exception");
        }
    }
}
