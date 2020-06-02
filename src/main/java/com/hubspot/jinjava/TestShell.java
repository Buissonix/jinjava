package com.hubspot.jinjava;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;


// classe pour tester le remplacement d'un nom de fichier/répertoire dans un script bash
public class TestShell {

    public static void main(String[] args) throws IOException {
        File f1 = new File("src/main/resources/testUnzip.sh");
        File f2 = new File("src/main/resources/testZip.sh");

        String nom1 = "AAA";
        String nom2 = "BBB";

        String unzipScript =
                "#cd src/main/resources\n" +
                "#mkdir template\n" +
                "#mv template.zip ./template\n" +
                "#cd template\n" +
                "#unzip -n template.zip\n" +
                "#rm template.zip";

        String zipScript =
                "#cd src/main/resources/template\n" +
                "#zip -r template.zip ./*\n" +
                "#mv template.zip ../\n" +
                "#rm -rf template";

        unzipScript = unzipScript.replaceAll("template", nom1);
        zipScript = zipScript.replaceAll("template", nom2);

        BufferedWriter writer = new BufferedWriter(new FileWriter(f1.getPath()));
        writer.write(unzipScript);
        writer.close();

        writer = new BufferedWriter(new FileWriter(f2.getPath()));
        writer.write(zipScript);
        writer.close();

    }

}
