package it.marmas.task.manager.api.service;

import java.util.List;

import it.marmas.task.manager.api.dto.RoleDto;

public interface RoleService {
	public RoleDto getRoleById(String id);
	public List<String> getAllRoles();
	public RoleDto updateRole(RoleDto r) ;
	public RoleDto insertRole(String r) ;
	public RoleDto deleteRole(long id);
	public RoleDto findByName(String name);
	public List<RoleDto> getRolesById(List<String> ids) ;

}
