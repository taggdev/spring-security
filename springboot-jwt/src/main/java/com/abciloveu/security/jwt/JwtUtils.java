package com.abciloveu.security.jwt;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import javax.crypto.SecretKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;

import com.abciloveu.configuration.properties.JwtTokenProperties;
import com.abciloveu.model.UserProfile;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;

/**
 * Util class for validating and accessing JSON Web Tokens.
 * <p>
 * Properties are loaded from the resources/application.yml file.
 * <p>
 * Code originated from the excellent JWT and Spring Boot example by Stephan Zerhusen:
 * https://github.com/szerhusenBC/jwt-spring-security-demo
 *
 * @author gazbert
 */
@Component
public class JwtUtils {

	private static final Logger LOG = LoggerFactory.getLogger(JwtUtils.class);

	public static final String CLAIM_KEY_ROLES                     = "roles";
	public static final String CLAIM_KEY_DISPLAYNAME               = "displayname";
	public static final String CLAIM_KEY_PRIVILEGES                = "privileges";
	public static final String CLAIM_KEY_LAST_PASSWORD_RESET       = "lastPasswordReset";

	private final String issuer;

	private final long expirationInMs;
	
	private long allowedClockSkewInSecs;

	private final SecretKey signingKey;

	@Autowired
	public JwtUtils(JwtTokenProperties jwtTokenProperties) {
		Objects.requireNonNull(jwtTokenProperties, "jwtTokenProperties cannot be null");

		this.issuer = jwtTokenProperties.getIssuer();
		this.expirationInMs = jwtTokenProperties.getExpiration().toMillis();
		this.allowedClockSkewInSecs = jwtTokenProperties.getAllowedClockSkewInSecs();

		LOG.debug("Initial Signing Key using {}: {}", jwtTokenProperties.getAlgorithm(),
				jwtTokenProperties.getSecretKey());

		this.signingKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtTokenProperties.getSecretKey()));
	}

	/**
	 * For simple validation it is enough to just check the token integrity by decrypting it with our private key.
	 * We don't have to call the database for an additional User lookup/check for every request.
	 *
	 * @param token the JWT.
	 * @return the token claims if the JWT was valid.
	 * @throws JwtAuthenticationException if the JWT was invalid.
	 */
	public Claims validateTokenAndGetClaims(String token) {

		final Claims claims;
		try {
			claims = getClaimsFromToken(token);
		}
		catch (Exception e) {
			final String errorMsg = "Invalid token! Details: " + e.getMessage();
			LOG.error(errorMsg);
			
			throw new JwtAuthenticationException(errorMsg, e);
		}
		
		final Date created = getIssuedAtDateFromTokenClaims(claims);
		final Date lastPasswordChangeDate = getLastPasswordResetDateFromTokenClaims(claims);

		if (isCreatedBeforeLastPasswordChange(created, lastPasswordChangeDate)) {
			final String errorMsg = "Invalid token! Created date claim is before last password reset date."
					+ " Created date: " + created + " Password reset date: " + lastPasswordChangeDate;

			LOG.error(errorMsg);
			throw new JwtAuthenticationException(errorMsg);
		}

		return claims;
	}

	public String generateToken(UserProfile userDetails) {

		final Claims claims = Jwts.claims()
				.setId(userDetails.getId())
				.setSubject(userDetails.getUsername())
				.setIssuer(issuer)
				.setIssuedAt(new Date());

		claims.put(CLAIM_KEY_DISPLAYNAME, userDetails.getDisplayname());
		claims.put(CLAIM_KEY_ROLES, mapRolesFromGrantedAuthorities(userDetails.getAuthorities()));
		claims.put(CLAIM_KEY_PRIVILEGES, userDetails.getPrivileges());
		claims.put(CLAIM_KEY_LAST_PASSWORD_RESET, userDetails.getLastPasswordReset());

		return generateToken(claims);
	}

	public boolean canTokenBeRefreshed(Claims claims, Date lastPasswordReset) {
		final Date created = getIssuedAtDateFromTokenClaims(claims);
		return !isCreatedBeforeLastPasswordChange(created, lastPasswordReset);
	}

	public String refreshToken(String token) throws JwtAuthenticationException {
		try {
			final Claims claims = getClaimsFromToken(token);
			claims.setIssuedAt(new Date());

			return generateToken(claims);
		}
		catch (Exception e) {
			final String errorMsg = "Failed to refresh token!";
			LOG.error(errorMsg, e);

			throw new JwtAuthenticationException(errorMsg, e);
		}
	}

	public String getUsernameFromTokenClaims(Claims claims) {
		try {
			final String username = claims.getSubject();
			if (username == null) {
				final String errorMsg = "Failed to extract username claim from token!";

				LOG.error("Failed to extract username claim from token!");
				throw new JwtAuthenticationException(errorMsg);
			}

			return username;
		}
		catch (Exception e) {
			final String errorMsg = "Failed to extract username claim from token!";

			LOG.error(errorMsg);
			throw new JwtAuthenticationException(errorMsg, e);
		}
	}

	public UserProfile getPrincipalFromTokenClaims(Claims claims) throws JwtAuthenticationException {
		try {
			final String username = claims.getSubject();
			final String id = claims.getId();
			final String displayName = (String) claims.get(JwtUtils.CLAIM_KEY_DISPLAYNAME);
			final Long lastPasswordReset = (Long)claims.get(JwtUtils.CLAIM_KEY_LAST_PASSWORD_RESET);
			final List<GrantedAuthority> authorities = getRolesFromTokenClaims(claims);
			final Set<String> privileges = getPrivilegesFromTokenClaims(claims);

			final UserProfile principal = new UserProfile(
					id,
					username, 
					displayName, 
					null, 
					true, 
					authorities);
			
			principal.setLastPasswordReset(lastPasswordReset);
			principal.setPrivileges(privileges);
			
			return principal;
		}
		catch (Exception e) {
			final String errorMsg = "Failed to extract roles claim from token!";

			LOG.error(errorMsg, e);
			throw new JwtAuthenticationException(errorMsg, e);
		}
	}
	
	public List<GrantedAuthority> getRolesFromTokenClaims(Claims claims) throws JwtAuthenticationException {
		final List<GrantedAuthority> roles = new ArrayList<>();
		try {
			@SuppressWarnings("unchecked")
			final List<String> rolesFromClaim = (List<String>) claims.get(CLAIM_KEY_ROLES);
			for (final String roleFromClaim : rolesFromClaim) {
				roles.add(new SimpleGrantedAuthority(roleFromClaim));
			}

			return roles;
		}
		catch (Exception e) {
			final String errorMsg = "Failed to extract roles claim from token!";

			LOG.error(errorMsg);
			throw new JwtAuthenticationException(errorMsg, e);
		}
	}
	
	public Set<String> getPrivilegesFromTokenClaims(Claims claims) throws JwtAuthenticationException {
		final Set<String> privileges = new HashSet<>();
		try {
			@SuppressWarnings("unchecked")
			final List<String> privilegesFromClaim = (List<String>) claims.get(CLAIM_KEY_PRIVILEGES);
			if(privilegesFromClaim != null) {
				privileges.addAll(privilegesFromClaim);
			}
			
			return privileges;
		}
		catch (Exception e) {
			final String errorMsg = "Failed to extract privileges claim from token!";
			
			LOG.error(errorMsg);
			throw new JwtAuthenticationException(errorMsg, e);
		}
	}

	public Date getIssuedAtDateFromTokenClaims(Claims claims) throws JwtAuthenticationException {
		try {
			return claims.getIssuedAt();
		}
		catch (Exception e) {
			final String errorMsg = "Failed to extract iat claim from token!";

			LOG.error(errorMsg);
			throw new JwtAuthenticationException(errorMsg, e);
		}
	}

	public Date getExpirationDateFromTokenClaims(Claims claims) throws JwtAuthenticationException {
		try {
			return claims.getExpiration();
		}
		catch (Exception e) {
			final String errorMsg = "Failed to extract expiration claim from token!";

			LOG.error(errorMsg);
			throw new JwtAuthenticationException(errorMsg, e);
		}
	}

	public Date getLastPasswordResetDateFromTokenClaims(Claims claims) {
		Date lastPasswordResetDate = null;
		try {
			final Long resetDate = (Long) claims.get(CLAIM_KEY_LAST_PASSWORD_RESET);
			if(resetDate != null) {
				lastPasswordResetDate = new Date(resetDate);
			}
		}
		catch (Exception e) {
			LOG.error("Failed to extract lastPasswordResetDate claim from token!");
		}
		
		return lastPasswordResetDate;
	}

	// ------------------------------------------------------------------------
	// Private utils
	// ------------------------------------------------------------------------

	private String generateToken(Claims claims) {

		final Date issuedAtDate = (Date) claims.getIssuedAt();
		final Date expirationDate = new Date(issuedAtDate.getTime() + expirationInMs);

		//@formatter:off
		return Jwts.builder()
				.setClaims(claims)
				.setIssuedAt(issuedAtDate)
				.setExpiration(expirationDate)
				.signWith(signingKey)
				.compact(); //@formatter:on
	}

	public Claims getClaimsFromToken(String jwtToken) { //@formatter:off
		return Jwts.parserBuilder()
				.setSigningKey(signingKey)
				.setAllowedClockSkewSeconds(allowedClockSkewInSecs)
				.requireIssuer(issuer)
				.build()
				.parseClaimsJws(jwtToken)
				.getBody(); //@formatter:on
	}

	private boolean isCreatedBeforeLastPasswordChange(Date created, Date lastPasswordChange) {
		return (lastPasswordChange != null && created.before(lastPasswordChange));
	}

	private List<String> mapRolesFromGrantedAuthorities(Collection<? extends GrantedAuthority> grantedAuthorities) { //@formatter:off
		return grantedAuthorities.stream()
				.map(GrantedAuthority::getAuthority)
				.collect(Collectors.toList());//@formatter:off
	}
	
//	private SecretKey buildSigningKey(SignatureAlgorithm algorithm, String secretKey) {
//		final String secretKeyHex = Sha512DigestUtils.shaHex(secretKey);
//		return new SecretKeySpec(secretKeyHex.getBytes(StandardCharsets.UTF_8), algorithm.getJcaName());
//	}
}