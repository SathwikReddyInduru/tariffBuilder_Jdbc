//package com.xius.TariffBuilder.UserService;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import com.xius.TariffBuilder.Dao.TariffDao;
//import com.xius.TariffBuilder.Entity.TariffEntity;
//import com.xius.TariffBuilder.UserRepository.TariffRepository;
// 
//@Service
//public class TariffService {
//
//	@Autowired
//	private TariffRepository repository;
//
//	public List<TariffDao> getTariffPackages() {
//
//		List<Object[]> result = repository.getTariffPackagesStatic();
//
//		List<TariffDao> list = new ArrayList<>();
//
//		for (Object[] row : result) {
//
//			Long networkId = ((Number) row[0]).longValue();
//			String tariffName = (String) row[1];
//			Long tariffPackageId = ((Number) row[2]).longValue();
//			String status = (String) row[3];
//			System.out.println(networkId + "  " + tariffName + "  " + status);
//
//			list.add(new TariffDao(networkId, tariffName, status, tariffPackageId));
//		}
//
//		return list;
//	}
//
//	// fetch pending cards
//	public List<TariffEntity> getPendingTariffs() {
//		return repository.findByStatusIsNull();
//	}
//
//	public void updateStatus(Long tariffPackageId, String status) {
//		System.out.println("Updating status for Network ID: " + tariffPackageId + " to " + status);
//		TariffEntity tariff = repository.findById(tariffPackageId).orElseThrow();
//		System.out.println("Updating status for Network ID2: " + tariffPackageId + " to " + status);
//		tariff.setStatus(status);
//		repository.save(tariff);
//	}
//}package com.xius.TariffBuilder.UserService;
package com.xius.TariffBuilder.UserService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.xius.TariffBuilder.Entity.TariffEntity;

@Service
public class TariffService {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public List<TariffEntity> getTariffPackages() {

		String sql = "SELECT NETWORK_ID, TARIFF_PACKAGE_ID, TARIFF_PACKAGE_NAME, STATUS "
				+ "FROM CS_TARIFF_PACK_AP_REG_STATUS";

		return jdbcTemplate.query(sql, (rs, rowNum) -> {

			TariffEntity t = new TariffEntity();

			t.setNetworkId(rs.getLong("NETWORK_ID"));
			t.setTariffPackageId(rs.getLong("TARIFF_PACKAGE_ID"));
			t.setTariffPackageName(rs.getString("TARIFF_PACKAGE_NAME"));
			t.setStatus(rs.getString("STATUS"));

			return t;
		});
	}

	public List<TariffEntity> getPendingTariffs() {

		String sql = "SELECT NETWORK_ID, TARIFF_PACKAGE_ID, TARIFF_PACKAGE_NAME, STATUS "
				+ "FROM CS_TARIFF_PACK_AP_REG_STATUS " + "WHERE STATUS IS NULL OR STATUS='P'";

		return jdbcTemplate.query(sql, (rs, rowNum) -> {

			TariffEntity t = new TariffEntity();

			t.setNetworkId(rs.getLong("NETWORK_ID"));
			t.setTariffPackageId(rs.getLong("TARIFF_PACKAGE_ID"));
			t.setTariffPackageName(rs.getString("TARIFF_PACKAGE_NAME"));
			t.setStatus(rs.getString("STATUS"));

			return t;
		});
	}

	public void updateStatus(Long tariffPackageId, String status) {

		String sql = "UPDATE CS_TARIFF_PACK_AP_REG_STATUS " + "SET STATUS=? " + "WHERE TARIFF_PACKAGE_ID=?";

		jdbcTemplate.update(sql, status, tariffPackageId);
	}

}