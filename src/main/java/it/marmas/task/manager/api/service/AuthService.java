package it.marmas.task.manager.api.service;

import it.marmas.task.manager.api.dto.AuthRequest;
import it.marmas.task.manager.api.dto.AuthResponse;
import it.marmas.task.manager.api.dto.GenericResponse;
import it.marmas.task.manager.api.dto.RefreshTokenDto;

public interface AuthService {
    public AuthResponse refresh(RefreshTokenDto refreshTokenDto);

	public  GenericResponse<String> logout(String refreshToken);

	public AuthResponse login(AuthRequest request);
	
	public GenericResponse<String>logoutAll(RefreshTokenDto refreshToken);
}
