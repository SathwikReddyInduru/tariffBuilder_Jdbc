package com.xius.TariffBuilder.Dto;

import java.util.List;


public class PrivilegeApiResponse {
	
		
		private List<ModulePrivilegeResponse> modules;

		public List<ModulePrivilegeResponse> getModules() {
			return modules;
		}

		public void setModules(List<ModulePrivilegeResponse> modules) {
			this.modules = modules;
		}
}
