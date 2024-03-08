package com.abciloveu.configuration;

import java.util.Optional;

import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@Configuration
public class DatabaseConfiguration {

	@Configuration
	@EnableTransactionManagement
	@EntityScan(basePackageClasses = { //@formatter:off
			com.abciloveu.entities.AppUser.class
		}
	)
	static class JpaConfiguration {

	};
	
	
	/**
	 * https://github.com/jared-carroll/spring-data-jpa-auditing
	 * https://docs.spring.io/spring-data/jpa/docs/current/reference/html/#auditing.annotations
	 */
	@Configuration
	@EnableJpaAuditing(auditorAwareRef = "springSecurityAuditorAware" )
	static class AuditorAwareConfiguration {
		
		@Bean
		public AuditorAware<String> springSecurityAuditorAware() {
			return new SpringSecurityAuditorAware();
		}
	}
	
	static class SpringSecurityAuditorAware implements AuditorAware<String> {

	  public Optional<String> getCurrentAuditor() { //@formatter:off

	    return Optional.ofNullable(SecurityContextHolder.getContext())
				  .map(SecurityContext::getAuthentication)
				  .filter(Authentication::isAuthenticated)
				  .map(Authentication::getName);
	  }//@formatter:off
	};
	
}
