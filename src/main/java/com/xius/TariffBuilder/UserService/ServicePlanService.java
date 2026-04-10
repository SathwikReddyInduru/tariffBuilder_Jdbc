package com.xius.TariffBuilder.UserService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.xius.TariffBuilder.Entity.ServicePlanPackMap;

@Service
public class ServicePlanService {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public List<ServicePlanPackMap> getPlans(Long networkId, String types) {

		String sql = "SELECT * FROM CS_SERVICE_PLAN_PACK_MAP " + "WHERE NETWORK_ID=? " + "AND TARIFF_PLAN_TYPE='TP' "
				+ "AND SERVICE_TYPES=?";

		return map(sql, networkId, types);
	}

	public List<ServicePlanPackMap> getDAtpPlans(Long networkId, String types) {

		String sql = "SELECT * FROM CS_SERVICE_PLAN_PACK_MAP " + "WHERE NETWORK_ID=? " + "AND TARIFF_PLAN_TYPE='ATP' "
				+ "AND SERVICE_TYPES=?";

		return map(sql, networkId, types);
	}

	public List<ServicePlanPackMap> getAAtpPlans(Long networkId, String types) {

		String sql = "SELECT * FROM CS_SERVICE_PLAN_PACK_MAP " + "WHERE NETWORK_ID=? " + "AND TARIFF_PLAN_TYPE='ATP' "
				+ "AND SERVICE_TYPES=?";

		return map(sql, networkId, types);
	}

	private List<ServicePlanPackMap> map(String sql, Long networkId, String types) {

		return jdbcTemplate.query(

				sql,

				(rs, rowNum) -> {

					ServicePlanPackMap s = new ServicePlanPackMap();

					s.setServicePackageId(rs.getString("SERVICE_PACKAGE_ID"));

					s.setServicePackageName(rs.getString("SERVICE_PACKAGE_NAME"));

					s.setNetworkId(rs.getInt("NETWORK_ID"));

					s.setTariffPlanType(rs.getString("TARIFF_PLAN_TYPE"));

					s.setServiceTypes(rs.getString("SERVICE_TYPES"));

					return s;
				},

				networkId, types);
	}
}