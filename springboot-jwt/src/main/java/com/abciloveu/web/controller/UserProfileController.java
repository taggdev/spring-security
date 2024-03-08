package com.abciloveu.web.controller;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import com.abciloveu.model.ChangePasswordRequest;
import com.abciloveu.model.UpdateProfileRequest;
import com.abciloveu.security.service.UserProfileService;

import io.swagger.annotations.ApiOperation;

@RestController
@PreAuthorize("isAuthenticated()")
@RequestMapping(UserProfileController.PROFILE_URL)
@CrossOrigin(origins = "*", maxAge = 3600)
public class UserProfileController {

	private static final Logger LOG = LoggerFactory.getLogger(UserProfileController.class);

	public static final String PROFILE_URL = "/api/v1/profile";
	
	private final UserProfileService appUserService;
	
	@Autowired
	public UserProfileController(UserProfileService appUserService) {
		this.appUserService = appUserService;
	}

	
	@GetMapping(value = "/username")
	@ApiOperation(value = "Get current username")
	public String currentUserName() {
		return SecurityContextHolder.getContext().getAuthentication().getName();
	}

	
	@GetMapping
	@ApiOperation(value = "Get profile")
	public UserDetails userProfile() {
		final Authentication currentAuth = SecurityContextHolder.getContext().getAuthentication();
		return (UserDetails) currentAuth.getPrincipal();
	}
	

	
	@PutMapping
	@ApiOperation(value = "Update profile", notes = "Change your Profile")
	@ResponseStatus(code=HttpStatus.NO_CONTENT, reason = "Your Profile has been changed successfully")
	public void updateUserProfile(@Valid @RequestBody UpdateProfileRequest request) {
		final Authentication currentAuth = SecurityContextHolder.getContext().getAuthentication();
		final String currentUsername = currentAuth.getName();
		
		if(!currentUsername.equals(request.getUsername())) {
			throw new AccessDeniedException("Cannot update profile as no Authentication object found in context for current user.");
		}
		
		appUserService.updateProfile(currentUsername, request);
	}

	@PatchMapping("/changepassword")
	@ApiOperation(value = "Change password", notes = "Change your password")
	@ResponseStatus(code=HttpStatus.NO_CONTENT, reason = "Your password has been changed successfully")
	public void changeUserPassword(@Valid @RequestBody ChangePasswordRequest request) {
		
		final Authentication currentAuth = SecurityContextHolder.getContext().getAuthentication();
		final String currentUsername = currentAuth.getName();
	
		LOG.debug("Changing password for user '{}'", currentUsername);
		
		appUserService.changeUserPassword(currentUsername, request);
		
		LOG.debug("Changing password for user '{}' completed successfully.", currentUsername);
	}
}
