package it.marmas.task.manager.api.exceptions;

public class TaskAlreadyPresentException extends RuntimeException{

	private static final long serialVersionUID = 1L;
	
	public TaskAlreadyPresentException() {
	  super("Task Already Present");
	}

}
