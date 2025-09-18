package it.marmas.task.manager.api.repo;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import it.marmas.task.manager.api.dto.TaskDto;
import it.marmas.task.manager.api.model.Task;

public interface TaskRepository {
	public Optional<Task> getTaskById(long id) ;
	public Optional<List<Task>> getAllTask();
	public Optional<Task> insertTask(Task t);
	public Optional<Task> updateTask(Task t);
	public Optional<Task> deleteTask(Task t);
 	public Optional<List<Task>> findAllTaskById(List<Long>ids);
	public Optional<List<TaskDto>> findByUsername(String username);
	public Optional<List<TaskDto>> findBetweenDates(Instant now, Instant tomorrow);
}

