package com.cogent.authservice.security;

import com.cogent.authservice.responseDTO.UserResponseDTO;
import com.cogent.authservice.feignInterface.UserInterface;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Component
@Slf4j
public class CustomUserDetailsService implements UserDetailsService {

    private UserInterface userInterface;

    public CustomUserDetailsService(UserInterface adminInterface) {
        this.userInterface = adminInterface;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        UserResponseDTO userResponseDTO = userInterface.fetchUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username:" + username + " not found"));
        System.out.println(userResponseDTO.getUsername());
        List<String> roles = new ArrayList<>();
        roles.add(userResponseDTO.getRoles());
        userResponseDTO.setRolesList(roles);
        System.out.println("[viki]roles: "+ roles);
        List<GrantedAuthority> grantedAuthorities = userResponseDTO.getRolesList()
                .stream().map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());


        return new User(username,
                userResponseDTO.getPassword(), true, true, true,
                true, grantedAuthorities);
    }
}
