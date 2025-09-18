package it.marmas.task.manager.api.dto;

public class TokenResquest {
private String accessToken;
private String refreshToken;
	public TokenResquest() {
		
	}
	public String getAccessToken() {
		return accessToken;
	}
	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
	public String getRefreshToken() {
		return refreshToken;
	}
	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}
	
}
