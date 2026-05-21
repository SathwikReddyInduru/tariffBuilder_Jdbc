package com.xius.TariffBuilder.UserService;

import java.sql.Date;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.xius.TariffBuilder.Dto.TariffPackageDetails;
import com.xius.TariffBuilder.Dto.TariffPackageDetailsDto;
import com.xius.TariffBuilder.UserService.BundleService.CloneAtpResult;
import com.xius.TariffBuilder.UserService.ServiceCloneService.CloneServiceResult;
import com.xius.TariffBuilder.util.JsonStorage;

@Service
public class TariffApprovalService {

	private static final Logger logger = LoggerFactory.getLogger(TariffApprovalService.class);

	private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private JsonStorage jsonStorage;

	@Autowired
	private ServiceCloneService serviceCloneService;

	@Autowired
	private BundleService bundleService;

	@Autowired
	private ServiceplanZone servicePlanZone;

	/*
	 * =====================================================
	 * APPROVE TARIFF
	 * Reads request body from JSON storage by tpName.
	 * =====================================================
	 */
	@Transactional(rollbackFor = Exception.class)
	public Map<String, Object> approve(String tpName) {

		long startTime = System.currentTimeMillis();

		logger.info("Approve request received tpName={}", tpName);

		Map<String, Object> json = (Map<String, Object>) jsonStorage.getTpData(tpName);

		if (json == null) {
			throw new RuntimeException("JSON NOT FOUND for tpName=" + tpName);
		}

		Map<String, Object> data = (Map<String, Object>) json.get("data");
		Long networkId = Long.valueOf(json.get("networkId").toString());
		String username = json.get("username").toString();

		logger.info("Approve payload tpName={} networkId={} username={}", tpName, networkId, username);

		Map<String, Object> result = executeTariffCreation(data, tpName, networkId, username);

		removeFromJson(tpName);

		long time = System.currentTimeMillis() - startTime;
		logger.info("Tariff approved tpName={} executionTime={}ms", tpName, time);

		return result;
	}

	/*
	 * =====================================================
	 * CLONE TARIFF
	 *
	 * Accepts the full request body from the caller.
	 * Before executing any queries:
	 * - tpName → tpName + "_CL"
	 * - publicityId → publicityId + "_CL"
	 * - chargeId → chargeId + "_CL" (chargeId is derived from tpName, e.g.
	 * TP_T1_PR)
	 *
	 * Returns the same result map as approve() plus the
	 * cloned tpName and publicityId so the caller knows
	 * what was created.
	 * =====================================================
	 */
	@Transactional(rollbackFor = Exception.class)
	public Map<String, Object> clone(Map<String, Object> requestBody) {

		long startTime = System.currentTimeMillis();

		// ── 1. Extract top-level fields ──────────────────────────────────────
		String originalTpName = requestBody.get("tpName").toString();
		Long networkId = Long.valueOf(requestBody.get("networkId").toString());
		String username = requestBody.get("username").toString();

		logger.info("Clone request received originalTpName={} networkId={} username={}",
				originalTpName, networkId, username);

		// ── 2. Build cloned names ─────────────────────────────────────────────
		String clonedTpName = originalTpName + "_CL";
		String clonedPublicityId = requestBody.get("data") != null
				? ((Map<String, Object>) requestBody.get("data")).get("publicityId").toString() + "_CL"
				: null;

		logger.info("Clone names clonedTpName={} clonedPublicityId={}", clonedTpName, clonedPublicityId);

		// ── 3. Deep-copy and mutate the data map ──────────────────────────────
		Map<String, Object> originalData = (Map<String, Object>) requestBody.get("data");
		Map<String, Object> clonedData = new HashMap<>(originalData);

		clonedData.put("publicityId", clonedPublicityId);
		clonedData.put("tariffPackageDesc", clonedTpName);

		// chargeId is typically tpName + "_PR"; update it to match the cloned name
		String originalChargeId = originalData.get("chargeId").toString();
		String clonedChargeId = originalChargeId.replace(originalTpName, clonedTpName);
		clonedData.put("chargeId", clonedChargeId);

		logger.info("Cloned data publicityId={} tariffPackageDesc={} chargeId={}",
				clonedPublicityId, clonedTpName, clonedChargeId);

		// ── 4. Execute all tariff creation queries with cloned values ─────────
		Map<String, Object> result = executeTariffCreation(clonedData, clonedTpName, networkId, username);

		// ── 5. Append clone-specific fields to response ───────────────────────
		result.put("clonedTpName", clonedTpName);
		result.put("clonedPublicityId", clonedPublicityId);
		result.put("clonedChargeId", clonedChargeId);

		long time = System.currentTimeMillis() - startTime;
		logger.info("Tariff cloned clonedTpName={} executionTime={}ms", clonedTpName, time);

		return result;
	}

