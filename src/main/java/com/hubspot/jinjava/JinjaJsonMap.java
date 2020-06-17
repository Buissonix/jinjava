package com.hubspot.jinjava;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class JinjaJsonMap {
    public static void main(String[] args) throws IOException {
        Jinjava jinjava = new Jinjava();
        System.out.println(jinjava.render("{% for hobby in Hobbies %}{{hobby}}\n{% endfor %}", createContextFromJson()));
    }


    private static Map<String, Object> createContextFromJson() throws IOException {
        ObjectMapper mapper = new ObjectMapper();

        // TODO insérer le JSON complet obtenu
        Map<String, Object> context = mapper.readValue(new File(
                "src/main/resources/MockJsonArray.json"), new TypeReference<Map<String, Object>>() {
        });
        return context;
    }
}
