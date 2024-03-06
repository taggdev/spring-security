package com.abciloveu.security.jwt;

import org.springframework.security.core.AuthenticationException;

/**
 * This runtime exception is thrown if JWT authentication fails.
 */
public class JwtAuthenticationException extends AuthenticationException {

	private static final long serialVersionUID = 1L;

	public JwtAuthenticationException(String msg, Throwable e) {
        super(msg, e);
    }

    public JwtAuthenticationException(String msg) {
        super(msg);
    }
}