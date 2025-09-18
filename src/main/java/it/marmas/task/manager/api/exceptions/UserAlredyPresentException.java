package it.marmas.task.manager.api.exceptions;

public class UserAlredyPresentException extends RuntimeException {

	private static final long serialVersionUID = 1L;
	public UserAlredyPresentException(String msg) {
		super(msg);
	}
	public UserAlredyPresentException() {
		super("username già presente");
		
	}
	
}
