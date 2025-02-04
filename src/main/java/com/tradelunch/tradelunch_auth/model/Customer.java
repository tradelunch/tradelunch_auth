package com.tradelunch.tradelunch_auth.model;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;

@Entity
@Table(name = "customer")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Customer extends BaseEntity implements Serializable {

	@Serial
	private static final long serialVersionUID = 1L;

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(unique = true, nullable = false)
	private String username; // email

	@Column(name = "email", unique = true, nullable = false)
	private String email;

	private String pwd;
	private String nickname;

	@Column(name = "role")
	private String role;
	private boolean active;

}
