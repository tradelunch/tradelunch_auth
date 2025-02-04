package com.tradelunch.tradelunch_auth.service;

import com.tradelunch.tradelunch_auth.model.Customer;

public abstract class UserService {
//	public abstract String login(String username, String password);
//	public abstract String logout();
//	public abstract Customer status();

	public abstract Customer registerUser(Customer customer);
}
