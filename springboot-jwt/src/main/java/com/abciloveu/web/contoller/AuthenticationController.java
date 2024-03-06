package com.abciloveu.web.contoller;

import javax.validation.Valid;

import com.abciloveu.model.JwtResponse;
import com.abciloveu.model.LoginRequest;
import com.abciloveu.security.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/auth")
@CrossOrigin(origins = "*", maxAge = 3600)
public class AuthenticationController {

	private final AuthenticationService authenticationService;
	
	@Autowired
	public AuthenticationController(AuthenticationService authenticationService) {
		this.authenticationService = authenticationService;
	}

	
	@PostMapping(value = "/login")
	public JwtResponse login(@RequestBody @Valid LoginRequest loginRequest) {
		return authenticationService.login(loginRequest);
	}
	
	
	@PostMapping(value = "/refreshtoken")
	public JwtResponse refreshToken(@RequestHeader("refresh_token") String refreshToken) {
        return authenticationService.refreshToken(refreshToken);
	}

	@PostMapping(value = "/logout")
	public void logout() throws AuthenticationException {

	}
}
