package com.abciloveu.service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.constraints.NotNull;

import com.abciloveu.entites.AppRole;
import com.abciloveu.entites.AppUser;
import com.abciloveu.exception.BadResourceException;
import com.abciloveu.exception.RecordNotFoundException;
import com.abciloveu.model.*;
import com.abciloveu.repositories.AppRoleRepository;
import com.abciloveu.repositories.AppUserRepository;
import com.abciloveu.security.service.UserProfileService;
import com.abciloveu.specification.AppUserSpecification;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.PropertyMapper;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
@Transactional(readOnly = true, rollbackFor = Exception.class)
public class AppUserService implements UserProfileService, UserManagementService {

	private static final Logger LOG = LoggerFactory.getLogger(AppUserService.class);

	private final AppUserRepository appUserRepository;

	private final AppRoleRepository appRoleRepository;

	private final PasswordEncoder passwordEncoder;

	@Autowired
	public AppUserService(final AppUserRepository appUserRepository, final AppRoleRepository appRoleRepository,
			final PasswordEncoder passwordEncoder) {
		super();

		this.appUserRepository = appUserRepository;
		this.appRoleRepository = appRoleRepository;
		this.passwordEncoder = passwordEncoder;
	}

	private PageRequest buildPageRequest(SearchCriteria criteria) {

		if (StringUtils.isBlank(criteria.getSort())) {
			return PageRequest.of(criteria.getPageNo() - 1, criteria.getPageSize());
		}

		else {
			return PageRequest.of(criteria.getPageNo() - 1, criteria.getPageSize(),
					Optional.of(criteria.getDirection()).orElse(Sort.Direction.ASC), criteria.getSort());
		}
	}

	public AppUser findById(final Long id) {
		return this.appUserRepository.findById(id)
				.orElseThrow(() -> new RecordNotFoundException("Cannot find User with id: " + id));
	}

	public AppUser findByUsername(final String username) {
		return this.appUserRepository.findByUsernameIgnoreCase(username)
				.orElseThrow(() -> new RecordNotFoundException("Cannot find User with username: " + username));
	}


	public Page<AppUser> findAllUsers(AppUserCriteria criteria) {
		return this.appUserRepository.findAll(AppUserSpecification.toPredicate(criteria), buildPageRequest(criteria));
	}

	public boolean userExists(final String username) {
		return this.appUserRepository.existsByUsernameIgnoreCase(username);
	}




	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public AppUser updateUser(final Long id, final AppUser user) {

		final AppUser entity = this.findById(id);

		final PropertyMapper mapper = PropertyMapper.get().alwaysApplyingWhenNonNull();
		mapper.from(user::getUsername).as(String::toLowerCase).to(entity::setUsername);
		mapper.from(user::getDisplayName).to(entity::setDisplayName);
		mapper.from(user::getContactName).to(entity::setContactName);
		mapper.from(user::getContactTel).to(entity::setContactTel);

		mapper.from(user::isEnabled).to(entity::setEnabled);
		mapper.from(user::isAccountNonExpired).to(entity::setAccountNonExpired);
		mapper.from(user::isAccountNonLocked).to(entity::setAccountNonLocked);
		mapper.from(user::isCredentialsNonExpired).to(entity::setCredentialsNonExpired);

		mapper.from(user::getRoles).as(this::lookup).to(entity::setRoles);

		return this.appUserRepository.save(entity);
	}

	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public AppUser updateUser(final Long id, final RegisteUpdateRequest user) {

		final AppUser entity = this.findById(id);

		final PropertyMapper mapper = PropertyMapper.get().alwaysApplyingWhenNonNull();
		mapper.from(user::getUsername).as(String::toLowerCase).to(entity::setUsername);
		mapper.from(user::getDisplayName).to(entity::setDisplayName);
		mapper.from(user::getContactName).to(entity::setContactName);
		mapper.from(user::getContactTel).to(entity::setContactTel);

		mapper.from(user::isEnabled).to(entity::setEnabled);
		mapper.from(user::isAccountNonExpired).to(entity::setAccountNonExpired);
		mapper.from(user::isAccountNonLocked).to(entity::setAccountNonLocked);
		mapper.from(user::isCredentialsNonExpired).to(entity::setCredentialsNonExpired);

		if(StringUtils.isNotEmpty(user.getPassword())){
			LocalDateTime localDateTime = LocalDateTime.now();
			mapper.from(user::getPassword).as(passwordEncoder::encode).to(entity::setPassword);
			mapper.from(convertToDateSqlTimestamp(localDateTime)).to(entity::setLastUpd);
		}

		mapper.from(user::getRoles)
				.as(this::mapRole)
				.to(entity::setRoles);

		return this.appUserRepository.save(entity);
	}

