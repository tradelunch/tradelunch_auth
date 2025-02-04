package com.tradelunch.tradelunch_auth.model;

import jakarta.annotation.Nullable;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@MappedSuperclass
@Getter @Setter
public class BaseEntity {
	private LocalDateTime createdAt;

	private LocalDateTime updatedAt;
	private String updatedBy;

	@Nullable
	private LocalDateTime deletedAt;

	@PrePersist
	protected void onCreate() {
		createdAt = LocalDateTime.now();
	}

	@PreUpdate
	protected void onUpdate() {
		updatedAt = LocalDateTime.now();
	}

	@PreRemove
	protected void onDelete() {
		deletedAt = LocalDateTime.now();
	}

}
