package it.marmas.task.manager.api.security;
import java.util.Collection;
import java.util.stream.Collectors;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import it.marmas.task.manager.api.model.User;

public class CustomUserDetails implements UserDetails {

    /**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private final User user; // la tua entità User

    public CustomUserDetails(User user) {
        this.user = user;
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        // Converte i ruoli dell'utente in oggetti SimpleGrantedAuthority
        return user.getRoles().stream()
                .map(role -> new SimpleGrantedAuthority(role.getName()))
                .collect(Collectors.toList());
    }

    @Override
    public String getPassword() {
        return user.getPassword(); // assicura che l'entità User abbia questo campo
    }

    @Override
    public String getUsername() {
        return user.getUsername(); // idem
    }

    @Override
    public boolean isAccountNonExpired() {
        return true; // logica tua se vuoi implementare scadenze account
    }

    @Override
    public boolean isAccountNonLocked() {
        return true; // oppure user.isLocked() se hai un campo nel DB
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true; // logica per scadenza password, se esiste
    }

    @Override
    public boolean isEnabled() {
        return  user.isEnabled();
    }

    public User getUser() {
        return this.user;
    }
}
