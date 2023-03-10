package com.robocon321.demo.util;

import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import com.robocon321.demo.model.User;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.SignatureException;
import io.jsonwebtoken.UnsupportedJwtException;

@Component
public class JwtUtils {
	private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

	@Value("${com.robocon321.demo.jwtSecret}")
	private String jwtSecret;

	@Value("${com.robocon321.demo.jwtExpirationMs}")
	private int jwtExpirationsMs;

	public String generateTokenFromJwtToken(String username) {
		return Jwts.builder().setSubject(username).setIssuedAt(new Date())
				.setExpiration(new Date((new Date()).getTime() + jwtExpirationsMs))
				.signWith(SignatureAlgorithm.HS512, jwtSecret).compact();
	}

	public boolean validateJwtToken(String token) {
		try {
			Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
			return true;
		} catch (SignatureException e) {
			throw new SignatureException("Invalid JWT signature: " + e.getMessage());
		} catch (MalformedJwtException e) {
			throw new MalformedJwtException("Invalid JWT token: " + e.getMessage());
		} catch (ExpiredJwtException e) {
			throw new ExpiredJwtException(null, null, "JWT token is expired: " + e.getMessage());
		} catch (UnsupportedJwtException e) {
			throw new UnsupportedJwtException("JWT token is unsupported: " + e.getMessage());
		} catch (IllegalArgumentException e) {
			throw new IllegalArgumentException("JWT claims string is empty: " + e.getMessage());
		}
	}
}
