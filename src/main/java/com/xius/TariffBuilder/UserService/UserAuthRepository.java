package com.xius.TariffBuilder.UserService;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import com.xius.TariffBuilder.Dto.UserAuthDetails;
import com.xius.TariffBuilder.Dto.UsrModuleDTO;
import com.xius.TariffBuilder.Dto.UsrPrivilegeDTO;


@Repository
public class UserAuthRepository {
	private static final Logger logger = LoggerFactory.getLogger(UserAuthRepository.class);

	@Autowired
	
	private JdbcTemplate JdbcTemplate;

	// GET NETWORK ID FROM NETWORK DISPLAY NAME
	public Long getNetworkId(String networkDisplay) {

		String sql = """
				SELECT network_id
				FROM glb_mt_network
				WHERE UPPER(network_display) = UPPER(?)
				""";

		try {

			Long networkId = JdbcTemplate.queryForObject(sql, Long.class, networkDisplay);
			
			logger.info("Fetched getNetworkId:::::> network_id={} for network_display={}", networkId, networkDisplay);

	        return networkId;

		} catch (EmptyResultDataAccessException ex) {

			logger.error("Invalid network login name {}", networkDisplay);

			throw new RuntimeException("Invalid login name");
		}
	}

	// CHECK NETWORK STATUS
	public String getNetworkStatus(Long networkId) {

		String sql = """
				SELECT status_code
				FROM glb_mt_network
				WHERE network_id = ?
				""";

		try {

			logger.debug("Fetching getNetworkStatus:::> status_code for network_id={}", networkId);

			String statusCode = JdbcTemplate.queryForObject(sql, String.class, networkId);

			logger.info("Fetched getNetworkStatus:::> status_code={} for network_id={}", statusCode, networkId);

			return statusCode;

		} catch (EmptyResultDataAccessException ex) {

			logger.error("No network found for network_id={}", networkId, ex);

			throw new RuntimeException("Network not found for network_id: " + networkId);
		}
	}

	// CHECK USER EXISTS
	public Integer countUser(String loginId, Long networkId) {

	    String sql = """
	            SELECT COUNT(1)
	            FROM ums_mt_user
	            WHERE UPPER(login_id)=UPPER(?)
	            AND network_id = ?
	            """;

	    try {

	        logger.debug("Counting_users:::> for loginId={} and networkId={}", loginId, networkId);

	        Integer count = JdbcTemplate.queryForObject(sql, Integer.class, loginId, networkId);

	        logger.info("Count_users:::> fetched for loginId={} and networkId={} : count={}", loginId, networkId, count);

	        return count;

	    } catch (Exception ex) {

	        logger.error("Error while counting users for loginId={} and networkId={}", loginId, networkId, ex);

	        throw new RuntimeException("Error while validating user existence");
	    }
	}

	// VALIDATE PASSWORD
	public Integer validatePassword(String loginId, Long networkId, String password) {

		try {

			logger.debug("Validating password:::> for loginId={} and networkId={}", loginId, networkId);

			String encryptedPassword = generateSha1Hash(password);

			logger.debug("Generated encrypted password for loginId={}", loginId);

			String sql = """
					SELECT COUNT(1)
					FROM ums_mt_user
					WHERE UPPER(login_id) = UPPER(?)
					AND network_id = ?
					AND password_name = ?
					""";

			Integer count = JdbcTemplate.queryForObject(sql, Integer.class, loginId, networkId,
					encryptedPassword);

			logger.info("Password validation::::> result for loginId={} and networkId={} : count={}", loginId, networkId,
					count);

			return count;

		} catch (Exception ex) {

			logger.error("Error validating password for loginId={} and networkId={}", loginId, networkId, ex);

			throw new RuntimeException("Error validating password");
		}
	}

