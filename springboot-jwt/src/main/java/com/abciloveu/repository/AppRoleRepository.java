package com.abciloveu.repository;

import com.abciloveu.entities.AppRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Optional;


/*@Repository
public interface AppRoleRepository extends JpaRepository<AppRole, Long> {
	
	Optional<AppRole> findByRoleName(String name);
}*/

@Repository
public interface AppRoleRepository extends JpaRepository<AppRole, Long>, JpaSpecificationExecutor<AppRole> {
    Optional<AppRole> findByRoleName(String name);
}
