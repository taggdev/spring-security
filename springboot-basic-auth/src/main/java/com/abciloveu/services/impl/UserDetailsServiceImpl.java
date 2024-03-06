package com.abciloveu.services.impl;

import com.abciloveu.entites.AppUser;
import com.abciloveu.repositories.AppUserRepository;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final AppUserRepository repository;

    public UserDetailsServiceImpl(AppUserRepository repository) {
        this.repository = repository;
    }

    @Override
    public UserDetails loadUserByUsername(String s) throws UsernameNotFoundException {
        Optional<AppUser> user = repository.findByEmail(s);
        if (user.isPresent()) {
            AppUser appuser = user.get();
            return User.withUsername(appuser.getEmail()).password(appuser.getPassword()).authorities("USER").build();
        } else {
            throw new UsernameNotFoundException(String.format("Email[%s] not found", s));
        }
    }

}
