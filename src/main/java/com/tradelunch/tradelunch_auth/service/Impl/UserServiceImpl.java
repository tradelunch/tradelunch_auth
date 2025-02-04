package com.tradelunch.tradelunch_auth.service.Impl;

import com.tradelunch.tradelunch_auth.model.Customer;
import com.tradelunch.tradelunch_auth.repository.CustomerRepository;
import com.tradelunch.tradelunch_auth.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserServiceImpl extends UserService {

	//	@Autowired // do not need anymore
	private final CustomerRepository customerRepository;
	private final PasswordEncoder encoder;

	@Override
	public Customer registerUser(Customer customer) {

		String hashed = encoder.encode(customer.getPwd());
		customer.setPwd(hashed);
		Customer ret = customerRepository.save(customer);
		return ret;
	}
}