	@Override
	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public AppUser updateProfile(String username, UpdateProfileRequest request) {

		final AppUser entity = this.findByUsername(username);

		final PropertyMapper mapper = PropertyMapper.get().alwaysApplyingWhenNonNull();
		mapper.from(request::getDisplayName).to(entity::setDisplayName);
		mapper.from(request::getContactName).to(entity::setContactName);
		mapper.from(request::getContactTel).to(entity::setContactTel);

		return this.appUserRepository.save(entity);
	}


	public Optional<AppRole> findRoleById(final Long roleId) {
		return this.appRoleRepository.findById(roleId);
	}

	public List<AppRole> findAllRoles() {
		return (List<AppRole>) this.appRoleRepository.findAll();
	}


	@Transactional(readOnly = false, noRollbackFor = Exception.class)
	public AppUser createUser(final RegisterRequest registerRequest) {
		if (this.userExists(registerRequest.getUsername())) {
			throw new DuplicateKeyException("Username is already exists: " + registerRequest.getUsername());
		}

		// Create new user's account
		final AppUser entity = new AppUser();

		final PropertyMapper mapper = PropertyMapper.get().alwaysApplyingWhenNonNull();
		mapper.from(registerRequest::getUsername).as(String::toLowerCase).to(entity::setUsername);
		mapper.from(registerRequest::getDisplayName).to(entity::setDisplayName);
		mapper.from(registerRequest::getContactName).to(entity::setContactName);
		mapper.from(registerRequest::getContactTel).to(entity::setContactTel);

		mapper.from(registerRequest::getPassword).as(passwordEncoder::encode).to(entity::setPassword);

		mapper.from(registerRequest::isEnabled).to(entity::setEnabled);
		mapper.from(registerRequest::isAccountNonExpired).to(entity::setAccountNonExpired);
		mapper.from(registerRequest::isAccountNonLocked).to(entity::setAccountNonLocked);

		// set false to Enforce Change Password on User's initial login
		mapper.from(registerRequest::isCredentialsNonExpired).to(entity::setCredentialsNonExpired);

		mapper.from(registerRequest::getRoles)
			.as(this::mapRole)
			.to(entity::setRoles);

		return appUserRepository.save(entity);
	}

	@Transactional(readOnly = false, noRollbackFor = Exception.class)
	public void resetPassword(Long id, String newPassword) {
		final AppUser entity = this.findById(id);

		entity.setPassword(passwordEncoder.encode(newPassword));
		entity.setLastPasswordResetDate(new Date());

		LOG.debug("Reset password for user id='{}'", id);

		appUserRepository.save(entity);
	}

	@Transactional(readOnly = false, noRollbackFor = Exception.class)
	public void changeUserPassword(String currentUsername, ChangePasswordRequest request) {
		LOG.debug("Changing password for user '{}'", currentUsername);

		final AppUser entity = this.findByUsername(currentUsername);

		// verify current pasword first
		LOG.debug("Verify current pasword..");
		if(!checkIfValidOldPassword(entity, request.getOldPassword())) {
			throw new BadResourceException("Current password not valid");
	    }

		entity.setPassword(passwordEncoder.encode(request.getPassword()));

		appUserRepository.save(entity);
	}

	@Transactional(readOnly = false, noRollbackFor = Exception.class)
	public AppUser deleteUser(@NotNull Long id) {

		final AppUser entity = this.findById(id);
		appUserRepository.delete(entity);

		return entity;
	}

	private boolean checkIfValidOldPassword(final AppUser user, final String oldPassword) {
		return passwordEncoder.matches(oldPassword, user.getPassword());
	}

	private Set<AppRole> mapRole(final Set<Long> strRoles) {
		// @formatter:off
		if (strRoles == null) {
			return Collections.emptySet();
		}

		return strRoles.stream()
				.map(this::findRoleById)
				.filter(Optional::isPresent)
				.map(Optional::get)
				.collect(Collectors.toSet()); // @formatter:on
	}

	private Set<AppRole> lookup(final Set<AppRole> strRoles) {
		// @formatter:off
		if (strRoles == null) {
			return Collections.emptySet();
		}

		return strRoles.stream()
				.map(roleId -> findRoleById(roleId.getId()))
				.filter(Optional::isPresent)
				.map(Optional::get)
				.collect(Collectors.toSet()); // @formatter:on
	}

	public Date convertToDateSqlTimestamp(LocalDateTime dateToConvert) {
		return java.sql.Timestamp.valueOf(dateToConvert);
	}

}
