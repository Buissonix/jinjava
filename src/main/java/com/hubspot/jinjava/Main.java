package com.hubspot.jinjava;

import com.google.common.collect.Maps;
import com.hubspot.jinjava.util.ForLoop;
import com.hubspot.jinjava.util.ObjectIterator;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Main {

    private static final String TEMPLATE_NAME = "template";
    private static final String TEMPLATE_EXTENSION = ".docx";
    private static final String TEMPLATE_PATH = "src/main/resources/" + TEMPLATE_NAME + TEMPLATE_EXTENSION;
    private static final String XML_PATH = "src/main/resources/" + TEMPLATE_NAME + "/word/document.xml";

    // Récupère le contenu de document.xml dans une String pour que jinja puisse remplacer les {{placeholder}}
    private static String readFile(String file) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(file));
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
            //System.out.println("stringbuilder closed");
        }
    }

    private static boolean renommerEnZip(File templateDocx){
        Boolean bool = templateDocx.renameTo(new File("src/main/resources/" + TEMPLATE_NAME + ".zip"));
        return bool;
    }

    private static boolean renommerEnDocx(File templateZip){
        Boolean bool = templateZip.renameTo(new File(TEMPLATE_PATH));
        return bool;
    }

    // Injecte le nom du template dans les scripts bash
    private static void creerBashScripts() throws IOException {
        File unzip = new File("src/main/resources/unzip.sh");
        String unzipScript =
                "cd src/main/resources\n" +
                "mkdir XXX\n" +
                "mv XXX.zip ./XXX\n" +
                "cd XXX\n" +
                "unzip -n XXX.zip\n" +
                "rm XXX.zip\n";

        File zip = new File("src/main/resources/zip.sh");
        String zipScript =
                "cd src/main/resources/XXX\n" +
                "zip -r XXX.zip ./*\n" +
                "mv XXX.zip ../\n" +
                "rm -rf XXX";

        unzipScript = unzipScript.replaceAll("XXX", TEMPLATE_NAME);
        zipScript = zipScript.replaceAll("XXX", TEMPLATE_NAME);

        BufferedWriter writer = new BufferedWriter(new FileWriter(unzip.getPath()));
        writer.write(unzipScript);
        writer.close();

        writer = new BufferedWriter(new FileWriter(zip.getPath()));
        writer.write(zipScript);
        writer.close();
    }

    // Dézippe le template en affichant l'output de l'OS dans la console
    private static boolean unzip() throws IOException, InterruptedException {
        boolean bool = true;

        System.out.println("Unzipping...");
        try {
            ProcessBuilder pb = new ProcessBuilder();
            pb.command("src/main/resources/unzip.sh");
            Process process = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null)
                System.out.println("unzip process: " + line);
            int exitVal = process.waitFor();
            if (exitVal == 0) {
                System.out.println("unzip success!");
            } else {
                System.out.println("Le dézippage s'est terminé avec un code " + exitVal + " (anormal)");
                bool = false;
            }
        } catch (Exception e){
            bool = false;
            System.out.println(e.getMessage());
            e.printStackTrace();
        } finally {
            return bool;
        }
    }

    // Zippe le CV rempli en affichant l'output de l'OS dans la console
    private static boolean zip() throws IOException, InterruptedException {
        boolean bool = true;

        System.out.println("Zipping...");
        try {
            ProcessBuilder pb = new ProcessBuilder();
            pb.command("src/main/resources/zip.sh");
            Process process = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null)
                System.out.println("zip process: " + line);
            int exitVal = process.waitFor();
            if (exitVal == 0) {
                System.out.println("zip success!");
            } else {
                System.out.println("Le zippage s'est terminé avec un code " + exitVal + " (anormal)");
                bool = false;
            }
        } catch (Exception e){
            bool = false;
            System.out.println(e.getMessage());
            e.printStackTrace();
        } finally {
            return bool;
        }
    }

    public static void main(String[] args) throws IOException, InterruptedException {

        File templateDocx = new File(TEMPLATE_PATH);
        File templateZip = new File("src/main/resources/" + TEMPLATE_NAME + ".zip");

        // trouver le template format .docx
        if(!(templateDocx.exists())){
            System.out.println("Le template " + templateDocx.getPath() + " est introuvable");
        }

        // convertir de .docx à .zip
        if (!(renommerEnZip(templateDocx))){
            System.out.println("Le template n'a pas pu être converti en archive .zip");
        }

        // trouver le template format .zip
        if(!(templateZip.exists())){
            System.out.println("Le template " + templateZip.getPath() + " est introuvable");
        }

        // injecter le nom du template dans les scripts bash
        try {
            creerBashScripts();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println(e.getStackTrace());
        }

        // dézipper l'archive pour pouvoir éditer le XML et remplacer les valeurs
        if(templateZip.exists()){
            try {
                unzip();
            } catch (Exception e) {
                System.out.println(e.getMessage());
                System.out.println(e.getStackTrace());
            }
        }

        // Enlever les balises XML indésirables qui se rajoutent dans les champs à remplacer
        String contenuDuXml = readFile(XML_PATH);
        CleanXml.setContenuDuXml(contenuDuXml);
        CleanXml.corrigerXML();
        String cleanXml = CleanXml.getContenuDuXml();

        // Remplacer les champs par les valeurs souhaitées
        System.out.println("Jinja processing...");

        List<HashMap<String,String>> listeCours = new ArrayList<>();

        HashMap<String, String> coursFrancais = new HashMap<>();
        coursFrancais.put("nom", "Français");
        coursFrancais.put("note", "18");
        listeCours.add(coursFrancais);

        HashMap<String, String> coursMaths = new HashMap<>();
        coursMaths.put("nom", "Maths");
        coursMaths.put("note", "15");
        listeCours.add(coursMaths);

        ForLoop cours = ObjectIterator.getLoop(listeCours);

        Jinjava jinjava = new Jinjava();
        Map<String, Object> context = Maps.newHashMap();
        context.put("nomEleve", "Jordan");
        context.put("dateNaissance", "03/03/1995");
        context.put("cours", cours);

        String renderedTemplate = jinjava.render(cleanXml, context);

        // Ecrire le nouveau contenu dans document.xml
        File output = new File(XML_PATH);
        BufferedWriter writer = new BufferedWriter(new FileWriter(output.getPath()));
        writer.write(renderedTemplate);
        writer.close();

        System.out.println("---");

        // Zipper le tout pour recréer un CV rempli en .docx
        File dossier = new File("src/main/resources/" + TEMPLATE_NAME);
        if(dossier.exists()){
            try {
                zip();
            } catch (Exception e) {
                System.out.println(e.getMessage());
                System.out.println(e.getStackTrace());
            }
        } else {
            System.out.println("Pas de dossier " + dossier.getPath() + " trouvé, le processus de zippage a donc été annulé.");
        }

        // convertir de .zip à .docx
        if (!(renommerEnDocx(templateZip))){
            System.out.println("Le CV au format .zip n'a pas pu être converti en fichier .docx");
            return;
        }
    }
}
