//package com.xius.TariffBuilder.UserService;
//
//import java.util.List;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import com.xius.TariffBuilder.UserRepository.HierarchyRepository;
//
//@Service
//public class HierarchyService {
//
//	  @Autowired
//	    private HierarchyRepository hierarchyRepository;
//
//		public List<Object[]> getHierarchy(Long networkId,Long tariffPackageId) {
//
//			return hierarchyRepository.getHierarchy(networkId,tariffPackageId);
//		}
//	}

package com.xius.TariffBuilder.UserService;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.xius.TariffBuilder.Entity.TariffHierarchy;

@Service
public class HierarchyService {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	public List<TariffHierarchy> getHierarchy(Long networkId, Long tariffPackageId) {

		String sql = "SELECT " + "tp.TARIFF_PACKAGE_ID, " + "tp.NETWORK_ID, " + "pub.PUBLICITY_ID, "
				+ "spm.SERVICE_PACKAGE_ID, " + "spm.TARIFF_PLAN_TYPE, " + "spm.PRIORITY " +

				"FROM CS_RAT_TARIFF_PACKAGE tp " +

				"LEFT JOIN CS_RAT_TPID_VS_PUBLICITYID pub " + "ON tp.TARIFF_PACKAGE_ID = pub.TARIFF_PACKAGE_ID "
				+ "AND tp.NETWORK_ID = pub.NETWORK_ID " +

				"LEFT JOIN CS_RAT_TARIFF_SERVICE_PACK_MAP spm " + "ON tp.TARIFF_PACKAGE_ID = spm.TARIFF_PACKAGE_ID "
				+ "AND tp.NETWORK_ID = spm.NETWORK_ID " +

				"WHERE tp.NETWORK_ID=? " + "AND tp.TARIFF_PACKAGE_ID=?";

		return jdbcTemplate.query(

				sql,

				(rs, rowNum) -> {

					TariffHierarchy t = new TariffHierarchy();

					t.setTariffPackageId(rs.getLong("TARIFF_PACKAGE_ID"));

					t.setNetworkId(rs.getLong("NETWORK_ID"));

					t.setPublicityId(rs.getString("PUBLICITY_ID"));

					t.setServicePackageId(rs.getLong("SERVICE_PACKAGE_ID"));

					t.setTariffPlanType(rs.getString("TARIFF_PLAN_TYPE"));

					t.setPriority(rs.getLong("PRIORITY"));

					return t;
				},

				networkId, tariffPackageId);
	}
}