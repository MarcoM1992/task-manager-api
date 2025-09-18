package it.marmas.task.manager.api.dto;

public class RefreshTokenDto {
private String refreshToken;
private String deviceInfo;

public RefreshTokenDto() {
	
}
public String getRefreshToken() {
	return refreshToken;
}

public void setRefreshToken(String refreshToken) {
	this.refreshToken = refreshToken;
}
public String getDeviceInfo() {
	return deviceInfo;
}
public void setDeviceInfo(String deviceInfo) {
	this.deviceInfo = deviceInfo;
}
@Override
public String toString() {
	return "RefreshTokenDto [refreshToken=" + refreshToken + ", deviceInfo=" + deviceInfo + "]";
}

 

}
