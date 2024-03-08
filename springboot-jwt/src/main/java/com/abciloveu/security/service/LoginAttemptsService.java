package com.abciloveu.security.service;

import java.util.Date;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.context.MessageSourceAware;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.SpringSecurityMessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import com.abciloveu.entities.AppUser;
import com.abciloveu.entities.LoginAttempts;
import com.abciloveu.exception.RecordNotFoundException;
import com.abciloveu.repository.AppUserRepository;
import com.abciloveu.repository.LoginAttemptsRepository;

/**
 * TODO load maxAttempts from global config
 */
@Service
@Transactional(readOnly = true, rollbackFor = Exception.class)
public class LoginAttemptsService implements InitializingBean, MessageSourceAware {
	
	private static final Logger LOG = LoggerFactory.getLogger(LoginAttemptsService.class);

	private static final int DEFAULT_MAX_ATTEMPTS = 5;

	private int maxAttempts;

	private final AppUserRepository userRepository;
	
	private final LoginAttemptsRepository loginAttemptsRepository;
	
	protected MessageSourceAccessor messages = SpringSecurityMessageSource.getAccessor();

	@Autowired
	public LoginAttemptsService(AppUserRepository userRepository, LoginAttemptsRepository loginAttemptsRepository) {
		this(userRepository, loginAttemptsRepository, DEFAULT_MAX_ATTEMPTS);
	}

	public LoginAttemptsService(AppUserRepository userRepository, LoginAttemptsRepository loginAttemptsRepository,
			int maxAttempts) {
		this.userRepository = userRepository;
		this.loginAttemptsRepository = loginAttemptsRepository;
		this.maxAttempts = maxAttempts;
	}
	
	@Override
	public final void afterPropertiesSet() throws Exception {
		Assert.notNull(this.userRepository, "A user repository must be set");
		Assert.notNull(this.loginAttemptsRepository, "A login-attempts repository must be set");
		Assert.notNull(this.messages, "A message source must be set");
	}
	
	@Override
	public void setMessageSource(MessageSource messageSource) {
		this.messages = new MessageSourceAccessor(messageSource);
	}

	public int getMaxAttempts() {
		return maxAttempts;
	}
	
	public void setMaxAttempts(int maxAttempts) {
		this.maxAttempts = maxAttempts;
	}

	@Transactional(rollbackFor = Exception.class, noRollbackFor = LockedException.class)
	public void updateFailAttempts(String username) {
		final Optional<LoginAttempts> mayAttempts = loginAttemptsRepository.getLoginAttemptsByUsername(username);
		if (!mayAttempts.isPresent()) {
			if (userRepository.existsByUsernameIgnoreCase(username)) {
				// if no record, insert a new
				LOG.info("Creating login attempts: username = '{}'.", username);
				loginAttemptsRepository.save(new LoginAttempts(username, 1, new Date()));
			}
		}
		else {
			if (userRepository.existsByUsernameIgnoreCase(username)) {
				// update attempts count, +1
				LOG.info("Increase number of attempts: username = '{}'.", username);
				loginAttemptsRepository.updateFailAttempts(username);
			}

			final LoginAttempts userAttempts = mayAttempts.get();
			if (userAttempts.getAttempts() + 1 >= maxAttempts) {
				// locked user
				LOG.warn("User '{}' has been locked by SYSTEM because maximum number of login attempts exceeded: {}", username, maxAttempts);
				userRepository.updateAccountNonLocked(false, username);

				throw new LockedException(messages.getMessage(
						"AbstractUserDetailsAuthenticationProvider.locked",
						"User account is locked"));
			}
		}
	}

	@Transactional(readOnly = false, rollbackFor = Exception.class)
	public void resetFailAttempts(String username) {
		LOG.info("Reset login fail attempts for user {}", username);
		loginAttemptsRepository.resetFailAttempts(username);
	}
	
	@Transactional(noRollbackFor = Exception.class)
	public AppUser unlockUser(final Long id) {
		LOG.debug("Unlocking User id = '{}'.", id);

		final Optional<AppUser> optionalUser = userRepository.findById(id);
		final AppUser entity = optionalUser
				.orElseThrow(() -> new RecordNotFoundException("User not found: id=" + id));

		entity.setAccountNonLocked(true);
		
		resetFailAttempts(entity.getUsername());

		return userRepository.save(entity);
	}


}