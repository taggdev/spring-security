package com.abciloveu.security.jwt;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;

/**
 * JWT 'login' entry point - there isn't one for JWT Authentication. 
 * We just send a 401 response, to which the client should call the /auth endpoint to fetch a JWT.
 *
 */
public class Http401UnauthorizedEntryPoint implements AuthenticationEntryPoint {

	private final Logger LOG = LoggerFactory.getLogger(this.getClass());

	/**
     * This is invoked when a user tries to access a secured REST resource without supplying any credentials.
     * <p>
     * We just send a 401 Unauthorized response because there is no 'login page' to redirect to. The client should
     * then post username/password to the /auth endpoint to obtain a JWT.
     *
     * @param request       the incoming request.
     * @param response      the outbound response.
     * @param authException the exception that got us here, e.g. user is not authenticated.
     */
	@Override
	public void commence(HttpServletRequest request, HttpServletResponse response,
			AuthenticationException authException) throws IOException {

		LOG.error("Unauthorized error: {}", authException.getMessage());
		response.sendError(HttpServletResponse.SC_UNAUTHORIZED, authException.getMessage());
	}
}