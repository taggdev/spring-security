package com.abciloveu.model;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Set;
import java.util.SortedSet;
import java.util.TreeSet;
import java.util.stream.Collectors;

import org.springframework.security.core.CredentialsContainer;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.SpringSecurityCoreVersion;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.util.Assert;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.ToStringSerializer;

public class UserProfile implements UserDetails, CredentialsContainer {

	private static final long serialVersionUID = 1L;
	
	private final String id;
	
	private final String username;

	private final String displayname;

	@JsonIgnore
	private String password;

	@JsonIgnore
	private final boolean enabled;
	
	@JsonIgnore
	private boolean accountNonExpired;
	
	@JsonIgnore
	private boolean accountNonLocked;
	
	@JsonIgnore
	private boolean credentialsNonExpired;
	
	private Long lastPasswordReset = 0L;

	@JsonSerialize(contentUsing = ToStringSerializer.class)
	private final Collection<? extends GrantedAuthority> authorities;
	
	private Collection<String> privileges = Collections.emptySet();
	
	public UserProfile(String id, String username, String displayname, String password, Collection<? extends GrantedAuthority> authorities) {
		this(id, username, displayname, password, true, authorities);
	}
	
	public UserProfile(String id, String username, String displayname, String password, boolean enabled,
			Collection<? extends GrantedAuthority> authorities) {
		
		this(id, username, displayname, password, enabled, true, true, true, authorities);
	}
	
	public UserProfile(String id, String username, String displayname, String password, boolean enabled,
			boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked, 
			Collection<? extends GrantedAuthority> authorities) {
		
		if (((username == null) || "".equals(username))) {
			throw new IllegalArgumentException(
					"Cannot pass null or empty values to constructor");
		}

		this.id = id;
		this.username = username;
		this.displayname = displayname;
		this.password = password;
		this.enabled = enabled;
		this.accountNonExpired = accountNonExpired;
		this.credentialsNonExpired = credentialsNonExpired;
		this.accountNonLocked = accountNonLocked;
		this.authorities = Collections.unmodifiableSet(sortAuthorities(authorities));
	}

	public String getId() {
		return id;
	}

	public String getUsername() {
		return username;
	}

	public String getDisplayname() {
		return displayname;
	}

	public String getPassword() {
		return password;
	}

	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	public boolean isEnabled() {
		return enabled;
	}

	@Override
	public boolean isAccountNonExpired() {
		return accountNonExpired;
	}

	@Override
	public boolean isAccountNonLocked() {
		return accountNonLocked;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return credentialsNonExpired;
	}
	
	@JsonIgnore
	public void eraseCredentials() {
		password = null;
	}
	
	public Long getLastPasswordReset() {
		return lastPasswordReset;
	}
	
	public void setLastPasswordReset(Long lastPasswordReset) {
		this.lastPasswordReset = lastPasswordReset;
	}
	
	public Collection<String> getPrivileges() {
		return privileges;
	}
	
	public void setPrivileges(Collection<String> privileges) {
		
		Assert.notNull(privileges, "Cannot pass a null privilege collection");
		this.privileges = Collections.unmodifiableSet(new TreeSet<>(privileges));
	}

	private static Set<GrantedAuthority> sortAuthorities(Collection<? extends GrantedAuthority> authorities) {
		Assert.notNull(authorities, "Cannot pass a null GrantedAuthority collection");
		// Ensure array iteration order is predictable (as per
		// UserDetails.getAuthorities() contract and SEC-717)
		final SortedSet<GrantedAuthority> sortedAuthorities = new TreeSet<>(new AuthorityComparator());

		for (GrantedAuthority grantedAuthority : authorities) {
			Assert.notNull(grantedAuthority, "GrantedAuthority list cannot contain any null elements");
			sortedAuthorities.add(grantedAuthority);
		}

		return sortedAuthorities;
	}

	private static class AuthorityComparator implements Comparator<GrantedAuthority>, Serializable {
		private static final long serialVersionUID = SpringSecurityCoreVersion.SERIAL_VERSION_UID;

		public int compare(GrantedAuthority g1, GrantedAuthority g2) {
			// Neither should ever be null as each entry is checked before adding it to
			// the set.
			// If the authority is null, it is a custom authority and should precede
			// others.
			if (g2.getAuthority() == null) {
				return -1;
			}

			if (g1.getAuthority() == null) {
				return 1;
			}

			return g1.getAuthority().compareTo(g2.getAuthority());
		}
	};

	/**
	 * Returns {@code true} if the supplied object is a {@code User} instance with the
	 * same {@code username} value.
	 * <p>
	 * In other words, the objects are equal if they have the same username, representing
	 * the same principal.
	 */
	@Override
	public boolean equals(Object rhs) {
		if (rhs instanceof UserProfile) {
			return username.equals(((UserProfile) rhs).username);
		}
		return false;
	}

	/**
	 * Returns the hashcode of the {@code username}.
	 */
	@Override
	public int hashCode() {
		return username.hashCode();
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder();
		sb.append("UserProfile: [");
		sb.append("Username: ").append(this.username).append("; ");
		sb.append("Dispalyname: ").append(this.displayname).append("; ");
		sb.append("Password: [PROTECTED]; ");
		sb.append("Enabled: ").append(this.enabled).append("; ");

		if (!authorities.isEmpty()) {
			sb.append("Granted Authorities: ");
			sb.append(authorities.stream().map(GrantedAuthority::getAuthority).collect(Collectors.joining(",")));
		}
		else {
			sb.append("Not granted any authorities");
		}
		sb.append("]");

		return sb.toString();
	}
}