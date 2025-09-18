package it.marmas.task.manager.api.dto;

public class GenericResponse  <T>{
	public GenericResponse() {}
	public GenericResponse(T t) {
		this.t=t;
	}
	
private T t;
private	ResponseError error;

	
	public T getContent() {
	return t;
}


public void setContent(T t) {
	this.t = t;
}


public ResponseError getError() {
	return error;
}



public void setError(ResponseError error) {
	this.error = error;
}




	public static class ResponseError{
		public ResponseError(String message,int code) {
			this.message=message;
			this.code=code;
		}
		public ResponseError() {
			
		}
		private String message;
		private int code;
		public String getMessage() {
			return message;
		}
		public void setMessage(String msg) {
			this.message = msg;
		}
		public int getCode() {
			return code;
		}
		public void setCode(int code) {
			this.code = code;
		}
		
		
	}
	
}
