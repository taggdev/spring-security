package com.abciloveu.admin.security;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import javax.crypto.SecretKey;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.codec.Hex;

import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.io.Encoders;
import io.jsonwebtoken.security.Keys;

class SecretKeysGenerator {
	
	private static final Logger LOG = LoggerFactory.getLogger(SecretKeysGenerator.class);

	@BeforeEach
	void setUp() throws Exception {
	}

	@AfterEach
	void tearDown() throws Exception {
	}

	/**
	 * If you want to generate a sufficiently strong SecretKey for use with the JWT HMAC-SHA algorithms, 
	 * use the Keys.secretKeyFor(SignatureAlgorithm) 
	 * 
	 * If you need to save this new SecretKey, you can Base64 (or Base64URL) encode it
	 * 
	 * 
	 */
	@Test
	void creatingSafeKeys() {
		
		final SignatureAlgorithm algorithm = SignatureAlgorithm.HS512; //or HS384 or HS512
		assertTrue(algorithm.isHmac());
		
		final SecretKey safeKeys = Keys.secretKeyFor(algorithm); 
		
		String safeKeysHex = new String(Hex.encode(safeKeys.getEncoded()));
		LOG.info("Hex secret: {}", safeKeysHex);
		
		String secretString = Encoders.BASE64.encode(safeKeys.getEncoded());
		LOG.info("Base64 Secret: {}", secretString);
		
		SecretKey restoreKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(secretString));
		assertEquals(restoreKey.getAlgorithm(), algorithm.getJcaName());
		

	}

}
