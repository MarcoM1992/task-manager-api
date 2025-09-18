package it.marmas.task.manager.api.model;
import java.time.Instant;
import java.util.HashSet;
import java.util.Set;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.PrePersist;
import jakarta.persistence.Table;

@Entity
@Table(name = "users")
public class User  {
	public User() {}

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String username;

    @Column(nullable = false, unique = true)
    private String email;

    private String password;

    private boolean enabled = true;
    
    @Column(name="timezone")
    private String timezone;

    @Column(name = "created_at", updatable = false)
    private Instant createdAt;
     
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
        name = "users_roles",
        joinColumns = @JoinColumn(name = "user_id"),
        inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles = new HashSet<>();

    @Column(name="imagePath")
    private String imagePath;
    
    @OneToMany(fetch= FetchType.LAZY, mappedBy = "user", cascade = {CascadeType.PERSIST, CascadeType.MERGE}, orphanRemoval = false)
    private Set<Task> tasks = new HashSet<>();

    @PrePersist
    protected void onCreate() {
        this.createdAt = Instant.now();
        this.enabled=false;
        //even if you remove this conditions, in DB  will set this as 'UTC' if timezone is null.
        if(timezone==null) {
        	timezone="UTC";
        }
    }
    
    

	public Long getId() {
		return id;
	}

	
	public void setId(Long id) {
		this.id = id;
	}


	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public Instant getCreatedAt() {
		return createdAt;
	}

	public void setCreatedAt(Instant createdAt) {
		this.createdAt = createdAt;
	}

	public Set<Role> getRoles() {
		return roles;
	}

	public void setRoles(Set<Role> roles) {
		this.roles = roles;
	}

	public Set<Task> getTasks() {
		return tasks;
	}

	public void setTasks(Set<Task> tasks) {
		this.tasks = tasks;
	}
	
	

	public String getImagePath() {
		return imagePath;
	}
	



	public void setImagePath(String imagePath) {
		this.imagePath = imagePath;
	}


	
 
	public String getTimezone() {
		return timezone;
	}



	public void setTimezone(String timezone) {
		this.timezone = timezone;
	}



 


	@Override
	public String toString() {
		return "User [id=" + id + ", username=" + username + ", email=" + email + ", password=" + password
				+ ", enabled=" + enabled + ", timezone=" + timezone + ", createdAt=" + createdAt + ", roles=" + roles
				+ ", imagePath=" + imagePath + ", tasks=" + tasks + "]";
	}



	public void addTask(Task task) {
		tasks.add(task);
		task.setUser(this);
	}
	public void removeTask(Task task) {
		tasks.remove(task);
		task.setUser(null);
	}

 
    

 }
