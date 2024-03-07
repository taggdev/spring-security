package com.abciloveu.service;

import com.abciloveu.entites.AppUser;
import com.abciloveu.model.AppUserCriteria;
import com.abciloveu.model.RegisteUpdateRequest;
import com.abciloveu.model.RegisterRequest;
import org.springframework.data.domain.Page;

public interface UserManagementService {

	AppUser findById(Long id);

	AppUser findByUsername(String username);

	Page<AppUser> findAllUsers(AppUserCriteria criteria);

	boolean userExists(String username);

	AppUser createUser(RegisterRequest registerRequest);

	AppUser updateUser(Long id, AppUser user);

	AppUser updateUser(Long id, RegisteUpdateRequest user);

	void resetPassword(Long id, String newPassword);

	AppUser deleteUser(Long id);

}
