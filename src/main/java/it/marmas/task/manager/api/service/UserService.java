package it.marmas.task.manager.api.service;

import java.util.List;
import java.util.Optional;

import it.marmas.task.manager.api.dto.AssignTaskDto;
import it.marmas.task.manager.api.dto.GenericResponse;
import it.marmas.task.manager.api.dto.TaskDto;
import it.marmas.task.manager.api.dto.UserDto;
import it.marmas.task.manager.api.model.User;

public interface UserService {
	  UserDto getUserById(long u, String currentUsername,List<String>roles) ;
	  List<UserDto>getAllUsers();
	  UserDto insertUser(UserDto u);
	  UserDto updateUser(UserDto u, String currentUsername, List<String> list);
	  UserDto deleteUser(long u);
	  UserDto findByUsername(String username);
	  UserDto findByEmail(String email) ;
	  UserDto updateUser(User user);
 	  String assignTask(AssignTaskDto assignTaskDto,String currentUser, List<String> currentAuthority);
	  UserDto removeTask(AssignTaskDto assignTaskDto, String name, List<String> list);    
	  Optional<User> getUserEntityByEmail(String email);
 	  GenericResponse<List<TaskDto>> getUserTasks(String currentUser, List<String>roles, long userId);
 

 }
