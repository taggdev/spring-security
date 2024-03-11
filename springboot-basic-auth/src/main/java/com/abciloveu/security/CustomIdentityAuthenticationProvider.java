package com.abciloveu.security;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

@Component
public class CustomIdentityAuthenticationProvider implements AuthenticationProvider {

    public UserDetails isValidUser(String username, String password) {
        UserDetails user = null;
        if (username.equalsIgnoreCase("user")
                && password.equalsIgnoreCase("password")) {
            user = User
                    .withUsername(username)
                    .password("********")
                    .roles("USER")
                    .authorities("READ")
                    .build();
        } else if (username.equalsIgnoreCase("admin")
                && password.equalsIgnoreCase("admin132")) {
            user = User
                    .withUsername(username)
                    .password("********")
                    .roles("ADMIN")
                    .authorities("READ", "CREATE", "DELETE")
                    .build();
        }
        return user;
    }


    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        String username = authentication.getName();
        String password = authentication.getCredentials().toString();

        UserDetails userDetails = isValidUser(username, password);

        if (userDetails != null) {
            return new UsernamePasswordAuthenticationToken(
                    username,
                    password,
                    userDetails.getAuthorities());
        } else {
            throw new BadCredentialsException("Incorrect user credentials !!");
        }
    }

    @Override
    public boolean supports(Class<?> authenticationType) {
        return authenticationType.equals(UsernamePasswordAuthenticationToken.class);
    }
}
