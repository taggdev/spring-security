package com.abciloveu.entites;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.abciloveu.entites.common.Audit;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.Type;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@Entity
@DynamicUpdate
@Table(name = "app_user", uniqueConstraints = { @UniqueConstraint(columnNames = "username") })
public class AppUser extends Audit implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@NotBlank
	@Size(max = 50)
	@Column(name = "username")
	private String username;

	@JsonIgnore
	@Size(max = 256)
	@Column(name = "password")
//	@JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
	private String password;

	@NotBlank
	@Size(max = 120)
	@Column(name = "display_name")
	private String displayName;

	@NotBlank
	@Size(max = 200)
	@Column(name = "contact_name")
	private String contactName;

	@NotBlank
	@Size(max = 50)
	@Column(name = "contact_tel")
	private String contactTel;

	@Type(type = "org.hibernate.type.NumericBooleanType")
	@Column(name = "enabled", columnDefinition = "TINYINT(1)")
	private boolean enabled;

	@Type(type = "org.hibernate.type.NumericBooleanType")
	@Column(name = "account_non_expired", columnDefinition = "TINYINT(1)")
	private boolean accountNonExpired = true;

	@Type(type = "org.hibernate.type.NumericBooleanType")
	@Column(name = "account_non_locked", columnDefinition = "TINYINT(1)")
	private boolean accountNonLocked = true;

	@Type(type = "org.hibernate.type.NumericBooleanType")
	@Column(name = "credentials_non_expired", columnDefinition = "TINYINT(1)")
	private boolean credentialsNonExpired = true;

	@Temporal(TemporalType.TIMESTAMP)
	@Column(name = "last_password_reset", columnDefinition = "TINYINT(1)")
	private Date lastPasswordResetDate;

	/**
	 * Roles are being eagerly loaded here because
	 * they are a fairly small collection of items for this example.
	 */
	//@formatter:off
    @ManyToMany(fetch = FetchType.EAGER, cascade = {CascadeType.PERSIST, CascadeType.REFRESH, CascadeType.DETACH})
    @JoinTable(name = "user_role", 
    		joinColumns = @JoinColumn(name = "user_id", referencedColumnName = "id"),
            inverseJoinColumns = @JoinColumn(name = "role_id", referencedColumnName = "id")
    ) //@formatter:on
	private Set<AppRole> roles = new HashSet<>();

	public AppUser() {
		super();
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

	public void setUsername(@NotBlank @Size(max = 50) String username) {
		this.username = username.toLowerCase();
	}

	@JsonIgnore
	public String getPassword() {
		return password;
	}

	@JsonProperty
	public void setPassword(String password) {
		this.password = password;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getContactName() {
		return contactName;
	}

	public void setContactName(String contactName) {
		this.contactName = contactName;
	}

	public String getContactTel() {
		return contactTel;
	}

	public void setContactTel(String contactTel) {
		this.contactTel = contactTel;
	}

	public boolean isEnabled() {
		return enabled;
	}

	public void setEnabled(boolean enabled) {
		this.enabled = enabled;
	}

	public boolean isAccountNonExpired() {
		return accountNonExpired;
	}

	public void setAccountNonExpired(boolean accountNonExpired) {
		this.accountNonExpired = accountNonExpired;
	}

	public boolean isAccountNonLocked() {
		return accountNonLocked;
	}

	public void setAccountNonLocked(boolean accountNonLocked) {
		this.accountNonLocked = accountNonLocked;
	}

	public boolean isCredentialsNonExpired() {
		return credentialsNonExpired;
	}

	public void setCredentialsNonExpired(boolean credentialsNonExpired) {
		this.credentialsNonExpired = credentialsNonExpired;
	}

	public Date getLastPasswordResetDate() {
		return lastPasswordResetDate;
	}

	@JsonIgnore
	public Long getLastPasswordReset() {
		return this.getLastPasswordResetDate() != null ? this.getLastPasswordResetDate().getTime() : null;
	}

	public void setLastPasswordResetDate(Date lastPasswordResetDate) {
		this.lastPasswordResetDate = lastPasswordResetDate;
	}

	public Set<AppRole> getRoles() {
		return roles;
	}

	public void setRoles(Collection<AppRole> roles) {
		this.roles.clear();
		if(roles != null) {
			this.roles.addAll(roles);
		}
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, username);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}

		if (!(obj instanceof AppUser)) {
			return false;
		}

		AppUser other = (AppUser) obj;
		return Objects.equals(id, other.id) && Objects.equals(username, other.username);
	}

	@Override
	public String toString() { //@formatter:off
		return new StringBuilder()
				.append("AppUser [username=").append(username)
				.append(", password=").append(password)
				.append(", displayName=").append(displayName)
				.append(", enabled=").append(enabled)
				.append(", accountNonExpired=").append(accountNonExpired)
				.append(", accountNonLocked=").append(accountNonLocked)
				.append(", credentialsNonExpired=").append(credentialsNonExpired)
				.append(", lastPasswordResetDate=").append(lastPasswordResetDate)
				.append("]")
				.toString(); //@formatter:on
	}

}
