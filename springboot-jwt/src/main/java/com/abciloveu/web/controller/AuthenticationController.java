package com.abciloveu.web.controller;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.abciloveu.model.JwtResponse;
import com.abciloveu.model.LoginRequest;
import com.abciloveu.security.service.AuthenticationService;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthenticationController {

	private AuthenticationService authenticationService;
	
	@Autowired
	public AuthenticationController(AuthenticationService authenticationService) {
		this.authenticationService = authenticationService;
	}

	
	/**
    * Clients initially call this with their username/password in order to receive a JWT for use in future requests.
    *
    * @param authenticationRequest the authentication request containing the client's username/password.
    * @return the JWT if the client was authenticated.
    * @throws AuthenticationException if the the client was not authenticated successfully.
    */
	@PostMapping(value = "/login")
	public JwtResponse login(@RequestBody @Valid LoginRequest loginRequest) {
		return authenticationService.login(loginRequest);
	}
	
	
	/**
	 * Clients should call this in order to refresh a JWT.
	 * 
	 * @param request the request from the client.
	 * @return the JWT with an extended expiry time if the client was authenticated, a 400 Bad Request otherwise.
	 */
	@PostMapping(value = "/refreshtoken")
	public JwtResponse refreshToken(@RequestHeader("refresh_token") String refreshToken) {
        return authenticationService.refreshToken(refreshToken);
	}

	@PostMapping(value = "/logout")
	public void logout() throws AuthenticationException {

	}
}
