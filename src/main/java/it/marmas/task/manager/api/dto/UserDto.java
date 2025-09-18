package it.marmas.task.manager.api.dto;

import java.time.LocalDateTime;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

public class UserDto {
@JsonInclude(JsonInclude.Include.NON_NULL)
private String username;
@JsonInclude(JsonInclude.Include.NON_NULL)
private List<String> roles;
private List<TaskDto> tasks;
@JsonInclude(JsonInclude.Include.NON_NULL)
private boolean enabled = true;
@JsonInclude(JsonInclude.Include.NON_NULL)
private LocalDateTime createdAt;
private String email;
@JsonInclude(JsonInclude.Include.NON_NULL)
private String password;
private String id;
private String imagePath;
private String timezone;

public UserDto() {
	
}
public UserDto(long id,String email,String username,String  timezone) {
	this.email=email;
	this.username=username;
	this.timezone=timezone;
}
public UserDto(String email, String username ) {
	this.email=email;
	this.username=username;
}
//insert User
public UserDto(String username,String password,String email  ) {
	super();
	this.username=username;
	this.password=password;
	this.email=email;
}

public UserDto(String username,String password,String email,String timezone) {
    this.username=username;
    this.password=password;
    this.email=email;
	this.timezone=timezone;
}



//updateUser
public UserDto(long id,String username, List<String> roles, boolean enabled, LocalDateTime createdAt,String email,String password,List<TaskDto>tasks,String imagePath,String timezone) {
	this(username,password,email);
 	this.roles = roles;
	this.enabled = enabled;
	this.createdAt = createdAt;
	this.tasks=tasks;
	this.id=""+id;
	this.imagePath=imagePath;
	this.timezone=timezone;
 }
public UserDto(String username, List<String> roles, boolean enabled, LocalDateTime createdAt,String email,List<TaskDto>tasks) {
	
}


public String getUsername() {
	return username;
}
public void setUsername(String username) {
	this.username = username;
}
public List<String> getRoles() {
	return roles;
}
public void setRoles(List<String> roles) {
	this.roles = roles;
}

public boolean isEnabled() {
	return enabled;
}
public void setEnabled(boolean enabled) {
	this.enabled = enabled;
}
public LocalDateTime getCreatedAt() {
	return createdAt;
}
public void setCreatedAt(LocalDateTime createdAt) {
	this.createdAt = createdAt;
}
public void setEmail(String email) {
	this.email=email;
}
public String getEmail() {
	return this.email;
}
public String getPassword() {
	return this.password;
}
public void setPassword(String password) {
	this.password=password;
}

public List<TaskDto> getTasks() {
	return tasks;
}
public void setTasks(List<TaskDto> tasks) {
	this.tasks = tasks;
}
 
public String getId() {
	return id;
}
public void setId(String id) {
	this.id = id;
}

public String getImagePath() {
	return imagePath;
}

public void setImagePath(String imagePath) {
	this.imagePath = imagePath;
}


public String getTimezone() {
	return timezone;
}

public void setTimezone(String timezone) {
	this.timezone = timezone;
}

@Override
public String toString() {
	return "UserDto [username=" + username + ", roles=" + roles + ", tasks=" + tasks + ", enabled=" + enabled
			+ ", createdAt=" + createdAt + ", email=" + email + ", password=" + password + ", id=" + id + ", imagePath="
			+ imagePath + ", timezone=" + timezone + "]";
}
 





}
