package it.marmas.task.manager.api.service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.marmas.task.manager.api.dto.RequestChangeDeadlineDto;
import it.marmas.task.manager.api.dto.TaskDto;
import it.marmas.task.manager.api.exceptions.ElementNotFoundException;
import it.marmas.task.manager.api.exceptions.IdException;
import it.marmas.task.manager.api.exceptions.TaskException;
import it.marmas.task.manager.api.mapper.Mapper;
import it.marmas.task.manager.api.model.Task;
import it.marmas.task.manager.api.model.Task.Status;
import it.marmas.task.manager.api.repo.TaskRepository;
import it.marmas.task.manager.api.util.Utility;
import it.marmas.task.manager.api.util.UtilityUser;


@Service
public class TaskServiceImpl implements TaskService{

 

 	private static final Logger logger = LoggerFactory.getLogger(TaskServiceImpl.class);
	@Autowired
	@Qualifier("taskMapper")
	private Mapper<Task,TaskDto> taskMapper;
  
	@Autowired
	private TaskRepository taskRepository;
	
	@Value("${time.victoria}")
	private  String defaultTimezone;
 
	@Transactional(readOnly = true)
	@Override
	public TaskDto getTaskById(long id) {
		Optional<Task> task= taskRepository.getTaskById(id);
		if(task.isPresent()) {
			return taskMapper.toDto(task.get());
		}
		throw new ElementNotFoundException("Not Task found with this ID "+id);
 	}
	@Transactional(readOnly = true)
	@Override
	public List<TaskDto> getAllTask() {
		 Optional<List<Task>> opt= taskRepository.getAllTask();
		 if(opt.isPresent()) {
		  List<TaskDto>tasks= opt.get().stream().map(taskMapper::toDto).collect(Collectors.toList());
		  return tasks;
		 }
		 String msgError="No task found";
		 logger.info(msgError);
		 throw new ElementNotFoundException(msgError);
	}
	@Transactional
	@Override
	public TaskDto insertTask(TaskDto t) {
		Task entity= taskMapper.toEntity(t);
		if(t.getDeadline()!=null) {
		entity.setDeadline(t.getDeadline());
		}
		logger.info("dto iniziale : "+ t);
		Optional<Task>opt= taskRepository.insertTask(entity);
		if(opt.isPresent()) {
		logger.info("opt is present");
		logger.info("entity :"+entity);
		TaskDto dto =taskMapper.toDto(opt.get());
		logger.info("deadline from db : "+Utility.formatLdl(opt.get().getDeadline()))  ;
		dto.setDeadline(opt.get().getDeadline());
		dto.setTimeZone(t.getTimeZone());
		logger.info("deadline with ZoneId "+ t.getTimeZone() +" : "+Utility.formatLdl(dto.getDeadline()));
		
 		return	dto;
		}
		 throw new TaskException("Task has not been created");
	}
	@Transactional
	@Override
	public TaskDto updateTask(TaskDto t)   {
		String msgError=null;
		if(t!=null) {
	    	 logger.info("searching existing task with id : "+ t.getId());
			Optional<Task> opt  = taskRepository.getTaskById(Long.parseLong( t.getId()));
			
	       if(opt.isPresent()) {
	    	 logger.info("task retrieved : "+ opt);
	    	   Task task= opt.get();
	    	   String description=t.getDescription();
	    	   String title=t.getTitle();
	    	   String status=t.getStatus();
	    	   Instant updatedAt=Instant.now();
	    	   
	    	   if(Utility.notNull(description)){
	    		   logger.info("description : "+ description);
	    		   task.setDescription(description);
	    		   
	    	   }
	    	   if(Utility.notNull(title)) {
	    		   logger.info("title : "+ title);
	    		   task.setTitle(title);
	    		   
	    	   }
	    	   if(Utility.notNull(status)){
	    		   logger.info("status : "+ status);
	    		   task.setStatus(Status.valueOf(status));
	    	   }
 	    	   task.setUpdatedAt(updatedAt);

	    	   
	    	   	    	    
	   			opt=  taskRepository.updateTask(task);
				  logger.info("task aggiornata \r"+ opt.get());

	   			 if(opt.isPresent()) {
 				  t=taskMapper.toDto(opt.get());
 				  return t;
	   			 }else {
	   				 msgError="Task not updated";
	   				 logger.info(msgError);
	   				 throw new ElementNotFoundException(msgError);
 				}
				
	       }else {
	    	   msgError="No task found for this ID : "+t.getId();
 				 logger.info(msgError);
			   throw new ElementNotFoundException(msgError);
	       }
			
		}
		msgError="Task not updated. Object Dto is null";
		logger.warn(msgError);
		throw new TaskException(msgError);

		
	}
	@Transactional
	@Override
	public TaskDto deleteTask(String  id,String username,List<String> roles){
		
 		try {
			logger.info("username : "+ username+" id "+ id );
			roles.forEach(logger::info);
			Task task= taskRepository.getTaskById(Long.parseLong(id)).orElseThrow(()->{
				
				String errorMsg="Task having this id : "+ id+ " not found ";
				logger.error(errorMsg);
				throw new ElementNotFoundException(errorMsg);
			});
			UtilityUser.checkAuthorization(id, username, roles);
			logger.info("task found ");
			Task deletedTask= taskRepository.deleteTask(task).orElseThrow(()->{
				String errorMsg="Task hasn't been found after deleting";
				logger.error(errorMsg);
				 throw  new ElementNotFoundException(errorMsg);
			});
			logger.info("task deleted ");
			return taskMapper.toDto(deletedTask);
			 
			 
		}catch(NumberFormatException e) {
			String errorMsg="invalid ID";
			logger.error(errorMsg);
			throw new TaskException(errorMsg);
		}
 	}
	
