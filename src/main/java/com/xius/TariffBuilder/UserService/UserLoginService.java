package com.xius.TariffBuilder.UserService;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xius.TariffBuilder.Dto.LoginRequestDto;
import com.xius.TariffBuilder.Dto.UsrPrivilegeDTO;
import com.xius.TariffBuilder.UserService.UserAuthRepository.LoginRepository;

@Service
public class UserLoginService {

        @Autowired
        private LoginRepository repository;

        public Map<String, Object> authenticate(
                        LoginRequestDto request) {

                List<UsrPrivilegeDTO> privileges =

                                repository.getUserPrivileges(

                                                request.getNetworkLoginName(),

                                                request.getLoginId(),

                                                request.getPassword());

                if (privileges.isEmpty()) {

                        throw new RuntimeException(
                                        "Invalid User Credentials");
                }

                Long networkId = repository.getNetworkId(
                                request.getNetworkLoginName());

                Map<String, Object> result = new HashMap<>();

                result.put(
                                "networkId",
                                networkId);

                result.put(
                                "privileges",
                                privileges);

                return result;
        }
}