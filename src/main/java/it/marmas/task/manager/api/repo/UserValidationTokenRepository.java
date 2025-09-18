package it.marmas.task.manager.api.repo;

import java.util.List;
import java.util.Optional;

import it.marmas.task.manager.api.model.UserValidationToken;

public interface UserValidationTokenRepository {
	public Optional<UserValidationToken> insertToken(UserValidationToken ust);

	public Optional<List<UserValidationToken>> getAllTokenForUsername(String username);

	public Optional<UserValidationToken> findByToken(String token);

	public Optional<UserValidationToken> updateToken(UserValidationToken token);
	
}
