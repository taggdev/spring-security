package com.abciloveu.model;

import javax.validation.constraints.NotEmpty;

import com.fasterxml.jackson.annotation.JsonPropertyOrder;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
@JsonPropertyOrder(value = {"username", "password"})
public class LoginRequest {

	@NotEmpty
	@ApiModelProperty(position = 1)
    private String username;
	
	@NotEmpty
	@ApiModelProperty(position = 2)
    private String password;

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