	/*
	 * =====================================================
	 * REJECT TARIFF
	 * =====================================================
	 */
	@Transactional
	public void reject(String tpName) {

		Map<String, Object> json = (Map<String, Object>) jsonStorage.getTpData(tpName);

		if (json == null) {
			throw new RuntimeException("JSON NOT FOUND for tpName=" + tpName);
		}

		removeFromJson(tpName);

		logger.info("Tariff rejected successfully tpName={}", tpName);
	}

	/*
	 * =====================================================
	 * SHARED CORE: all 11 steps extracted here.
	 * Called by both approve() and clone().
	 *
	 * @param data — the (possibly mutated) data map
	 * 
	 * @param tpName — effective name (original or cloned)
	 * 
	 * @param networkId
	 * 
	 * @param username
	 * =====================================================
	 */
	private Map<String, Object> executeTariffCreation(
			Map<String, Object> data,
			String tpName,
			Long networkId,
			String username) {

		try {
			/*
			 * ── STEP 1: Get old plan and old plan zone ──────────────────────
			 */
			Long oldServicePackageId = Long.valueOf(data.get("tariffPlanId").toString());

			Long oldPlanId = serviceCloneService.getOldPlanId(networkId, oldServicePackageId);

			if (oldPlanId == null) {
				throw new RuntimeException("Old plan not found for packageId=" + oldServicePackageId);
			}

			Long oldPlanZoneId = serviceCloneService.getPlanZoneId(oldPlanId);

			/*
			 * ── STEP 2: Get old bucket and old bucket zone ──────────────────
			 */
			List<Map<String, Object>> defaultAtps = (List<Map<String, Object>>) data.get("defaultAtps");
			List<Map<String, Object>> addAtps = (List<Map<String, Object>>) data.get("allowedAtps");

			boolean hasDefaultAtps = defaultAtps != null && !defaultAtps.isEmpty();
			boolean hasAllowedAtps = addAtps != null && !addAtps.isEmpty();
			boolean hasAnyAtps = hasDefaultAtps || hasAllowedAtps;

			logger.info("ATP presence check hasDefaultAtps={} hasAllowedAtps={} hasAnyAtps={}",
					hasDefaultAtps, hasAllowedAtps, hasAnyAtps);

			Long oldBucketZoneId = null;

			if (hasAnyAtps) {

				List<Map<String, Object>> firstAtpList = hasDefaultAtps ? defaultAtps : addAtps;
				Long firstOldAtpId = Long.valueOf(firstAtpList.get(0).get("servicePackageId").toString());

				String oldBucketId = bundleService.getOldBucketId(firstOldAtpId, networkId);

				if (oldBucketId == null) {
					throw new RuntimeException("Old bucket not found for ATP=" + firstOldAtpId);
				}

				oldBucketZoneId = bundleService.getBucketZoneId(oldBucketId);

				logger.info("Old mapping oldPlanId={} oldPlanZoneId={} oldBucketId={} oldBucketZoneId={}",
						oldPlanId, oldPlanZoneId, oldBucketId, oldBucketZoneId);

			} else {
				logger.info("No ATPs present. Skipping bucket zone resolution. oldPlanId={} oldPlanZoneId={}",
						oldPlanId, oldPlanZoneId);
			}

			/*
			 * ── STEP 3: Decide new plan zone and new bucket zone ────────────
			 */
			Long newPlanZoneId;
			Long newBucketZoneId;

			if (hasAnyAtps) {

				if (oldPlanZoneId != null && oldPlanZoneId.equals(oldBucketZoneId)) {

					Long newZoneId = servicePlanZone.generateNewZoneId();
					newPlanZoneId = newZoneId;
					newBucketZoneId = newZoneId;
					servicePlanZone.cloneZoneIfExists(oldPlanZoneId, newZoneId, networkId, tpName);

				} else {

					newPlanZoneId = servicePlanZone.generateNewZoneId();
					servicePlanZone.cloneZoneIfExists(oldPlanZoneId, newPlanZoneId, networkId, tpName);

					newBucketZoneId = servicePlanZone.generateNewZoneId();
					servicePlanZone.cloneZoneIfExists(oldBucketZoneId, newBucketZoneId, networkId, tpName);
				}

			} else {

				newPlanZoneId = servicePlanZone.generateNewZoneId();
				servicePlanZone.cloneZoneIfExists(oldPlanZoneId, newPlanZoneId, networkId, tpName);
				newBucketZoneId = null;

				logger.info("No ATPs present. newPlanZoneId={} newBucketZoneId=null.", newPlanZoneId);
			}

			/*
			 * ── STEP 4: Clone service package and plan ──────────────────────
			 */
			CloneServiceResult serviceResult = serviceCloneService.cloneService(
					networkId, oldServicePackageId, tpName, newPlanZoneId);

			Long newServicePackageId = serviceResult.getNewPackageId();
			Long newServicePlanId = serviceResult.getNewPlanId();

			logger.info("Base service cloned newServicePackageId={} newServicePlanId={}",
					newServicePackageId, newServicePlanId);

			/*
			 * ── STEP 5: Clone ATPs, bundles and buckets ─────────────────────
			 */
			List<Long> defaultAtpIds = new ArrayList<>();
			List<Long> allowedAtpIds = new ArrayList<>();
			List<Long> newAtpIds = new ArrayList<>();

			if (hasAnyAtps) {

				if (hasDefaultAtps) {
					for (Map<String, Object> atp : defaultAtps) {
						Long oldAtpId = Long.valueOf(atp.get("servicePackageId").toString());
						CloneAtpResult atpResult = bundleService.cloneAtpData(
								oldAtpId, networkId, tpName, newBucketZoneId);
						defaultAtpIds.add(atpResult.getNewAtpId());
						newAtpIds.add(atpResult.getNewAtpId());
						logger.info("Default ATP cloned oldAtpId={} newAtpId={}", oldAtpId, atpResult.getNewAtpId());
					}
				}

				logger.info("After default ATPs defaultAtpIds={}", defaultAtpIds);
				logger.info("Allowed ATP count={}", hasAllowedAtps ? addAtps.size() : 0);

				if (hasAllowedAtps) {
					for (Map<String, Object> atp : addAtps) {
						Long oldAtpId = Long.valueOf(atp.get("servicePackageId").toString());
						CloneAtpResult atpResult = bundleService.cloneAtpData(
								oldAtpId, networkId, tpName, newBucketZoneId);
						allowedAtpIds.add(atpResult.getNewAtpId());
						newAtpIds.add(atpResult.getNewAtpId());
						logger.info("Allowed ATP cloned oldAtpId={} newAtpId={}", oldAtpId, atpResult.getNewAtpId());
					}
				}

			} else {
				logger.info("No ATPs present. Skipping bundle and bucket clone.");
			}

			logger.info("Final defaultAtpIds={} allowedAtpIds={}", defaultAtpIds, allowedAtpIds);

			/*
			 * ── STEP 6: Periodic charge ─────────────────────────────────────
			 */
			insertPeriodicCharge(data, networkId, username);

			/*
			 * ── STEP 7: Create tariff package ───────────────────────────────
			 */
			Long tariffId = jdbcTemplate.queryForObject(
					"select SEQ_TARIFF_PACK_ID.nextval from dual", Long.class);

			jdbcTemplate.update("""
					insert into CS_RAT_TARIFF_PACKAGE
					(
					    TARIFF_PACKAGE_ID,
					    TARIFF_PACKAGE_DESC,
					    NETWORK_ID,
					    END_DATE,
					    PUBLICITY_ID,
					    CHARGE_ID,
					    PACKAGE_TYPE,
					    IS_CORPORATE_YN,
					    TARIFF_PACK_CATEGORY
					)
					values (?,?,?,?,?,?,?,?,?)
					""",
					tariffId,
					data.get("tariffPackageDesc"),
					networkId,
					Date.valueOf(LocalDate.parse(data.get("endDate").toString(), formatter)),
					data.get("publicityId"),
					data.get("chargeId"),
					data.get("packageType"),
					convertYN(data.get("isCorporateYn")),
					data.get("tariffPackCategory"));

			/*
			 * ── STEP 8: Publicity mapping ────────────────────────────────────
			 */
			jdbcTemplate.update("""
					insert into CS_RAT_TPID_VS_PUBLICITYID
					(
					    NETWORK_ID,
					    TARIFF_PACKAGE_ID,
					    TARIFF_PACKAGE_DESC,
					    PUBLICITY_ID,
					    RECORD_INSERTED_BY,
					    REC_INSERTED_DATE
					)
					values (?,?,?,?,?,sysdate)
					""",
					networkId,
					tariffId,
					data.get("tariffPackageDesc"),
					data.get("publicityId"),
					username);

			/*
			 * ── STEP 9: Map tariff to base service package ───────────────────
			 */
			jdbcTemplate.update("""
					insert into CS_RAT_TARIFF_SERVICE_PACK_MAP
					(
					    TARIFF_PACKAGE_ID,
					    SERVICE_PACKAGE_ID,
					    NETWORK_ID,
					    TARIFF_PLAN_TYPE
					)
					values (?,?,?,?)
					""",
					tariffId, newServicePackageId, networkId, "TP");

			/*
			 * ── STEP 10: Map tariff to ATPs ──────────────────────────────────
			 */
			for (Long atpId : defaultAtpIds) {
				jdbcTemplate.update("""
						insert into CS_RAT_TARIFF_SERVICE_PACK_MAP
						(
						    TARIFF_PACKAGE_ID,
						    SERVICE_PACKAGE_ID,
						    NETWORK_ID,
						    TARIFF_PLAN_TYPE,
						    CHARGE_ID,
						    PRIORITY,
						    SERVICE_DURATION
						)
						values (?,?,?,?,?,?,?)
						""",
						tariffId, atpId, networkId, "DATP", data.get("chargeId"), 1, 30);
			}

			for (Long atpId : allowedAtpIds) {
				jdbcTemplate.update("""
						insert into CS_RAT_TARIFF_SERVICE_PACK_MAP
						(
						    TARIFF_PACKAGE_ID,
						    SERVICE_PACKAGE_ID,
						    NETWORK_ID,
						    TARIFF_PLAN_TYPE,
						    CHARGE_ID,
						    PRIORITY,
						    SERVICE_DURATION
						)
						values (?,?,?,?,?,?,?)
						""",
						tariffId, atpId, networkId, "AATP", data.get("chargeId"), 1, 30);
			}

			/*
			 * ── STEP 11: Approval status ─────────────────────────────────────
			 */
			jdbcTemplate.update("""
					insert into CS_TARIFF_PACK_AP_REG_STATUS
					(
					    NETWORK_ID,
					    TARIFF_PACKAGE_ID,
					    TARIFF_PACKAGE_NAME,
					    STATUS
					)
					values (?,?,?,?)
					""",
					networkId, tariffId, data.get("tariffPackageDesc"), "A");

			logger.info("executeTariffCreation complete tpName={} tariffId={}", tpName, tariffId);

			Map<String, Object> response = new HashMap<>();
			response.put("tariffPackageId", tariffId);
			response.put("newServicePackageId", newServicePackageId);
			response.put("newServicePlanId", newServicePlanId);
			response.put("newAtpIds", newAtpIds);
			response.put("newPlanZoneId", newPlanZoneId);
			response.put("newBucketZoneId", newBucketZoneId);

			return response;

		} catch (Exception ex) {
			logger.error("Tariff creation failed tpName={} error={}", tpName, ex.getMessage(), ex);
			throw ex;
		}
	}

