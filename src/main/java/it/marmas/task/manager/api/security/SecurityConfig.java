package it.marmas.task.manager.api.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import it.marmas.task.manager.api.auth.JwtUtil;
import jakarta.servlet.http.HttpServletResponse;

@Configuration
@EnableWebSecurity
@EnableMethodSecurity // <-- per utilizzare @Preauthorize
public class SecurityConfig {
 
	@Autowired
    private   CustomUserDetailsService userDetailsService;

	
    public SecurityConfig(CustomUserDetailsService userDetailsService) {
        this.userDetailsService = userDetailsService;
    }
    @Bean JwtUtil getJwtUtil() {
    	return new JwtUtil();
    }

    @Bean
    JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(getJwtUtil(),userDetailsService);
    }
    
    @Bean 
    PasswordEncoder getPasswordEncoder() {    	
    	return new BCryptPasswordEncoder();
    }
    //
    
    @Bean
     AuthenticationManager authenticationManager(AuthenticationConfiguration authConfig) throws Exception {
        return authConfig.getAuthenticationManager();
    }

	
	@Bean
	  SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
		return httpSecurity.csrf(x-> x.disable())
				.authorizeHttpRequests(auth->auth
						.requestMatchers("/css/**", "/js/**", "/imgs/**").permitAll()
						.requestMatchers("/auth/**").permitAll() 
 						.requestMatchers("/user/*","/task/**").hasAnyRole("ADMIN","USER")
						.requestMatchers("/ADMIN/*").hasRole("ADMIN")
						 .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
  						.anyRequest().authenticated()
  						
						)
				//Handling role not allowed exception
				.exceptionHandling(ex -> ex
		                .accessDeniedHandler((request, response, accessDeniedException) -> {
		                    response.setStatus(HttpServletResponse.SC_FORBIDDEN);
		                    response.setContentType("application/json");
		                    response.getWriter().write("{\"error\": \"Non hai i permessi per accedere a questa risorsa." +
		                                              " Contatta l'amministratore.\"}");
		                    
		                })
		                .authenticationEntryPoint((request, response, authException) -> {
		                    response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		                    response.setContentType("application/json");
		                    response.getWriter().write("{\"error\": \"Accesso non autorizzato. Token assente o non valido.\"}");
		                })
		            )
				
				//“Non mantenere una sessione HTTP tra le richieste. Tratta ogni chiamata come indipendente, basandoti solo sul token.”
	            .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
	            
				.addFilterBefore(jwtAuthenticationFilter() ,UsernamePasswordAuthenticationFilter.class)
						.build();
	}
 
	@Bean
	  AuthenticationProvider authenticationProvider() {
	    DaoAuthenticationProvider provider = new DaoAuthenticationProvider(userDetailsService);
 	    provider.setPasswordEncoder(getPasswordEncoder()); 
 	    return provider;
	}
}
