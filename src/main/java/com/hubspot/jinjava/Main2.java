package com.hubspot.jinjava;

import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import com.google.common.io.Resources;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;

public class Main2 {
    public static void main(String[] args) throws IOException {
        System.out.println("Jinja processing...");
        Jinjava jinjava = new Jinjava();
        Map<String, Object> context = Maps.newHashMap();
        context.put("replaceMe", "Hello World");

        String template = Resources.toString(Resources.getResource("document.xml"), Charsets.UTF_8);
        String renderedTemplate = jinjava.render(template, context);

        BufferedWriter writer = new BufferedWriter(new FileWriter("src/main/resources/documentNEW.xml"));
        writer.write(renderedTemplate);
        writer.close();
    }
}
