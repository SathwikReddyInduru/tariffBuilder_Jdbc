package com.xius.TariffBuilder.UserService;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.xius.TariffBuilder.Entity.SaveConfigDao;
import com.xius.TariffBuilder.util.JsonStorage;

@Service
public class SaveConfigService {

	@Autowired
	private SaveConfigDao dao;

	@Autowired
	private JsonStorage jsonStorage;

	public Map<String, Object> prepareConfig(

			Map<String, Object> request,

			String username,

			Long networkId) {

		Map<String, Object> response = new HashMap<>();

		// -----------------------
		// Read required fields
		// -----------------------

		String tpName = (String) request.get("tariffPackageDesc");

		String publicityId = (String) request.get("publicityId");

		Number tariffPlanId = (Number) request.get("tariffPlanId");

		// -----------------------
		// BASIC VALIDATION
		// -----------------------

		if (tpName == null || tpName.isBlank()) {

			response.put("error", "tariffPackageDesc required");

			return response;
		}

		if (publicityId == null) {

			response.put("error", "publicityId required");

			return response;
		}

		if (tariffPlanId == null) {

			response.put("error", "tariffPlanId required");

			return response;
		}

		// -----------------------
		// DB VALIDATION
		// -----------------------

		if (dao.checkTariffExists(

				networkId,

				tpName)) {

			response.put("error", "Tariff already exists in DB");

			return response;
		}

		if (dao.checkPublicityExists(

				networkId,

				publicityId)) {

			response.put("error", "Publicity already mapped in DB");

			return response;
		}

		// -----------------------
		// DATP VALIDATION
		// -----------------------

		List<Map<String, Object>> datp = (List<Map<String, Object>>) request.get("defaultAtps");

		if (datp != null) {

			for (Map<String, Object> atp : datp) {

				if (atp.get("servicePackageId") == null || atp.get("chargeId") == null) {

					response.put("error", "DATP requires servicePackageId and chargeId");

					return response;
				}
			}
		}

		// -----------------------
		// AATP VALIDATION
		// -----------------------

		List<Map<String, Object>> aatp = (List<Map<String, Object>>) request.get("allowedAtps");

		if (aatp != null) {

			for (Map<String, Object> atp : aatp) {

				if (atp.get("servicePackageId") == null || atp.get("chargeId") == null) {

					response.put("error", "AATP requires servicePackageId and chargeId");

					return response;
				}
			}
		}

		// -----------------------
		// JSON DUPLICATE CHECK
		// -----------------------

		if (jsonStorage.exists(tpName)) {

			response.put("error", "Tariff already prepared in JSON");

			return response;
		}

		// -----------------------
		// STORE JSON
		// -----------------------

		jsonStorage.store(

				tpName,

				username,

				networkId,

				request);

		// -----------------------
		// SUCCESS RESPONSE
		// -----------------------

		response.put("message", "Configuration prepared successfully");

		response.put("tpName", tpName);

		return response;
	}

	public void saveDraft(Map<String, Object> draft, String username) {

		try {
			ObjectMapper mapper = new ObjectMapper();

			Path path = Paths.get("drafts", username + ".json");

			Files.createDirectories(path.getParent());

			List<Map<String, Object>> drafts = new ArrayList<>();

			if (Files.exists(path) && Files.size(path) > 0) {
				drafts = mapper.readValue(
						path.toFile(),
						new TypeReference<List<Map<String, Object>>>() {
						});
			}

			drafts.removeIf(d -> d.get("name").equals(draft.get("name")));
			// add this before drafts.add(draft)
			boolean shouldDelete = Boolean.TRUE.equals(draft.get("_delete"));
			drafts.removeIf(d -> d.get("name").equals(draft.get("name")));
			if (shouldDelete) {
				mapper.writerWithDefaultPrettyPrinter().writeValue(path.toFile(), drafts);
				return;
			}
			drafts.add(draft);

			mapper.writerWithDefaultPrettyPrinter()
					.writeValue(path.toFile(), drafts);

		} catch (Exception e) {
			System.out.println("❌ ERROR SAVING DRAFT");
			e.printStackTrace();
		}
	}
}