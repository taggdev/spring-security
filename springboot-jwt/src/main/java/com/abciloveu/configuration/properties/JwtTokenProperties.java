package com.abciloveu.configuration.properties;

import java.time.Duration;
import java.util.UUID;

import org.springframework.boot.context.properties.ConfigurationProperties;

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

	/**
	 * <code>
	 *   SecretKey key = Keys.secretKeyFor(SignatureAlgorithm.HS256); //or HS384 or HS512
	 * </code>
	 *   Then encode with Base64:
	 * <code>
	 *   String secretString = Encoders.BASE64.encode(key.getEncoded());
	 * </code>
	 *   

	 * @return The Base64-encoded string of secretKey with HMAC-SHA algorithms
	 */
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
		return "JwtTokenProperties [issuer=" + issuer +
                ", secretKey=********" +
                ", expiration=" + expiration +
                ", allowedClockSkewInSecs=" + allowedClockSkewInSecs +
                ", algorithm=" + algorithm +
                "]";
	}
    
    
}
