package com.hubspot.jinjava;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class RegexTest {

    public static void main(String[] args) throws IOException, InterruptedException {

        contenuDuXml = readFile("src/main/resources/template/word/document.xml");
        corrigerXML(contenuDuXml);

    }

    private static String contenuDuXml;
    private static Pattern pattern;
    private static Matcher matcher;


    // Récupère le contenu de document.xml dans une String
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

    // Echappe les caractères meta pour la regex
    private static String escapeMetaCharacters(String inputString){
        //final String[] allMetaCharacters = {"\\","^","$","{","}","[","]","(",")",".","*","+","?","|","<",">","-","&","%"};
        final String[] metaCharacters = {"\\","$","{","}","<",">","|","-","&","%"};

        for (int i = 0 ; i < metaCharacters.length ; i++){
            if(inputString.contains(metaCharacters[i])){
                inputString = inputString.replace(metaCharacters[i],"\\"+metaCharacters[i]);
            }
        }
        //System.out.println("Escaped regex String: " + inputString);
        return inputString;
    }

    // Renvoie une liste de tous les matchs de la regex
    private static List<String> retournerListeMatchs(String escapedRegexString, String contenuDuXml) {
        pattern = Pattern.compile(escapedRegexString);
        matcher = pattern.matcher(contenuDuXml);

        int count = 0;
        List<String> listeProblemes = new ArrayList<>();

        while(matcher.find()) {
            ++count;
            listeProblemes.add(matcher.group(1));
            //System.out.println(listeProblemes.get(count-1));
        }

        if (listeProblemes.size() > 0 ){
            count = 0;

            for (String placeholder : listeProblemes){
                ++count;
                System.out.println("---");
                System.out.println("prob " + count + ". {{" + placeholder + "}}");
                System.out.println("---");
            }

            System.out.println("");
        }
        return listeProblemes;
    }

    // Enlève les doublons d'une liste mais préserve l'ordre
    private static List<String> enleverDoublonListe(List<String> liste){
        Set<String> set = new LinkedHashSet<>(liste);
        liste.clear();
        liste.addAll(set);
        return liste;
    }

    // Supprime les balises <XML> à l'intérieur d'un champ à remplacer
    private static List<String> cleanBalises(List<String> listeProbleme) {
        String regexBalise = "<.*?>";
        String escapedRegex = escapeMetaCharacters(regexBalise);
        List<String> listeClean = new ArrayList<>(listeProbleme);

        if (!(listeClean.isEmpty())) {
            String remplacement;
            for (int i = 0; i < listeClean.size(); i++) {
                remplacement = listeClean.get(i).replaceAll(escapedRegex, "");
                listeClean.set(i, remplacement);
            }
        }
        return listeClean;
    }

    // TODO Supprime ou rajoute les espaces nécessaires dans les champs
    private static List<String> cleanEspaces(List<String> listeProbleme){
        return listeProbleme;
    }

    // Remplace les mauvais champs du XML par les champs propres
    private static void reinjecterChampsPropres(List<String> listeChampsARemplacer){

        if (!(listeChampsARemplacer.isEmpty())){
            List<String> listeClean = cleanEspaces(cleanBalises(listeChampsARemplacer));

            for (int i = 0; i < listeChampsARemplacer.size(); i++){
                contenuDuXml = contenuDuXml.replaceAll(escapeMetaCharacters(listeChampsARemplacer.get(i)), listeClean.get(i));
                System.out.println("----");
                System.out.println("La string :");
                System.out.println(listeChampsARemplacer.get(i));
                System.out.println("a été remplacée par :");
                System.out.println(listeClean.get(i));
                System.out.println("------");
                // TODO output le contenu dans un txt
            }
        }
    }

    // Enlève le XML indésirable qui se rajoute tout seul pour éviter que jinjava ne génère une exception.
    // TODO si supprimer les balises corrompt le template, tester une fonction qui vire les balises à l'extérieur ex: {{<balise1>placeholder<balise2>}} -> <balise1>{{placeholder}}<balise2>
    private static void corrigerXML(String contenuDuXml){
        String regexChampSimple = "({{(?:[^><}]*?)(?:<[^}]*?)}}).*?";
        String regexChampLoop = "{%(?:.)*?endfor(?:.)*?%}";


        System.out.println("");
        System.out.println("Vérification du template...");

        List<String> listeChampSimple = retournerListeMatchs(escapeMetaCharacters(regexChampSimple), contenuDuXml);
        listeChampSimple = enleverDoublonListe(listeChampSimple);
        reinjecterChampsPropres(listeChampSimple);

        //List<String> listeChampLoop = retournerListeMatchs(escapeMetaCharacters(regexChampLoop), contenuDuXml);
        //listeChampLoop = enleverDoublonListe(listeChampLoop);
        //reinjecterChampsPropres();

        System.out.println("Template vérifié ou corrigé.");

    }



}
