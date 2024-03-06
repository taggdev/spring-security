package com.abciloveu.repositories;

import java.util.Optional;

import com.abciloveu.entites.AppUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface AppUserRepository extends JpaRepository<AppUser, Long>, JpaSpecificationExecutor<AppUser>{
	
	Optional<AppUser> findByUsernameIgnoreCase(String username);
	
	boolean existsByUsernameIgnoreCase(String username);

	@Modifying(clearAutomatically=true, flushAutomatically = true)
	@Query("UPDATE AppUser u SET u.accountNonLocked = :accNonLocked, updBy = 'SYSTEM', lastUpd = CURRENT_DATE WHERE username = :username")
	int updateAccountNonLocked(@Param("accNonLocked") boolean accNonLocked, @Param("username") String username);
	
}
