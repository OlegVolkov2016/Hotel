package com.axiomsl.hotel.service;

import com.axiomsl.hotel.model.Guest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by Oleg Volkov (AxiomSL) on 12.06.2016.
 *
 * UserDetails Security Service implementation
 */
public class UserDetailsServiceImpl implements UserDetailsService {
    @Autowired
    GuestService guestService;

    @Override
    public UserDetails loadUserByUsername(String login) throws UsernameNotFoundException {
        Guest guest = guestService.findByLogin(login);
        Set<GrantedAuthority> roles = new HashSet<GrantedAuthority>();
        roles.add(new SimpleGrantedAuthority(guest.getRole().name()));
        UserDetails userDetails = new org.springframework.security.core.userdetails.User(guest.getLogin(),
                guest.getPassword(), roles);
        return userDetails;
    }
}
