package com.abciloveu.web.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.abciloveu.constant.SecurityConstants;

import io.swagger.annotations.ApiOperation;

@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequestMapping("/test")
public class TestController {

	@ApiOperation("Public Access.")
	@GetMapping("/all")
	public String allAccess() {
		return "Public Access.";
	}

	
	@ApiOperation("Any Authenticated User Access.")
	@GetMapping("/authenticated")
	@PreAuthorize("isAuthenticated()")
	public String anyUserAccess() {
		return "Any Authenticated User Access.";
	}

	@ApiOperation("User Access.")
	@GetMapping("/user")
	@PreAuthorize("hasRole('USER') or hasRole('ADMIN')")
	public String userAccess() {
		return "User Access.";
	}

	@ApiOperation("Admin Access.")
	@GetMapping("/admin")
	@PreAuthorize("hasRole('ADMIN')")
	public String adminAccess() {
		return "Admin Access.";
	}
	
	@ApiOperation("Has Permission " +SecurityConstants.MENU_SETTING_CLEAR_CACHE)
	@GetMapping("/hasPermission")
	@PreAuthorize("hasPermission(T(SecurityConstants).MENU_SETTING_CLEAR_CACHE)")
	public String hasPermission() {
		return "Has Permission " +SecurityConstants.MENU_SETTING_CLEAR_CACHE;
	}
	
	
}