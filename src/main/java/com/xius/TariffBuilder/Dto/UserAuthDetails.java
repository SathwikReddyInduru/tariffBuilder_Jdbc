package com.xius.TariffBuilder.Dto;


import java.time.LocalDateTime;


public class UserAuthDetails {
	private String managerId;
	private String forceChangePwd;
	private LocalDateTime validityDate;
	private Integer loginAttempts;
	private String statusCode;


	public String getManagerId() {
		return managerId;
	}

	public void setManagerId(String managerId) {
		this.managerId = managerId;
	}

	public String getForceChangePwd() {
		return forceChangePwd;
	}

	public void setForceChangePwd(String forceChangePwd) {
		this.forceChangePwd = forceChangePwd;
	}

	public LocalDateTime getValidityDate() {
		return validityDate;
	}

	public void setValidityDate(LocalDateTime validityDate) {
		this.validityDate = validityDate;
	}

	public Integer getLoginAttempts() {
		return loginAttempts;
	}

	public void setLoginAttempts(Integer loginAttempts) {
		this.loginAttempts = loginAttempts;
	}

	public String getStatusCode() {
		return statusCode;
	}

	public void setStatusCode(String statusCode) {
		this.statusCode = statusCode;
	}

}
