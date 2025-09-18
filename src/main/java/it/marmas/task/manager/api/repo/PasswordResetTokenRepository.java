package it.marmas.task.manager.api.repo;

import java.util.Optional;

import it.marmas.task.manager.api.model.PasswordResetToken;

public interface PasswordResetTokenRepository {

	Optional<PasswordResetToken> findByToken(String token);
	
	Optional<PasswordResetToken>insertToken(PasswordResetToken passwordResetToken);

	Optional<PasswordResetToken> updateToken(PasswordResetToken token);
	
}
