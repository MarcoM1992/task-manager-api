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
	        // Use EntityManager find to fetch User by primary key
	        return Optional.of(em.find(User.class, id));
	    } catch (Exception e) {
	        logger.warn("Error fetching user by ID {}: {}", id, e.getMessage());
	        return Optional.empty();
	    }
	}

	@Override
	public Optional<List<User>> getAllUsers() {
	    try {
	        // JPQL query to fetch all User entities
	        String sql = "SELECT u FROM User u";
	        List<User> users = em.createQuery(sql, User.class).getResultList();
	        return Optional.of(users);
	    } catch (NoResultException e) {
	        // Return empty if no users are found
	        return Optional.empty();
	    }
	}

	@Override
	public User insertUser(User u) {
	    // Persist the User entity
	    em.persist(u);
	    return u;
	}

	@Override
	public User updateUser(User u) {
	    logger.info("Updating user: {}", u.getUsername());
	    // Merge changes to the managed entity and return it
	    return em.merge(u);
	}

	@Override
	public User deleteUser(User u) {
	    // Merge the entity (if detached) and remove it from the database
	    em.remove(em.merge(u));
	    return u;
	}

	@Override
	public Optional<User> findByUsername(String username) {
	    // JPQL query to find a user by username (case-insensitive) and fetch roles eagerly
	    TypedQuery<User> query = em.createQuery(
	        "SELECT u FROM User u JOIN FETCH u.roles WHERE LOWER(u.username) = LOWER(:username)", User.class);
	    query.setParameter("username", username.toLowerCase());

	    try {
	        return Optional.of(query.getSingleResult());
	    } catch (NoResultException e) {
	        // Return empty if no user matches the username
	        return Optional.empty();
	    }
	}

	@Override
	public Optional<User> findByEmail(String email) {
	    // JPQL query to find a user by email (case-insensitive) and fetch roles eagerly
	    TypedQuery<User> query = em.createQuery(
	        "SELECT u FROM User u JOIN FETCH u.roles WHERE LOWER(u.email) = LOWER(:email)", User.class);
	    query.setParameter("email", email.toLowerCase());

	    try {
	        return Optional.of(query.getSingleResult());
	    } catch (NoResultException e) {
	        // Return empty if no user matches the email
	        return Optional.empty();
	    }
	}

 	@Override
 	public Optional<List<TaskDto>> getUserTasks(long userid) {
 	    try {
 	        // JPQL query: select tasks associated with a specific user
 	        // Using a constructor expression to directly map results to TaskDto
 	        String jpql = "SELECT new it.marmas.task.manager.api.dto.TaskDto(" +
 	                      "u.username, u.createdAt, t.title, t.description, t.status, " +
 	                      "t.updatedAt, t.deadline, u.timezone, t.id) " +
 	                      "FROM User u JOIN u.tasks t " +
 	                      "WHERE u.id = :id";

 	        // Create the TypedQuery using the JPQL
 	        TypedQuery<TaskDto> query = em.createQuery(jpql, TaskDto.class);

 	        // Set the user ID parameter
 	        query.setParameter("id", userid);

 	        // Execute the query and return results wrapped in Optional
 	        List<TaskDto> tasks = query.getResultList();
 	        return Optional.of(tasks);

 	    } catch (Exception e) {
 	        // Log any exception and return an empty Optional
 	        logger.error("Error fetching tasks for user ID {}: {}", userid, e.getMessage());
 	        return Optional.empty();
 	    }
 	}

 	
 }