	// GET USER DETAILS
	public UserAuthDetails getUserDetails(String loginId, Long networkId) {

		String sql = """
				SELECT manager_id,
				       force_chang_pwd_yn,
				       validity_date,
				       consecutive_login_attempts,
				       failed_login_date,
				       status_code
				FROM ums_mt_user
				WHERE UPPER(login_id)=UPPER(?)
				AND network_id = ?
				""";

		try {

			logger.debug("Fetching getUserDetails:::> for loginId={} and networkId={}", loginId, networkId);

			UserAuthDetails user = JdbcTemplate.queryForObject(sql, (rs, rowNum) -> {

				UserAuthDetails userDetails = new UserAuthDetails();

				userDetails.setManagerId(rs.getString("manager_id"));
				userDetails.setForceChangePwd(rs.getString("force_chang_pwd_yn"));
				userDetails.setValidityDate(rs.getTimestamp("validity_date").toLocalDateTime());
				userDetails.setLoginAttempts(rs.getInt("consecutive_login_attempts"));
				userDetails.setStatusCode(rs.getString("status_code"));

				return userDetails;

			}, loginId, networkId);

			logger.info("Fetched getUserDetails:::> for loginId={} networkId={} managerId={} statusCode={}", loginId,
					networkId, user.getManagerId(), user.getStatusCode());

			return user;

		} catch (EmptyResultDataAccessException ex) {

			logger.error("No user found for loginId={} and networkId={}", loginId, networkId, ex);

			throw new RuntimeException("User not found");

		} catch (Exception ex) {

			logger.error("Error fetching user details for loginId={} and networkId={}", loginId, networkId, ex);

			throw new RuntimeException("Error fetching user details");
		}
	}

	// GET USER ROLES
	public List<String> getUserRoles(String loginId, Long networkId) {

	    String sql = """
	            SELECT role_id
	            FROM ums_tt_user_roles
	            WHERE network_id = ?
	            AND UPPER(login_id)=UPPER(?)
	            """;

	    try {

	        logger.debug("Fetching getUserRoles:::> for loginId={} and networkId={}", loginId, networkId);

	        List<String> roles = JdbcTemplate.query(
	                sql,
	                (rs, rowNum) -> rs.getString("role_id"),
	                networkId,
	                loginId
	        );

	        logger.info("Fetched getUserRoles:::> {} role(s) for loginId={} and networkId={}",
	                roles.size(), loginId, networkId);

	        return roles;

	    } catch (Exception ex) {

	        logger.error("Error fetching roles for loginId={} and networkId={}", loginId, networkId, ex);

	        throw new RuntimeException("Error fetching user roles");
	    }
	}

	// GET USER PRIVILEGES
	public List<UsrPrivilegeDTO> getPrivileges(String loginId, Long networkId) {

		String sql = """
				SELECT DISTINCT a.privilege_id,
				       a.privilege_name,
				       a.privilege_desc,
				       a.module_id
				FROM ums_mt_privilege a,
				     ums_tt_roleprofile c,
				     ums_tt_user_roles d
				WHERE a.privilege_id = c.privilege_id
				AND c.role_id = d.role_id
				AND d.network_id = ?
				AND UPPER(d.login_id)=UPPER(?)
				""";

		try {

			logger.debug("Fetching getPrivileges:::> for loginId={} and networkId={}", loginId, networkId);

			List<UsrPrivilegeDTO> privileges = JdbcTemplate.query(sql, (rs, rowNum) -> {

				UsrPrivilegeDTO dto = new UsrPrivilegeDTO();

				dto.setPrivilegeId(rs.getString("privilege_id"));
				dto.setPrivilegeName(rs.getString("privilege_name"));
				dto.setPrivilegeDesc(rs.getString("privilege_desc"));
				dto.setModuleId(rs.getString("module_id"));

				return dto;

			}, networkId, loginId);

			logger.info("Fetched getPrivileges:::> {} privilege(s) for loginId={} and networkId={}", privileges.size(), loginId,
					networkId);

			return privileges;

		} catch (Exception ex) {

			logger.error("Error fetching privileges for loginId={} and networkId={}", loginId, networkId, ex);

			throw new RuntimeException("Error fetching user privileges");
		}
	}

