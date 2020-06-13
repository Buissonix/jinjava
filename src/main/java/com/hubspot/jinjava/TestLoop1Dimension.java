package com.hubspot.jinjava;

import com.google.common.collect.Maps;
import com.hubspot.jinjava.util.ForLoop;
import com.hubspot.jinjava.util.ObjectIterator;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestLoop1Dimension {

    // teste le remplacement des boucles simples (sans boucles imbriquées)
    public static void main(String[] args) throws IOException {

        File inputFile = new File("src/main/resources/loop1D.txt");
        String inputString = readFile(inputFile.getPath());

        List<HashMap<String,String>> experiencesPro = new ArrayList<>();

        HashMap<String, String> exp1 = new HashMap<>();
        exp1.put("nom", "Développeur back-end JAVA");
        exp1.put("entreprise", "Crédit Agricole");
        exp1.put("date", "06/2019 à 06/2020");
        experiencesPro.add(exp1);

        HashMap<String, String> exp2 = new HashMap<>();
        exp2.put("nom", "Consultant JAVA");
        exp2.put("entreprise", "BNP Paribas");
        exp2.put("date", "03/2018 à 05/2019");
        experiencesPro.add(exp2);

        ForLoop experiencesProIterator = ObjectIterator.getLoop(experiencesPro);

        Jinjava jinjava = new Jinjava();
        Map<String, Object> context = Maps.newHashMap();
        context.put("nomEmploye", "Vincent MARTIN");
        context.put("dateNaissance", "03/03/1995");
        context.put("experiencesPro", experiencesProIterator);

        String renderedTemplate = jinjava.render(inputString, context);
        System.out.println(renderedTemplate);



    }

    // Récupère le contenu de loop1D.txt dans une String
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
}

