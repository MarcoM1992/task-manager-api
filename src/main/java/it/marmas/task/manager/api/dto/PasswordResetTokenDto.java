package it.marmas.task.manager.api.dto;

import java.time.LocalDateTime;

public class PasswordResetTokenDto {
	
	private String token;
	
	private String email;
	
    private LocalDateTime expiryDate;
     
    private boolean enabled;
     
	public PasswordResetTokenDto() {
		
	}
	
	public PasswordResetTokenDto(String token,String email,LocalDateTime exDateTime,boolean enabled) {
		this.token=token;
		this.email=email;
		this.expiryDate=exDateTime;
		this.enabled=enabled;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public LocalDateTime getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(LocalDateTime expiryDate) {
		this.expiryDate = expiryDate;
	}
	public void setEnabled(boolean enabled) {
		this.enabled=enabled;
	}
	
	public boolean isEnabled() {
		return enabled;
	}
	@Override
	public String toString() {
		return "PasswordResetTokenDto [token=" + token + ", email=" + email + ", expiryDate=" + expiryDate + "enabled="+enabled+ "]";
	}
	
	
 	
}
