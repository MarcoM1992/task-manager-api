package it.marmas.task.manager.api.dto;

public class AssignTaskDto {
private String userId;
private String taskId;
	public AssignTaskDto(String userId,String taskId) {
		this.userId=userId;
		this.taskId=taskId;
	}
	public String getUserId() {
		return userId;
	}
	public void setUserId(String userId) {
		this.userId = userId;
	}
	public String getTaskId() {
		return taskId;
	}
	public void setTaskId(String taskId) {
		this.taskId = taskId;
	}
	@Override
	public String toString() {
		return "AssignTaskDto [userId=" + userId + ", taskId=" + taskId + "]";
	}
	
	
}
