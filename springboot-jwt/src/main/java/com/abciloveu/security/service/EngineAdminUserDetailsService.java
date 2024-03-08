package com.abciloveu.security.service;

import java.util.Collection;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsPasswordService;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.stereotype.Service;

import com.abciloveu.entities.AppRole;
import com.abciloveu.entities.AppUser;
import com.abciloveu.model.UserProfile;
import com.abciloveu.repository.AppUserRepository;

/**
 * @see UserDetailsService
 * @see UserDetailsPasswordService
 * @see InMemoryUserDetailsManager
 */
@Service
public class EngineAdminUserDetailsService implements UserDetailsService, UserDetailsPasswordService {

	private static final Logger LOG = LoggerFactory.getLogger(EngineAdminUserDetailsService.class);

	public final String AUTHORITIES_PREFIX = "ROLE_";

	private final AppUserRepository appUserRepository;

	@Autowired
	public EngineAdminUserDetailsService(final AppUserRepository appUserRepository) {
		this.appUserRepository = appUserRepository;
	}


	@Override
	public UserDetails loadUserByUsername(final String username) throws UsernameNotFoundException {

		final Optional<AppUser> optionalUser = this.appUserRepository.findByUsernameIgnoreCase(username);
		final AppUser user = optionalUser
				.orElseThrow(() -> new UsernameNotFoundException(String.format("No user found with username '%s'.", username)));

		final UserProfile userprofile = buildUserDetails(user, user.getRoles());

		LOG.debug("UserDetails: {}", userprofile);

		return userprofile;
	}

	@Override
	public UserDetails updatePassword(UserDetails user, String newPassword) {
		final String username = user.getUsername();
		final UserProfile profile = (UserProfile) user;
		final Optional<AppUser> optionalUser = this.appUserRepository.findByUsernameIgnoreCase(username);
		
		AppUser entity = optionalUser
				.orElseThrow(() -> new UsernameNotFoundException(String.format("No user found with username '%s'.", username)));
		
		entity.setPassword(newPassword);
		entity.setUpdBy("SYSTEM"); // force UpdBy to SYSTEM
		entity = appUserRepository.save(entity);

		LOG.info("User '{}' was re-encoded password by SYSTEM....", username);

		final UserProfile newProfile = new UserProfile(
				UUID.randomUUID().toString(),
				username, 
				profile.getDisplayname(), 
				entity.getPassword(),
				entity.isEnabled(), 
				entity.isAccountNonExpired(), 
				entity.isCredentialsNonExpired(), 
				entity.isAccountNonLocked(),
				profile.getAuthorities());
		
		newProfile.setLastPasswordReset(entity.getLastPasswordReset());
		newProfile.setPrivileges(newProfile.getPrivileges());

		return newProfile;
	}

	UserProfile buildUserDetails(AppUser entity, Collection<AppRole> roles) { //@formatter:off
		final Set<String> privileges = new HashSet<>();
		final Set<GrantedAuthority> authorities = new HashSet<>(roles.size());
		
		extractPermissions(roles, authorities, privileges);
		
		final UserProfile profile = new UserProfile(
				UUID.randomUUID().toString(),
				entity.getUsername(), 
				entity.getDisplayName(), 
				entity.getPassword(), 
				entity.isEnabled(),
				entity.isAccountNonExpired(), 
				entity.isCredentialsNonExpired(), 
				entity.isAccountNonLocked(),
				authorities); //@formatter:on

		profile.setLastPasswordReset(entity.getLastPasswordReset());
		profile.setPrivileges(privileges);

		return profile;
	}

	void extractPermissions(Collection<AppRole> roles, Set<GrantedAuthority> authorities, Set<String> privileges) {
		for (AppRole role : roles) {
			authorities.add(mapAuthority(role.getRoleName()));
			privileges.addAll(role.getPrivileges());
		}
	}

	GrantedAuthority mapAuthority(String name) {
		name = name.toUpperCase();
		if (!name.startsWith(AUTHORITIES_PREFIX)) {
			name = AUTHORITIES_PREFIX + name;
		}

		return new SimpleGrantedAuthority(name);
	}

}
