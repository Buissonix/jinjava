package com.hubspot.jinjava;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.util.Map;

public class TestJSONParser2 {

    public static void main(String args[]) {
        ObjectMapper mapper = new ObjectMapper();
        String inputString = "{% for item in accounting %}{{item}}\n{% endfor %}";
        try {
            Map<String, Object> context = mapper.readValue(new File(
                    "src/main/resources/dict4.txt"), new TypeReference<Map<String, Object>>() {
            });

            context.put("Rajout", "ceci a été rajouté avec context.put()");

            Jinjava jinjava = new Jinjava();
            String renderedTemplate = jinjava.render(inputString, context);

            System.out.println(renderedTemplate);



        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}