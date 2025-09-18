package it.marmas.task.manager.api.repo;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import it.marmas.task.manager.api.dto.TaskDto;
import it.marmas.task.manager.api.model.User;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

@Repository
public class UserRepositoryImpl implements UserRepository {
	
	private static Logger logger = LoggerFactory.getLogger(UserRepositoryImpl.class);
	
	@PersistenceContext
	private EntityManager em;
 	@Override
	public Optional<User> getUserById(long id) {
		try {
			return  Optional.of(em.find(User.class, id));
		}catch (Exception e) {
			logger.warn(e.getMessage());
			return Optional.empty();
 		}
	}
 	@Override
	public Optional<List<User>> getAllUsers() {
 		try {
		String sql="SELECT u from User u";
		List<User>users= em.createQuery(sql,User.class).getResultList();
		return Optional.of(users);
 		}catch(NoResultException e) {
 			
		return Optional.empty();
	}
	}
 	@Override
	public User insertUser(User u) {
		  em.persist(u);
		  return u;
	}
 	@Override
	public User updateUser(User u) {
 		logger.info("updating user");
		return em.merge(u);
	}
	@Override
	public User deleteUser(User u) {
		em.remove(em.merge(u));
		return u;
	}
	@Override
	public Optional<User> findByUsername(String username) {
		TypedQuery<User>query= em.createQuery( "SELECT u FROM User u JOIN FETCH u.roles WHERE LOWER(u.username) = LOWER(:username)", User.class);
		query.setParameter("username", username.toLowerCase());
		try {
			
			return  Optional.of(query.getSingleResult());
			
		}catch(NoResultException e) {
			return Optional.empty();
		}
	}
 	public Optional<User> findByEmail(String email){
		TypedQuery<User>query= em.createQuery("SELECT u FROM User u JOIN FETCH u.roles WHERE LOWER(u.email) = LOWER(:email)", User.class);
		query.setParameter("email", email.toLowerCase());
		try {
		
		return  Optional.of(query.getSingleResult());
				
		
		}catch(NoResultException e) {
			return Optional.empty();
		}
 	}
	@Override
	public Optional<List<TaskDto>> getUserTasks(long userid) {
		try {
			String jpql="SELECT new it.marmas.task.manager.api.dto.TaskDto(" +
 		              "u.username, u.createdAt, t.title, t.description, t.status, " +
		              "t.updatedAt, t.deadline, u.timezone, t.id) " +
		              "FROM User u JOIN u.tasks t "+
		              "WHERE u.id = :id";
 		TypedQuery<TaskDto> query=  em.createQuery( 
	            jpql,TaskDto.class);
		query.setParameter("id", userid);
		  query.getResultList();
		return Optional.of( query.getResultList());
		}catch (Exception e) {
		logger.error(e.getMessage());
		return Optional.empty();
		}
 
	}
	 
 	
 }
