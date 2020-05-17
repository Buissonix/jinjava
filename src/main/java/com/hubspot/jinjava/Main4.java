package com.hubspot.jinjava;

import com.google.common.base.Charsets;
import com.google.common.collect.Maps;
import com.google.common.io.Resources;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.function.Consumer;


public class Main4 {

    // Permet de récupérer l'output de l'éxecution des scripts bash
    private static class StreamGobbler implements Runnable {
        private InputStream inputStream;
        private Consumer<String> consumer;

        public StreamGobbler(InputStream inputStream, Consumer<String> consumer) {
            this.inputStream = inputStream;
            this.consumer = consumer;
        }

        @Override
        public void run() {
            new BufferedReader(new InputStreamReader(inputStream)).lines()
                    .forEach(consumer);
        }
    }

    // Récupérer le contenu de document.xml
    private static String readFile(String file) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader (file));
        String         line = null;
        StringBuilder  stringBuilder = new StringBuilder();
        String         ls = System.getProperty("line.separator");

        try {
            while((line = reader.readLine()) != null) {
                stringBuilder.append(line);
                //stringBuilder.append(ls);
            }
            return stringBuilder.toString();
        } finally {
            reader.close();
            System.out.println("stringbuilder closed");
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {

        // TODO créer une variable finale CHEMIN_TEMPLATE et l'injecter dans les scripts bash
        // TODO avoir déjà un dossier décompressé pour chaque template (décompresser seulement la 1ère fois)

        // Check si le .docx existe
        File f1 = new File("src/main/resources/template.docx");
        System.out.println("template.docx exists ? " + f1.exists());
        System.out.println("---");

        // .docx -> .zip
        Boolean bool = f1.renameTo(new File("src/main/resources/template.zip"));
        System.out.println("renamed to .zip ? " + bool);
        System.out.println("---");

        // Check si le .zip existe
        File f2 = new File("src/main/resources/template.zip");
        System.out.println(f2.exists() ? ".zip found!" : "No .zip found");
        System.out.println("---");

        // Si le .zip existe:

        if (f2.exists()) {

            // 1. unzip
            System.out.println("Unzipping...");

            Process process2;

            process2 = Runtime.getRuntime().exec(String.format("bash -c src/main/resources/unzip.sh"));
            StreamGobbler streamGobbler = new StreamGobbler(process2.getInputStream(), System.out::println);
            Executors.newSingleThreadExecutor().submit(streamGobbler);
            int exitCode = process2.waitFor();

            System.out.println("---");

            // 2. jinja
            System.out.println("Jinja processing...");
            Jinjava jinjava = new Jinjava();
            Map<String, Object> context = Maps.newHashMap();
            context.put("replaceMe", "hello world");

//            File xml = new File("src/main/resources/template/word/document.xml");
//            System.out.println("document.xml exists ? " + xml.exists());
//
//            String template = readFile("src/main/resources/template/word/document.xml");
//            String renderedTemplate = jinjava.render(template, context);
//            System.out.println("replaceMe still exists ? " + renderedTemplate.contains("replaceMe"));
//            System.out.println("hello world exists ? " + renderedTemplate.contains("hello"));
//
//            BufferedWriter writer = new BufferedWriter(new FileWriter("/Users/bastien/IdeaProjects/jinjava/src/main/resources/template/word/document.xml"));
//            writer.write(renderedTemplate);
//            writer.close();

            System.out.println("---");

            // 3. zip
            System.out.println("Zipping...");
            Process zipProcess = Runtime.getRuntime().exec("src/main/resources/zip.sh");
            zipProcess.waitFor();
            System.out.println("---");

            // 4. .zip -> .docx
            System.out.println("changing extension to .docx...");
            File f3 = new File("src/main/resources/template.zip");
            Boolean bool2 = f3.renameTo(new File("src/main/resources/template.docx"));
            System.out.println("Renamed to .docx ? " + bool2);
            System.out.println("---");

        }
    }
}
