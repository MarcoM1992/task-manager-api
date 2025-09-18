package it.marmas.task.manager.api.service;

import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.marmas.task.manager.api.dto.RequestChangeDeadlineDto;
import it.marmas.task.manager.api.dto.TaskDto;
import it.marmas.task.manager.api.exceptions.ElementNotFoundException;
import it.marmas.task.manager.api.exceptions.IdException;
import it.marmas.task.manager.api.exceptions.TaskException;
import it.marmas.task.manager.api.mapper.Mapper;
import it.marmas.task.manager.api.model.Task;
import it.marmas.task.manager.api.repo.TaskRepository;
import it.marmas.task.manager.api.util.Utility;
import it.marmas.task.manager.api.util.UtilityUser;

@Service
public class TaskServiceImpl implements TaskService {

    private static final Logger logger = LoggerFactory.getLogger(TaskServiceImpl.class);

    @Autowired
    @Qualifier("taskMapper")
    private Mapper<Task, TaskDto> taskMapper; // Mapper between entity and DTO

    @Autowired
    private TaskRepository taskRepository; // Repository to interact with Task DB

    @Value("${time.victoria}")
    private String defaultTimezone; // Default timezone for task deadlines

    /**
     * Retrieve a single task by its ID
     */
    @Transactional(readOnly = true)
    @Override
    public TaskDto getTaskById(long id) {
        Optional<Task> task = taskRepository.getTaskById(id);
        if (task.isPresent()) {
            return taskMapper.toDto(task.get());
        }
        throw new ElementNotFoundException("No Task found with this ID " + id);
    }

    /**
     * Retrieve all tasks in the database
     */
    @Transactional(readOnly = true)
    @Override
    public List<TaskDto> getAllTask() {
        Optional<List<Task>> opt = taskRepository.getAllTask();
        if (opt.isPresent()) {
            return opt.get().stream()
                      .map(taskMapper::toDto)
                      .collect(Collectors.toList());
        }
        String msgError = "No task found";
        logger.info(msgError);
        throw new ElementNotFoundException(msgError);
    }

    /**
     * Insert a new task into the database
     */
    @Transactional
    @Override
    public TaskDto insertTask(TaskDto t) {
        Task entity = taskMapper.toEntity(t);
        if (t.getDeadline() != null) {
            entity.setDeadline(t.getDeadline());
        }
        logger.info("Initial DTO: " + t);

        Optional<Task> opt = taskRepository.insertTask(entity);
        if (opt.isPresent()) {
            TaskDto dto = taskMapper.toDto(opt.get());
            dto.setDeadline(opt.get().getDeadline());
            dto.setTimeZone(t.getTimeZone());
            logger.info("Task inserted with deadline: " + Utility.formatLdl(dto.getDeadline()));
            return dto;
        }
        throw new TaskException("Task has not been created");
    }

    /**
     * Update an existing task
     */
    @Transactional
    @Override
    public TaskDto updateTask(TaskDto t) {
        if (t == null) {
            String msgError = "Task not updated. DTO is null";
            logger.warn(msgError);
            throw new TaskException(msgError);
        }

        logger.info("Searching existing task with ID: " + t.getId());
        Optional<Task> opt = taskRepository.getTaskById(Long.parseLong(t.getId()));

        if (opt.isPresent()) {
            Task task = opt.get();

            // Update fields if they are not null
            if (Utility.notNull(t.getDescription())) {
                task.setDescription(t.getDescription());
            }
            if (Utility.notNull(t.getTitle())) {
                task.setTitle(t.getTitle());
            }
            if (Utility.notNull(t.getStatus())) {
                task.setStatus(Task.Status.valueOf(t.getStatus()));
            }
            task.setUpdatedAt(Instant.now());

            opt = taskRepository.updateTask(task);
            if (opt.isPresent()) {
                return taskMapper.toDto(opt.get());
            } else {
                String msgError = "Task not updated";
                logger.info(msgError);
                throw new ElementNotFoundException(msgError);
            }
        } else {
            String msgError = "No task found for this ID: " + t.getId();
            logger.info(msgError);
            throw new ElementNotFoundException(msgError);
        }
    }

