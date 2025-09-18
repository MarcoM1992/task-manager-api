package it.marmas.task.manager.api.repo;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import it.marmas.task.manager.api.model.UserValidationToken;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

@Repository
public class UserValidationTokenRepositoryImpl implements UserValidationTokenRepository {

 	public static final Logger logger= LoggerFactory.getLogger(UserValidationTokenRepositoryImpl.class);
	@PersistenceContext
	private EntityManager em;

 
	@Override
	public Optional<UserValidationToken> insertToken(UserValidationToken ust) {
		try {
 			em.persist(ust);
			return Optional.of(ust);
 		}catch (Exception e) {
		return Optional.empty();
		}
		
	}
	@Override
	public Optional<List<UserValidationToken>> getAllTokenForUsername(String username) {
	  String query= "SELECT u from UservalidationToken u join u.user usr  where usr.username=:username";
	  try {
	 return Optional.of( em.createQuery(query,UserValidationToken.class).getResultList());
		}catch(NoResultException e) {
	        logger.error("Errore durante il recupero del token: {}", e.getMessage());
			
			return Optional.empty();

		}catch(Exception e) {
			return Optional.empty();
		}
	}
	@Override
	public Optional<UserValidationToken> findByToken(String token) {
	
		String query="SELECT u from UserValidationToken u where u.token=:token";
		TypedQuery<UserValidationToken>typedQuery=em.createQuery(query,UserValidationToken.class);
		typedQuery.setParameter("token", token);
		try {
		 	 return Optional.of( typedQuery.getSingleResult());

		}catch(NoResultException e) {
	        logger.error("Errore durante il recupero del token: {}", e.getMessage());
			
			return Optional.empty();

		}catch(Exception e) {
			return Optional.empty();
		}
	}
	@Override
	public Optional<UserValidationToken> updateToken(UserValidationToken token) {
			try {
			return Optional.of(	em.merge(token));
			}catch(NoResultException e) {
		        logger.error("Errore durante il recupero del token: {}", e.getMessage());
				
				return Optional.empty();

			}catch(Exception e) {
				return Optional.empty();
			}
	}
	  

}
