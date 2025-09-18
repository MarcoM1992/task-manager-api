package it.marmas.task.manager.api.controller;

import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import it.marmas.task.manager.api.dto.AssignTaskDto;
import it.marmas.task.manager.api.dto.UserDto;
import it.marmas.task.manager.api.exceptions.ElementNotFoundException;
import it.marmas.task.manager.api.service.UserService;

@RestController
@RequestMapping("/user")
public class UserController {

     
	private static final Logger logger= org.slf4j.LoggerFactory.getLogger(UserController.class);
	
@Autowired
private UserService userService;

    
	@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
	@GetMapping("/getUser")
 	public ResponseEntity<?> getUser(@RequestParam int id,Authentication authentication) {
		try {
		return ResponseEntity.ok(userService.getUserById(id,authentication.getName(),authentication.getAuthorities().stream().map(x->x.getAuthority()).toList()));
		}catch(Exception e){
			logger.warn(e.getMessage());
		return	ResponseEntity.badRequest().body(e.getMessage());
		}
		
	}	
	@PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/update")
    public ResponseEntity<?>update(@RequestBody UserDto userDto,Authentication authentication){
    	try {
    		return ResponseEntity.ok(userService.updateUser(userDto,authentication.getName(),authentication.getAuthorities().stream().map(x->x.getAuthority()).collect(Collectors.toList())));
    	}catch(Exception e) {
    		logger.error(e.getMessage());
    		return ResponseEntity.badRequest().body(e.getMessage());
    	}
    }
	
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/allUsers")
	 public ResponseEntity<?>getAllUsers(){
		try {
			return	ResponseEntity.status(HttpStatusCode.valueOf(200)).body(userService.getAllUsers()) ;
		}catch(ElementNotFoundException e) {
			logger.error(e.getMessage());
    		return ResponseEntity.badRequest().body(e.getMessage());

		}
	}
	
	@PreAuthorize("hasAnyRole('USER','ADMIN')")
	@PatchMapping("/assignTask")
	public ResponseEntity<?>assignTask(@RequestBody AssignTaskDto assignTaskDto,Authentication authentication ){
		try {
			return ResponseEntity.ok( userService.assignTask(assignTaskDto,authentication.getName(),authentication.getAuthorities().stream().map(x->x.getAuthority()).toList()));
		}catch (Exception e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
		
	}
	@PreAuthorize("hasAnyRole('USER', 'ADMIN')")
	@PatchMapping("/removeTask")
	public ResponseEntity<?>removeTask(@RequestBody AssignTaskDto assignTaskDto,Authentication authentication ){
		try {
			return ResponseEntity.ok( userService.removeTask(assignTaskDto,authentication.getName(),authentication.getAuthorities().stream().map(x->x.getAuthority()).toList()));
		}catch (Exception e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
	}
	@PreAuthorize("hasAnyRole('ADMIN','USER')")
	@GetMapping("getTaskByUser/{userId}")
	public ResponseEntity<?>getTaskByUser(Authentication authentication,@PathVariable long userId){
		logger.info("chiamata ricevuta");
 	 
			return ResponseEntity.ok(userService.getUserTasks(authentication.getName(),authentication.getAuthorities().stream().map(x->x.getAuthority()).toList(), userId));
	
	} 
	
}