package it.marmas.task.manager.api.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.marmas.task.manager.api.dto.AssignTaskDto;
import it.marmas.task.manager.api.dto.GenericResponse;
import it.marmas.task.manager.api.dto.GenericResponse.ResponseError;
import it.marmas.task.manager.api.dto.RoleDto;
import it.marmas.task.manager.api.dto.TaskDto;
import it.marmas.task.manager.api.dto.UserDto;
import it.marmas.task.manager.api.exceptions.ElementNotFoundException;
import it.marmas.task.manager.api.exceptions.IdException;
import it.marmas.task.manager.api.exceptions.UserAlredyPresentException;
import it.marmas.task.manager.api.mapper.Mapper;
import it.marmas.task.manager.api.model.Role;
import it.marmas.task.manager.api.model.Task;
import it.marmas.task.manager.api.model.User;
import it.marmas.task.manager.api.repo.UserRepository;
import it.marmas.task.manager.api.util.Utility;
import it.marmas.task.manager.api.util.UtilityUser;
@Service
public class UserServiceImpl implements UserService {

    private static final Logger logger = LoggerFactory.getLogger(UserServiceImpl.class);

    @Qualifier("taskMapper")
    @Autowired
    private Mapper<Task, TaskDto> taskMapper;

    @Qualifier("roleMapper")
    @Autowired
    private Mapper<Role, RoleDto> roleMapper;

    @Qualifier("userMapper")
    @Autowired
    private Mapper<User, UserDto> userMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Autowired
    private RoleService roleService;

    @Autowired
    private TaskService taskService;

    // ===================== USER RETRIEVAL =====================
    @Transactional(readOnly = true)
    @Override
    public UserDto getUserById(long id, String username, List<String> roles) {
        User u = userRepository.getUserById(id)
                .orElseThrow(() -> new ElementNotFoundException("No user corresponds to ID: " + id));

        // Authorization check: only admin or the user itself can view
        UtilityUser.checkAuthorization(username, u.getUsername(), roles);

        return userMapper.toDto(u);
    }

    @Transactional(readOnly = true)
    @Override
    public List<UserDto> getAllUsers() {
        List<User> users = userRepository.getAllUsers()
                .orElseThrow(() -> new UsernameNotFoundException("No users found"));

        return users.stream().map(userMapper::toDto).toList();
    }

    // ===================== USER CREATION =====================
    @Override
    @Transactional
    public UserDto insertUser(UserDto dto) {
        logger.info("Inserting user: {}", dto);

        if (findByUsername(dto.getUsername()) != null) {
            throw new UserAlredyPresentException("Username already present");
        }
        if (findByEmail(dto.getEmail()) != null) {
            throw new UserAlredyPresentException("Email already present");
        }

        User u = userMapper.toEntity(dto);
        Role r = roleMapper.toEntity(roleService.findByName("ROLE_USER")); // default role
        u.setRoles(Set.of(r));

        u.setPassword(passwordEncoder.encode(u.getPassword()));
        userRepository.insertUser(u);

        logger.info("User successfully inserted: {}", u.getUsername());
        return dto; // returning original DTO (could return updated entity DTO as well)
    }

    // ===================== USER UPDATE =====================
    @Transactional
    @Override
    public UserDto updateUser(UserDto dto, String currentUsername, List<String> roles) {
        if (dto.getId() == null) {
            throw new ElementNotFoundException("No ID associated with this request");
        }

        User u = userRepository.getUserById(Long.parseLong(dto.getId()))
                .orElseThrow(() -> new ElementNotFoundException("User with ID not found: " + dto.getId()));

        // Authorization: only admin or the user itself can update
        UtilityUser.checkAuthorization(currentUsername, u.getUsername(), roles);

        // Update email if not taken
        if (Utility.notNull(dto.getEmail()) && !dto.getEmail().equalsIgnoreCase(u.getEmail())) {
            userRepository.findByEmail(dto.getEmail())
                    .ifPresent(existing -> { throw new UserAlredyPresentException("Email already exists"); });
            u.setEmail(dto.getEmail());
        }

        // Update username if not taken
        if (Utility.notNull(dto.getUsername()) && !dto.getUsername().equalsIgnoreCase(u.getUsername())) {
            userRepository.findByUsername(dto.getUsername())
                    .ifPresent(existing -> { throw new UserAlredyPresentException("Username already exists"); });
            u.setUsername(dto.getUsername()); // FIX: should set username, not email
        }

        // Enable/disable user
        if (Utility.notNull(dto.isEnabled())) {
            u.setEnabled(dto.isEnabled());
        }

        // Update tasks
        if (Utility.notNull(dto.getTasks()) && !dto.getTasks().isEmpty()) {
            List<Long> ids = dto.getTasks().stream()
                    .map(TaskDto::getId)
                    .filter(UtilityUser::idIsValid)
                    .map(Long::parseLong)
                    .toList();

            List<TaskDto> tasks = taskService.findAllTaskById(ids);
            Set<Task> taskEntities = tasks.stream()
                    .map(t -> { Task task = taskMapper.toEntity(t); task.setUser(u); return task; })
                    .collect(Collectors.toSet());
            taskEntities.forEach(u::addTask);
        }

        return userMapper.toDto(userRepository.updateUser(u));
    }

