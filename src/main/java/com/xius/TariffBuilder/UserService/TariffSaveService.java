//package com.xius.TariffBuilder.UserService;
//
//import java.time.LocalDate;
//import java.time.format.DateTimeFormatter;
//
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.stereotype.Service;
//
//import com.xius.TariffBuilder.Dao.SavePackageRequest;
//import com.xius.TariffBuilder.Entity.PeriodicChargeInfo;
//import com.xius.TariffBuilder.Entity.TariffPackage;
//import com.xius.TariffBuilder.Entity.TariffPublicityMap;
//import com.xius.TariffBuilder.Entity.TariffServicePackMap;
//import com.xius.TariffBuilder.Entity.TariffStatus;
//import com.xius.TariffBuilder.UserRepository.PeriodicChargeRepository;
//import com.xius.TariffBuilder.UserRepository.TariffPackageRepository;
//import com.xius.TariffBuilder.UserRepository.TariffPublicityMapRepository;
//import com.xius.TariffBuilder.UserRepository.TariffStatusRepository;
//
//import jakarta.transaction.Transactional;
//import lombok.RequiredArgsConstructor;
//
//@Service
//@RequiredArgsConstructor
//public class TariffSaveService {
//	@Autowired
//	private TariffPackageRepository packageRepo;
//	@Autowired
//	private TariffPublicityMapRepository publicityRepo;
//	@Autowired
//	private TariffPackageRepository serviceRepo;
//	@Autowired
//	private PeriodicChargeRepository periodicRepo;
//	@Autowired
//	private TariffStatusRepository statusRepo;
//
//	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
//
//	@Transactional
//	public Long savePackage(SavePackageRequest req, Long networkId, String username) {
//
//		/* 1️⃣ CS_RAT_TARIFF_PACKAGE */
//
//		TariffPackage pkg = new TariffPackage();
//
//		pkg.setTariffPackageDesc(req.getTariffPackageDesc().toUpperCase());
//
//		pkg.setNetworkId(networkId);
//
//		pkg.setEndDate(LocalDate.parse(req.getEndDate(), formatter));
//
//		pkg.setChargeId(String.valueOf(req.getChargeId()));
//
//		pkg.setPackageType(req.getPackageType());
//
//		pkg.setSubscriberCategoryId(req.getSubscriberCategoryId());
//
//		pkg.setIsCorporateYn(req.getIsCorporateYn());
//
//		pkg.setTariffPackCategory(req.getTariffPackCategory());
//
//		pkg.setPublicityId(req.getPublicityId());
//
//		TariffPackage saved = packageRepo.save(pkg);
//
//		Long tariffPackageId = saved.getTariffPackageId();
//
//		/*
//		 * NEW INSERT CS_TARIFF_PACK_AP_REG_STATUS
//		 */
//
//		TariffStatus status = new TariffStatus();
//
//		status.setNetworkId(networkId);
//
//		status.setTariffPackageId(tariffPackageId);
//
//		status.setTariffPackageName(req.getTariffPackageDesc().toUpperCase());
//
//		status.setStatus("P");
//
//		statusRepo.save(status);
//
//		/* 2️⃣ CS_RAT_PERIODIC_CHARGE_INFO */
//
//		if (!periodicRepo.existsByChargeIdAndNetworkId(String.valueOf(req.getChargeId()), networkId)) {
//
//			PeriodicChargeInfo charge = new PeriodicChargeInfo();
//
//			charge.setChargeId(String.valueOf(req.getChargeId()));
//
//			charge.setNetworkId(networkId);
//
//			charge.setServiceType(1);
//
//			charge.setRentalType("MONTHLY");
//
//			charge.setRentalPeriod(30);
//
//			charge.setCreatedBy(username);
//
//			periodicRepo.save(charge);
//		}
//
//		/* 3️⃣ CS_RAT_TPID_VS_PUBLICITYID */
//
//		TariffPublicityMap pub = new TariffPublicityMap();
//
//		pub.setNetworkId(networkId);
//
//		pub.setTariffPackageId(tariffPackageId);
//
//		pub.setTariffPackageDesc(req.getTariffPackageDesc().toUpperCase());
//
//		pub.setPublicityId(req.getPublicityId());
//
//		pub.setRecordInsertedBy(username);
//
//		pub.setRecInsertedDate(LocalDate.now());
//
//		publicityRepo.save(pub);
//
//		/* 4️⃣ TP */
//
//		TariffServicePackMap tp = new TariffServicePackMap();
//
//		tp.setTariffPackageId(tariffPackageId);
//
//		tp.setServicePackageId(req.getTariffPlanId());
//
//		tp.setNetworkId(networkId);
//
//		tp.setTariffPlanType("TP");
//
//		serviceRepo.save(tp);
//
//		/* 5️⃣ DATP */
//
//		if (req.getDefaultAtps() != null) {
//			for (var item : req.getDefaultAtps()) {
//
//				TariffServicePackMap datp = new TariffServicePackMap();
//
//				datp.setTariffPackageId(tariffPackageId);
//
//				datp.setServicePackageId(item.getServicePackageId());
//
//				datp.setNetworkId(networkId);
//
//				datp.setTariffPlanType("DATP");
//
//				datp.setChargeId(String.valueOf(item.getChargeId()));
//
//				datp.setPriority(1);
//
//				datp.setServiceDuration(30);
//
//				serviceRepo.save(datp);
//			}
//		}
//
//		/* 6️⃣ AATP */
//
//		if (req.getAllowedAtps() != null) {
//			for (var item : req.getAllowedAtps()) {
//
//				TariffServicePackMap aatp = new TariffServicePackMap();
//
//				aatp.setTariffPackageId(tariffPackageId);
//
//				aatp.setServicePackageId(item.getServicePackageId());
//
//				aatp.setNetworkId(networkId);
//
//				aatp.setTariffPlanType("AATP");
//
//				aatp.setChargeId(String.valueOf(item.getChargeId()));
//
//				serviceRepo.save(aatp);
//			}
//		}
//
//		return tariffPackageId;
//	}
//}

