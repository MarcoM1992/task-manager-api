package it.marmas.task.manager.api.mapper;

import java.time.Instant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import it.marmas.task.manager.api.dto.TaskDto;
import it.marmas.task.manager.api.model.Task;
import it.marmas.task.manager.api.model.Task.Status;
import it.marmas.task.manager.api.model.User;
import it.marmas.task.manager.api.util.UtilityUser;

@Component("taskMapper")
public class TaskMapper implements Mapper<Task, TaskDto>{

 private Logger logger = LoggerFactory.getLogger(TaskMapper.class);

 

	 @Override
	 public TaskDto toDto(Task entity) {
		 
		 Long id=entity.getId();
		 User user= entity.getUser();
		 String username=null;
		 Instant createdAt=null;
		 Instant updatedAt=null;
		 Instant deadLine=null;
 		 String title=entity.getTitle();
 		 String description=entity.getDescription();
 		 Status status= entity.getStatus();
 		 String timezone=null;
 		String statusName=null;
 		 
  		 if(status!=null) {
 			 statusName= status.name();
 		 }
		 if(user!=null&&user.getUsername()!=null) {
			 username=user.getUsername();
		 }
		 
		  if(entity.getCreatedAt()!=null) {
				  createdAt=entity.getCreatedAt();
		  }
		  if(entity.getUpdatedAt()!=null) {
			   updatedAt =entity.getUpdatedAt();
		  }
		  if(entity.getDeadline()!=null) {
			  deadLine=entity.getDeadline();
		  }
		  if(user!=null&&user.getTimezone()!=null) {
			timezone= user.getTimezone();
		  }
		  
 		  		
		  return new TaskDto( 
				  id,
				  username,
				  createdAt,
				  title,
				  description,
				   statusName,
				   updatedAt,
				   deadLine, 
				  	timezone);
 	 }
	 
	 
	 

	 @Override
	 public Task toEntity(TaskDto dto) {
			logger.info("mappando il dto to entity");
		
		
		Task t = new Task();
		
		if(dto.getId()!=null&&UtilityUser.idIsValid(dto.getId())) {
			logger.info("id : "+dto.getId());
			t.setId(Long.parseLong(dto.getId()));
		}
		
		if(dto.getCreatedAt()!=null) {
		logger.info("createdAt :"+dto.getCreatedAt());
		
		t.setCreatedAt(dto.getCreatedAt());
		}
		if(dto.getDescription()!=null) {
			logger.info("description :"+dto.getDescription());
		t.setDescription(dto.getDescription());
		}
		if(dto.getTitle()!=null) {
			logger.info("title :"+dto.getTitle());
		t.setTitle(dto.getTitle());
		}
		if(dto.getUpdatedAt()!=null) {
			logger.info("updatedAt :"+dto.getUpdatedAt());
		t.setUpdatedAt( dto.getUpdatedAt() );
		}
		if(dto.getStatus()!=null) {
		t.setStatus(Status.valueOf(dto.getStatus()));
		logger.info("createdAt :"+dto.getCreatedAt());
		}
		if(dto.getDeadline()!=null) {
			
			t.setDeadline( dto.getDeadline());
			logger.info("deadline :"+dto.getDeadline());
		}
	
 
		return t;
	 }
	 
     
 
}