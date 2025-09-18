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

     Long id = entity.getId();
     User user = entity.getUser();
     String username = null;
     Instant createdAt = null;
     Instant updatedAt = null;
     Instant deadLine = null;
     String title = entity.getTitle();
     String description = entity.getDescription();
     Status status = entity.getStatus();
     String timezone = null;
     String statusName = null;

     if (status != null) {
         statusName = status.name();
     }
     if (user != null && user.getUsername() != null) {
         username = user.getUsername();
     }

     // Copy timestamps if present
     if (entity.getCreatedAt() != null) createdAt = entity.getCreatedAt();
     if (entity.getUpdatedAt() != null) updatedAt = entity.getUpdatedAt();
     if (entity.getDeadline() != null) deadLine = entity.getDeadline();

     // Use user's timezone if available
     if (user != null && user.getTimezone() != null) timezone = user.getTimezone();

     return new TaskDto(
             id,
             username,
             createdAt,
             title,
             description,
             statusName,
             updatedAt,
             deadLine,
             timezone
     );
 }

 @Override
 public Task toEntity(TaskDto dto) {
     logger.info("Mapping DTO to Task entity");

     Task t = new Task();

     // Convert string ID to long if valid
     if (dto.getId() != null && UtilityUser.idIsValid(dto.getId())) {
         t.setId(Long.parseLong(dto.getId()));
         logger.info("ID set: " + dto.getId());
     }

     if (dto.getCreatedAt() != null) t.setCreatedAt(dto.getCreatedAt());
     if (dto.getDescription() != null) t.setDescription(dto.getDescription());
     if (dto.getTitle() != null) t.setTitle(dto.getTitle());
     if (dto.getUpdatedAt() != null) t.setUpdatedAt(dto.getUpdatedAt());

     // Convert status string back to enum
     if (dto.getStatus() != null) {
         t.setStatus(Status.valueOf(dto.getStatus()));
     }

     if (dto.getDeadline() != null) t.setDeadline(dto.getDeadline());

     return t;
 }
}