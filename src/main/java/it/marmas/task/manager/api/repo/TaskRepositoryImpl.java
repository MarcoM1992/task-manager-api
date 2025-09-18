package it.marmas.task.manager.api.repo;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import it.marmas.task.manager.api.dto.TaskDto;
import it.marmas.task.manager.api.model.Task;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
@Repository
public class TaskRepositoryImpl implements TaskRepository {

    private static final Logger logger = LoggerFactory.getLogger(TaskRepositoryImpl.class);

    @PersistenceContext
    private EntityManager em;

    @Override
    public Optional<Task> getTaskById(long id) {
        try {
            // Find a Task by primary key using EntityManager
            return Optional.of(em.find(Task.class, id));
        } catch (Exception e) {
            logger.error("Error fetching task by ID {}: {}", id, e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public Optional<List<Task>> getAllTask() {
        try {
            // JPQL query to retrieve all Task entities
            String sql = "SELECT t FROM Task t";
            List<Task> tasks = em.createQuery(sql, Task.class).getResultList();
            return Optional.of(tasks);
        } catch (Exception e) {
            logger.info("No tasks found in repository");
            return Optional.empty();
        }
    }

    @Override
    public Optional<Task> insertTask(Task t) {
        try {
            // Persist a new Task entity
            em.persist(t);
            return Optional.of(t);
        } catch (Exception e) {
            logger.error("Error inserting task: {}", e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public Optional<Task> updateTask(Task t) {
        try {
            // Merge changes to a Task entity
            return Optional.of(em.merge(t));
        } catch (Exception e) {
            logger.error("Error updating task: {}", e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public Optional<Task> deleteTask(Task t) {
        try {
            // Merge (if detached) and remove Task entity
            em.remove(em.merge(t));
            return Optional.of(t);
        } catch (Exception e) {
            logger.error("Error deleting task: {}", e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public Optional<List<Task>> findAllTaskById(List<Long> ids) {
        // JPQL query to select all Tasks whose ID is in a list
        TypedQuery<Task> sql = em.createQuery("SELECT t FROM Task t WHERE t.id IN :ids", Task.class);
        sql.setParameter("ids", ids);

        try {
            return Optional.of(sql.getResultList());
        } catch (NoResultException e) {
            return Optional.empty();
        }
    }

    @Override
    public Optional<List<TaskDto>> findByUsername(String username) {
        try {
            // JPQL query to fetch tasks by username, mapping them to TaskDto
            String sql = "SELECT new it.marmas.task.manager.api.dto.TaskDto(" +
                         "t.id, t.title, t.description, t.status, t.createdAt, t.updatedAt) " +
                         "FROM Task t INNER JOIN t.user u WHERE u.username = :username";
            TypedQuery<TaskDto> query = em.createQuery(sql, TaskDto.class);
            query.setParameter("username", username);

            return Optional.of(query.getResultList());
        } catch (Exception e) {
            logger.error("Error fetching tasks for username {}: {}", username, e.getMessage());
            return Optional.empty();
        }
    }

    @Override
    public Optional<List<TaskDto>> findBetweenDates(Instant start, Instant end) {
        try {
            // JPQL query to fetch tasks whose deadline is between two instants
            // Includes user info in the TaskDto mapping
            TypedQuery<TaskDto> query = em.createQuery(
                    "SELECT new it.marmas.task.manager.api.dto.TaskDto(" +
                    "t.title, t.description, t.status, " +
                    "new it.marmas.task.manager.api.dto.UserDto(u.id, u.email, u.username, u.timezone)) " +
                    "FROM Task t JOIN t.user u " +
                    "WHERE t.deadline BETWEEN :start AND :end", TaskDto.class);

            query.setParameter("start", start);
            query.setParameter("end", end);

            return Optional.of(query.getResultList());
        } catch (NoResultException e) {
            return Optional.empty();
        } catch (Exception e) {
            logger.error("Error executing findBetweenDates query: {}", e.getMessage());
            return Optional.empty();
        }
    }
}
