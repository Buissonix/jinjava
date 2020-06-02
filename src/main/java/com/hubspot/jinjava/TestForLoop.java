package com.hubspot.jinjava;

import com.google.common.collect.Maps;
import com.hubspot.jinjava.util.ForLoop;
import com.hubspot.jinjava.util.ObjectIterator;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestForLoop {

    // teste le remplacement des champs simples et des boucles
    public static void main(String[] args) throws IOException {

        File inputFile = new File("src/main/resources/loop.txt");
        String inputString = readFile(inputFile.getPath());

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

        String renderedTemplate = jinjava.render(inputString, context);
        System.out.println(renderedTemplate);



    }

    // Récupère le contenu de loop.txt dans une String
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

