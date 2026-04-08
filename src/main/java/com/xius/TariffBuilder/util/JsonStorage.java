package com.xius.TariffBuilder.util;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Component;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.Map;

@Component
public class JsonStorage {

    private final ObjectMapper mapper =
            new ObjectMapper();

    private final String FILE_PATH =
            "json-storage/tariff-config.json";


    public boolean exists(String tpName){

        try{

            File file = new File(FILE_PATH);

            if(!file.exists()){

                return false;
            }


            Map<String,Object> json =
                    mapper.readValue(
                            file,
                            new TypeReference<Map<String,Object>>() {}
                    );


            return json.containsKey(tpName);
        }

        catch(Exception e){

            return false;
        }
    }



    public void store(

            String tpName,

            String username,

            Long networkId,

            Map<String,Object> request){

        try{

            File file = new File(FILE_PATH);

            file.getParentFile().mkdirs();


            Map<String,Object> json =
                    new LinkedHashMap<>();


            if(file.exists()){

                json =
                        mapper.readValue(
                                file,
                                new TypeReference<Map<String,Object>>() {}
                        );
            }


            Map<String,Object> tpData =
                    new LinkedHashMap<>();


            tpData.put("tpName", tpName);

            tpData.put("username", username);

            tpData.put("networkId", networkId);

            tpData.put("data", request);


            json.put(tpName, tpData);


            mapper
                    .writerWithDefaultPrettyPrinter()
                    .writeValue(file, json);

        }

        catch(Exception e){

            e.printStackTrace();
        }
    }
}