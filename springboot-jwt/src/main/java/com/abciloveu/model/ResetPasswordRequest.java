package com.abciloveu.model;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;

import com.abciloveu.validation.ValidPassword;

import io.swagger.annotations.ApiModel;

@ApiModel
public class ResetPasswordRequest implements Serializable {

	private static final long serialVersionUID = 1L;

	@NotBlank
	@ValidPassword
	private String password;

	public ResetPasswordRequest() {
		super();
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
