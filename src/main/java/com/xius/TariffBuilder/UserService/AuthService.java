//package com.xius.TariffBuilder.UserService;
//
//import java.security.MessageDigest;
//import java.util.Optional;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import com.xius.TariffBuilder.Entity.User;
//import com.xius.TariffBuilder.UserRepository.NetworkRepository;
//import com.xius.TariffBuilder.UserRepository.UserRepository;
//
//@Service
//public class AuthService {
//
//    @Autowired
//    private UserRepository userRepository;
//    
//    @Autowired
//    private NetworkRepository networkRepository;
//
//    public boolean loginUser(String username, String password, String networkName) {
//
//        String encryptedPassword = encryptPassword(password);
//
//        return userRepository.findByLoginIdAndPasswordAndNetworkDisplay(username, encryptedPassword, networkName)
//                .isPresent();
//    }
//
//    // CHECK: Network exists
//    public boolean isValidNetwork(String networkName) {
//        return userRepository.existsByNetwork_NetworkDisplay(networkName);
//    }
//
//    // CHECK: Username exists under network
//    public boolean isValidUsername(String username, String networkName) {
//        return userRepository.existsByLoginIdAndNetwork_NetworkDisplay(username, networkName);
//    }
//
//    // Password correct for that user
//    public boolean isValidPassword(String username, String password, String networkName) {
//
//        String encryptedPassword = encryptPassword(password);
//
//        Optional<User> userOpt = userRepository.findByLoginIdAndNetwork_NetworkDisplay(username, networkName);
//
//        if (userOpt.isPresent()) {
//            return userOpt.get().getPassword().equals(encryptedPassword);
//        }
//
//        return false;
//    }
//
//    // PASSWORD ENCRYPTION (SHA-1)
//    public String encryptPassword(String password) {
//        try {
//            MessageDigest md = MessageDigest.getInstance("SHA-1");
//            byte[] result = md.digest(password.getBytes());
//
//            StringBuilder sb = new StringBuilder();
//            for (byte b : result) {
//                sb.append(String.format("%02X", b));
//            }
//
//            return sb.toString();
//        } catch (Exception e) {
//            throw new RuntimeException(e);
//        }
//    }
//
//    // added
//    public User getUser(String username, String password, String network) {
//
//        String encryptedPassword = encryptPassword(password);
//
//        return userRepository
//                .findByLoginIdAndPasswordAndNetworkDisplay(username, encryptedPassword, network)
//                .orElse(null);
//    }
//   
//    
//}
package com.xius.TariffBuilder.UserService;

import java.security.MessageDigest;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.xius.TariffBuilder.Entity.User;

@Service
public class AuthService {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	// LOGIN CHECK
	public boolean loginUser(String username, String password, String networkName) {

		String encryptedPassword = encryptPassword(password);

		String sql =

				"SELECT COUNT(*) " +

						"FROM UMS_MT_USER u " +

						"JOIN GLB_MT_NETWORK n " +

						"ON u.NETWORK_ID = n.NETWORK_ID " +

						"WHERE u.LOGIN_ID=? " +

						"AND u.PASSWORD_NAME=? " +

						"AND n.NETWORK_DISPLAY=?";

		Integer count = jdbcTemplate.queryForObject(

				sql, Integer.class, username, encryptedPassword, networkName);

		return count != null && count > 0;
	}

	// CHECK NETWORK EXISTS
	public boolean isValidNetwork(String networkName) {

		String sql =

				"SELECT COUNT(*) " +

						"FROM GLB_MT_NETWORK " +

						"WHERE NETWORK_DISPLAY=?";

		Integer count = jdbcTemplate.queryForObject(

				sql, Integer.class, networkName);

		return count != null && count > 0;
	}

	// CHECK USERNAME EXISTS IN NETWORK
	public boolean isValidUsername(String username, String networkName) {

		String sql =

				"SELECT COUNT(*) " +

						"FROM UMS_MT_USER u " +

						"JOIN GLB_MT_NETWORK n " +

						"ON u.NETWORK_ID = n.NETWORK_ID " +

						"WHERE u.LOGIN_ID=? " +

						"AND n.NETWORK_DISPLAY=?";

		Integer count = jdbcTemplate.queryForObject(

				sql, Integer.class, username, networkName);

		return count != null && count > 0;
	}

	// CHECK PASSWORD VALID
	public boolean isValidPassword(String username, String password, String networkName) {

		String encryptedPassword = encryptPassword(password);

		String sql =

				"SELECT COUNT(*) " +

						"FROM UMS_MT_USER u " +

						"JOIN GLB_MT_NETWORK n " +

						"ON u.NETWORK_ID = n.NETWORK_ID " +

						"WHERE u.LOGIN_ID=? " +

						"AND u.PASSWORD_NAME=? " +

						"AND n.NETWORK_DISPLAY=?";

		Integer count = jdbcTemplate.queryForObject(

				sql, Integer.class, username, encryptedPassword, networkName);

		return count != null && count > 0;
	}

	// GET USER DETAILS
	public User getUser(String username, String password, String networkName) {

		String encryptedPassword = encryptPassword(password);

		String sql =

				"SELECT u.LOGIN_ID, " + "u.PASSWORD_NAME, " + "u.NETWORK_ID " +

						"FROM UMS_MT_USER u " +

						"JOIN GLB_MT_NETWORK n " +

						"ON u.NETWORK_ID = n.NETWORK_ID " +

						"WHERE u.LOGIN_ID=? " +

						"AND u.PASSWORD_NAME=? " +

						"AND n.NETWORK_DISPLAY=?";

		List<User> list = jdbcTemplate.query(

				sql,

				(rs, rowNum) -> {

					User user = new User();

					user.setLoginId(rs.getString("LOGIN_ID"));

					user.setPassword(rs.getString("PASSWORD_NAME"));

					user.setNetworkId(rs.getLong("NETWORK_ID"));

					return user;
				},

				username, encryptedPassword, networkName);

		return list.isEmpty() ? null : list.get(0);
	}

	// SHA-1 PASSWORD ENCRYPTION
	private String encryptPassword(String password) {

		try {

			MessageDigest md = MessageDigest.getInstance("SHA-1");

			byte[] result = md.digest(password.getBytes());

			StringBuilder sb = new StringBuilder();

			for (byte b : result) {

				sb.append(

						String.format("%02X", b)

				);
			}

			return sb.toString();

		} catch (Exception e) {

			throw new RuntimeException(e);
		}
	}

}
