package com.tradelunch.tradelunch_auth;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;

@SpringBootApplication
//@EnableWebSecurity
//@EnableJpaRepositories("com.tradelunch.tradelunch_auth.repository")
//@EntityScan(value = "com.tradelunch.tradelunch_auth.model")
public class TradelunchAuthApplication implements CommandLineRunner {
    //	@Autowired
    //	public PropertyPrinter pp;

    public static void main(String[] args) {
        SpringApplication.run(TradelunchAuthApplication.class, args);
        System.out.println("-> Tradlunch Auth Application: Hello World! With Security");
    }

    @Override
    public void run(String... args) throws Exception {
        System.out.println("-> Command Line Runner: Hello World! With Security");
        // pp.printProperties();
    }
}
