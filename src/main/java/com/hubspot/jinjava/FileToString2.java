package com.hubspot.jinjava;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class FileToString2 {

    public static void main(String[] args) {

        String path = "/Users/bastien/IdeaProjects/jinjava/src/main/resources/template/word/document.xml";

        try (Stream<String> lines = Files.lines(Paths.get(path))) {

            // Formatting like \r\n will be lost
            // String content = lines.collect(Collectors.joining());

            // UNIX \n, WIndows \r\n
            String content = lines.collect(Collectors.joining(System.lineSeparator()));
            System.out.println("replaceMe still exists ? " + content.contains("replaceMe"));
            System.out.println("hello world exists ? " + content.contains("hello"));


            // File to List
            //List<String> list = lines.collect(Collectors.toList());

        } catch (IOException e) {
            e.printStackTrace();
        }

    }

}
