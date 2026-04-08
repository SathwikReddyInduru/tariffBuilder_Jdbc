package com.xius.TariffBuilder.Dto;

import java.util.List;


public class ModulePrivilegeResponse {
	 private String moduleName;
	 private List<PrivilegeResponse> privileges;
	
	 
	public List<PrivilegeResponse> getPrivileges() {
		return privileges;
	}
	public void setPrivileges(List<PrivilegeResponse> privileges) {
		this.privileges = privileges;
	}
	public String getModuleName() {
		return moduleName;
	}
	public void setModuleName(String moduleName) {
		this.moduleName = moduleName;
	}
}
