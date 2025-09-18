package it.marmas.task.manager.api.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name="user_validation_token")
public class UserValidationToken {
	@Id
	@GeneratedValue(strategy=GenerationType.IDENTITY)
	private Long id;
	
	@OneToOne
	@JoinColumn(name="user_id")
	private User user;
	
	
	@Column(name="expiry_date",updatable = false)
	private LocalDateTime expiryDate ;
 	
	@Column(name="token")
	private String token;
	
	@Column(name="enabled")
	private boolean enabled;
	
	@PrePersist
	protected void onCreate() {
		enabled=true;
	}
	
	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

    
	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public LocalDateTime getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(LocalDateTime expiryDate) {
		this.expiryDate = expiryDate;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public boolean isEnabled() {
		return enabled;
	}

	
	
	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	@Override
	public String toString() {
		return "UserValidationToken [id=" + id + ", user=" + user + ", expiryDate=" + expiryDate + ", token=" + token
				+"enabled="+enabled +"]";
	}
	
	
	
	 
	

}
