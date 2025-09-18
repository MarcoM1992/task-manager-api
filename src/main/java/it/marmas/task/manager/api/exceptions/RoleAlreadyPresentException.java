package it.marmas.task.manager.api.exceptions;

public class RoleAlreadyPresentException extends RuntimeException {

 
	private static final long serialVersionUID = 1L;
	public RoleAlreadyPresentException(String msg) {
		super(msg);
	}
	public RoleAlreadyPresentException() {
		super("Role already  in DB");
	}
}
