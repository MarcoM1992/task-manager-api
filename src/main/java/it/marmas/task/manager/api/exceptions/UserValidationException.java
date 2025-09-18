package it.marmas.task.manager.api.exceptions;

public class UserValidationException extends RuntimeException {

	
	private static final long serialVersionUID = -8203214040439332222L;

	public UserValidationException(String msg) {
		super(msg);
	}
}
