package com.abciloveu.entities;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

import javax.persistence.Column;
import javax.persistence.Convert;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.abciloveu.common.Audit;
import com.abciloveu.converter.StringSetConverter;

@Entity
@Table(name = "app_role")
public class AppRole extends Audit implements Serializable {

	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "id")
	private Long id;

	@NotBlank
	@Size(max = 100)
	@Column(name = "role_name")
	private String roleName;

	@Size(max = 255)
	@Column(name = "description")
	private String description;

	@Convert(converter = StringSetConverter.class)
	@Column(name = "privileges")
	private Set<String> privileges = new HashSet<>();

	public AppRole() {
		super();
	}

	public AppRole(Long id) {
		super();
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public String getRoleName() {
		return roleName;
	}

	public void setRoleName(String roleName) {
		this.roleName = roleName;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Set<String> getPrivileges() {
		return privileges;
	}
	
	@JsonProperty("privileges")
	public String getPrivilegesAsString() {
		return String.join(",", getPrivileges());
	}

	public void setPrivileges(Collection<String> privileges) {
		if(privileges == null) {
			privileges = new HashSet<>();
		}
		else {
			this.privileges = new HashSet<>(privileges);
		}
	}

	@Override
	public int hashCode() {
		return Objects.hash(id, roleName);
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (!(obj instanceof AppRole)) {
			return false;
		}
		
		AppRole other = (AppRole) obj;
		return Objects.equals(id, other.id) && Objects.equals(roleName, other.roleName);
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("AppRole [");
		if (id != null) {
			builder.append("id=").append(id).append(", ");
		}
		if (roleName != null) {
			builder.append("roleName=").append(roleName).append(", ");
		}
		if (description != null) {
			builder.append("description=").append(description);
		}
		builder.append("]");
		return builder.toString();
	}

}
