package it.marmas.task.manager.api.repo;

import java.util.List;
import java.util.Optional;

import it.marmas.task.manager.api.dto.TaskDto;
import it.marmas.task.manager.api.model.User;

public interface UserRepository {
	public Optional<User> getUserById(long u);
	public Optional<List<User>>getAllUsers();
	public User insertUser(User u);
	public User updateUser(User u);
	public User deleteUser(User u);
	public Optional<User>findByUsername(String username);
	public Optional<User> findByEmail(String email);
	public Optional<List<TaskDto>>getUserTasks(long userid);
 	
}
