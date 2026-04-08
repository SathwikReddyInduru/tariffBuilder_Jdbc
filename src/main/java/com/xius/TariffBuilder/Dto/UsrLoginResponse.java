package com.xius.TariffBuilder.Dto;

import java.util.List;

public class UsrLoginResponse {

	private String status;
	private String message;
	private Long networkId;
	private String managerId;
	private String forceChangePassword;
	private String currency;
	private String voucherTopupAllowed;
	private String passwordExpiryWarning;
	private Integer passwordExpiryDays;
	private List<String> roles;
	private List<UsrPrivilegeDTO> privileges;
	private List<UsrModuleDTO> modules;
	private String loginId;


	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	public String getMessage() {
		return message;
	}

	public void setMessage(String message) {
		this.message = message;
	}

	public Long getNetworkId() {
		return networkId;
	}

	public void setNetworkId(Long networkId) {
		this.networkId = networkId;
	}

	public String getManagerId() {
		return managerId;
	}

	public void setManagerId(String managerId) {
		this.managerId = managerId;
	}

	public String getForceChangePassword() {
		return forceChangePassword;
	}

	public void setForceChangePassword(String forceChangePassword) {
		this.forceChangePassword = forceChangePassword;
	}

	public String getCurrency() {
		return currency;
	}

	public void setCurrency(String currency) {
		this.currency = currency;
	}

	public String getVoucherTopupAllowed() {
		return voucherTopupAllowed;
	}

	public void setVoucherTopupAllowed(String voucherTopupAllowed) {
		this.voucherTopupAllowed = voucherTopupAllowed;
	}

	public String getPasswordExpiryWarning() {
		return passwordExpiryWarning;
	}

	public void setPasswordExpiryWarning(String passwordExpiryWarning) {
		this.passwordExpiryWarning = passwordExpiryWarning;
	}

	public Integer getPasswordExpiryDays() {
		return passwordExpiryDays;
	}

	public void setPasswordExpiryDays(Integer passwordExpiryDays) {
		this.passwordExpiryDays = passwordExpiryDays;
	}

	public List<String> getRoles() {
		return roles;
	}

	public void setRoles(List<String> roles) {
		this.roles = roles;
	}

	public List<UsrPrivilegeDTO> getPrivileges() {
		return privileges;
	}

	public void setPrivileges(List<UsrPrivilegeDTO> privileges) {
		this.privileges = privileges;
	}

	public List<UsrModuleDTO> getModules() {
		return modules;
	}

	public void setModules(List<UsrModuleDTO> modules) {
		this.modules = modules;
	}
	
	public String getLoginId() {
		return loginId;
	}

	public void setLoginId(String loginId) {
		this.loginId = loginId;
	}

}

