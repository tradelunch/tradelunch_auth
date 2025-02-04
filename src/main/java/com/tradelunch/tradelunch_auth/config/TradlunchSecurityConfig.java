package com.tradelunch.tradelunch_auth.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.password.CompromisedPasswordChecker;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.JdbcUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.password.HaveIBeenPwnedRestApiPasswordChecker;

import javax.sql.DataSource;
import java.util.logging.Logger;

@Configuration
public class TradlunchSecurityConfig {

	public Logger logger = Logger.getLogger(TradlunchSecurityConfig.class.getName());

	@Bean
	SecurityFilterChain defaultSecurityFilterChain(HttpSecurity http) throws Exception {
		System.out.println("-> defaultSecurityFilterChain");

		http.authorizeHttpRequests((requests) -> {
//			((AuthorizeHttpRequestsConfigurer.AuthorizedUrl) requests.anyRequest()).authenticated();
			requests
					.requestMatchers("/ping", "/*/ping").permitAll()
					.requestMatchers("/user/register").permitAll()
					.requestMatchers("/my").authenticated()
					.anyRequest().authenticated();
//					.anyRequest().permitAll();
		});

//  	Form login with pre-built login page
		http.formLogin(Customizer.withDefaults());
//		http.formLogin(flc -> flc.disable());
//		http.formLogin(AbstractHttpConfigurer::disable);

//		Http Basic log with alert window
		http.httpBasic(Customizer.withDefaults());
//		http.httpBasic(hbc -> hbc.disable());
//		http.httpBasic(AbstractHttpConfigurer::disable);

		return (SecurityFilterChain) http.build();
	}


//  use TradeLunchUserDetailsServiceImpl -> Component using Repository
//	@Bean
//	public UserDetailsService userDetailsService(DataSource dataSource) {
//		this.logger.info("-> userDetailsService");
//
////		return new InMemoryUserDetailsManager(user, admin);
////		return new JdbcUserDetailsManager(dataSource);
//	}

	@Bean
	public PasswordEncoder passwordEncoder() {
		return PasswordEncoderFactories.createDelegatingPasswordEncoder();
	}

	@Bean
	public CompromisedPasswordChecker compromisedPasswordChecker() {
		return new HaveIBeenPwnedRestApiPasswordChecker();
	}

}
