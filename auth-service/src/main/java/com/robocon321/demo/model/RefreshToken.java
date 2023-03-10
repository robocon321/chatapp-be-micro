package com.robocon321.demo.model;

import java.time.LocalDate;

import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Document("refreshToken")
@Data
public class RefreshToken {
	@NotBlank
	private String token;

	@NotNull
	@CreatedDate
	private LocalDate expireDate;
}
