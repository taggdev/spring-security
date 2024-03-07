package com.abciloveu.model;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Pattern.Flag;
import javax.validation.constraints.Size;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel
public class UpdateProfileRequest implements Serializable {

	private static final long serialVersionUID = 1L;

	@NotBlank
	@Size(min = 2, max = 50)
	@ApiModelProperty(position = 1)
	@Pattern(regexp = "^[a-z0-9._-]{2,20}$", flags = Flag.UNICODE_CASE)
	private String username;

    @NotBlank
	@Size(max = 120)
    private String displayName;
    
    @NotBlank
	@Size(max = 200)
    private String contactName;
    
    @NotBlank
	@Size(max = 50)
    private String contactTel;

   
    public UpdateProfileRequest() {
		super();
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	public String getContactName() {
		return contactName;
	}

	public void setContactName(String contactName) {
		this.contactName = contactName;
	}

	public String getContactTel() {
		return contactTel;
	}

	public void setContactTel(String contactTel) {
		this.contactTel = contactTel;
	}

}
