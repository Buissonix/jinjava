package com.hubspot.jinjava;

import java.io.*;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class CleanXml {

    // le main ne sert qu'à des fins de test
    public static void main(String[] args) throws IOException, InterruptedException {
//
//        contenuDuXml = readFile("src/main/resources/template/word/document.xml");
//
//        // output le contenu du XML initial dans input.txt
//        File input = new File("src/main/resources/input.txt");
//        BufferedWriter writer = new BufferedWriter(new FileWriter(input.getPath()));
//        writer.write(contenuDuXml);
//        writer.close();
//
//        corrigerXML();
//
//        // output le contenu du XML corrigé dans output.txt
//        File output = new File("src/main/resources/output.txt");
//        writer = new BufferedWriter(new FileWriter(output.getPath()));
//        writer.write(contenuDuXml);
//        writer.close();
    }

    private static String contenuDuXml;

    public static String getContenuDuXml() {
        return contenuDuXml;
    }

    public static void setContenuDuXml(String contenuDuXml) {
        CleanXml.contenuDuXml = contenuDuXml;
    }

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
        Pattern pattern = Pattern.compile(escapedRegexString);
        Matcher matcher = pattern.matcher(contenuDuXml);

        int count = 0;
        List<String> listeProblemes = new ArrayList<>();

        while(matcher.find()) {
            ++count;
            listeProblemes.add(matcher.group(1));
            //System.out.println(listeProblemes.get(count-1));
        }

//        if (listeProblemes.size() > 0 ){
//            count = 0;
//
//            for (String placeholder : listeProblemes){
//                ++count;
//                System.out.println("---");
//                System.out.println("prob " + count + ". " + placeholder);
//                System.out.println("---");
//            }
//
//            System.out.println("");
//        }
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

    // Ajoute un espace entre les délimiteurs {% / %} et leur contenu
    private static List<String> cleanEspaces(List<String> listeProbleme, int leftOrRight) {
        // UPDATE: les espaces n'empêchent pas le bon fonctionnement, cela ne change rien au final
        // Aucune différence de fonctionnement entre {% for A in B %} et {%for a in B   %}
        List<String> listeClean = new ArrayList<>(listeProbleme);

        if (!(listeClean.isEmpty())) {
            String remplacement;
            if (leftOrRight == 0) {
                for (int i = 0; i < listeClean.size(); i++) {
                    remplacement = listeClean.get(i).substring(0, 2) + " " + listeClean.get(i).substring(2);
                    listeClean.set(i, remplacement);
                }
            } else {
                for (int i = 0; i < listeClean.size(); i++) {
                    remplacement = listeClean.get(i).substring(0, 1) + " " + listeClean.get(i).substring(1, 3);
                    listeClean.set(i, remplacement);
                }
            }
        }
        return listeClean;
    }

    // Remplace les mauvais champs du XML par les champs propres
    private static void reinjecterChampsPropres(List<String> listeChampsARemplacer, List<String> listeClean){

        if (!(listeChampsARemplacer.isEmpty())){
            for (int i = 0; i < listeChampsARemplacer.size(); i++){
                setContenuDuXml(contenuDuXml.replaceAll(escapeMetaCharacters(listeChampsARemplacer.get(i)), listeClean.get(i)));
                System.out.println("----");
                System.out.println("La string :");
                System.out.println(listeChampsARemplacer.get(i));
                System.out.println("a été remplacée par :");
                System.out.println(listeClean.get(i));
                System.out.println("------");
            }
        }
    }

    // Enlève le XML indésirable qui se rajoute tout seul pour éviter que jinjava ne génère une exception.
    static void corrigerXML(){
        System.out.println();
        System.out.println("Vérification du template...");

        List<String> listeChampsARemplacer = new ArrayList<>();
        List<String> listeChampsPropres = new ArrayList<>();
        List<String> listeRegex = new ArrayList<>();

        // Pour supprimer les balises xml
        String regexChampSimple = "({{(?:[^><}]*?)(?:<[^}]*?)}}).*?";
        String regexForLoop = "({%(?:[^><}]*?)(?:<[^}]*?)%}).*?";

        listeRegex.add(regexChampSimple); listeRegex.add(regexForLoop);

        for (String regex: listeRegex){
            listeChampsARemplacer = retournerListeMatchs(escapeMetaCharacters(regex), contenuDuXml);
            listeChampsARemplacer = enleverDoublonListe(listeChampsARemplacer);
            listeChampsPropres = cleanBalises(listeChampsARemplacer);
            reinjecterChampsPropres(listeChampsARemplacer, listeChampsPropres);
        }
        listeChampsARemplacer.clear(); listeChampsPropres.clear(); listeRegex.clear();

        // Pour corriger tous les mauvais {% endfor %} à la volée
        String regexEndLoop = "({%(?:[^%])*?endfor(?:[^%])*?%}).*?";

        listeChampsARemplacer = retournerListeMatchs(escapeMetaCharacters(regexEndLoop), contenuDuXml);
        listeChampsARemplacer = enleverDoublonListe(listeChampsARemplacer);
        int indexNormal = listeChampsARemplacer.indexOf("{% endfor %}");
        if (indexNormal != -1) listeChampsARemplacer.remove(indexNormal);
        for (String match : listeChampsARemplacer) {
            listeChampsPropres.add("{% endfor %}");
        }
        reinjecterChampsPropres(listeChampsARemplacer, listeChampsPropres);

        System.out.println("Template vérifié ou corrigé.");

    }
}
