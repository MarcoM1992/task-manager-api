package it.marmas.task.manager.api.security;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import it.marmas.task.manager.api.model.User;
import it.marmas.task.manager.api.repo.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository; // mio User

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        // Cerca l'utente nel DB e lo wrappa in CustomUserDetails
    	
     	UserDetails user=null;
     	String findBy="";
     	Optional<User>opt;
     	if(username.contains("@")) {
     		
     		opt= userRepository.findByEmail(username);
     		findBy="username";
     	
     	}else {
     		opt =userRepository.findByUsername(username);
     		findBy="email";
     	}
     	final String findByFinal=findBy;
     	
         user=   opt.map(CustomUserDetails::new)
            .orElseThrow(() -> new UsernameNotFoundException("Utente non trovato con "+findByFinal+" : "+ username));
     	return user;
    }
    
    
   


}
