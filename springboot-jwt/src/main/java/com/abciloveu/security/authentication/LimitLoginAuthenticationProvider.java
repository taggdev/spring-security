package com.abciloveu.security.authentication;

import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import com.abciloveu.security.service.LoginAttemptsService;

public class LimitLoginAuthenticationProvider extends DaoAuthenticationProvider {

	private final LoginAttemptsService loginAttemptsService;

	public LimitLoginAuthenticationProvider(LoginAttemptsService loginAttemptsService) {
		this.loginAttemptsService = loginAttemptsService;
	}
	

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {

		try {

			final Authentication auth = super.authenticate(authentication);

			//if reach here, means login success, else an exception will be thrown
			//reset the user_attempts
			loginAttemptsService.resetFailAttempts(authentication.getName());

			return auth;

		}
		catch (BadCredentialsException e) {
			//invalid login, update to user_attempts
			loginAttemptsService.updateFailAttempts(authentication.getName());
			
			throw e;
		}
	}

}
