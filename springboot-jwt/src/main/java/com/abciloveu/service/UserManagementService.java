package com.abciloveu.service;

import com.abciloveu.model.RegisterUpdateRequest;
import org.springframework.data.domain.Page;

import com.abciloveu.entities.AppUser;
import com.abciloveu.model.AppUserCriteria;
import com.abciloveu.model.RegisterRequest;

public interface UserManagementService {

	AppUser findById(Long id);

	AppUser findByUsername(String username);

	Page<AppUser> findAllUsers(AppUserCriteria criteria);

	boolean userExists(String username);

	AppUser createUser(RegisterRequest registerRequest);

	AppUser updateUser(Long id, AppUser user);

	AppUser updateUser(Long id, RegisterUpdateRequest user);

	void resetPassword(Long id, String newPassword);

	AppUser deleteUser(Long id);

}
