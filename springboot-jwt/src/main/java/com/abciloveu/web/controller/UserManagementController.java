package com.abciloveu.web.controller;

import java.net.URI;

import javax.validation.Valid;
import javax.validation.constraints.NotNull;

import com.abciloveu.model.RegisteUpdateRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import com.abciloveu.constant.SecurityConstants;
import com.abciloveu.entities.AppUser;
import com.abciloveu.model.AppUserCriteria;
import com.abciloveu.model.RegisterRequest;
import com.abciloveu.model.ResetPasswordRequest;
import com.abciloveu.security.service.LoginAttemptsService;
import com.abciloveu.service.UserManagementService;
import com.abciloveu.specification.AppUserSpecification;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@Secured({ SecurityConstants.ROLE_ADMIN })
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping(UserManagementController.MAPPING_URL)
@Api(value = "application-user-setting", tags = "System Setting")
public class UserManagementController {

	public static final String MAPPING_URL = "/api/v1/setting/users";

	private final UserManagementService userService;

	private final LoginAttemptsService loginAttemptsService;

	@Autowired
	public UserManagementController(UserManagementService appUserService, LoginAttemptsService loginAttemptsService) {
		this.userService = appUserService;
		this.loginAttemptsService = loginAttemptsService;
	}

	@GetMapping
	@ApiOperation(value = "Get all App Users")
	public Page<AppUser> findAllUsers(AppUserCriteria criteria) {
		return userService.findAllUsers(criteria);
	}

	@GetMapping("/{id}")
	@ApiOperation(value = "Get App Users by ID")
	public AppUser getUserById(@NotNull @PathVariable Long id) {
		return userService.findById(id);
	}

	@GetMapping("/username/{username}")
	@ApiOperation(value = "Get App Users by Username")
	public AppUser getUserByUsername(@NotNull @PathVariable String username) {
		return userService.findByUsername(username);
	}

	/**
	 * https://www.baeldung.com/registration-with-spring-mvc-and-spring-security
	 * https://www.baeldung.com/role-and-privilege-for-spring-security-registration
	 * @param registerRequest
	 * @return
	 */
	@PostMapping
	@Secured({ SecurityConstants.ROLE_ADMIN })
	@ApiOperation(value = "Create new User Account")
	public ResponseEntity<AppUser> createUser(@Valid @RequestBody RegisterRequest registerRequest) {

		final AppUser savedUser = userService.createUser(registerRequest);
		final URI location = ServletUriComponentsBuilder.fromCurrentRequest().path("/{id}")
				.buildAndExpand(savedUser.getId()).toUri();

		return ResponseEntity.created(location).body(savedUser);
	}

	@PutMapping("/{id}")
	@Secured({ SecurityConstants.ROLE_ADMIN })
	@ApiOperation(value = "Update User")
	public AppUser updateUser(@NotNull @PathVariable Long id, @Valid @RequestBody RegisteUpdateRequest user) {
		return userService.updateUser(id, user);
	}

	@PatchMapping("/{id}/uplock")
	@Secured({ SecurityConstants.ROLE_ADMIN })
	@ApiOperation(value = "Unlocks the account")
	public AppUser unlockUser(@NotNull @PathVariable Long id) {
		return loginAttemptsService.unlockUser(id);
	}

	@PatchMapping("/{id}/changepassword")
	@Secured({ SecurityConstants.ROLE_ADMIN })
	@ApiOperation(value = "Change password")
	@ResponseStatus(code = HttpStatus.NO_CONTENT, reason = "Changing password completed successfully")
	public void changePassword(@NotNull @PathVariable Long id, @Valid @RequestBody ResetPasswordRequest resetPassword) {
		userService.resetPassword(id, resetPassword.getPassword());
	}

	@DeleteMapping("/{id}")
	@Secured({ SecurityConstants.ROLE_ADMIN })
	@ResponseStatus(code = HttpStatus.NO_CONTENT, reason = "Delete user completed successfully")
	public AppUser deleteUser(@NotNull @PathVariable Long id) {
		return userService.deleteUser(id);
	}

}
