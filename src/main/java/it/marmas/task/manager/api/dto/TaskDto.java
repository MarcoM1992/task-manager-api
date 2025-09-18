package it.marmas.task.manager.api.dto;

import java.time.Instant;
import java.time.ZoneId;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;

import it.marmas.task.manager.api.model.Task.Status;

public class TaskDto {

    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String id;
    @JsonInclude(Include.NON_NULL)
    private String userUsername;
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private Instant createdAt;
    @JsonInclude(Include.NON_NULL)
    private String title;
    @JsonInclude(Include.NON_NULL)
    private String description;
    @JsonInclude(Include.NON_NULL)
    private String status;
    @JsonInclude(Include.NON_NULL)
    private Instant updatedAt;
    @JsonInclude(Include.NON_NULL)
    private Instant deadline;
    @JsonInclude(Include.NON_NULL)
    private ZoneId timeZone;
    @JsonInclude(Include.NON_NULL)
    private UserDto user;

    public TaskDto() {}
   
    public TaskDto(String title, String description, Status status, UserDto user) {
        this.title = title;
        this.description = description;
        this.status = status != null ? status.name() : null;
        this.user = user;
    }
    
    public TaskDto(String username, Instant createdAt, String title, String description, Status status,
            Instant updatedAt, Instant deadline, String timeZone,long id) {
    		this.id=id+"";
    	   this.userUsername = username;
           this.createdAt = createdAt;
           this.title = title;
           this.description = description;
           if(status!=null) {
               this.status = status.name();
           }
           this.updatedAt = updatedAt;
           this.deadline = deadline;
           try {
               this.timeZone = timeZone != null ? ZoneId.of(timeZone) : null;
           } catch (Exception e) {
               this.timeZone = null;
           }
    }
    public TaskDto(String username, Instant createdAt, String title, String description, String status,
                   Instant updatedAt, Instant deadline, String timeZone) {
        this.userUsername = username;
        this.createdAt = createdAt;
        this.title = title;
        this.description = description;
        this.status = status;
        this.updatedAt = updatedAt;
        this.deadline = deadline;
        try {
            this.timeZone = timeZone != null ? ZoneId.of(timeZone) : null;
        } catch (Exception e) {
            this.timeZone = null;
        }
    }
    public TaskDto(long id,String username, Instant createdAt, String title, String description, String status,
            Instant updatedAt, Instant deadline, String timeZone) {
 this.id=id+"";
 this.userUsername = username;
 this.createdAt = createdAt;
 this.title = title;
 this.description = description;
 this.status = status;
 this.updatedAt = updatedAt;
 this.deadline = deadline;
 try {
     this.timeZone = timeZone != null ? ZoneId.of(timeZone) : null;
 } catch (Exception e) {
     this.timeZone = null;
 }
}

    public TaskDto(Long id, String title, String description, Status status, Instant createdAt, Instant updatedAt,
                   String userUsername, String timeZone) {
        this.id = id != null ? "" + id : null;
        this.createdAt = createdAt;
        this.userUsername = userUsername;
        this.title = title;
        this.description = description;
        this.status = status != null ? status.name() : null;
        this.updatedAt = updatedAt;
        try {
            this.timeZone = timeZone != null ? ZoneId.of(timeZone) : null;
        } catch (Exception e) {
            this.timeZone = null;
        }
    }

    public TaskDto(String id, Instant createdAt, String title, String description, String status,
                   Instant updatedAt, Instant deadline, String timeZone, UserDto user) {
        this.id = id;
        this.createdAt = createdAt;
        this.title = title;
        this.description = description;
        this.status = status;
        this.updatedAt = updatedAt;
        this.deadline = deadline;
        try {
            this.timeZone = timeZone != null ? ZoneId.of(timeZone) : null;
        } catch (Exception e) {
            this.timeZone = null;
        }
        this.user = user;
    }

    public TaskDto(Long id, String title, String description, Status status, Instant createdAt, Instant updatedAt) {
        this.id = id != null ? "" + id : null;
        this.createdAt = createdAt;
        this.title = title;
        this.description = description;
        this.status = status != null ? status.name() : null;
        this.updatedAt = updatedAt;
    }

    public TaskDto(String title, String description, String status, Instant createdAt, Instant updatedAt,
                   String userUsername, Instant deadline, String timeZone) {
        this.createdAt = createdAt;
        this.userUsername = userUsername;
        this.title = title;
        this.description = description;
        this.status = status;
        this.updatedAt = updatedAt;
        this.deadline = deadline;
        try {
            this.timeZone = timeZone != null ? ZoneId.of(timeZone) : null;
        } catch (Exception e) {
            this.timeZone = null;
        }
    }

    public TaskDto(Instant createdAt, String title, String description, String status, Instant updatedAt) {
        this.createdAt = createdAt;
        this.title = title;
        this.description = description;
        this.status = status;
        this.updatedAt = updatedAt;
    }

    // --- Getters and Setters ---
    public UserDto getUser() { return user; }
    public void setUser(UserDto user) { this.user = user; }
    public void setUserUsername(String userUsername) { this.userUsername = userUsername; }
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getTitle() { return title; }
    public void setTitle(String title) { this.title = title; }
    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
    public Instant getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(Instant updatedAt) { this.updatedAt = updatedAt; }
    public ZoneId getTimeZone() { return timeZone; }
    public void setTimeZone(ZoneId timeZone) { this.timeZone = timeZone; }
    public String getUserUsername() { return userUsername; }
    public void setUser(String userUsername) { this.userUsername = userUsername; }
    public Instant getCreatedAt() { return createdAt; }
    public void setCreatedAt(Instant createdAt) { this.createdAt = createdAt; }
    public Instant getDeadline() { return deadline; }
    public void setDeadline(Instant deadline) { this.deadline = deadline; }

    @Override
    public String toString() {
        return "TaskDto [id=" + id + ", userUsername=" + userUsername + ", createdAt=" + createdAt + ", title=" + title
                + ", description=" + description + ", status=" + status + ", updatedAt=" + updatedAt + ", deadline="
                + deadline + ", timeZone=" + timeZone + ", user=" + user + "]";
    }
}
