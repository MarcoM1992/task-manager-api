package it.marmas.task.manager.api.dto;

import java.time.LocalDateTime;

public class UserValidationTokenDto {
	
	

	private String token;
	
	private String email;
	
    private LocalDateTime expiryDate;
    
    private boolean enabled;

	public UserValidationTokenDto(String token, String email, LocalDateTime expiryDate,boolean enabled) {
		super();
		this.token = token;
		this.email = email;
		this.expiryDate = expiryDate;
		this.enabled=enabled;
	}
     
    public UserValidationTokenDto() {
    	
    	
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

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	@Override
	public String toString() {
		return "UserValidationTokenDto [token=" + token + ", email=" + email + ", expiryDate=" + expiryDate
				+ ", enabled=" + enabled + "]";
	}
	

}
