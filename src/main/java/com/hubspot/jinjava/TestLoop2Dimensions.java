package com.hubspot.jinjava;

import com.google.common.collect.Maps;
import com.hubspot.jinjava.util.ForLoop;
import com.hubspot.jinjava.util.ObjectIterator;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TestLoop2Dimensions {

    // teste le remplacement des boucles imbriquées (boucle dans une boucle) ex: boucle de taches dans boucle d'expériences pro
    public static void main(String[] args) throws IOException {

        File inputFile = new File("src/main/resources/loop2D.txt");
        String inputString = readFile(inputFile.getPath());

        List<Object> experiencesPro = new ArrayList<>();

        // Taches
        List<Map<String, Object>> listeTachesExp1 = new ArrayList<>();
        List<Map<String, Object>> listeTachesExp2 = new ArrayList<>();
        HashMap<String,Object> exp1_tache1 = new HashMap<>();
        exp1_tache1.put("nom", "Expérience 1 tâche 1");
        exp1_tache1.put("technos", "Spring");
        listeTachesExp1.add(exp1_tache1);
        HashMap<String,Object> exp1_tache2 = new HashMap<>();
        exp1_tache2.put("nom", "Expérience 1 tâche 2");
        exp1_tache2.put("technos", "UML");
        listeTachesExp1.add(exp1_tache2);
        HashMap<String,Object> exp2_tache1 = new HashMap<>();
        exp2_tache1.put("nom", "Expérience 2 tâche 1");
        exp2_tache1.put("technos", "Hibernate");
        listeTachesExp2.add(exp2_tache1);
        HashMap<String,Object> exp2_tache2 = new HashMap<>();
        exp2_tache2.put("nom", "Expérience 2 tâche 2");
        exp2_tache2.put("technos", "Spring");
        listeTachesExp2.add(exp2_tache2);
        HashMap<String,Object> exp2_tache3 = new HashMap<>();
        exp2_tache3.put("nom", "Expérience 2 tâche 3");
        exp2_tache3.put("technos", "Angular");
        listeTachesExp2.add(exp2_tache3);

        Map<String, Object> experience1 = new HashMap<>();
        experience1.put("nom", "Développeur back-end JAVA");
        experience1.put("entreprise", "Crédit Agricole");
        experience1.put("date", "06/2019 à 06/2020");
        experience1.put("taches", ObjectIterator.getLoop(listeTachesExp1));
        experiencesPro.add(experience1);

        HashMap<String, Object> experience2 = new HashMap<>();
        experience2.put("nom", "Consultant JAVA");
        experience2.put("entreprise", "BNP Paribas");
        experience2.put("date", "03/2018 à 05/2019");
        experience2.put("taches", ObjectIterator.getLoop(listeTachesExp2));
        experiencesPro.add(experience2);

        ForLoop experiencesProIterator = ObjectIterator.getLoop(experiencesPro);

        Jinjava jinjava = new Jinjava();
        Map<String, Object> context = Maps.newHashMap();
        context.put("nomEmploye", "Vincent MARTIN");
        context.put("dateNaissance", "03/03/1995");
        context.put("experiencesPro", experiencesProIterator);

        String renderedTemplate = jinjava.render(inputString, context);
        System.out.println(renderedTemplate);

        File output = new File("src/main/resources/outputLoop.txt");
        BufferedWriter writer = new BufferedWriter(new FileWriter(output.getPath()));
        writer.write(renderedTemplate);
        writer.close();

    }

    // Récupère le contenu de loop2D.txt dans une String
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

