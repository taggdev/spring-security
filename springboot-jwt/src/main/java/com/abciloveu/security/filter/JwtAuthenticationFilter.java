package com.abciloveu.security.filter;

import java.io.IOException;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.exception.ExceptionUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.web.filter.OncePerRequestFilter;

import com.abciloveu.model.UserProfile;
import com.abciloveu.security.jwt.JwtUtils;

import io.jsonwebtoken.Claims;

/**
 * The JWT Authentication filter extracts the token from the Authorization header and it validates it. If no JWT is
 * present, the next filter in the Spring Security filter chain is invoked.
 * <p>
 * Code originated from the excellent JWT and Spring Boot example by Stephan Zerhusen:
 * https://github.com/szerhusenBC/jwt-spring-security-demo
 *
 * @author gazbert
 */
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private static final Logger LOG = LoggerFactory.getLogger(JwtAuthenticationFilter.class);

	private static final String BEARER_PREFIX = "Bearer ";
	private static final int BEARER_PREFIX_LENGTH = BEARER_PREFIX.length();

	private final JwtUtils jwtUtils;

	@Autowired
	public JwtAuthenticationFilter(JwtUtils jwtUtils) {
		this.jwtUtils = jwtUtils;
	}

	@Override
	public void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
			throws ServletException, IOException {

		// Extract token after Bearer prefix if present
		final String token = extractToken(request);
		if (token != null) {
			try {
				final Claims claims = jwtUtils.validateTokenAndGetClaims(token);
				final String username = jwtUtils.getUsernameFromTokenClaims(claims);
				LOG.info("Username in JWT: {}", username);

				if (SecurityContextHolder.getContext().getAuthentication() == null) {

					// It is not compulsory to load the User details from the database.
					// We can just use the information in the token claims - this saves a repo lookup.
					//
					// final UserDetails userDetails = userDetailsService.loadUserByUsername(username);
					// if (userDetails != null && !(userDetails.getUsername().equals(username))) {
					//    final String errorMsg = "Username is token not found in User repository! Token username: " + username;
					//    throw new JwtAuthenticationException(errorMsg);
					// }

					LOG.info("JWT is valid");

					final UserProfile userDetails = jwtUtils.getPrincipalFromTokenClaims(claims);
					final UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
							userDetails, null, userDetails.getAuthorities());

					authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
					SecurityContextHolder.getContext().setAuthentication(authentication);

					LOG.info("Authenticated User: {} has been set in Spring SecurityContext.", username);
				}
			}
			catch (AuthenticationException e) {
				SecurityContextHolder.clearContext();

				final Throwable cause = ExceptionUtils.getRootCause(e);

				LOG.error("JWT Authentication failure: {}", cause.getMessage());
				response.sendError(HttpServletResponse.SC_UNAUTHORIZED, cause.getMessage());

				return;
			}
		}
		
		chain.doFilter(request, response);

	}

	private String extractToken(HttpServletRequest request) {
		final String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

		// Extract token after Bearer prefix if present
		if (authorizationHeader != null && authorizationHeader.startsWith(BEARER_PREFIX)) {
			return authorizationHeader.substring(BEARER_PREFIX_LENGTH);
		}

		return null;
	}
}