package com.abciloveu.config.properties;

import java.time.Duration;
import java.util.UUID;

import org.springframework.boot.context.properties.ConfigurationProperties;

import io.jsonwebtoken.security.Keys;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "security.jwt.token")
public class JwtTokenProperties {

	private String issuer = "IT-GMS";
	
    private String secretKey = UUID.randomUUID().toString();
    
    private Duration expiration = Duration.ofMinutes(30L);
    
    private long allowedClockSkewInSecs = 60;

    
    private String algorithm = "HS256";
    
    public JwtTokenProperties() {
    	super();
	}

	public String getIssuer() {
		return issuer;
	}

	public void setIssuer(String issuer) {
		this.issuer = issuer;
	}

	public String getSecretKey() {
		return secretKey;
	}

	public void setSecretKey(String secretKey) {
		this.secretKey = secretKey;
	}

	public Duration getExpiration() {
		return expiration;
	}

	public void setExpiration(Duration expiration) {
		this.expiration = expiration;
	}
	
	public long getAllowedClockSkewInSecs() {
		return allowedClockSkewInSecs;
	}
	
	public void setAllowedClockSkewInSecs(long allowedClockSkewInSecs) {
		this.allowedClockSkewInSecs = allowedClockSkewInSecs;
	}
	
	public String getAlgorithm() {
		return algorithm;
	}
	
	public void setAlgorithm(String algorithm) {
		this.algorithm = algorithm;
	}

	@Override
	public String toString() {
		return  new StringBuilder()
				.append("JwtTokenProperties [issuer=").append(issuer)
				.append(", secretKey=********")
				.append(", expiration=").append(expiration)
				.append(", allowedClockSkewInSecs=").append(allowedClockSkewInSecs)
				.append(", algorithm=").append(algorithm)
				.append("]")
				.toString();
	}
    
    
}
