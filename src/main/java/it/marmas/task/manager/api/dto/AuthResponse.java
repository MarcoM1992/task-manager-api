package it.marmas.task.manager.api.dto;

public class AuthResponse {
	public AuthResponse(String accessToken,String refreshToken,long userId,AuthError error) {
		this.accessToken=accessToken;
		this.refreshToken=refreshToken;
	}
	public AuthResponse() {
		
	}
	
private AuthError error;
private long userId;
private String refreshToken;
private String accessToken;;

public void setUserId(long userId) {
    this.userId = userId;
}
public long getUserId() {
    return userId;
} 

public void setAuthError(AuthError error) {
	this.error=error;
}

public AuthError getAuthError() {
	return error;
}


 public String getRefreshToken() {
	return refreshToken;
}
public void setRefreshToken(String refreshToken) {
	this.refreshToken = refreshToken;
}
public String getAccessToken() {
	return accessToken;
}
public void setAccessToken(String accessToken) {
	this.accessToken = accessToken;
}

public static class AuthError {
	public AuthError(String errorMsg,int errorCode) {
		this.errorCode=errorCode;
		this.errorMsg=errorMsg;
	}
	private String errorMsg;
	private int errorCode;
	public String getErrorMsg() {
		return errorMsg;
	}
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
	public int getErrorCode() {
		return errorCode;
	}
	public void setErrorCode(int errorCode) {
		this.errorCode = errorCode;
	}
	
	
}

@Override
public String toString() {
	return "AuthResponse [error=" + error + ", userId=" + userId + ", refreshToken=" + refreshToken + ", accessToken="
			+ accessToken + "]";
}


}
