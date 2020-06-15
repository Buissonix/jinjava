package com.hubspot.jinjava;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.Map;

public class TestJSONParser {

    public static void main(String args[]) {
        ObjectMapper mapper = new ObjectMapper();


        /**
         * Read JSON from a file into a Map
         */
        try {
            Map<String, Object> carMap = mapper.readValue(new File(
                    "src/main/resources/dict2.txt"), new TypeReference<Map<String, Object>>() {
            });

            System.out.println("car : " + carMap.get("car"));
            System.out.println("Price : " + carMap.get("price"));
            System.out.println("Model : " + carMap.get("model"));
            System.out.println("Colors : " + carMap.get("colors"));

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}