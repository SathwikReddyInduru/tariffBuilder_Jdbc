package com.xius.TariffBuilder.util;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

@Component
public class JsonStorage {

        private static final Logger logger = LoggerFactory.getLogger(JsonStorage.class);

        private final ObjectMapper mapper = new ObjectMapper();
        private final String FILE_PATH = "json-storage/tariff-config.json";

        public boolean exists(String tpName) {

                logger.debug("Checking JSON existence for tpName={}", tpName);

                try {
                        File file = new File(FILE_PATH);

                        // FIX: handle file not exists OR empty file
                        if (!file.exists() || file.length() == 0) {

                                logger.debug("JSON file not found OR empty path={}", FILE_PATH);

                                return false;
                        }

                        Map<String, Object> json = mapper.readValue(file, new TypeReference<Map<String, Object>>() {
                        });

                        boolean exists = json.containsKey(tpName);

                        logger.debug("JSON exists={} for tpName={}", exists, tpName);

                        return exists;
                }

                catch (Exception e) {

                        logger.error("Error checking JSON existence tpName={}", tpName, e);

                        return false;
                }
        }

        public void store(

                        String tpName,
                        String username,
                        Long networkId,
                        Map<String, Object> request) {

                logger.info("Storing JSON config tpName={} username={} networkId={}", tpName, username, networkId);

                try {
                        File file = new File(FILE_PATH);

                        file.getParentFile().mkdirs();

                        Map<String, Object> json = new LinkedHashMap<>();

                        // FIX: read only if file has data
                        if (file.exists() && file.length() > 0) {

                                logger.debug("Existing JSON file found. Loading data");

                                json = mapper.readValue(file, new TypeReference<Map<String, Object>>() {
                                });
                        }

                        Map<String, Object> tpData = new LinkedHashMap<>();

                        tpData.put("tpName", tpName);
                        tpData.put("username", username);
                        tpData.put("networkId", networkId);
                        tpData.put("data", request);
                        json.put(tpName, tpData);

                        mapper.writerWithDefaultPrettyPrinter().writeValue(file, json);

                        logger.info("JSON stored successfully for tpName={}", tpName);

                }

                catch (Exception e) {

                        logger.error("Error storing JSON tpName={}", tpName, e);
                }
        }

        public Set<String> getAllTpNames() {

                logger.debug("Fetching all TP names from JSON");

                try {
                        File file = new File(FILE_PATH);

                        // FIX: return empty if file empty
                        if (!file.exists() || file.length() == 0) {

                                logger.debug("JSON file not found OR empty while fetching TP names");

                                return Set.of();
                        }

                        Map<String, Object> json = mapper.readValue(file, new TypeReference<Map<String, Object>>() {
                        });

                        logger.debug("TP names fetched count={}", json.size());

                        return json.keySet();
                }

                catch (Exception e) {

                        logger.error("Error fetching TP names from JSON", e);

                        return Set.of();
                }
        }

        public Object getTpData(String tpName) {

                logger.info("Fetching TP data from JSON tpName={}", tpName);

                try {
                        File file = new File(FILE_PATH);

                        // FIX: return null if file empty
                        if (!file.exists() || file.length() == 0) {

                                logger.warn("JSON file not found OR empty for tpName={}", tpName);

                                return null;
                        }

                        Map<String, Object> json = mapper.readValue(file, new TypeReference<Map<String, Object>>() {
                        });

                        Object data = json.get(tpName);

                        logger.debug("TP data found={} for tpName={}", data != null, tpName);

                        return data;
                }

                catch (Exception e) {

                        logger.error("Error fetching TP data tpName={}", tpName, e);

                        return null;
                }
        }
}