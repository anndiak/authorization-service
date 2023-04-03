package com.authorization_service.service;

import com.authorization_service.Entity.Role;
import com.authorization_service.Entity.User;
import com.authorization_service.Entity.UserRoles;
import com.authorization_service.repository.interfaces.UserRepository;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.Collection;
import java.util.Collections;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    private UserRepository userRepository;

    public CustomUserDetailsService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Transactional
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        User user = userRepository.findByEmail(email);

        if (user != null) {
            return new org.springframework.security.core.userdetails.User(
                    user.getEmail(),
                    user.getPassword(),
                    getAuthorities(user.getRole()));
        }else{
            throw new UsernameNotFoundException("Invalid username or password.");
        }
    }

    public Collection<? extends GrantedAuthority> getAuthorities(Role role) {
        SimpleGrantedAuthority authority =
                new SimpleGrantedAuthority(role.getRole());
        return Collections.singletonList(authority);
    }
}
