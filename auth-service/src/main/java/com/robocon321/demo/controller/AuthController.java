package com.robocon321.demo.controller;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.robocon321.demo.dto.request.LoginRequest;
import com.robocon321.demo.dto.request.RegisterRequest;
import com.robocon321.demo.dto.response.JwtResponse;
import com.robocon321.demo.model.ERole;
import com.robocon321.demo.model.RefreshToken;
import com.robocon321.demo.repository.UserRepository;
import com.robocon321.demo.service.AuthService;
import com.robocon321.demo.service.RefreshTokenService;
import com.robocon321.demo.service.impl.UserDetailsImpl;
import com.robocon321.demo.util.JwtUtils;

import jakarta.validation.Valid;

@RestController
@RequestMapping("api/auth")
public class AuthController {		
	@Autowired
	private AuthService authService;

	@Autowired
	private AuthenticationManager authenticationManager;
	
	@Autowired
	private JwtUtils jwtUtils;
	
	@Autowired
	private RefreshTokenService refreshTokenService;
	
	@Autowired
	private UserRepository userRepository;
				
	@PostMapping("sign-in")
	public ResponseEntity authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
		Authentication authentication = authenticationManager.authenticate(
			new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
		);
		SecurityContextHolder.getContext().setAuthentication(authentication);
		UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
		String jwt = jwtUtils.generateTokenFromJwtToken(userDetails.getUsername());
		List<String> roles = userDetails.getAuthorities().stream().map(item -> item.getAuthority()).collect(Collectors.toList());
		RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getUser());
	    return ResponseEntity.ok(new JwtResponse(userDetails.getUser().getId(), jwt, refreshToken.getToken(), roles, userDetails.getUsername()));
	}

	@PostMapping("sign-up")	
	public void register(@Valid @RequestBody RegisterRequest registerRequest) {
		authService.save(registerRequest, ERole.CLIENT);
	}
	
	@PostMapping("refreshAccessToken")
	public String refreshAccessToken(@RequestBody String refreshToken) {
		return refreshTokenService.refreshAccessToken(refreshToken);
	}

}