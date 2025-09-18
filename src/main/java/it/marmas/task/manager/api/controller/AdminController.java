package it.marmas.task.manager.api.controller;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import it.marmas.task.manager.api.dto.UserDto;
import it.marmas.task.manager.api.service.UserService;

@RestController
@RequestMapping("/ADMIN") // Base path for all admin endpoints
public class AdminController {
	
	// Logger to track warnings and other information
	private static final Logger logger = LoggerFactory.getLogger(AdminController.class);
	
	@Autowired
	private UserService userService; // Service to handle user-related operations
	
	/**
	 * Delete a user by ID
	 * Only accessible by users with the ADMIN role
	 * @param id The ID of the user to delete
	 * @return ResponseEntity with success or error message
	 */
	@DeleteMapping("/delete")
	@PreAuthorize("harRole('ADMIN')") // Only admins can access this endpoint
	public ResponseEntity<?> delete(@RequestParam int id) {
		try {
			// Call service to delete the user and get a UserDto in response
			UserDto u = userService.deleteUser(id);
			return ResponseEntity.status(200).body("User successfully deleted: " + u);
		} catch (Exception e) {
			// Log warning and return a 400 Bad Request with error message
			logger.warn(e.getMessage());
			return ResponseEntity.badRequest().body("You couldn't delete the user: " + e.getMessage());
		}
		// Note: It is recommended to return UserDto instead of the entity in service methods
	}
	
	/**
	 * Debug endpoint to inspect current authentication details
	 * Useful for checking the logged-in user's username, authorities, and authentication status
	 * @return ResponseEntity containing authentication information or message if none
	 */
	@GetMapping("/debug-auth")
	public ResponseEntity<?> debugAuth() {
	    // Retrieve the current authentication from the security context
	    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    
	    if (auth == null) {
	        return ResponseEntity.ok("No authentication present");
	    }
	    
	    // Extract authentication details
	    String username = auth.getName();
	    boolean authenticated = auth.isAuthenticated();
	    Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();

	    // Build a map with relevant authentication info
	    Map<String, Object> info = new HashMap<>();
	    info.put("username", username);
	    info.put("authenticated", authenticated);
	    info.put("authorities", authorities.stream()
	        .map(GrantedAuthority::getAuthority)
	        .collect(Collectors.toList()));

	    return ResponseEntity.ok(info);
	}
}
