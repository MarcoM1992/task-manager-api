package it.marmas.task.manager.api.repo;

import java.util.List;
import java.util.Optional;

import it.marmas.task.manager.api.model.Role;

public interface RoleRepository  {
	public Optional<Role> getRoleById(long id);
	public List<Role> getAllRoles();
	public Optional<Role> updateRole(Role r);
	public Role insertRole(Role r);
	public Optional<Role> deleteRole(Role r);
	public  Optional<Role> findByName(String name);
	public Optional< List<Role>> getRolesById(List<String>ids);
 
	
	

}
