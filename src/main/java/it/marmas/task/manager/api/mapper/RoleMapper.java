package it.marmas.task.manager.api.mapper;

import java.util.Arrays;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import it.marmas.task.manager.api.dto.RoleDto;
import it.marmas.task.manager.api.model.Role;

@Component("roleMapper")
public class RoleMapper implements Mapper<Role, RoleDto> {
private static final Logger logger=LoggerFactory.getLogger(RoleMapper.class);
	@Override
	public RoleDto toDto(Role entity) {
	   return new RoleDto(entity.getId()+"",entity.getName());
	}

	@Override
	public Role toEntity(RoleDto dto) {
		Role r = new Role();
		String id= dto.getId();
		String name=dto.getName();
		boolean isValidId=Arrays.stream(dto.getId().split("")).allMatch(x->Character.isDigit(x.charAt(0)));
 		logger.info("id "+dto.getId()+" is all digit : "+ isValidId);
 		if(id!=null&&isValidId) {
 			r.setId(Long.parseLong(id));
 		}
 		r.setName(name);
 		return r;
	}

	
}