	// GET MODULES
	public List<UsrModuleDTO> getModules(String loginId, Long networkId) {

		String sql = """
				SELECT DISTINCT a.module_id,
				       a.module_name,
				       a.module_desc
				FROM ums_mt_module a,
				     ums_mt_privilege b,
				     ums_tt_roleprofile c,
				     ums_tt_user_roles d
				WHERE a.module_id = b.module_id
				AND b.privilege_id = c.privilege_id
				AND c.role_id = d.role_id
				AND d.network_id = ?
				AND UPPER(d.login_id)=UPPER(?)
				""";

		try {

			logger.debug("Fetching modules for loginId={} and networkId={}", loginId, networkId);

			List<UsrModuleDTO> modules = JdbcTemplate.query(sql, (rs, rowNum) -> {

				UsrModuleDTO dto = new UsrModuleDTO();

				dto.setModuleId(rs.getString("module_id"));
				dto.setModuleName(rs.getString("module_name"));
				dto.setModuleDesc(rs.getString("module_desc"));

				return dto;

			}, networkId, loginId);

			logger.info("Fetched getModules :::> {} module(s) for loginId={} and networkId={}", modules.size(), loginId, networkId);

			return modules;

		} catch (Exception ex) {

			logger.error("Error fetching modules for loginId={} and networkId={}", loginId, networkId, ex);

			throw new RuntimeException("Error fetching user modules");
		}
	}


	// UPDATE LOGIN ATTEMPTS
	public void updateLoginAttempts(String loginId, Long networkId, int attempts) {

		String sql = """
				UPDATE ums_mt_user
				SET consecutive_login_attempts = ?
				WHERE network_id = ?
				AND UPPER(login_id)=UPPER(?)
				""";

		try {

			logger.debug("UpdateLoginAttempts::> for loginId={} and networkId={} with attempts={}", loginId, networkId,
					attempts);

			int rowsUpdated = JdbcTemplate.update(sql, attempts, networkId, loginId);

			logger.info("UpdateLoginAttempts::> for loginId={} and networkId={}, rowsUpdated={}", loginId, networkId,
					rowsUpdated);

		} catch (Exception ex) {

			logger.error("Error updating login attempts for loginId={} and networkId={}", loginId, networkId, ex);

			throw new RuntimeException("Error updating login attempts");
		}
	}


	//get All privilieges
	public List<UsrPrivilegeDTO> getAllPrivileges() {

	    String sql = """
	        SELECT privilege_id, privilege_name, privilege_desc, module_id
	        FROM ums_mt_privilege

	        
	    """;

	    return JdbcTemplate.query(sql, (rs, rowNum) -> {

	        UsrPrivilegeDTO dto = new UsrPrivilegeDTO();
	        dto.setPrivilegeId(rs.getString("privilege_id"));
	        dto.setPrivilegeName(rs.getString("privilege_name"));
	        dto.setPrivilegeDesc(rs.getString("privilege_desc"));
	        dto.setModuleId(rs.getString("module_id"));

	        return dto;

	    });
	}
	public List<UsrModuleDTO> getAllModules() {

	    String sql = """
	        SELECT module_id, module_name
	        FROM ums_mt_module
	        
	    """;

	    return JdbcTemplate.query(sql, (rs, rowNum) -> {

	        UsrModuleDTO dto = new UsrModuleDTO();
	        dto.setModuleId(rs.getString("module_id"));
	        dto.setModuleName(rs.getString("module_name"));

	        return dto;

	    });
	}
	
	 public static String generateSha1Hash(String input) {
	        try {
	            MessageDigest md = MessageDigest.getInstance("SHA-1");
	            byte[] hashBytes = md.digest(input.getBytes(StandardCharsets.UTF_8));

	            StringBuilder hex = new StringBuilder();
	            for (byte b : hashBytes) {
	                hex.append(String.format("%02X", b)); // uppercase hex
	            }
	            return hex.toString();

	        } catch (Exception e) {
	            logger.error("Error generating SHA-1 hash", e);
	            throw new RuntimeException("Error generating SHA-1 hash", e);
	        }
	    }
	
}
	

