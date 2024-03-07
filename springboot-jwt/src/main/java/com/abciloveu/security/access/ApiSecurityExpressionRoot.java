package com.abciloveu.security.access;

import java.util.Collection;

import com.abciloveu.model.UserProfile;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.access.expression.SecurityExpressionRoot;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.core.Authentication;

public class ApiSecurityExpressionRoot extends SecurityExpressionRoot implements MethodSecurityExpressionOperations {

	private static final Logger LOG = LoggerFactory.getLogger(ApiSecurityExpressionRoot.class);

    private Object filterObject;
    
    private Object returnObject;

    public ApiSecurityExpressionRoot(Authentication authentication) {
        super(authentication);
    }

    @Override
    public Object getFilterObject() {
        return this.filterObject;
    }

    @Override
    public Object getReturnObject() {
        return this.returnObject;
    }

    @Override
    public Object getThis() {
        return this;
    }

    @Override
    public void setFilterObject(Object obj) {
        this.filterObject = obj;
    }

    @Override
    public void setReturnObject(Object obj) {
        this.returnObject = obj;
    }
    
    
    /*-------------------------------------------------------------------------
     * Custom method
     * ------------------------------------------------------------------------
     */
    
    public boolean isAdmin() {
		return this.hasRole("ADMIN");
	}

    /**
	 * @param permission a representation of the permissionId as supplied by the
	 * expression system. Not null.
	 * @return true if the permission is granted, false otherwise
	 */
	public boolean hasPermission(String permission) {

		if ((authentication == null) || !authentication.isAuthenticated()) {
			return false;
		}
		
		LOG.debug("Checking permission to access object {}: user='{}', permission='{}'.", 
				getThis(), authentication.getName(), permission);

		final Object principal = authentication.getPrincipal();
		if (principal instanceof UserProfile) {
			final UserProfile userProfile = (UserProfile) principal;

			 final Collection<String> privileges = userProfile.getPrivileges();
			 if(privileges == null || privileges.isEmpty()) {
				 LOG.trace("User '{}' does not have any privileges");
				 return false;
			 }
			 
			 return privileges.contains(permission.toUpperCase());
		}

		return false;
	}
	
}