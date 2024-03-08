package com.abciloveu.security.service;

import com.abciloveu.entities.AppUser;
import com.abciloveu.model.ChangePasswordRequest;
import com.abciloveu.model.UpdateProfileRequest;

public interface UserProfileService {

	AppUser findByUsername(String username);

	/**
	 * Update the specified user.
	 */
	AppUser updateProfile(String currentUsername, UpdateProfileRequest request);

	/**
	 * Modify the current user's password. This should change the user's password in the
	 * persistent user repository (datbase, LDAP etc).
	 * 
	 */
	void changeUserPassword(String currentUsername, ChangePasswordRequest request);

}