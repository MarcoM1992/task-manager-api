package it.marmas.task.manager.api.repo;

import java.util.Optional;

import it.marmas.task.manager.api.model.RefreshToken;

public interface AuthRepository {
	public Optional<RefreshToken> findByToken(String token);

	public void remove(RefreshToken oldToken);

	public Optional<RefreshToken> save(RefreshToken newToken);

 
	public Optional<Integer> logoutAll(String string);

 	

}
