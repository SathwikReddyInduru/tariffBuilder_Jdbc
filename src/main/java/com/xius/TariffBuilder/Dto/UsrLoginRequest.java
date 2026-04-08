package com.xius.TariffBuilder.Dto;

import jakarta.validation.constraints.NotBlank;

public class UsrLoginRequest {

	@NotBlank(message = "Login ID is required")
	private String loginId;

	@NotBlank(message = "Network Login Name is required")
	private String networkLoginName;

	@NotBlank(message = "Password is required")
	private String password;

	public String getLoginId() {
		return loginId;
	}

	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}

	public String getNetworkLoginName() {
		return networkLoginName;
	}

	public void setNetworkLoginName(String networkLoginName) {
		this.networkLoginName = networkLoginName;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

}
