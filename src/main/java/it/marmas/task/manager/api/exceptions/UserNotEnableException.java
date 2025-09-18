package it.marmas.task.manager.api.exceptions;

public class UserNotEnableException extends RuntimeException {
 
	private static final long serialVersionUID = 1L;

	public UserNotEnableException() {
		super("User not enabled ... contact the administrator");
	}
}
