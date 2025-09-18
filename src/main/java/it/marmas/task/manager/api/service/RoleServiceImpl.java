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
public class RoleServiceImpl implements RoleService {

    @Autowired
    private RoleRepository roleRepository; // Repository for role persistence

    @Autowired
    @Qualifier("roleMapper")
    private Mapper<Role, RoleDto> roleMapper; // Mapper between Role entity and RoleDto

    private Logger logger = LoggerFactory.getLogger(getClass());

    /**
     * Retrieve a role by its string ID.
     * Validates the ID and throws IdException if invalid.
     */
    @Transactional(readOnly = true)
    @Override
    public RoleDto getRoleById(String idString) {
        if (!UtilityUser.idIsValid(idString)) {
            throw new IdException("Role ID is null or invalid");
        }
        long id = Long.parseLong(idString);
        Role role = roleRepository.getRoleById(id)
                                  .orElseThrow(() -> new IdException("Couldn't find role with this ID: " + id));
        return roleMapper.toDto(role);
    }

    /**
     * Retrieve all role names in the system
     */
    @Transactional(readOnly = true)
    @Override
    public List<String> getAllRoles() {
        return roleRepository.getAllRoles().stream()
                             .map(Role::getName)
                             .collect(Collectors.toList());
    }

    /**
     * Update an existing role
     * Throws exceptions if name or ID is invalid or role doesn't exist
     */
    @Transactional
    @Override
    public RoleDto updateRole(RoleDto r) {
        if (r.getName() == null || r.getId() == null) {
            throw new RoleException("Role name or ID is null");
        }
        if (!UtilityUser.idIsValid(r.getId())) {
            throw new IdException("ID must be a number");
        }

        long idNumerical = Long.parseLong(r.getId());

        Role role = roleRepository.getRoleById(idNumerical)
                                  .orElseThrow(() -> new ElementNotFoundException("Role not found for name: " + r.getName()));

        role = roleRepository.updateRole(role)
                             .orElseThrow(() -> {
                                 String errorMsg = "Update didn't succeed";
                                 logger.error(errorMsg);
                                 return new RoleException(errorMsg);
                             });

        return roleMapper.toDto(role);
    }

    /**
     * Insert a new role.
     * Checks for existing role to avoid duplicates.
     * Automatically adds "ROLE_" prefix if missing.
     */
    @Transactional
    @Override
    public RoleDto insertRole(String r) {
        if (r == null) {
            throw new RuntimeException("Role name cannot be null");
        }

        Optional<Role> opt = roleRepository.findByName(r);
        if (opt.isPresent()) {
            throw new RoleAlreadyPresentException();
        }

        Role role = new Role();
        role.setName(handlePrefix(r)); // Ensure role has the proper prefix

        role = roleRepository.insertRole(role);
        logger.info("Role inserted: " + role);

        return roleMapper.toDto(role);
    }

    /**
     * Ensure role name starts with "ROLE_"
     */
    private String handlePrefix(String r) {
        String rolePrefix = "ROLE_";
        boolean containsPrefix = r.contains(rolePrefix);
        String logMsg = containsPrefix ? "The name already contains the right prefix"
                                       : "The name didn't contain the prefix and it has been added!";
        logger.info(logMsg);
        return containsPrefix ? r : rolePrefix + r;
    }

    /**
     * Delete a role by ID.
     * If role is associated with users, detach them first.
     */
    @Transactional
    @Override
    public RoleDto deleteRole(long id) {
        Role role = roleRepository.getRoleById(id)
                                  .orElseThrow(() -> new RoleException("Cannot find role with ID: " + id));

        // Detach users before deletion to avoid constraint errors
        if (role.getUsers() != null) {
            role.setUsers(null);
        }

        Role deletedRole = roleRepository.deleteRole(role)
                                         .orElseThrow(() -> new RoleException("Role with ID " + id + " hasn't been deleted"));

        return roleMapper.toDto(deletedRole);
    }

    /**
     * Find a role by name
     */
    @Override
    public RoleDto findByName(String name) {
        return roleRepository.findByName(name)
                             .map(roleMapper::toDto)
                             .orElse(null);
    }

    /**
     * Retrieve multiple roles by their string IDs
     */
    @Override
    public List<RoleDto> getRolesById(List<String> ids) {
        Optional<List<Role>> roles = roleRepository.getRolesById(ids);
        return roles.orElseGet(List::of)
                    .stream()
                    .map(roleMapper::toDto)
                    .toList();
    }
}
