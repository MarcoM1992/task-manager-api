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
@RequestMapping("/ADMIN")
public class AdminController {
	
	private static final Logger logger = LoggerFactory.getLogger(AdminController.class);
	
	@Autowired
	private UserService userService;
	
	@DeleteMapping("/delete")
	@PreAuthorize("harRole('ADMIN')")
	public ResponseEntity<?>delete(@RequestParam int id ){
		try {
		UserDto u=userService.deleteUser(id);
		return ResponseEntity.status(200).body("utente eliminato con successo : "+u );
		}catch(Exception e) {
			logger.warn(e.getMessage());
			return ResponseEntity.badRequest().body("you couldn't delete the User "+e.getMessage());
		}
	// crea Dto e fai ritornare UserDto invece di user ai metodi del service
	
	}
	
	@GetMapping("/debug-auth")
	public ResponseEntity<?> debugAuth() {
	    Authentication auth = SecurityContextHolder.getContext().getAuthentication();
	    if (auth == null) {
	        return ResponseEntity.ok("Nessuna autenticazione presente");
	    }
	    String username = auth.getName();
	    boolean authenticated = auth.isAuthenticated();
	    Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();

	    Map<String, Object> info = new HashMap<>();
	    info.put("username", username);
	    info.put("authenticated", authenticated);
	    info.put("authorities", authorities.stream()
	        .map(GrantedAuthority::getAuthority)
	        .collect(Collectors.toList()));

	    return ResponseEntity.ok(info);
	}
	
	
	
}
