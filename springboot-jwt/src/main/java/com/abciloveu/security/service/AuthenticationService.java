package com.abciloveu.security.service;

import java.util.Date;

import com.abciloveu.model.JwtResponse;
import com.abciloveu.model.LoginRequest;
import com.abciloveu.model.UserProfile;
import com.abciloveu.security.jwt.JwtAuthenticationException;
import com.abciloveu.security.jwt.JwtUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import io.jsonwebtoken.Claims;

@Service
public class AuthenticationService {

	static final Logger LOG = LoggerFactory.getLogger(AuthenticationService.class);

	private final AuthenticationManager authenticationManager;
	private final UserDetailsService userDetailsService;
	private final JwtUtils jwtUtils;
	
	@Autowired
	public AuthenticationService(AuthenticationManager authenticationManager, UserDetailsService userDetailsService,
			JwtUtils jwtUtils) {

		this.authenticationManager = authenticationManager;
		this.userDetailsService = userDetailsService;
		this.jwtUtils = jwtUtils;
	}
	
	
	private Authentication authenticate(Authentication authentication) {
		return authenticationManager.authenticate(authentication);
	}

	public JwtResponse login(LoginRequest authenticationRequest) {
		final UsernamePasswordAuthenticationToken authentication = (UsernamePasswordAuthenticationToken) authenticate(
				new UsernamePasswordAuthenticationToken(
							authenticationRequest.getUsername(),
							authenticationRequest.getPassword()));

		SecurityContextHolder.getContext().setAuthentication(authentication);

		// Reload password post-security check, so we can generate the token...
		final UserProfile userDetails = (UserProfile) userDetailsService
				.loadUserByUsername(authenticationRequest.getUsername());
		
		final String accessToken = jwtUtils.generateToken(userDetails);

		final Claims claims = jwtUtils.getClaimsFromToken(accessToken);
		
		final JwtResponse jwtResponse = new JwtResponse();
		
		jwtResponse.setAccessToken(accessToken);
		jwtResponse.setExpiration(claims.getExpiration());

		return jwtResponse;
	}

	public JwtResponse refreshToken(String jwtToken) {
		try {
			
			final Claims claims = jwtUtils.validateTokenAndGetClaims(jwtToken);
			final String username = jwtUtils.getUsernameFromTokenClaims(claims);
			final UserProfile userDetails = (UserProfile) userDetailsService.loadUserByUsername(username);
			
			LOG.debug("Reauthenticating user '{}' for account status check.", username);
			authenticate(new UsernamePasswordAuthenticationToken(username, userDetails.getPassword()));
	
			Date resetDate = null;
			Long lastResetTime = userDetails.getLastPasswordReset();
			if(lastResetTime != null) {
				resetDate = new Date(lastResetTime);
			}
			
			if (jwtUtils.canTokenBeRefreshed(claims, resetDate)) {
				final String refreshedToken = jwtUtils.refreshToken(jwtToken);
				
				final JwtResponse jwtResponse = new JwtResponse();
				jwtResponse.setAccessToken(refreshedToken);
				jwtResponse.setExpiration(claims.getExpiration());
	
				return jwtResponse;
			}
		}
		catch (Exception ignored) {
			// Ignored any errors
			LOG.error("Failed to refresh token because {}", ignored.getMessage());
		}
		
		throw new JwtAuthenticationException("Failed to refresh token!");
	}
}
