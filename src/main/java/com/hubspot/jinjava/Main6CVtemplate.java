package com.hubspot.jinjava;

import com.google.common.collect.Maps;
import java.io.*;
import java.util.ArrayList;
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

    // Vérifie la validité d'un template pour éviter que jinja ne génère une exception.
    // Quand un docx est converti en xml, si le formatage d'un {{placeholder}} n'est pas consistent et homogène,
    // des balises xml se rajoutent dans le champ ( exemple: {{<couleur>placeholder<italique>}} )
    // et jinja génère donc une exception. La regexp se charge de trouver ces pb de formatage.
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
            System.out.println("Le remplissage du template est annulé car les champs à remplacer suivants ne sont pas formatés correctement:");

            for (String placeholder : problemeFormattage){
                ++count;
                System.out.println(count + ". {{" + placeholder + "}}");
            }

            System.out.println("Dans le template .docx, veuillez vérifier le formatage de chaque {{champ}} cité et l'appliquer de manière consistante et homogène." +
                    " Dans le doute vous pouvez effacer le formatage et le refaire.");
            return false;
        }
        System.out.println("Le formatage de chaque {{champ}} du template est correct.");
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

            Jinjava jinjava = new Jinjava();
            Map<String, Object> context = Maps.newHashMap();
            context.put("name", "Peter Jackson");
            context.put("job", "Développeur Java junior");
            context.put("email", "peter.jackson@gmail.com");
            context.put("phone", "06 33 44 55 69");
            context.put("birthday", "03/10/1967");
            context.put("address", "3 rue de la Paix, Paris");
            context.put("University", "Institut Français des Affaires");
            context.put("major", "Formation devlog");
            context.put("date", "2019-2020");

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
