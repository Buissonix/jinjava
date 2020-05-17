package com.hubspot.jinjava;

import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import com.google.common.io.Resources;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Map;


public class Main {
    public static void main(String[] args) throws IOException, InterruptedException {

        // TODO créer une variable finale CHEMIN_TEMPLATE et l'injecter dans les scripts bash
        // TODO avoir déjà un dossier décompressé pour chaque template (décompresser seulement la 1ère fois)

        // Check si le .docx existe
        File f1 = new File("src/main/resources/template.docx");
        System.out.println("template.docx exists ? " + f1.exists());
        System.out.println("---");

        // .docx -> .zip
        Boolean bool = f1.renameTo(new File("src/main/resources/template.zip"));
        System.out.println("Renamed to .zip ? " + bool);
        System.out.println("---");

        // Check si le .zip existe
        File f2 = new File("src/main/resources/template.zip");
        System.out.println(f2.exists() ? ".zip found!" : "No .zip found");

        // Si le .zip existe:

        if (f2.exists()) {

            // 1. unzip
            System.out.println("Unzipping...");
            Process unzipProcess = Runtime.getRuntime().exec("src/main/resources/unzip.sh");
            unzipProcess.waitFor();

            // 2. jinja
            System.out.println("Jinja processing...");
            Jinjava jinjava = new Jinjava();
            Map<String, Object> context = Maps.newHashMap();
            context.put("replaceMe", "hello world");



            String template = Resources.toString(Resources.getResource("template/word/document.xml"), Charsets.UTF_8);
            String renderedTemplate = jinjava.render(template, context);

            BufferedWriter writer = new BufferedWriter(new FileWriter("/Users/bastien/IdeaProjects/jinjava/src/main/resources/template/word/document.xml"));
            writer.write(renderedTemplate);
            writer.close();

            // 3. zip
            System.out.println("Zipping...");
            Process zipProcess = Runtime.getRuntime().exec("src/main/resources/zip.sh");
            zipProcess.waitFor();

            // 4. .zip -> .docx
            System.out.println("changing extension to .docx...");
            File f3 = new File("src/main/resources/template/template.zip");
            Boolean bool2 = f3.renameTo(new File("src/main/resources/template.docx"));
            System.out.println("Renamed to .docx ? " + bool2);
            System.out.println("---");

        }
    }
}
