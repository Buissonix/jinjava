package com.hubspot.jinjava;

import com.google.common.collect.Maps;
import com.hubspot.jinjava.util.ForLoop;
import com.hubspot.jinjava.util.ObjectIterator;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class Main6CVtemplate {

    private static Pattern pattern;
    private static Matcher matcher;

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

    // Vérifie la validité d'un template pour éviter que jinjava ne génère une exception.
    // Quand un docx est converti en xml, des fois le {{placeholder}} se retrouve injecté de
    // balises qui empêchent jinjava de fonctionner, par exemple: {{<balise1>placeholder<balise2>}}
    // Cela est dû au fait qu'il faut dans Word taper les {{placeholder}} d'une seule traite sans appuyer sur "retour arrière" du clavier (oui pour de vrai)
    // La regexp se charge de trouver ces pb de formatage.
    // TODO tester une fonction qui vire les balises à l'extérieur ex: {{<balise1>placeholder<balise2>}} -> <balise1>{{placeholder}}<balise2>
    private static boolean isXmlValide(String contenuDuXml){
        System.out.println("");
        System.out.println("Vérification du template...");

        String regexString = "{{(?:[^}]*?)>([^><]+)<(?:.*?)}}";
        String escapedRegexString = escapeMetaCharacters(regexString);

        pattern = Pattern.compile(escapedRegexString);
        matcher = pattern.matcher(contenuDuXml);

        int count = 0;
        List<String> problemeFormattage = new ArrayList<>();

        while(matcher.find()) {
            ++count;
            problemeFormattage.add(matcher.group(1));
            //System.out.println(problemeFormattage.get(count-1));
        }

        if (problemeFormattage.size() > 0 ){
            count = 0;
            System.out.println("Le remplissage du template est annulé car les champs suivants posent problème:");

            for (String placeholder : problemeFormattage){
                ++count;
                System.out.println(count + ". {{" + placeholder + "}}");
            }

            System.out.println("Dans Word, veuillez retaper les {{champ}} de votre template cités au dessus d'une seule traite, " +
                    "sans appuyer sur 'retour arrière'. Conseillé: ctrl+a et retaper le {{champ}} sans se tromper ni supprimer un caractère" +
                    "durant sa réécriture.");
            return false;
        }
        System.out.println("Template accepté!");
        System.out.println("");
        return true;
    }

    private static String escapeMetaCharacters(String inputString){
        //final String[] metaCharacters = {"\\","^","$","{","}","[","]","(",")",".","*","+","?","|","<",">","-","&","%"};
        final String[] metaCharacters = {"\\","$","{","}","<",">","|","-","&","%"};

        for (int i = 0 ; i < metaCharacters.length ; i++){
            if(inputString.contains(metaCharacters[i])){
                inputString = inputString.replace(metaCharacters[i],"\\"+metaCharacters[i]);
            }
        }
        //System.out.println("Escaped regex String: " + inputString);
        return inputString;
    }

    public static void main(String[] args) throws IOException, InterruptedException {
        // Check si le .docx existe
        File f1 = new File("src/main/resources/template.docx");
        System.out.println("template.docx exists ? " + f1.exists());
        System.out.println("---");

        // .docx -> .zip
        Boolean bool = f1.renameTo(new File("src/main/resources/template.zip"));
        System.out.println("Renamed to .zip ? " + bool);
        System.out.println("---");

        // Check si le .zip existe
        File zipFile = new File("src/main/resources/template.zip");
        System.out.println(zipFile.exists() ? ".zip found!" : "No .zip found");

        // Si le .zip existe:

        // 1. unzip
        if (zipFile.exists()) {

            System.out.println("Unzipping...");
            ProcessBuilder pb = new ProcessBuilder();
            pb.command("src/main/resources/unzip.sh");
            Process process = pb.start();
            BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            while ((line = reader.readLine()) != null)
                System.out.println("unzip process: " + line);
            int exitVal = process.waitFor();
            if (exitVal == 0) System.out.println("unzip success!");
        }

        // 2. jinja
        String template = readFile("src/main/resources/template/word/document.xml");
        if (isXmlValide(template)) {
            System.out.println("Jinja processing...");

            List<HashMap<String, String[]>> jobs = new ArrayList<>();

            Jinjava jinjava = new Jinjava();
            Map<String, Object> context = Maps.newHashMap();
            context.put("name", "Peter Jackson");
            context.put("job", "Développeur Java junior");
            context.put("mail", "peter.jackson@gmail.com");
            context.put("phone", "06 33 44 55 69");
            context.put("birthday", "03/10/1967");
            context.put("address", "3 rue de la Paix, Paris");
            context.put("university", "Institut Français des Affaires");
            context.put("major", "Formation devlog");
            context.put("date", "2019-2020");
            context.put("nobackspace", "No Backspace");
            context.put("backspacein", "Backspace in");
            context.put("backspaceout", "Backspace out");
            context.put("jobs",jobs);

            String renderedTemplate = jinjava.render(template, context);

            BufferedWriter writer = new BufferedWriter(new FileWriter("/Users/bastien/IdeaProjects/jinjava/src/main/resources/template/word/document.xml"));
            writer.write(renderedTemplate);
            writer.close();

            System.out.println("---");

            // 3. zip
            System.out.println("Zipping...");
            Process zipProcess = Runtime.getRuntime().exec("src/main/resources/zip.sh");
            zipProcess.waitFor();

            // 4. .zip -> .docx
            System.out.println("changing extension to .docx...");
            File f3 = new File("src/main/resources/template.zip");
            Boolean bool2 = f3.renameTo(new File("src/main/resources/template.docx"));
            System.out.println("Renamed to .docx ? " + bool2);
            System.out.println("---");
        }


    }
}