	// ── Helpers ──────────────────────────────────────────────────────────────

	private void insertPeriodicCharge(Map<String, Object> data, Long networkId, String username) {

		String chargeId = data.get("chargeId").toString();

		Integer count = jdbcTemplate.queryForObject("""
				select count(*)
				from CS_RAT_PERIODIC_CHARGE_INFO
				where CHARGE_ID = ?
				and NETWORK_ID = ?
				""", Integer.class, chargeId, networkId);

		if (count != null && count > 0) {
			logger.info("Periodic charge already exists chargeId={}", chargeId);
			return;
		}

		List<Map<String, Object>> atps = (List<Map<String, Object>>) data.get("defaultAtps");

		if (atps == null || atps.isEmpty()) {
			logger.info("Default ATP not available. Skipping periodic charge.");
			return;
		}

		Map<String, Object> atp = atps.get(0);

		jdbcTemplate.update("""
				insert into CS_RAT_PERIODIC_CHARGE_INFO
				(
				    CHARGE_ID,
				    CHARGE_DESC,
				    NETWORK_ID,
				    SERVICE_TYPE,
				    RENTAL_TYPE,
				    RENTAL_PERIOD,
				    RENTAL_FEE,
				    RENTAL_FREE_CYCLES,
				    AUTO_RENEWAL,
				    PLAN_EXP_MIDNIGHT_YN,
				    MAX_RENEWAL_COUNT,
				    CREATED_BY
				)
				values (?,?,?,?,?,?,?,?,?,?,?,?)
				""",
				chargeId, chargeId, networkId, data.get("tariffPlanId"),
				atp.get("validity"), 1, atp.get("rental"),
				atp.get("freeCycles"), convertYN(atp.get("renewal")),
				convertYN(atp.get("midnightExpiry")), atp.get("maxCount"), username);
	}