    // ===================== USER DELETION =====================
    @Transactional
    @Override
    public UserDto deleteUser(long id) {
        User u = userRepository.getUserById(id)
                .orElseThrow(() -> new ElementNotFoundException("Couldn't delete user with ID: " + id));

        // Remove task associations
        u.setTasks(null);
        u = userRepository.deleteUser(u);

        logger.info("User {} deleted", u.getUsername());
        return userMapper.toDto(u);
    }

    // ===================== TASK MANAGEMENT =====================
    @Override
    @Transactional
    public String assignTask(AssignTaskDto assignTaskDto, String currentUser, List<String> currentAuthority) {
        long userId = Long.parseLong(assignTaskDto.getUserId());
        long taskId = Long.parseLong(assignTaskDto.getTaskId());

        if (!UtilityUser.idIsValid(assignTaskDto.getUserId()) || !UtilityUser.idIsValid(assignTaskDto.getTaskId())) {
            throw new IdException("User ID or Task ID is invalid");
        }

        User user = userRepository.getUserById(userId)
                .orElseThrow(() -> new ElementNotFoundException("User with ID " + userId + " not found"));

        // Authorization: only admin or the user itself
        UtilityUser.checkAuthorization(currentUser, user.getUsername(), currentAuthority);

        Task task = taskMapper.toEntity(taskService.getTaskById(taskId));
        user.addTask(task);
        userRepository.updateUser(user);

        String msg = "Task " + task.getId() + " assigned to user " + user.getUsername();
        logger.info(msg);
        return msg;
    }

    @Override
    @Transactional
    public UserDto removeTask(AssignTaskDto assignTaskDto, String currentUser, List<String> currentAuthority) {
        long userId = Long.parseLong(assignTaskDto.getUserId());
        long taskId = Long.parseLong(assignTaskDto.getTaskId());

        User user = userRepository.getUserById(userId)
                .orElseThrow(() -> new ElementNotFoundException("User with ID " + userId + " not found"));

        UtilityUser.checkAuthorization(currentUser, user.getUsername(), currentAuthority);

        Task task = taskService.getEntityTaskById(taskId)
                .orElseThrow(() -> new ElementNotFoundException("Task with ID " + taskId + " not found"));

        user.removeTask(task);
        task.setUser(null); // remove back-reference
        user = userRepository.updateUser(user);

        return userMapper.toDto(user);
    }

    // ===================== OTHER UTILS =====================
    @Override
    public Optional<User> getUserEntityByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public UserDto updateUser(User user) {
        return userMapper.toDto(userRepository.updateUser(user));
    }

    @Override
    public GenericResponse<List<TaskDto>> getUserTasks(String currentUser, List<String> roles, long userId) {
        GenericResponse<List<TaskDto>> response = new GenericResponse<>();

        try {
            UserDto user = getUserById(userId, currentUser, roles);
            Optional<List<TaskDto>> tasksOpt = userRepository.getUserTasks(Long.parseLong(user.getId()));
            List<TaskDto> tasks = tasksOpt.orElse(List.of());

            response.setContent(tasks);

        } catch (Exception e) {
            logger.error("Error retrieving user or tasks", e);
            ResponseError error = new ResponseError();
            error.setCode(500);
            error.setMessage("Internal server error: " + e.getMessage());
            response.setError(error);
        }

        return response;
    }

    @Transactional(readOnly = true)
    @Override
    public UserDto findByUsername(String username) {
        return userRepository.findByUsername(username)
                .map(userMapper::toDto)
                .orElse(null);
    }

    @Transactional(readOnly = true)
    @Override
    public UserDto findByEmail(String email) {
        return userRepository.findByEmail(email)
                .map(userMapper::toDto)
                .orElse(null);
    }
}