    /**
     * Delete a task by ID with authorization check
     */
    @Transactional
    @Override
    public TaskDto deleteTask(String id, String username, List<String> roles) {
        try {
            logger.info("Username: " + username + " Task ID: " + id);
            roles.forEach(logger::info);

            Task task = taskRepository.getTaskById(Long.parseLong(id))
                                      .orElseThrow(() -> new ElementNotFoundException("Task with ID " + id + " not found"));

            // Check if the user is authorized to delete the task
            UtilityUser.checkAuthorization(id, username, roles);

            Task deletedTask = taskRepository.deleteTask(task)
                                             .orElseThrow(() -> new ElementNotFoundException("Task not found after deletion"));

            return taskMapper.toDto(deletedTask);

        } catch (NumberFormatException e) {
            String errorMsg = "Invalid ID";
            logger.error(errorMsg);
            throw new TaskException(errorMsg);
        }
    }

    /**
     * Find multiple tasks by a list of IDs
     */
    @Transactional(readOnly = true)
    @Override
    public List<TaskDto> findAllTaskById(List<Long> ids) {
        Optional<List<Task>> opt = taskRepository.findAllTaskById(ids);
        return opt.map(tasks -> tasks.stream().map(taskMapper::toDto).toList())
                  .orElse(null);
    }

    /**
     * Find tasks assigned to a specific username
     */
    @Override
    public List<TaskDto> findByUsername(String username) {
        Optional<List<TaskDto>> opt = taskRepository.findByUsername(username);
        return opt.orElseThrow(() -> new ElementNotFoundException("No Task associated with this username"));
    }

    /**
     * Get the Task entity directly by ID
     */
    public Optional<Task> getEntityTaskById(long id) {
        return taskRepository.getTaskById(id);
    }

    /**
     * Retrieve tasks expiring between "now" and "tomorrow"
     */
    @Override
    public Optional<List<TaskDto>> findBetweenDates() {
        ZoneId zone = ZoneId.of(defaultTimezone);
        Instant now = Instant.now().atZone(zone).toInstant();
        Instant tomorrow = now.plus(1, ChronoUnit.DAYS).atZone(zone).toInstant();

        logger.info("Scanning for expiring tasks using timezone: " + defaultTimezone);

        List<TaskDto> res = taskRepository.findBetweenDates(now, tomorrow)
                                          .orElseGet(() -> {
                                              String msgError = "No tasks found between " +
                                                                Utility.formatLdl(now) + " and " +
                                                                Utility.formatLdl(tomorrow);
                                              logger.warn(msgError);
                                              return List.of();
                                          });

        return Optional.of(res);
    }

    /**
     * Update the deadline of a task
     */
    @Override
    @Transactional
    public Optional<TaskDto> updateDeadline(String id, RequestChangeDeadlineDto request) {
        if (!UtilityUser.idIsValid(id)) {
            throw new IdException("ID not valid: " + id);
        }

        Task task = taskRepository.getTaskById(Long.parseLong(id))
                                  .orElseThrow(() -> new ElementNotFoundException("Task not found by ID: " + id));

        logger.info("Task found: " + task.getTitle());
        logger.info("Previous deadline: " + request.getDeadline());

        task.setDeadline(request.getDeadline());
        task = taskRepository.updateTask(task)
                             .orElseThrow(() -> new TaskException("Exception occurred while updating task"));

        logger.info("Task deadline updated: " + task.getDeadline());

        TaskDto dto = taskMapper.toDto(task);
        dto.setDeadline(task.getDeadline());
        dto.setTimeZone(ZoneId.of(request.getTimeZone()));

        return Optional.of(dto);
    }
}
