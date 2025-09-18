package it.marmas.task.manager.api.service;

import java.util.List;
import java.util.Optional;

import it.marmas.task.manager.api.dto.RequestChangeDeadlineDto;
import it.marmas.task.manager.api.dto.TaskDto;
import it.marmas.task.manager.api.model.Task;

public interface TaskService {
	 TaskDto getTaskById(long id) ;
	 List<TaskDto> getAllTask() ;
	 TaskDto insertTask(TaskDto t);
	 TaskDto updateTask(TaskDto t) ;
	 TaskDto deleteTask(String  id,String username,List<String> roles);
 	  List<TaskDto> findAllTaskById(List<Long>ids);
	  List <TaskDto> findByUsername(String username);
	Optional<Task> getEntityTaskById(long id);
	 Optional<List<TaskDto>> findBetweenDates();
 	 Optional<TaskDto> updateDeadline(String id, RequestChangeDeadlineDto request);
 
}