	@Transactional(readOnly = true)
	@Override
 	public List<TaskDto> findAllTaskById(List<Long>ids){
		Optional<List<Task>> opt=taskRepository.findAllTaskById(ids);
		if(opt.isPresent()) {
			return opt.get().stream().map(taskMapper::toDto).toList();
		}
		return null;
	}
	@Override
	public List<TaskDto> findByUsername(String username) {
		
		 Optional<List<TaskDto>>opt= taskRepository.findByUsername(username);
		 if(opt.isPresent()) {
			 return opt.get();
		 }
		 throw new ElementNotFoundException("No Task associated to this username");
 	}

   public Optional<Task> getEntityTaskById(long id) {
	return taskRepository.getTaskById(id);
	}
   @Override
   public Optional<List<TaskDto>> findBetweenDates() {
	   ZoneId zone= ZoneId.of(defaultTimezone);
	logger.info("scanning for expiring tasks");
	Instant now =Instant.now().atZone(zone).toInstant();
    Instant tomorrow = now.plus(1, ChronoUnit.DAYS).atZone(zone).toInstant(); // same time tomorrow
	logger.info("now "+Utility.formatLdl(now,zone));
	logger.info("tomorrow "+Utility.formatLdl(tomorrow,zone));
	logger.info("using timezone :"+defaultTimezone);
	 
	List<TaskDto>res= taskRepository.findBetweenDates(now, tomorrow).orElseGet(()->{
		String msgError="No result found dates between "+Utility.formatLdl(now)+" "+Utility.formatLdl(tomorrow);
		logger.warn(msgError);
	return List.of();
	}
	);
	
 	return Optional.of(res);
   }
   @Override
   @Transactional
   public Optional<TaskDto> updateDeadline(String id,RequestChangeDeadlineDto request) {
	   if(!UtilityUser.idIsValid(id)) {
		 throw new IdException("Id not valid :"+id);
	   }
	
	   logger.info("getting task by id");
	   Task task= taskRepository.getTaskById(Long.parseLong(id)).orElseThrow(()->new ElementNotFoundException("Task not found by id :"+id));
	   logger.info("task found : "+task.getTitle());
 	   
	   logger.info("deadline previous :"+ request.getDeadline());
 	   
	   task.setDeadline(request.getDeadline());
	   
	   task= taskRepository.updateTask(task).orElseThrow(()-> new TaskException("exception in task updating occurred"));
		logger.info("task deadline updated"+ task.getDeadline());
		
	    TaskDto dto = taskMapper.toDto(task);
	    dto.setDeadline(task.getDeadline());
		logger.info("task transformed in dto : "+ dto);
		ZoneId timeZoneId= ZoneId.of(request.getTimeZone());
		dto.setTimeZone(timeZoneId);
 	return Optional.of(dto);
   }

 
	
 }
