package it.marmas.task.manager.api.repo;

import java.util.List;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Repository;

import it.marmas.task.manager.api.model.Role;
import jakarta.persistence.EntityManager;
import jakarta.persistence.NoResultException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

@Repository
public class RoleRepositoryImpl implements RoleRepository{

  private final Logger logger = LoggerFactory.getLogger(RoleRepositoryImpl.class);
	@PersistenceContext
private EntityManager em;
 
	
 	@Override
	public Optional<Role> getRoleById(long id) {
 		try {
		 return Optional.of( em.find(Role.class, id));
 		}catch (Exception e) {
 			logger.error(e.getMessage());
 	 		return Optional.empty();
 		}
		 
	}
 	@Override
	public List<Role> getAllRoles() {
		String query="Select r from Role r";
		return em.createQuery(query,Role.class).getResultList();
	}
 	@Override
	public Optional<Role> updateRole(Role r) {
 		try {
		return Optional.of(em.merge(r));
 		}catch (Exception e) {
 		logger.error(e.getMessage());
 		return Optional.empty();
 		}
		
	}
 	@Override
	public Role insertRole(Role r) {
		  em.persist(r);
		  return r;
		
	}
 	@Override
	public Optional<Role> deleteRole(Role r) {
		 
 		try{
 			em.remove(em.merge(r));
 			return Optional.of(r);
 		}catch(Exception e) {
 			logger.error(e.getMessage());
 	 		return Optional.empty();
 		}
 		}
		  
		
	
	@Override
	public  Optional<Role> findByName(String name) {
		 TypedQuery<Role>sql= em.createQuery("SELECT r from Role r where name=:name",Role.class);
		 sql.setParameter("name", name);
		 try {
			 return  Optional.of(sql.getSingleResult());
		 }catch (NoResultException e) {
			return  Optional.empty();
		}
	}
	@Override
	public Optional<List<Role>> getRolesById(List<String> ids) {
		
		 TypedQuery<Role>sql = em.createQuery("SELECT r from Role r where username IN :list",Role.class);
		 sql.setParameter("list", ids);
		
		 try {
			 return Optional.of(sql.getResultList());
		 }catch(NoResultException e) {
			 return Optional.empty();
		 }
		 
 	}

}
