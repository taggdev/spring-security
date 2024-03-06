package com.abciloveu.entites;

import java.io.Serializable;
import java.util.Date;
import java.util.Objects;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.springframework.data.annotation.LastModifiedDate;

@Entity
@Table(name = "login_attempts")
public class LoginAttempts implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@NotBlank
	@Size(max = 50)
	@Column(name = "username")
	private String username;

	@Column(name = "attempts", columnDefinition = "TINYINT(4)")
	private int attempts;

	@LastModifiedDate
	@Column(name = "last_upd")
	@Temporal(TemporalType.TIMESTAMP)
	private Date lastUpd;

	public LoginAttempts() {
		super();
	}
	
	public LoginAttempts(@NotBlank @Size(max = 50) String username, int attempts, Date lastUpd) {
		super();
		this.username = username;
		this.attempts = attempts;
		this.lastUpd = lastUpd;
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

	public int getAttempts() {
		return attempts;
	}

	public void setAttempts(int attempts) {
		this.attempts = attempts;
	}
	
	public Date getLastUpd() {
		return lastUpd;
	}

	public void setLastUpd(Date lastUpd) {
		this.lastUpd = lastUpd;
	}

	@Override
	public int hashCode() {
		return Objects.hash(username);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof LoginAttempts)) {
			return false;
		}
		LoginAttempts other = (LoginAttempts) obj;
		return Objects.equals(username, other.username);
	}

}
