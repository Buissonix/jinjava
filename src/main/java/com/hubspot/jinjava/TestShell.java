package com.hubspot.jinjava;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class TestShell {

    public static void main(String[] args) throws IOException {
        File f1 = new File("src/main/resources/test.sh");

        String interpolationA = "AAA";
        String interpolationB = "BBB";

        String script =
                "#cd src/main/resources\n" +
                "#mkdir template\n" +
                "#mv template.zip ./template\n" +
                "#cd template\n" +
                "#unzip -n template.zip\n" +
                "#rm template.zip";

        script = script.replaceAll("template", interpolationA);
        script = script.replaceAll("zip", interpolationB);

        BufferedWriter writer = new BufferedWriter(new FileWriter(f1.getPath()));
        writer.write(script);
        writer.close();

    }

}
