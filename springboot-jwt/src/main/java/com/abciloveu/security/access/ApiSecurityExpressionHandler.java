package com.abciloveu.security.access;

import org.aopalliance.intercept.MethodInvocation;
import org.springframework.expression.spel.support.StandardEvaluationContext;
import org.springframework.expression.spel.support.StandardTypeLocator;
import org.springframework.security.access.expression.method.DefaultMethodSecurityExpressionHandler;
import org.springframework.security.access.expression.method.MethodSecurityExpressionOperations;
import org.springframework.security.core.Authentication;

import com.abciloveu.constant.SecurityConstants;

public class ApiSecurityExpressionHandler extends DefaultMethodSecurityExpressionHandler {

	public ApiSecurityExpressionHandler() {
		super();
	}

	/**
	 * Overrids {@link #createEvaluationContextInternal(Authentication, MethodInvocation)}
	 * to register a new import prefix that will be used when searching for unqualified types.
	 * Expected format is something like "java.lang".
	 * 
	 * <pre>
	 * And refer to it without package name in annotations:
	 * <code>
	 *   @PreAuthorize("hasRole(T(Roles).ROLE_AUTHENTICATED)")
	 * </code>
	 * instead of: 
	 * <code>
	 *   @PreAuthorize("hasRole(T(my.example.Roles).ROLE_AUTHENTICATED)")
	 * </code>
	 * Makes it more readable imho. Also roles are now typed. Write:
	 * <code>
	 *   @PreAuthorize("hasRole(T(Roles).ROLE_AUTHENTICATEDDDD)")
	 * </code>
	 * 
	 * and you will get startup errors that wouldn't have been there if you wrote:
	 * <code>
	 *   @PreAuthorize("hasRole('ROLE_AUTHENTICATEDDDD')")
	 * </code>
	 * </pre>
	 * 
	 * 
	 * @see #createEvaluationContextInternal(Authentication, MethodInvocation)
	 * @see StandardTypeLocator#registerImport(String)
	 */
	@Override
	public StandardEvaluationContext createEvaluationContextInternal(final Authentication auth,
			final MethodInvocation mi) {
		final StandardEvaluationContext standardEvaluationContext = super.createEvaluationContextInternal(auth, mi);
		
		((StandardTypeLocator) standardEvaluationContext.getTypeLocator())
				.registerImport(SecurityConstants.class.getPackage().getName());

		return standardEvaluationContext;
	}

	@Override
	protected MethodSecurityExpressionOperations createSecurityExpressionRoot(Authentication authentication,
			MethodInvocation invocation) {
		
		final ApiSecurityExpressionRoot root = new ApiSecurityExpressionRoot(authentication);
		root.setPermissionEvaluator(getPermissionEvaluator());
		root.setTrustResolver(getTrustResolver());
		root.setRoleHierarchy(getRoleHierarchy());

		return root;
	}
}