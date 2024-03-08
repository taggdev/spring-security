package com.abciloveu.admin.security.filter;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import javax.servlet.FilterChain;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.abciloveu.security.filter.JwtAuthenticationFilter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.junit4.SpringRunner;

import com.abciloveu.security.jwt.JwtUtils;

import io.jsonwebtoken.Claims;

/**
 * Tests the behaviour of the JWT Authentication filter is as expected.
 */
@RunWith(SpringRunner.class)
@SpringBootTest
public class JwtAuthenticationFilterTest {

    private static final String AUTHORIZATION_HEADER = "Authorization";
    private static final String BEARER_PREFIX = "Bearer ";
    private static final String USERNAME = "bobafett";

    @MockBean
    private HttpServletRequest request;

    @MockBean
    private HttpServletResponse response;

    @MockBean
    private FilterChain filterChain;

    @MockBean
    private JwtUtils jwtUtils;

    @MockBean
    private Claims claims;

    private JwtAuthenticationFilter jwtAuthenticationFilter;


    @BeforeEach
    public void setup() throws Exception {
        jwtAuthenticationFilter = new JwtAuthenticationFilter(jwtUtils);
    }

    @Test
    public void whenFilterCalledWithoutAuthorizationHeaderThenCallNextFilterInChain() throws Exception {

        when(request.getHeader(AUTHORIZATION_HEADER)).thenReturn(null);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(request, times(1)).getHeader(AUTHORIZATION_HEADER);
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    public void whenFilterCalledWithBearerTokenWithMissingUsernameThenCallNextFilterInChain() throws Exception {

        when(request.getHeader(AUTHORIZATION_HEADER)).thenReturn(BEARER_PREFIX + "dummy-token");

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(request, times(1)).getHeader(AUTHORIZATION_HEADER);
        verify(jwtUtils, times(1)).getUsernameFromTokenClaims(any());
        verify(filterChain, times(1)).doFilter(request, response);
    }


    @Test
    public void whenFilterCalledWithTokenWithMissingUsernameThenCallNextFilterInChain() throws Exception {

        when(request.getHeader(AUTHORIZATION_HEADER)).thenReturn("dummy-token");
        when(jwtUtils.getUsernameFromTokenClaims((any()))).thenReturn(null);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(request, times(1)).getHeader(AUTHORIZATION_HEADER);
        verify(jwtUtils, times(1)).getUsernameFromTokenClaims(any());
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    public void whenFilterCalledWithInvalidTokenThenCallNextFilterInChain() throws Exception {

        when(request.getHeader(AUTHORIZATION_HEADER)).thenReturn("dummy-token");
        when(jwtUtils.getUsernameFromTokenClaims((any()))).thenReturn(USERNAME);
        when(jwtUtils.validateTokenAndGetClaims((any()))).thenReturn(claims);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(request, times(1)).getHeader(AUTHORIZATION_HEADER);
        verify(jwtUtils, times(1)).getUsernameFromTokenClaims(any());
        verify(jwtUtils, times(1)).validateTokenAndGetClaims(any());
        verify(filterChain, times(1)).doFilter(request, response);
    }

    @Test
    public void whenFilterCalledWithValidTokenThenExpectSuccessfulAuthenticationAndCallNextFilterInChain() throws Exception {

        when(request.getHeader(AUTHORIZATION_HEADER)).thenReturn("dummy-token");
        when(jwtUtils.getUsernameFromTokenClaims((any()))).thenReturn(USERNAME);
        when(jwtUtils.validateTokenAndGetClaims((any()))).thenReturn(claims);

        jwtAuthenticationFilter.doFilterInternal(request, response, filterChain);

        verify(request, times(1)).getHeader(AUTHORIZATION_HEADER);
        verify(jwtUtils, times(1)).getUsernameFromTokenClaims(any());
        verify(jwtUtils, times(1)).validateTokenAndGetClaims(any());
        verify(jwtUtils, times(1)).getRolesFromTokenClaims(any());
        verify(filterChain, times(1)).doFilter(request, response);
    }
}