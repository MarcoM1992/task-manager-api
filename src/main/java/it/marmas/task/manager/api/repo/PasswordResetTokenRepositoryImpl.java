package it.marmas.task.manager.api.repo;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import it.marmas.task.manager.api.model.PasswordResetToken;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

@Repository
public class PasswordResetTokenRepositoryImpl implements PasswordResetTokenRepository{
	
	private static Logger logger = LoggerFactory.getLogger(PasswordResetTokenRepositoryImpl.class);
	
	@PersistenceContext
	private EntityManager em;
  
	
	public Optional<PasswordResetToken> findByToken(String token) {
	    try {
	        String jpql = "SELECT t FROM PasswordResetToken t WHERE t.token = :token";
	        TypedQuery<PasswordResetToken> query = em.createQuery(jpql, PasswordResetToken.class);
	        query.setParameter("token", token);
	        return Optional.of(query.getSingleResult());
	    } catch (NoResultException e) {
	        return Optional.empty();
	    } catch (Exception e) {
	        logger.error("Errore durante il recupero del token: {}", e.getMessage());
	        return Optional.empty();
	    }
	}



	@Override
	public Optional<PasswordResetToken> insertToken(PasswordResetToken passwordResetToken) {
			try {
		em.persist(passwordResetToken);
		return Optional.of(passwordResetToken);
			}catch(Exception e) {
				return Optional.empty();
			}
	}



	@Override
	public Optional<PasswordResetToken> updateToken(PasswordResetToken token) {
 
			try {
 			return	Optional.of(em.merge(token)); 
		}catch(Exception e) {
			logger.error("error in updating token status query");
			return Optional.empty();
		}
		
	}
	
	

}
