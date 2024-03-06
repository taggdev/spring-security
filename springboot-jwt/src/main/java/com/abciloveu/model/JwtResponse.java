package com.abciloveu.model;

import java.io.Serializable;
import java.util.Date;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonFormat.Shape;

public class JwtResponse implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private String type = "Bearer";

	private String accessToken;
	
	@JsonFormat(shape = Shape.NUMBER)
	private Date expiration;
	
	public JwtResponse() {
		super();
	}
	
	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}

	public String getTokenType() {
		return type;
	}

	public void setTokenType(String tokenType) {
		this.type = tokenType;
	}

	public Date getExpiration() {
		return expiration;
	}
	
	public void setExpiration(Date expiration) {
		this.expiration = expiration;
	}

}