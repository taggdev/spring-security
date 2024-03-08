package com.abciloveu.security.jwt;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.Duration;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import javax.crypto.SecretKey;

import com.abciloveu.security.jwt.JwtAuthenticationException;
import com.abciloveu.security.jwt.JwtUtils;
import org.assertj.core.util.DateUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.platform.runner.JUnitPlatform;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.test.util.ReflectionTestUtils;

import com.abciloveu.configuration.properties.JwtTokenProperties;
import com.abciloveu.entities.AppRole;
import com.abciloveu.entities.AppUser;
import com.abciloveu.model.UserProfile;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;

@ExtendWith(MockitoExtension.class)
@RunWith(JUnitPlatform.class)
class JwtUtilsTest {

    private static final long GRADLE_FRIENDLY_TIME_TOLERANCE_IN_MILLIS = 10_000L;

    private static final SecretKey SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS512);
    private static final long EXPIRATION_PERIOD = 3_600L;
    private static final long ALLOWED_CLOCK_SKEW_IN_SECS = Duration.ofMinutes(5L).getSeconds(); // 5 mins

    private static final String ISSUER = "IT-GMS";
    private static final Date ISSUED_AT_DATE = new Date();
    private static final Date EXPIRATION_DATE = new Date(ISSUED_AT_DATE.getTime() + (EXPIRATION_PERIOD * 1_000));


    private static final Long USER_ROLE_ID = 21_344_565_442_342L;

    private static final Long USER_ID = 2_323_267_789_789L;
    private static final String USERNAME = "hansolo";
    private static final String PASSWORD = "password";
    private static final String DISPLAYNAME = "Han";
    private static final boolean USER_ENABLED = true;
    private static final Date LAST_PASSWORD_RESET_DATE = DateUtil.yesterday();
    private static final List<String> ROLES = Arrays.asList("ROLE_ADMIN", "ROLE_USER");
    private static final List<String> PRIVALAGES = Arrays.asList("100", "200", "300");

    private JwtUtils jwtUtils;

    @Mock
    private Claims claims;
    
	@BeforeEach
	void setUp() throws Exception {
		
		 JwtTokenProperties jwtTokenProperties = new JwtTokenProperties();
		 jwtTokenProperties.setAlgorithm(SignatureAlgorithm.HS256.name());
		 jwtTokenProperties.setExpiration(Duration.ofMillis(EXPIRATION_PERIOD));
		 jwtTokenProperties.setSecretKey(Encoders.BASE64.encode(SECRET_KEY.getEncoded()));
		 jwtTokenProperties.setIssuer(ISSUER);
		 
		 jwtUtils = new JwtUtils(jwtTokenProperties);
		 
		 ReflectionTestUtils.setField(jwtUtils, "allowedClockSkewInSecs", ALLOWED_CLOCK_SKEW_IN_SECS);
	}

	@AfterEach
	void tearDown() throws Exception {
	}


    // ------------------------------------------------------------------------
    // Get claims tests
    // ------------------------------------------------------------------------

    @Test
    public void testUsernameCanBeExtractedFromTokenClaims() throws Exception {
        when(claims.getSubject()).thenReturn(USERNAME);
        assertThat(jwtUtils.getUsernameFromTokenClaims(claims)).isEqualTo(USERNAME);
        verify(claims, times(1)).getSubject();
    }

    @Test
    public void testExceptionThrownIfUsernameCannotBeExtractedFromTokenClaims() throws Exception {
        when(claims.getSubject()).thenReturn(null);
        Assertions.assertThrows(JwtAuthenticationException.class, () -> {
        	jwtUtils.getUsernameFromTokenClaims(claims);
		 });
        verify(claims, times(1)).getSubject();
    }

    @Test
    public void testIssuedAtDateCanBeExtractedFromTokenClaims() throws Exception {
        when(claims.getIssuedAt()).thenReturn(ISSUED_AT_DATE);
        assertThat(jwtUtils.getIssuedAtDateFromTokenClaims(claims))
                .isCloseTo(ISSUED_AT_DATE, GRADLE_FRIENDLY_TIME_TOLERANCE_IN_MILLIS);
        verify(claims, times(1)).getIssuedAt();
    }

    @Test
    public void testExpirationDateCanBeExtractedFromTokenClaims() throws Exception {
        when(claims.getExpiration()).thenReturn(EXPIRATION_DATE);
        assertThat(jwtUtils.getExpirationDateFromTokenClaims(claims))
                .isCloseTo(EXPIRATION_DATE, GRADLE_FRIENDLY_TIME_TOLERANCE_IN_MILLIS);
        
        verify(claims, times(1)).getExpiration();
    }
    
    /**
     * Test Re-Create Principal from Token Claims
     * For {@link UserProfile#getAuthorities()} @see {@link #testRolesCanBeExtractedFromTokenClaims()}
     */
    @Test
    public void testPrincipalCanBeExtractedFromTokenClaims() throws Exception {
    	when(claims.getSubject()).thenReturn(USERNAME);
    	when(claims.get(JwtUtils.CLAIM_KEY_DISPLAYNAME)).thenReturn(DISPLAYNAME);
    	when(claims.get(JwtUtils.CLAIM_KEY_LAST_PASSWORD_RESET)).thenReturn(LAST_PASSWORD_RESET_DATE.getTime());
        when(claims.get(JwtUtils.CLAIM_KEY_ROLES)).thenReturn(ROLES);
        
        final UserProfile principal = jwtUtils.getPrincipalFromTokenClaims(claims);
        assertThat(principal.getUsername()).isEqualTo(USERNAME);
        assertThat(principal.getDisplayname()).isEqualTo(DISPLAYNAME);
        assertThat(principal.getLastPasswordReset()).isEqualTo(LAST_PASSWORD_RESET_DATE.getTime());
        
        verify(claims, times(1)).getSubject();
        verify(claims, times(1)).get(JwtUtils.CLAIM_KEY_ROLES);
    }

    @Test
    public void testRolesCanBeExtractedFromTokenClaims() throws Exception {
        when(claims.get(JwtUtils.CLAIM_KEY_ROLES)).thenReturn(ROLES);
        final List<GrantedAuthority> roles = jwtUtils.getRolesFromTokenClaims(claims);
        assertThat(roles.size()).isEqualTo(2);
        assertThat(roles.get(0).getAuthority()).isEqualTo("ROLE_ADMIN");
        assertThat(roles.get(1).getAuthority()).isEqualTo("ROLE_USER");
        verify(claims, times(1)).get(JwtUtils.CLAIM_KEY_ROLES);
    }

    @Test
    public void testLastPasswordResetDateCanBeExtractedFromTokenClaims() throws Exception {
        when(claims.get(JwtUtils.CLAIM_KEY_LAST_PASSWORD_RESET))
                .thenReturn(LAST_PASSWORD_RESET_DATE.getTime());
        
        assertThat(jwtUtils.getLastPasswordResetDateFromTokenClaims(claims))
                .isCloseTo(LAST_PASSWORD_RESET_DATE, GRADLE_FRIENDLY_TIME_TOLERANCE_IN_MILLIS);
    }

    // ------------------------------------------------------------------------
    // Validation tests
    // ------------------------------------------------------------------------

    @Test
    public void whenValidateTokenCalledWithNonExpiredTokenThenExpectSuccess() throws Exception {
        final String token = createToken();
        assertThat(jwtUtils.validateTokenAndGetClaims(token)).isNotNull();
    }

    @Test
    public void whenValidateTokenCalledWithExpiredTokenThenExpectFailure() throws Exception {
        ReflectionTestUtils.setField(jwtUtils, "allowedClockSkewInSecs", 0L);
        ReflectionTestUtils.setField(jwtUtils, "expirationInMs", 0L); // will expire fast!
        final String token = createToken();
        
        Assertions.assertThrows(JwtAuthenticationException.class, () -> {
        	jwtUtils.validateTokenAndGetClaims(token);
		 });
    }

    @Test
    public void whenValidateTokenCalledWithCreatedDateEarlierThanLastPasswordResetDateThenExpectFailure() throws Exception {
        final String token = createTokenWithInvalidCreationDate();
        
        Assertions.assertThrows(JwtAuthenticationException.class, () -> {
        	jwtUtils.validateTokenAndGetClaims(token);
		 });
    }

    // ------------------------------------------------------------------------
    // Util methods
    // ------------------------------------------------------------------------

    private String createToken() {
        return jwtUtils.generateToken(createUserProfile(LAST_PASSWORD_RESET_DATE));
    }

    private String createTokenWithInvalidCreationDate() {
        return jwtUtils.generateToken(createUserProfile(DateUtil.tomorrow()));
    }

    private UserProfile createUserProfile(Date lastPasswordResetDate) {
        final AppUser entity = createUser(lastPasswordResetDate);
        Collection<AppRole> roles = entity.getRoles();
        
		final Set<GrantedAuthority> authorities = new HashSet<>(roles.size());
		final Set<String> privileges = new HashSet<>();
		
		extractPermissions(roles, authorities, privileges);
		
        final UserProfile profile =  new UserProfile(
        		UUID.randomUUID().toString(),
				entity.getUsername(), 
				entity.getDisplayName(), 
				entity.getPassword(), 
				entity.isEnabled(),
				authorities); //@formatter:on
        
        profile.setPrivileges(privileges);
        profile.setLastPasswordReset(entity.getLastPasswordReset());
        
        return profile;
    }
    
    void extractPermissions(Collection<AppRole> roles, Set<GrantedAuthority> authorities, Set<String> privileges) {
		for (AppRole role : roles) {
			authorities.add(mapAuthority(role.getRoleName()));
			privileges.addAll(role.getPrivileges());
		}
	}

	GrantedAuthority mapAuthority(String name) {
		name = name.toUpperCase();
		if (!name.startsWith("ROLE_")) {
			name = "ROLE_" + name;
		}

		return new SimpleGrantedAuthority(name);
	}

    private AppUser createUser(Date lastPasswordResetDate) {

        final AppUser user = new AppUser();
        user.setId(USER_ID);
        user.setUsername(USERNAME);
        user.setPassword(PASSWORD);
        user.setDisplayName(DISPLAYNAME);
        user.setEnabled(USER_ENABLED);
        user.setLastPasswordResetDate(lastPasswordResetDate);

        final Set<AppRole> roles = createRoles(user);
        user.setRoles(roles);

        return user;
    }

    private Set<AppRole> createRoles(AppUser user) {

        final AppRole role1 = new AppRole();
        role1.setId(USER_ROLE_ID);
        role1.setRoleName("ROLE_USER");
        role1.setPrivileges(new HashSet<>(PRIVALAGES));

        return Collections.singleton(role1);
    }

}

