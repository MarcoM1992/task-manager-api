package it.marmas.task.manager.api.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.v3.oas.annotations.Operation;
import it.marmas.task.manager.api.dto.RequestChangeDeadlineDto;
import it.marmas.task.manager.api.dto.TaskDto;
import it.marmas.task.manager.api.exceptions.ElementNotFoundException;
import it.marmas.task.manager.api.exceptions.TaskException;
import it.marmas.task.manager.api.service.TaskService;

@RestController
@RequestMapping("/task")
public class TaskController {

 	private static Logger logger = LoggerFactory.getLogger(TaskController.class);
	@Autowired
	private TaskService taskService;

  
	@PreAuthorize("hasAnyRole('ADMIN','USER')")
	@GetMapping("/taskById")
	public ResponseEntity<?>getTaskById(@RequestParam long id){
		try {
			 TaskDto t=taskService.getTaskById(id);
			 logger.info(t.toString());
			 return	 ResponseEntity.ok(t);
		}catch (ElementNotFoundException e) {
			return ResponseEntity.badRequest().body(e.getMessage());	
		}catch(Exception e) {
			return ResponseEntity.internalServerError().body(e.getMessage());	
		}
	}

	@PreAuthorize("hasRole('ADMIN')")
	@PostMapping("/insert")
	public ResponseEntity<?>insert(@RequestBody TaskDto taskDto){
		try {
		    return ResponseEntity.ok(taskService.insertTask(taskDto));
		}catch(TaskException e) {
			logger.warn(e.getMessage());
			return ResponseEntity.internalServerError().body(e.getMessage());	
		}catch(Exception e) {
			logger.warn(e.getMessage());
			return ResponseEntity.internalServerError().body(e.getMessage());
		}
	}
	
	@PreAuthorize("hasRole('ADMIN')")
	@GetMapping("/getAllTasks")
	@Operation(summary = "Recupera tutti i task")

		public ResponseEntity<?>getAllTasks(){
		try {
			return ResponseEntity.ok(taskService.getAllTask());
		}catch (Exception e) {
			logger.info(e.getMessage());
			return ResponseEntity.internalServerError().body("No task Found");	

			}
		}
	
	@PreAuthorize("hasRole('ADMIN')")
	@PatchMapping("/update")
	public ResponseEntity<?>update(@RequestBody TaskDto dto){
		try {
			return ResponseEntity.ok(taskService.updateTask(dto));
		}catch(TaskException e) {
			logger.error(e.getMessage());
			return ResponseEntity.internalServerError().body(e.getMessage());
		} catch (ElementNotFoundException e) {
			logger.error(e.getMessage());
			return ResponseEntity.badRequest().body(e.getMessage());
 		} catch (Exception e) {
			logger.error(e.getMessage());

			return ResponseEntity.badRequest().body(e.getMessage());
 		}
	}
	
	@PreAuthorize("hasAnyRole('ADMIN', 'USER')")
	@DeleteMapping("/delete/{taskId}")
	public ResponseEntity<?>deleteTask(@PathVariable(required = true ) String taskId, Authentication authentication){
		if (taskId==null) {
			String errorMsg="ID cannot be null";
			logger.error(errorMsg);
			return ResponseEntity.badRequest().body(errorMsg);
		}
		try {
			return ResponseEntity.ok().body(taskService.deleteTask(taskId,authentication.getName(),authentication.getAuthorities().stream().map(x->x.getAuthority()).toList()));
		}catch(ElementNotFoundException|TaskException e) {
			return ResponseEntity.badRequest().body(e.getMessage());
		}
		 
	}
	
	@PreAuthorize("hasAnyRole('ADMIN','USER')")
	@GetMapping("/byUsername")
	public ResponseEntity<?>findByUsername(@RequestParam(required=true) String username){
		try {
			return ResponseEntity.ok(taskService.findByUsername(username));

		}catch(ElementNotFoundException e) {
			logger.error(e.getMessage());
			return ResponseEntity.badRequest().body(e.getMessage());
		}catch(Exception e) {
			logger.error(e.getMessage());
			return ResponseEntity.ok().body(e.getMessage());
		}
	}
	
	@PreAuthorize("hasAnyRole('ADMIN','USER')")
	@GetMapping("/expiringTasks")
	public ResponseEntity<?> expiringTasks(){
		try {
			return ResponseEntity.ok(taskService.findBetweenDates());

		}catch(ElementNotFoundException e) {
			logger.error(e.getMessage());
			return ResponseEntity.badRequest().body(e.getMessage());
		}catch(Exception e) {
			logger.error(e.getMessage());
			return ResponseEntity.ok().body(e.getMessage());
		}
	}
	
	@PreAuthorize("hasAnyRole('ADMIN','USER')")
	@PatchMapping("/updateDeadline/{id}")
	public ResponseEntity<?>updateDeadline(@PathVariable String id, @RequestBody RequestChangeDeadlineDto request){
 		try {
			return ResponseEntity.ok(taskService.updateDeadline(id,request));

		}catch(ElementNotFoundException e) {
			logger.error(e.getMessage());
			return ResponseEntity.badRequest().body(e.getMessage());
		}catch(Exception e) {
			logger.error(e.getMessage());
			return ResponseEntity.ok().body(e.getMessage());
		}
	}
	

	
	
	
	
	
	
	
}