package it.marmas.task.manager.api.dto;

public class RequestPasswordDto {
	String newPassword;
	String token;
	public RequestPasswordDto(String newPassword,String token) {
		this.newPassword=newPassword;
		this.token=token;
	}
	public void setToken(String token) {
		this.token=token;
		
	}
	public void setNewPassword(String newPassword) {
		this.newPassword=newPassword;
	}
	@Override
	public String toString() {
		return "RequestPasswordDto [newPassword=" + newPassword + ", token=" + token + "]";
	}
	public String getNewPassword() {return newPassword;}
	public String getToken() {return token;}

}