	private String convertYN(Object value) {

		if (value == null) {
			return "N";
		}

		String v = value.toString();

		if (v.equalsIgnoreCase("Y") || v.equalsIgnoreCase("YES") || v.equalsIgnoreCase("TRUE")) {
			return "Y";
		}

		return "N";
	}

	private void removeFromJson(String tpName) {

		Map<String, Object> json = (Map<String, Object>) jsonStorage.readAll();

		json.remove(tpName);

		jsonStorage.writeAll(json);

		logger.info("TP removed from json storage tpName={}", tpName);
	}

	public List<Map<String, Object>> getTariffPackages(Long networkId) {

		String sql = """
				SELECT
				    tariff_package_id,
				    tariff_package_desc
				FROM cs_rat_tariff_package
				WHERE network_id = ?
				""";

		return jdbcTemplate.queryForList(sql, networkId);
	}

	public List<TariffPackageDetails> getTariffPackageDetails(
			Long networkId,
			Long tariffPackageId) {

		String sql = """
				SELECT
				       tp.TARIFF_PACKAGE_ID,
				       tp.TARIFF_PACKAGE_DESC,
				       tp.NETWORK_ID,
				       tp.PACKAGE_TYPE,
				       tp.IS_CORPORATE_YN,
				       tp.TARIFF_PACK_CATEGORY,

				       tspm.SERVICE_PACKAGE_ID,
				       tspm.TARIFF_PLAN_TYPE,
				       tspm.CHARGE_ID,
				       tspm.EFFECTIVE_START_OFFSET,
				       tspm.SERVICE_DURATION,
				       tspm.PRIORITY,
				       tspm.CREATED_OR_MODIFIED_DATE,

				       spp.SERVICE_PLAN_ID,

				       pci.CHARGE_DESC,
				       pci.SERVICE_TYPE,
				       pci.RENTAL_TYPE,
				       pci.RENTAL_PERIOD,
				       pci.ALCS_ID,
				       pci.ACTIVATION_FEE,
				       pci.RENTAL_FEE,
				       pci.RENTAL_FREE_CYCLES,
				       pci.PRO_RAT_YN,
				       pci.ADV_YN,
				       pci.TAX1,
				       pci.TAX2,
				       pci.TAX3,
				       pci.ADD_PACK_CHARGE_YN,
				       pci.RENTAL_DEDUCTION_IN_GRACE,
				       pci.DEACTIVATION_FEE,
				       pci.AUTO_RENEWAL,
				       pci.CREATED_BY,
				       pci.CREATED_DATE,
				       pci.PLAN_EXP_MIDNIGHT_YN,
				       pci.RESERVED_AMOUNT,
				       pci.MAX_RENEWAL_COUNT,
				       pci.SALES_FEE,
				       pci.ATP_BNDL_BKT_MULTIREC_INS

				FROM CS_RAT_TARIFF_PACKAGE tp

				LEFT JOIN CS_RAT_TARIFF_SERVICE_PACK_MAP tspm
				       ON tp.TARIFF_PACKAGE_ID = tspm.TARIFF_PACKAGE_ID
				      AND tp.NETWORK_ID = tspm.NETWORK_ID

				LEFT JOIN CS_RAT_SERVICE_PLAN_PACKAGE spp
				       ON tspm.SERVICE_PACKAGE_ID = spp.SERVICE_PACKAGE_ID
				      AND tspm.NETWORK_ID = spp.NETWORK_ID

				LEFT JOIN CS_RAT_PERIODIC_CHARGE_INFO pci
				       ON tspm.CHARGE_ID = pci.CHARGE_ID
				      AND tspm.NETWORK_ID = pci.NETWORK_ID

				WHERE tp.NETWORK_ID = ?
				  AND tp.TARIFF_PACKAGE_ID = ?

				ORDER BY tspm.SERVICE_PACKAGE_ID
				""";

		return jdbcTemplate.query(
				sql,
				new BeanPropertyRowMapper<>(TariffPackageDetails.class),
				networkId,
				tariffPackageId);
	}
}