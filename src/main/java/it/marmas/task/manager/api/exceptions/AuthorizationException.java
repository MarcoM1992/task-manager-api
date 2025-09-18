package it.marmas.task.manager.api.exceptions;

public class AuthorizationException extends RuntimeException {
 
	private static final long serialVersionUID = 2598125065456979211L;

	public AuthorizationException(String msg) {
		super(msg);
	}

}
