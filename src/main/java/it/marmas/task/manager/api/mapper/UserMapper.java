package it.marmas.task.manager.api.mapper;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import it.marmas.task.manager.api.dto.TaskDto;
import it.marmas.task.manager.api.dto.UserDto;
import it.marmas.task.manager.api.model.Task;
import it.marmas.task.manager.api.model.User;
import it.marmas.task.manager.api.util.Utility;

@Component("userMapper")
public class UserMapper implements Mapper<User, UserDto>{
private static final Logger logger= LoggerFactory.getLogger(UserMapper.class);
	@Qualifier("taskMapper")
	@Autowired
     private  Mapper<Task,TaskDto> taskMapper; 
    

	@Override
	public UserDto toDto(User entity) {
	    Set<Task> tasks = entity.getTasks();
	    String imagePath = entity.getImagePath();
	    Instant createdAt = entity.getCreatedAt();
	    LocalDateTime localDate = null;
	    String timezone = entity.getTimezone();
	    
	    // Convert UTC createdAt to user's local timezone
	    if (createdAt != null && timezone != null) {
	        ZoneId zone = ZoneId.of(timezone);
	        localDate = Utility.instantToLocalDateTime(createdAt, zone);
	    }

	    UserDto userDto = new UserDto(
	        entity.getId(),
	        entity.getUsername(),
	        entity.getRoles() != null 
	            ? entity.getRoles().stream().map(role -> role.getName()).collect(Collectors.toList())
	            : null,
	        entity.isEnabled(),
	        localDate,
	        entity.getEmail(),
	        entity.getPassword(),
	        tasks == null ? null : entity.getTasks().stream().map(taskMapper::toDto).toList(),
	        imagePath == null ? "user.jpg" : imagePath, // default image
	        entity.getTimezone()
	    );
	    return userDto;
	}

	@Override
	public User toEntity(UserDto dto) {
	    User u = new User();
	    if (dto.getUsername() != null) {
	        u.setUsername(dto.getUsername());
	    }
	    // Convert user's local datetime back to UTC Instant
	    if (dto.getCreatedAt() != null && dto.getTimezone() != null) {
	        u.setCreatedAt(Utility.localDateTimeToInstant(dto.getCreatedAt(), dto.getTimezone()));
	        logger.info("User creation date: " + Utility.formatLdl(u.getCreatedAt()));
	    }
	    if (dto.getEmail() != null) u.setEmail(dto.getEmail());
	    if (dto.getPassword() != null) u.setPassword(dto.getPassword());
	    if (dto.getTasks() != null) {
	        Set<Task> tasks = dto.getTasks().stream().map(taskMapper::toEntity).collect(Collectors.toSet());
	        u.setTasks(tasks);
	    }
	    if (dto.getImagePath() != null) u.setImagePath(dto.getImagePath());
	    if (dto.getTimezone() != null) u.setTimezone(dto.getTimezone());

	    return u;
	}
}