package com.abciloveu.model;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;

import com.abciloveu.validation.ValidPassword;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
public class ChangePasswordRequest implements Serializable {

	private static final long serialVersionUID = 1L;

	@NotBlank
	@ApiModelProperty(position = 1)
	private String oldPassword;

	@NotBlank
	@ValidPassword
	@ApiModelProperty(position = 2)
	private String password;

	public ChangePasswordRequest() {
		super();
	}

	public String getOldPassword() {
		return oldPassword;
	}

	public void setOldPassword(String oldPassword) {
		this.oldPassword = oldPassword;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
