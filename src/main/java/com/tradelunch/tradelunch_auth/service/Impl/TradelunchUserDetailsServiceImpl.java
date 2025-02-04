package com.tradelunch.tradelunch_auth.service.Impl;

import com.tradelunch.tradelunch_auth.model.Customer;
import com.tradelunch.tradelunch_auth.repository.CustomerRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.text.MessageFormat;
import java.util.List;

@Service
public class TradelunchUserDetailsServiceImpl implements UserDetailsService {

	@Autowired
	private CustomerRepository customerRepository;

	/**
	 * Loads the user by username.
	 *
	 * @param username the username identifying the user whose data is required.
	 * @return a fully populated user record (never null)
	 * @throws UsernameNotFoundException if the user could not be found or the user has no GrantedAuthority
	 */
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Customer customer = customerRepository.findByEmail(username).orElseThrow(() -> {
			String msg = MessageFormat.format("Username not found for this user: {0}", username);
			return new UsernameNotFoundException(msg);
		});

		List<GrantedAuthority> authorities = List.of(new SimpleGrantedAuthority(customer.getRole()));

		return new User(customer.getUsername(), customer.getPwd(), authorities);
	}

}
