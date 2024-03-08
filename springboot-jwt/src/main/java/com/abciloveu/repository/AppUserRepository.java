package com.abciloveu.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.abciloveu.entities.AppUser;


@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Long>, JpaSpecificationExecutor<AppUser>{
	
	Optional<AppUser> findByUsernameIgnoreCase(String username);
	
	boolean existsByUsernameIgnoreCase(String username);

	@Modifying(clearAutomatically=true, flushAutomatically = true)
	@Query("UPDATE AppUser u SET u.accountNonLocked = :accNonLocked, u.updBy = 'SYSTEM', u.lastUpd = CURRENT_DATE WHERE u.username = :username")
	void updateAccountNonLocked(@Param("accNonLocked") boolean accNonLocked, @Param("username") String username);
	
}
