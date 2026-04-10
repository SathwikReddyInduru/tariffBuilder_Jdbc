package com.xius.TariffBuilder.UserService;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.xius.TariffBuilder.util.JsonStorage;

@Service
public class TariffService {

	@Autowired
	private JsonStorage jsonStorage;

	public List<String> getTariffPackages() {

		return new ArrayList<>(
				jsonStorage.getAllTpNames());

	}

	public Object getHierarchy(String tpName) {

		return jsonStorage.getTpData(tpName);

	}
}