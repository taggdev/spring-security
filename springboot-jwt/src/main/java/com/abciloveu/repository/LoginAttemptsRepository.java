package com.abciloveu.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.abciloveu.entities.LoginAttempts;


@Repository
public interface LoginAttemptsRepository extends JpaRepository<LoginAttempts, Long> {
	
	@Modifying(clearAutomatically=true, flushAutomatically = true)
	@Query("UPDATE LoginAttempts a SET a.attempts = a.attempts + 1, a.lastUpd = CURRENT_TIMESTAMP WHERE a.username = :username")
	int updateFailAttempts(@Param("username") String username);
	
	@Modifying(clearAutomatically = true)
	@Query("UPDATE LoginAttempts a SET a.attempts = 0, a.lastUpd = null WHERE a.username = :username")
	int resetFailAttempts(@Param("username") String username);
	
	Optional<LoginAttempts> getLoginAttemptsByUsername(String username);
}
