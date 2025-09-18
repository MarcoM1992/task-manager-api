package it.marmas.task.manager.api.service;

import java.util.ArrayList;
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
import it.marmas.task.manager.api.dto.RoleDto;
import it.marmas.task.manager.api.dto.TaskDto;
import it.marmas.task.manager.api.dto.UserDto;
import it.marmas.task.manager.api.dto.GenericResponse.ResponseError;
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
	private Mapper<Task,TaskDto> taskMapper;
	
	@Qualifier("roleMapper")
	@Autowired
	private Mapper<Role,RoleDto> roleMapper;
	
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
	private TaskService  taskService;


  


 
	
	@Transactional(readOnly=true)
		@Override
		public UserDto getUserById(long id,String username,List<String>roles)  {
			 User u=userRepository.getUserById(id).orElseThrow(()->
			 new ElementNotFoundException("No user corresponds to "+id+" id"));
			 UtilityUser.checkAuthorization(username, u.getUsername(), roles);

	  			 return userMapper.toDto(u);
	 		    
 		}
	@Transactional(readOnly=true)
		@Override
		public List<UserDto> getAllUsers(){
		
		  List<User>users= userRepository.getAllUsers().orElseThrow(()->
	    	 new UsernameNotFoundException("No User Found")  );
		    
 			List<UserDto>userDto=new ArrayList<UserDto>();
			for (int i = 0; i< users.size(); i++) {
				 
 				User current= users.get(i);
			  userDto.add( userMapper.toDto(current) );
			}
			 return userDto;
		}
 
	@Transactional
		@Override
		public UserDto updateUser(UserDto dto ,String currentUsername, List<String>roles) {
		  	logger.info("currentUsername : "+currentUsername);
		  	
		  	roles.forEach(x->logger.info(x));
		  	
				if(dto.getId()==null) {
					String errorMsg="There's no id associated with this request";
					logger.error(errorMsg);
					throw new ElementNotFoundException(errorMsg);
				}
			  
				User u= userRepository.getUserById(Long.parseLong(dto.getId())).orElseThrow(()->{
					String errorMsg="User with this ID not found : "+dto.getId();
					logger.error(errorMsg);
					throw new ElementNotFoundException(errorMsg);

			  });
			  	logger.info("username retrieved  : "+u.getUsername());

			  	UtilityUser.checkAuthorization(currentUsername, u.getUsername(), roles);
 			 
 			 
 			 
 				  String email=dto.getEmail();
 				  String username=dto.getUsername();
				  Boolean isEnable=dto.isEnabled();
				
				  
				 if(Utility.notNull(email)){
					 if(!email.equalsIgnoreCase(u.getEmail())) {
						var emailEsistenteOpt= userRepository.findByEmail(email);
						if(emailEsistenteOpt.isEmpty()) {
							 logger.info("email : " +email);
							 u.setEmail(email);
						}else {
							throw new UserAlredyPresentException("Email already existent , choose another one");
						}
					 }
					
				 }
				 

				 if(Utility.notNull(username)){
					 if(!username.equalsIgnoreCase(u.getUsername())) {
						var emailEsistenteOpt= userRepository.findByUsername(username);
						if(emailEsistenteOpt.isEmpty()) {
							 logger.info("username : " +username);
							 u.setEmail(username);
						}else {
							throw new UserAlredyPresentException("Username already existent , choose another one");
						}
					 }
					
				 }			
				 
				 if(Utility.notNull(isEnable)) {
					 logger.info("isEnable : " +isEnable);
					 u.setEnabled(isEnable);
				 }
			
			    List<TaskDto> tasksList= dto.getTasks();
 			   if(Utility.notNull(tasksList)&&!tasksList.isEmpty()) {
					 logger.info("taskList ids' from service");
					 List<Long>ids=tasksList.stream()
							 .map(TaskDto::getId)
							 .filter(x->UtilityUser.idIsValid(x))
							 .map(x-> Long.parseLong(x))
							 .peek(id->logger.info(id+""))
							 .toList();
					 List<TaskDto> task= taskService.findAllTaskById(ids);
					 task.forEach(x->logger.info(x.toString()));
					 
					 
					 Set<Task> tasks= task.stream().map(t1 -> {
						Task t= taskMapper.toEntity(t1);
						 t.setUser(u);
						return t;
					 }).collect(Collectors.toSet());
					  for( Task t : tasks) {
						  u.addTask(t);
					  }
				 }
			   /*
				 if(Utility.notNull(rolesList)&&!rolesList.isEmpty()) {
					List<Role>roles= roleService.getRolesById(rolesList);
					u.setRoles(roles.stream().peek(x-> logger.info(x.getName())).collect(Collectors.toSet()));
				 }
				 */
				 
	  			return  userMapper.toDto(userRepository.updateUser(u));
	  			     
			   	
		}
	
	@Transactional
		@Override
		public UserDto deleteUser(long id)  {
		
			 var opt= userRepository.getUserById(id);
			 if(opt.isPresent()) {	 
				User u= opt.get();
				  u.setTasks(null);
				  
				  u= userRepository.deleteUser(u);
			 logger.info("utente "+u.getUsername()+" deleted");
			 
			 return userMapper.toDto(u);
			 }
			 throw new ElementNotFoundException("couldn't delete user with id "+id);
			
			 
		}
	@Transactional(readOnly = true)
		@Override
		public UserDto findByUsername(String username){
		
		Optional<User> opt= userRepository.findByUsername(username);
 		if(opt.isPresent()) {
			return userMapper.toDto(opt.get());
		}
 		logger.info("utente : "+username+" not found ");
		return null;
	}
	
	@Transactional(readOnly = true)
		public UserDto findByEmail(String email) {
		Optional<User> opt = userRepository.findByEmail(email);
 		if(opt.isPresent()) {
			return  userMapper.toDto(opt.get());
		}
        return null;		
 	}
	@Override
	@Transactional()
	public UserDto insertUser(UserDto dto){
		logger.info("UserDto: "+dto);
 		boolean userAlreadyPresent =findByUsername(dto.getUsername())!=null;
		if (userAlreadyPresent) {
			throw new UserAlredyPresentException("Username already present");
 		}
		boolean emailAlreadyPresent= findByEmail(dto.getEmail())!=null;
		if (emailAlreadyPresent) {
			throw new UserAlredyPresentException("Email already present");
		}
		
 		User u = userMapper.toEntity(dto);
		logger.info("User trasformed in entity: "+u);

 		Role r=  roleMapper.toEntity(roleService.findByName("ROLE_USER"));
 		
 		u.setRoles(Set.of(r)); 
 		logger.info(" default role assigned");
 		
 		u.setPassword(passwordEncoder.encode(u.getPassword()));
		logger.info("password codified");
 		
		userRepository.insertUser(u);
		logger.info("user succesfully inserted");
		return dto;
  	}
	@Override
	@Transactional
	public String assignTask(AssignTaskDto assignTaskDto,String currentUser, List<String> currentAuthority) {
		 String userId=assignTaskDto.getUserId();
		 String taskId=assignTaskDto.getTaskId();
		 String errorMsg;
		 if(userId==null||taskId==null) {
			   errorMsg="Both ids cannot be null";
			 logger.error(errorMsg);
			 throw new IdException(errorMsg);
		 }
		 
		 if(!UtilityUser.idIsValid(taskId)) {
			 errorMsg="task id is not valid";
			 throw new IdException(errorMsg);
		 }
		 
		 if(!UtilityUser.idIsValid(userId)) {
			 errorMsg="user id is not valid";
			 throw new IdException(errorMsg);
		 }
		 
		 User user= userRepository.getUserById(Long.parseLong(userId)).orElseThrow(()->
		   new ElementNotFoundException("User with id "+userId+" not found"));
		 logger.info("user retrieved :"+user);
		 //check if is the same user:
		 UtilityUser.checkAuthorization(currentUser,user.getUsername(), currentAuthority);
		 
		 
 		 
		 Task task = taskMapper.toEntity(taskService.getTaskById(Long.parseLong(taskId)));
		 
		 logger.info("task retrieved :"+task);
         
 		 user.addTask(task);
 		
 		 userRepository.updateUser(user);
 		 String msg="task : "+task.getId()+", "+task.getTitle()+" assegnata a utente "+user.getUsername();
 		 logger.info(msg);
 		 
 		 return msg;
	}
	
	
 
	@Override
	@Transactional
	public UserDto removeTask(AssignTaskDto assignTaskDto, String name, List<String> currentAuthority) {
		String errorMsg="";
		if(!UtilityUser.idIsValid(assignTaskDto.getUserId()) ) {
			errorMsg="UserId given not valid";
			throw new IdException(errorMsg);
		}
		 long userId= Long.parseLong(assignTaskDto.getUserId());
		User user= userRepository.getUserById(userId).orElseThrow(()->new ElementNotFoundException("User with id "+assignTaskDto.getUserId()));
		logger.info("user retrieved : "+user.getUsername());
		
		UtilityUser.checkAuthorization(name, user.getUsername(),currentAuthority);
		
		if(!UtilityUser.idIsValid(assignTaskDto.getTaskId())) {
			errorMsg="taskId given not valid";
			throw new IdException(errorMsg);
		}
		long taskId= Long.parseLong(assignTaskDto.getTaskId());
		Task task= taskService.getEntityTaskById(taskId).orElseThrow(()->new ElementNotFoundException("Task with id "+taskId+ " not found"));
		
		logger.info("removing task from user");
		user.removeTask(task);
		
		logger.info("task removed");;
		logger.info("removing user from task"); 

 		task.setUser(null);
		logger.info("user removed"); 

		logger.info("calling update user"); 

		user =userRepository.updateUser(user);
		 
		logger.info("user retrieved "+user); 

		return userMapper.toDto(user);
	}
	@Override
	public Optional<User> getUserEntityByEmail(String email) {
		return userRepository.findByEmail(email);
	}
	@Override
	public UserDto updateUser(User user) {
		return userMapper.toDto(userRepository.updateUser(user));
 		 
	}
	
	   @Override
	   public GenericResponse<List<TaskDto>> getUserTasks(String currentUser,List<String>roles,long userId) {
		   GenericResponse<List<TaskDto>>response=new GenericResponse<>();

 
		   try {
		       // Recupero utente
		       UserDto user = getUserById(userId, currentUser, roles);
		       logger.info("User found: {}", user.getUsername());

		       // Recupero tasks
		       Optional<List<TaskDto>> tasksOpt = userRepository.getUserTasks(Long.parseLong(user.getId()));
		       List<TaskDto> tasks = tasksOpt.orElse(List.of());

		       if (!tasks.isEmpty()) {
		           logger.info("Tasks present: {}", tasks.stream()
		                   .map(TaskDto::getTitle)
		                   .collect(Collectors.joining(", ")));
		       } else {
		           logger.info("No tasks found for user {}", user.getUsername());
		       }

		       response.setContent(tasks);

		   } catch (Exception e) {
		       logger.error("Error retrieving user or tasks", e);

		       ResponseError error = new ResponseError();
		       error.setCode(500);
		       error.setMessage("Internal server error: " + e.getMessage());
		       response.setError(error);
		   }

		   // Restituisco sempre la risposta, sia in caso di successo che di errore
		   return response;

		   
	   }
	   
	
	 
	
  
 }
