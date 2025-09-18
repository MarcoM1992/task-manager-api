package it.marmas.task.manager.api.model;
 
import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "refresh_tokens")
public class RefreshToken {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

     @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(nullable = false, unique = true, length = 500)
    private String token;

    @Column(nullable = false)
    private Instant expiry;

    @Column(nullable = false, updatable = false)
    private Instant createdAt = Instant.now();

    @Column(length = 255)
    private String deviceInfo;

    // Costruttori
    public RefreshToken() {}

    public RefreshToken(User user, String token, Instant expiry, String deviceInfo) {
        this.user = user;
        this.token = token;
        this.expiry = expiry;
        this.deviceInfo = deviceInfo;
    }
    @PrePersist()
    public void onCreate() {
    	createdAt= Instant.now();
    }

    @Override
	public String toString() {
		return "RefreshToken [id=" + id + ", user=" + user + ", token=" + token + ", expiry=" + expiry + ", createdAt="
				+ createdAt + ", deviceInfo=" + deviceInfo + "]";
	}

	public Long getId() { return id; }
    public User getUser() { return user; }
    public void setUser(User user) { this.user = user; }
    public String getToken() { return token; }
    public void setToken(String token) { this.token = token; }
    public Instant getExpiry() { return expiry; }
    public void setExpiry(Instant expiry) { this.expiry = expiry; }
    public Instant getCreatedAt() { return createdAt; }
    public String getDeviceInfo() { return deviceInfo; }
    public void setDeviceInfo(String deviceInfo) { this.deviceInfo = deviceInfo; }
}
