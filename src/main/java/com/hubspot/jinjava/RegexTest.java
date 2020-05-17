package com.hubspot.jinjava;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexTest {

    private static Pattern pattern;
    private static Matcher matcher;

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
        }
    }

    private static void isXmlValide(String contenuDuXml){
        System.out.println("");
        System.out.println("Vérification du template...");

        String regexString = "{{(?:[^}]*?)>([^><]+)<(?:.*?)}}";
        String escaped = escapeMetaCharacters(regexString);

        pattern = Pattern.compile(escaped);
        matcher = pattern.matcher(contenuDuXml);
        int count = 0;
        while(matcher.find()) {
            ++count;
            System.out.println("Trouvé !");
            System.out.println(matcher.group(1));
        }
        System.out.println("fin de la recherche");


        //String[] problemeFormattage = contenuDuXml.split(escaped);

//        if (problemeFormattage.length != 0){
//            int count = 0;
//            System.out.println("Le remplissage du template est annulé car les champs à remplacer suivants ne sont pas formatés correctement:");
//
//            for (String placeholder : problemeFormattage){
//                ++count;
//                System.out.println(count + ". {{" + placeholder + "}}");
//            }
//
//            System.out.println("Dans le template .docx, veuillez vérifier le formatage de chaque {{champ}} cité et l'appliquer de manière consistante et homogène." +
//                    " Dans le doute vous pouvez effacer le formatage et le refaire.");
//            return false;
//        }
//        System.out.println("Le formatage de chaque {{champ}} du template est correct.");
//        System.out.println("");
//        return true;
    }

    private static String escapeMetaCharacters(String inputString){
        //final String[] metaCharacters = {"\\","^","$","{","}","[","]","(",")",".","*","+","?","|","<",">","-","&","%"};
        final String[] metaCharacters = {"\\","$","{","}","<",">","|","-","&","%"};

        for (int i = 0 ; i < metaCharacters.length ; i++){
            if(inputString.contains(metaCharacters[i])){
                inputString = inputString.replace(metaCharacters[i],"\\"+metaCharacters[i]);
            }
        }
        System.out.println("Escaped regex String: " + inputString);
        return inputString;
    }

    public static void main(String[] args) throws IOException {
        String template = readFile("src/main/resources/template/word/document.xml");
        System.out.println("contenu xml:" + template.substring(0,100));
        isXmlValide(template);
    }
}