package com.xius.TariffBuilder.UserService;

import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import com.xius.TariffBuilder.Entity.SavePackageRequest;

@Service
public class TariffSaveService {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

	public Long savePackage(SavePackageRequest req, Long networkId, String username) {

		/* 1️⃣ INSERT CS_RAT_TARIFF_PACKAGE */

		String insertPackageSql =

				"INSERT INTO CS_RAT_TARIFF_PACKAGE (" +

						"TARIFF_PACKAGE_ID, " + "NETWORK_ID, " + "TARIFF_PACKAGE_DESC, " + "END_DATE, " + "CHARGE_ID, "
						+ "PACKAGE_TYPE, " + "SUBSCRIBER_CATEGORY_ID, " + "IS_CORPORATE_YN, " + "TARIFF_PACK_CATEGORY, "
						+ "PUBLICITY_ID" +

						") VALUES (" +

						"CS_RAT_TARIFF_PACKAGE_SEQ.NEXTVAL,?,?,?,?,?,?,?,?,?" +

						")";

		LocalDate endDate = LocalDate.parse(req.getEndDate(), formatter);

		jdbcTemplate.update(

				insertPackageSql,

				networkId, req.getTariffPackageDesc().toUpperCase(), Date.valueOf(endDate),
				String.valueOf(req.getChargeId()), req.getPackageType(), req.getSubscriberCategoryId(),
				req.getIsCorporateYn(), req.getTariffPackCategory(), req.getPublicityId());

		/* GET GENERATED TARIFF_PACKAGE_ID */

		Long tariffPackageId =

				jdbcTemplate.queryForObject(

						"SELECT CS_RAT_TARIFF_PACKAGE_SEQ.CURRVAL FROM dual",

						Long.class);

		/* 2️⃣ INSERT CS_TARIFF_PACK_AP_REG_STATUS */

		String insertStatusSql =

				"INSERT INTO CS_TARIFF_PACK_AP_REG_STATUS (" +

						"NETWORK_ID, " + "TARIFF_PACKAGE_ID, " + "TARIFF_PACKAGE_NAME, " + "STATUS" +

						") VALUES (?,?,?,?)";

		jdbcTemplate.update(

				insertStatusSql,

				networkId, tariffPackageId, req.getTariffPackageDesc().toUpperCase(), "P");

		/* 3️⃣ INSERT CS_RAT_PERIODIC_CHARGE_INFO */

		String checkChargeSql =

				"SELECT COUNT(*) " +

						"FROM CS_RAT_PERIODIC_CHARGE_INFO " +

						"WHERE CHARGE_ID=? " +

						"AND NETWORK_ID=?";

		Integer exists =

				jdbcTemplate.queryForObject(

						checkChargeSql,

						Integer.class,

						String.valueOf(req.getChargeId()), networkId);

		if (exists == 0) {

			String insertChargeSql =

					"INSERT INTO CS_RAT_PERIODIC_CHARGE_INFO (" +

							"CHARGE_ID, " + "NETWORK_ID, " + "SERVICE_TYPE, " + "RENTAL_TYPE, " + "RENTAL_PERIOD, "
							+ "CREATED_BY" +

							") VALUES (?,?,?,?,?,?)";

			jdbcTemplate.update(

					insertChargeSql,

					String.valueOf(req.getChargeId()), networkId, 1, "MONTHLY", 30, username);
		}

		/* 4️⃣ INSERT CS_RAT_TPID_VS_PUBLICITYID */

		String insertPublicitySql =

				"INSERT INTO CS_RAT_TPID_VS_PUBLICITYID (" +

						"NETWORK_ID, " + "TARIFF_PACKAGE_ID, " + "TARIFF_PACKAGE_DESC, " + "PUBLICITY_ID, "
						+ "RECORD_INSERTED_BY, " + "REC_INSERTED_DATE" +

						") VALUES (?,?,?,?,?,?)";

		jdbcTemplate.update(

				insertPublicitySql,

				networkId, tariffPackageId, req.getTariffPackageDesc().toUpperCase(), req.getPublicityId(), username,
				Date.valueOf(LocalDate.now()));

		/* 5️⃣ INSERT TP */

		String insertTpSql =

				"INSERT INTO CS_RAT_TARIFF_SERVICE_PACK_MAP (" +

						"TARIFF_PACKAGE_ID, " + "SERVICE_PACKAGE_ID, " + "NETWORK_ID, " + "TARIFF_PLAN_TYPE" +

						") VALUES (?,?,?,?)";

		jdbcTemplate.update(

				insertTpSql,

				tariffPackageId, req.getTariffPlanId(), networkId, "TP");

		/* 6️⃣ INSERT DATP */

		if (req.getDefaultAtps() != null) {

			for (var item : req.getDefaultAtps()) {

				String insertDatpSql =

						"INSERT INTO CS_RAT_TARIFF_SERVICE_PACK_MAP (" +

								"TARIFF_PACKAGE_ID, " + "SERVICE_PACKAGE_ID, " + "NETWORK_ID, " + "TARIFF_PLAN_TYPE, "
								+ "CHARGE_ID, " + "PRIORITY, " + "SERVICE_DURATION" +

								") VALUES (?,?,?,?,?,?,?)";

				jdbcTemplate.update(

						insertDatpSql,

						tariffPackageId, item.getServicePackageId(), networkId, "DATP",
						String.valueOf(item.getChargeId()), 1, 30);
			}
		}

		/* 7️⃣ INSERT AATP */

		if (req.getAllowedAtps() != null) {

			for (var item : req.getAllowedAtps()) {

				String insertAatpSql =

						"INSERT INTO CS_RAT_TARIFF_SERVICE_PACK_MAP (" +

								"TARIFF_PACKAGE_ID, " + "SERVICE_PACKAGE_ID, " + "NETWORK_ID, " + "TARIFF_PLAN_TYPE, "
								+ "CHARGE_ID" +

								") VALUES (?,?,?,?,?)";

				jdbcTemplate.update(

						insertAatpSql,

						tariffPackageId, item.getServicePackageId(), networkId, "AATP",
						String.valueOf(item.getChargeId()));
			}
		}

		return tariffPackageId;
	}

}