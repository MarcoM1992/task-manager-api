package it.marmas.task.manager.api.repo;

import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import jakarta.persistence.Query;

import it.marmas.task.manager.api.model.RefreshToken;
import it.marmas.task.manager.api.util.Utility;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

@Repository
public class AuthRepositoryImpl implements AuthRepository{

 
 	private static Logger logger =LoggerFactory.getLogger(AuthRepositoryImpl.class);
	@PersistenceContext
	private EntityManager em;

	
	@Override
	public Optional<RefreshToken> findByToken(String refreshTokenStr) {
		try {
	    TypedQuery<RefreshToken> query = em.createQuery(
	            "SELECT rt FROM RefreshToken rt WHERE rt.token = :token", RefreshToken.class);
	    query.setParameter("token", refreshTokenStr.trim());

	    RefreshToken token = query.getSingleResult();
		return Optional.of(token);

		}catch (Exception e) {
		logger.warn("no result found for this token : "+Utility.seeTokenEnding(refreshTokenStr));
		return Optional.empty();
		}
	}
	@Override
	public void remove(RefreshToken oldToken) {
		em.remove(em.merge(oldToken));
		
	}
	@Override
	public Optional<RefreshToken> save(RefreshToken newToken) {
		try {
 			em.persist(newToken);
	 return Optional.of(newToken);
	}catch (Exception e) {
 			logger.error("Error saving refreshToken: "+newToken);
		
		return Optional.empty();
 	}
	}
 
	@Override
	public Optional<Integer> logoutAll(String username) {
		int updatedRows=0;
		try {
			logger.info("prima della query");
		Query query=
				em.createQuery("DELETE FROM RefreshToken rt WHERE rt.user.username=:username");
			query.setParameter("username", username);
			  updatedRows=query.executeUpdate();
			  logger.info("dopo la query");
					
		}catch (Exception e) {
			logger.error(e.getMessage());
			logger.error("Error in global logging out");
			return Optional.empty();
		}
		return Optional.of(updatedRows);
	 
 	}

}
