package com.abciloveu.security.service;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Date;
import java.util.Optional;

import com.abciloveu.security.service.EngineAdminUserDetailsService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import com.abciloveu.entities.AppUser;
import com.abciloveu.model.UserProfile;
import com.abciloveu.repository.AppUserRepository;

/**
 * Tests the behaviour of the JWT User Details Service is as expected.
 */
@ExtendWith(MockitoExtension.class)
@RunWith(JUnitPlatform.class)
class EngineAdminUserDetailsServiceTest {

	private static final String KNOWN_USERNAME = "known-username";
	private static final String UNKNOWN_USERNAME = "unknown-username";

	@Mock
	private AppUserRepository userRepository;
	
	private EngineAdminUserDetailsService userDetailsService;

	@BeforeEach
	void setUp() throws Exception {
		userDetailsService = new EngineAdminUserDetailsService(userRepository);
		
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	@Test
	public void whenLoadByUsernameCalledWithKnownUsernameThenExpectUserDetailsToBeReturned() throws Exception {

		final AppUser mockUser = new AppUser();
		mockUser.setUsername(KNOWN_USERNAME);
		mockUser.setLastPasswordResetDate(new Date());
		
		when(userRepository.findByUsernameIgnoreCase(KNOWN_USERNAME)).thenReturn(Optional.of(mockUser));

		final UserProfile userDetails = (UserProfile) userDetailsService.loadUserByUsername(KNOWN_USERNAME);
		assertEquals(mockUser.getUsername(), userDetails.getUsername());
		assertEquals(mockUser.getLastPasswordReset(), userDetails.getLastPasswordReset());
	}

	@Test
	public void whenLoadByUsernameCalledWithUnknownUsernameThenExpectUsernameNotFoundException() throws Exception {

		when(userRepository.findByUsernameIgnoreCase(UNKNOWN_USERNAME)).thenReturn(Optional.empty());

		 Assertions.assertThrows(UsernameNotFoundException.class, () -> {
			 userDetailsService.loadUserByUsername(UNKNOWN_USERNAME);
		 });

	}

}
