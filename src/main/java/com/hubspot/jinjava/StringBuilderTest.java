package com.hubspot.jinjava;

import com.google.common.collect.Maps;

import java.io.*;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.function.Consumer;


public class StringBuilderTest {

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
            System.out.println("in stringbuilder");
            while((line = reader.readLine()) != null) {
                System.out.println("line found!");
                stringBuilder.append(line);
                //stringBuilder.append(ls);
            }
            return stringBuilder.toString();
        } finally {
            System.out.println("closing stringbuilder");
            reader.close();
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {

            File xml = new File("src/main/resources/template/word/document.xml");
            System.out.println(xml.getAbsolutePath());
            System.out.println("document.xml exists ? " + xml.exists());

            String template = readFile("src/main/resources/template/word/document.txt");
            System.out.println(template);


    }
}
