package it.marmas.task.manager.api.dto;

import java.time.Instant;

public class RequestChangeDeadlineDto {

	private Instant deadline;
	private String timeZone;
	
	public RequestChangeDeadlineDto() {
		
	}
	public RequestChangeDeadlineDto(Instant deadline, String timeZone) {
				this.deadline=deadline;
				this.timeZone= timeZone;
	}
	public Instant getDeadline() {
		return deadline;
	}
	public void setDeadline(Instant deadline) {
		this.deadline = deadline;
	}
	public String getTimeZone() {
		return timeZone;
	}
	public void setTimeZone(String timeZone) {
		this.timeZone = timeZone;
	}
	
}
