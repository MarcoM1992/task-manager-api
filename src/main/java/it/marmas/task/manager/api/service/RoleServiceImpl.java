package it.marmas.task.manager.api.service;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import it.marmas.task.manager.api.dto.RoleDto;
import it.marmas.task.manager.api.exceptions.ElementNotFoundException;
import it.marmas.task.manager.api.exceptions.IdException;
import it.marmas.task.manager.api.exceptions.RoleAlreadyPresentException;
import it.marmas.task.manager.api.exceptions.RoleException;
import it.marmas.task.manager.api.mapper.Mapper;
import it.marmas.task.manager.api.model.Role;
import it.marmas.task.manager.api.repo.RoleRepository;
import it.marmas.task.manager.api.util.UtilityUser;

@Service
public class RoleServiceImpl implements RoleService{

 

 
 	@Autowired
 	private RoleRepository roleRepository;
 	
 	@Autowired
 	@Qualifier("roleMapper")
 	private Mapper<Role,RoleDto> roleMapper;

 	private Logger logger= LoggerFactory.getLogger(getClass());


	@Transactional(readOnly=true)
	@Override
	public RoleDto getRoleById(String idString) {
		if(!UtilityUser.idIsValid(idString)) {
			throw new IdException("id role is null");
		}
		long id = Long.parseLong(idString);
 		return roleMapper.toDto(roleRepository.getRoleById(id).orElseThrow(()->new IdException("couldn't find role with this id :"+id)));
	}
	@Transactional(readOnly=true)
	@Override
	public List<String> getAllRoles() {
		return roleRepository.getAllRoles().stream().map(x->x.getName()).collect(Collectors.toList());
	}
	@Transactional
	@Override
	public RoleDto updateRole(RoleDto r)  {
		if(r.getName()==null||r.getId()==null){
			throw new RoleException("role name is null");
		}
		if(!UtilityUser.idIsValid(r.getId())) {
			throw new IdException("ID must be a number");
		}
		long idNumerical= Long.parseLong(r.getId());
		
		Role role= roleRepository.getRoleById(idNumerical).orElseThrow(()->
		new ElementNotFoundException("Role not found for name "+ r.getName())
 		);
		 
		role= roleRepository.updateRole(role).orElseThrow(()->{
			String errorMsg="Update didn't succeed ";
			logger.error(errorMsg);
	 		 return  new RoleException(errorMsg);
 		});
		return roleMapper.toDto(role);
 	}
	@Transactional
	@Override
	public RoleDto insertRole(String r)  {
		if(r==null) {
			throw new RuntimeException("ID cannot be null");
		}
		Optional<Role>opt= roleRepository.findByName(r);
		if(opt.isPresent()) {
			throw new RoleAlreadyPresentException();
		}
		Role role = new Role();
		String name=handlePrefix(r);
		role.setName(name);
		
 		role= roleRepository.insertRole(role);
 		logger.info("role inserted "+ role);
 		return roleMapper.toDto(role);
	}
	private String handlePrefix(String r) {
		String rolePrefix="ROLE_";
		boolean containRolePrefix=r.contains(rolePrefix);
		String log = containRolePrefix?"The name already contain the right prefix":"The name didn't contain the right prefix and it has been added!";
		logger.info(log); 
		return containRolePrefix?r:rolePrefix+r;
	}
	@Transactional
	@Override
	public RoleDto deleteRole(long id) {
		
		
	 Role r= roleRepository.getRoleById(id).orElseThrow(()->new RoleException("hasn't  possible to find role associated with id :"+id));
		if(r.getUsers()!=null) {
			r.setUsers(null);;
		}
		return roleMapper.toDto( roleRepository.deleteRole(r)
				.orElseThrow(
						()->new RoleException("Role with this id "+id  +" hasn't been deleted ")
						)
				);
		
	}
	@Override
	public RoleDto findByName(String name) {
 		return roleMapper.toDto(roleRepository.findByName(name).orElseGet(()->null));
		 
	}
	@Override
	public  List<RoleDto> getRolesById(List<String> ids) {
		 Optional< List<Role>>roles=  roleRepository.getRolesById(ids); 
		return    roles.orElseGet(()->null).stream().map(x-> roleMapper.toDto(x)).toList();
		
	}
	
	
	
	 

}
