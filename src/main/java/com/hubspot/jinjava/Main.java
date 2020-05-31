package com.hubspot.jinjava;

import java.io.*;

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
        writer = new BufferedWriter(new FileWriter(zip.getPath()));
        writer.write(zipScript);
        writer.close();
    }

    // Dézippe le template en affichant l'output de l'OS dans le terminal
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

    // Zippe le CV rempli en affichant l'output de l'OS dans le terminal
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

    private static boolean renommerEnZip(File templateDocx){
        Boolean bool = templateDocx.renameTo(new File("src/main/resources/" + TEMPLATE_NAME + ".zip"));
        return bool;
    }

    private static boolean renommerEnDocx(File templateZip){
        Boolean bool = templateZip.renameTo(new File(TEMPLATE_PATH));
        return bool;
    }

    public static void main(String[] args) throws IOException, InterruptedException {

        File templateDocx = new File(TEMPLATE_PATH);
        File templateZip = new File("src/main/resources/" + TEMPLATE_NAME + ".zip");

        // trouver le template format .docx
        if(!(templateDocx.exists())){
            System.out.println("Le template " + templateDocx.getPath() + " est introuvable");
            return;
        }

        // convertir de .docx à .zip
        if (!(renommerEnZip(templateDocx))){
            System.out.println("Le template n'a pas pu être converti en archive .zip");
            return;
        }

        // trouver le template format .zip
        if(!(templateZip.exists())){
            System.out.println("Le template " + templateZip.getPath() + " est introuvable");
            return;
        }

        try {
            creerBashScripts();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println(e.getStackTrace());
        }

        // Dézipper l'archive pour pouvoir éditer le XML et remplacer les valeurs
        try {
            unzip();
        } catch (Exception e) {
            System.out.println(e.getMessage());
            System.out.println(e.getStackTrace());
        }

        // Enlever les balises XML indésirables qui se rajoutent dans les champs à remplacer
        String contenuDuXml = readFile(XML_PATH);
        CleanXml.corrigerXML(contenuDuXml);

        // Remplacer les champs par les valeurs souhaitées
//        System.out.println("Jinja processing...");
//
//        HashMap<String, List<String>> jobs = new HashMap<>();
//        List<String> previousJobs = new ArrayList<>();
//        previousJobs.add("Cuisinier"); previousJobs.add("Jardinier");
//        jobs.put("name", previousJobs );
//
//        Jinjava jinjava = new Jinjava();
//        Map<String, Object> context = Maps.newHashMap();
//        context.put("name", "Julie Martin");
//        context.put("job", "Développeur Java junior");
//        context.put("mail", "julie.martin@gmail.com");
//        context.put("phone", "06 33 44 55 69");
//        context.put("birthday", "03/10/1967");
//        context.put("address", "3 rue de la Paix, Paris");
//        context.put("university", "Institut Français des Affaires");
//        context.put("major", "Formation devlog");
//        context.put("date", "2019-2020");
//        context.put("jobs",jobs);
//
//        String renderedTemplate = jinjava.render(template, context);
//
//        BufferedWriter writer = new BufferedWriter(new FileWriter("/Users/bastien/IdeaProjects/jinjava/src/main/resources/template/word/document.xml"));
//        writer.write(renderedTemplate);
//        writer.close();
//
//        System.out.println("---");


        // Zipper le tout pour recréer un CV rempli en .docx
        zip();

        // convertir de .zip à .docx
        if (!(renommerEnDocx(templateZip))){
            System.out.println("Le CV au format .zip n'a pas pu être converti en fichier .docx");
            return;
        }


    }
}
