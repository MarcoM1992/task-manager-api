package it.marmas.task.manager.api.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import it.marmas.task.manager.api.dto.RoleDto;
import it.marmas.task.manager.api.service.RoleService;

@RestController
@RequestMapping("/role") // Base path for all role-related endpoints
public class RoleController {

    // Logger for tracking information and errors
    private Logger logger = LoggerFactory.getLogger(RoleController.class);

    @Autowired
    private RoleService roleService; // Service layer for role operations

    /**
     * Retrieve all roles.
     * Only accessible by ADMIN users.
     * @param a Authentication object to log current user authorities
     * @return List of roles or error message
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/getAllRoles")
    public ResponseEntity<?> getAllRoles(Authentication a) {
        // Log all authorities of the current authenticated user
        a.getAuthorities().forEach(x -> logger.info(x.getAuthority()));
        try {
            return ResponseEntity.ok(roleService.getAllRoles());
        } catch (Exception e) {
            logger.error(e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Insert a new role.
     * Only accessible by ADMIN users.
     * @param roleName RoleDto containing the name of the role
     * @return Success message or error
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/insert")
    public ResponseEntity<?> insertRole(@RequestBody RoleDto roleName) {
        try {
            return ResponseEntity.ok(roleService.insertRole(roleName.getName()));
        } catch (Exception e) {
            logger.error(e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Delete a role by its ID.
     * Only accessible by ADMIN users.
     * @param id Role ID to delete
     * @return Success message or error
     */
    @PreAuthorize("hasRole('ADMIN')")
    @DeleteMapping("/delete/{id}")
    public ResponseEntity<?> deleteRole(@PathVariable long id) {
        try {
            return ResponseEntity.ok(roleService.deleteRole(id));
        } catch (Exception e) {
            logger.error(e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Update an existing role.
     * Only accessible by ADMIN users.
     * @param roleDto RoleDto containing updated role information
     * @return Updated role or error
     */
    @PreAuthorize("hasRole('ADMIN')")
    @PatchMapping("/update")
    public ResponseEntity<?> updateRole(@RequestBody RoleDto roleDto) {
        try {
            return ResponseEntity.ok(roleService.updateRole(roleDto));
        } catch (Exception e) {
            logger.error(e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    /**
     * Retrieve a role by its ID.
     * Only accessible by ADMIN users.
     * @param id Role ID
     * @return Role details or error
     */
    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/byId/{id}")
    public ResponseEntity<?> getById(@PathVariable String id) {
        try {
            logger.info("Fetching role with ID: " + id);
            return ResponseEntity.ok(roleService.getRoleById(id));
        } catch (Exception e) {
            logger.error(e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
