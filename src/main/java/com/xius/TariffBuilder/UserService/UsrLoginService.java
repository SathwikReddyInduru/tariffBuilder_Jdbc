package com.xius.TariffBuilder.UserService;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xius.TariffBuilder.Dto.UserAuthDetails;
import com.xius.TariffBuilder.Dto.UsrLoginRequest;
import com.xius.TariffBuilder.Dto.UsrLoginResponse;
import com.xius.TariffBuilder.Dto.UsrModuleDTO;
import com.xius.TariffBuilder.Dto.UsrPrivilegeDTO;

@Service
public class UsrLoginService {

	private static final Logger logger = LoggerFactory.getLogger(UsrLoginService.class);

	@Autowired
	private UserAuthRepository repository;

	public UsrLoginResponse authenticate(UsrLoginRequest request) {

		UsrLoginResponse response = new UsrLoginResponse();
		Long networkId = repository.getNetworkId(request.getNetworkLoginName());
		String status = repository.getNetworkStatus(networkId);

		if (!"AC".equals(status)) {
			throw new RuntimeException("Inactive network");
		}

		Integer userCount = repository.countUser(request.getLoginId(), networkId);

		if (userCount == 0) {
			throw new RuntimeException("Wrong login id");
		}

		Integer passwordCount = repository.validatePassword(request.getLoginId(), networkId, request.getPassword());

		if (passwordCount == 0) {
			throw new RuntimeException("Wrong password");
		}

		UserAuthDetails user = repository.getUserDetails(request.getLoginId(), networkId);

		List<String> roles = repository.getUserRoles(request.getLoginId(), networkId);

		List<UsrPrivilegeDTO> privileges = repository.getPrivileges(request.getLoginId(), networkId);

		List<UsrModuleDTO> modules = repository.getModules(request.getLoginId(), networkId);

		// UsrConfigDTO config = repository.getSystemConfig(networkId);

		// repository.insertLoginHistory(request.getLoginId(),
		// request.getNetworkLoginName());
		// for chief all privileges should display

		if ("chief".equalsIgnoreCase(request.getLoginId())) {

			privileges = repository.getAllPrivileges();
			modules = repository.getAllModules();

		} else {

			privileges = repository.getPrivileges(request.getLoginId(), networkId);
			modules = repository.getModules(request.getLoginId(), networkId);
		}
		response.setStatus("SUCCESS");
		response.setMessage("Login Successful");
		response.setNetworkId(networkId);
		response.setManagerId(user.getManagerId());
		response.setForceChangePassword(user.getForceChangePwd());
		response.setRoles(roles);
		response.setPrivileges(privileges);
		response.setModules(modules);
		response.setLoginId(request.getLoginId());
		return response;
	}
}
