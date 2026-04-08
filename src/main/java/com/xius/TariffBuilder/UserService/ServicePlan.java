//package com.xius.TariffBuilder.UserService;
//
//
//import java.util.List;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.jdbc.core.BeanPropertyRowMapper;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.stereotype.Service;
//
//import com.xius.TariffBuilder.Entity.ServicePlanResponse;
//
//@Service
//public class ServicePlan {
//
//
//	@Autowired
//	private JdbcTemplate jdbcTemplate;
//	
//	
//	private String FILE_PATH = "D:\\API_TEST\\DATA_INSERTING.txt";
//
//
//    public ServicePlan(JdbcTemplate jdbcTemplate) {
//        this.jdbcTemplate = jdbcTemplate;
//    }
//
//    public List<ServicePlanResponse> fetchPlans(Long servicePackageId, Long networkId) {
//
//		String sql = """
//				SELECT sp.*
//				FROM CS_RAT_SERVICE_PLANS sp
//				JOIN CS_RAT_SERVICE_PLAN_PACKAGE spp
//				  ON sp.SERVICE_PLAN_ID = spp.SERVICE_PLAN_ID
//				 AND sp.NETWORK_ID = spp.NETWORK_ID
//				WHERE spp.SERVICE_PACKAGE_ID = ?
//				AND spp.NETWORK_ID = ?
//				""";
//
//		return jdbcTemplate.query(sql, new BeanPropertyRowMapper<>(ServicePlanResponse.class), servicePackageId,networkId);
//    }
//
//}
//

//working
//package com.xius.TariffBuilder.UserService;
//
//import java.io.File;
//import java.io.FileWriter;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.jdbc.core.BeanPropertyRowMapper;
//import org.springframework.jdbc.core.JdbcTemplate;
//import org.springframework.stereotype.Service;
//
//import com.xius.TariffBuilder.Entity.ServicePlanResponse;
//
//import tools.jackson.databind.ObjectMapper;
//
//@Service
//public class ServicePlan {
//
//    @Autowired
//    private JdbcTemplate jdbcTemplate;
//
//	private String FILE_PATH = "D:\\API_TEST\\DATA_INSERTING.txt";
//
//	public Map<String, Object> fetchPlans(Long servicePackageId, Long networkId) {
//
//        String sql = """
//                SELECT sp.*
//                FROM CS_RAT_SERVICE_PLANS sp
//                JOIN CS_RAT_SERVICE_PLAN_PACKAGE spp
//                  ON sp.SERVICE_PLAN_ID = spp.SERVICE_PLAN_ID
//                 AND sp.NETWORK_ID = spp.NETWORK_ID
//                WHERE spp.SERVICE_PACKAGE_ID = ?
//                AND spp.NETWORK_ID = ?
//                """;
//
//		List<ServicePlanResponse> plans = jdbcTemplate.query(sql,new BeanPropertyRowMapper<>(ServicePlanResponse.class), servicePackageId, networkId);
//
//
//        Map<String,Object> response =
//                new HashMap<>();
//
//        response.put("servicePackageId", servicePackageId);
//        response.put("networkId", networkId);
//        response.put("plans", plans);
//
//
//		saveJsonToFile(response);
//
//		return response;
//	}
//
//
//	private void saveJsonToFile(Map<String, Object> response) {
//
//		try {
//
//			ObjectMapper mapper = new ObjectMapper();
//
//			FileWriter writer = new FileWriter(FILE_PATH);
//
//			writer.write(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(response));
//
//			writer.close();
//
//			System.out.println("JSON saved in file: " + FILE_PATH);
//
//		} catch (Exception e) {
//
//			e.printStackTrace();
//		}
//	}
//}

package com.xius.TariffBuilder.UserService;

import java.io.FileWriter;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.xius.TariffBuilder.Entity.ServicePlanResponse;

import tools.jackson.databind.ObjectMapper;

@Service
public class ServicePlan {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	private String FILE_1 = "D:\\API_TEST\\SERVICE_PLAN_IDS.txt";

	private String FILE_2 = "D:\\API_TEST\\SERVICE_PLAN_DETAILS.txt";

	public Map<String, Object> fetchPlanDetails(Long servicePackageId, Long networkId) {

		// 1️ get SERVICE_PLAN_ID list
		String idQuery = """
				SELECT SERVICE_PLAN_ID
				FROM CS_RAT_SERVICE_PLAN_PACKAGE
				WHERE SERVICE_PACKAGE_ID = ?
				AND NETWORK_ID = ?
				""";

		List<Long> servicePlanIds = jdbcTemplate.queryForList(idQuery, Long.class, servicePackageId, networkId);

		// 2️⃣ get full details using SERVICE_PLAN_IDs
		String detailQuery = """
				SELECT *
				FROM CS_RAT_SERVICE_PLANS
				WHERE SERVICE_PLAN_ID IN (
				    SELECT SERVICE_PLAN_ID
				    FROM CS_RAT_SERVICE_PLAN_PACKAGE
				    WHERE SERVICE_PACKAGE_ID = ?
				    AND NETWORK_ID = ?
				)
				AND NETWORK_ID = ?
				""";

		List<ServicePlanResponse> details = jdbcTemplate.query(detailQuery,
				new BeanPropertyRowMapper<>(ServicePlanResponse.class), servicePackageId, networkId, networkId);

		// 3️⃣ create response objects
		Map<String, Object> idResponse = new LinkedHashMap<>();

		idResponse.put("servicePackageId", servicePackageId);

		idResponse.put("networkId", networkId);

		idResponse.put("servicePlanIds", servicePlanIds);

		Map<String, Object> detailResponse = new LinkedHashMap<>();

		detailResponse.put("servicePackageId", servicePackageId);

		detailResponse.put("networkId", networkId);

		detailResponse.put("servicePlanDetails", details);

		// 4️⃣ save both files
		saveFile(FILE_1, idResponse);
		saveFile(FILE_2, detailResponse);

		// 5️⃣ return combined response
		Map<String, Object> finalResponse = new LinkedHashMap<>();

		finalResponse.putAll(idResponse);
		finalResponse.putAll(detailResponse);

		return finalResponse;
	}

	private void saveFile(String path, Map<String, Object> data) {

		try {

			ObjectMapper mapper = new ObjectMapper();

			FileWriter writer = new FileWriter(path);

			writer.write(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(data));

			writer.close();

			System.out.println("Saved file: " + path);

		} catch (Exception e) {

			e.printStackTrace();
		}
	}

}