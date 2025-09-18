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
@Table(name="password_reset_token")
public class PasswordResetToken {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private long id;
	
	@Column(name="token")
	private String token;
	
	@JoinColumn(name="user_id")
	@OneToOne
	private User user;

	@Column(name="expiry_date")
    private LocalDateTime expiryDate;
	
	@Column(name="enabled")
	private boolean enabled ;
	
	@PrePersist 
	protected void onCreate(){
		enabled=true;
	}
	
	
	public boolean isEnabled() {
		return enabled;
	}


	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public LocalDateTime getExpiryDate() {
		return expiryDate;
	}

	public void setExpiryDate(LocalDateTime expiryDate) {
		this.expiryDate = expiryDate;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}
	
 

	@Override
	public String toString() {
		return "PasswordResetToken [id=" + id + ", token=" + token + ", expiryDate=" + expiryDate
				+ ", enabled=" + enabled + ", user=" + user +"]";
	}

 
	
	
}
