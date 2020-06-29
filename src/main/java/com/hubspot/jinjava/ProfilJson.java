package com.hubspot.jinjava;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.fasterxml.jackson.databind.util.JSONPObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.function.Consumer;

// Créé un Json qui rassemble toutes les données d'un profil
// En vue d'être passé dans jinja
public class ProfilJson {
    public static void main(String[] args) throws IOException {
        System.out.println(creerProfilJsonAvecNodes(1));
    }

    private static String URL_SERVEUR = "http://localhost:8080";
    private static ObjectMapper mapper = new ObjectMapper();

    static String creerProfilJsonAvecNodes(int idProfil) throws IOException {
        ObjectNode rootNode = mapper.createObjectNode();
        JsonNode tree;

        // Profil
        tree = mapper.readTree(new URL(URL_SERVEUR + "/profils/" + idProfil));
        rootNode.set("ProfileId", tree.get("id"));
        rootNode.set("PictureLink", tree.get("fichierPhoto"));
        rootNode.set("FirstName", tree.get("prenom"));
        rootNode.set("LastName", tree.get("nom"));
        rootNode.set("DateOfBirth", tree.get("dateNaissance"));
        rootNode.set("Nationality", tree.at("/nationalite/description"));
        rootNode.set("Email", tree.get("email"));
        rootNode.set("PhoneNumber", tree.get("telephone"));
        rootNode.set("Address", tree.get("adresse"));
        rootNode.set("City", new ObjectMapper().readTree(new URL(URL_SERVEUR+"/profils/"+idProfil+"/ville")).get("nom"));
        rootNode.set("ZipCode", tree.get("codePostal"));
        // CityCountry

        // Hobbies
        tree = mapper.readTree(new URL(URL_SERVEUR + "/profils/" + idProfil + "/listeHobby")).at("/_embedded/loisirs");
        List<String> hobbiesList = getValuesAtPath(tree, "/idLoisir/idLoisir");
        rootNode.set("Hobbies", arrayNodeFromList(hobbiesList));

        // Langues


        // Catégories compétences linguistiques

        // Niveau de langue


        // Taches

        // Description expériences pro.

        // Postes

        // Expériences pro.

        // Titres / Certifs

        // Entreprises

        // Technologies

        // Principes

        // Niveau de tech

        return rootNode.toString();
    }

    static String creerProfilJsonAvecStringBuilder(int idProfil){
        StringBuilder builder = new StringBuilder();
        builder.append("{");
        String json;

        // Profil
        json = getJsonFrom("http://localhost:8080/profils/" + idProfil);
//        builder.append(json.)

        // Hobbies

        // Nationalité

        // Genre

        // Catégories compétences linguistiques

        // Niveau de langue

        // Langues

        // Taches

        // Description expériences pro.

        // Postes

        // Expériences pro.

        // Titres / Certifs

        // Entreprises

        // Villes

        // Pays

        // Technologies

        // Principes

        // Niveau de tech



        builder.append("}");
        System.out.println(builder.toString());
        return builder.toString();
    }

    private StringBuilder wrapAvecAccolades(StringBuilder string){
        return string.insert(0,"{").append("}");
    }

    private static String streamToString(InputStream inputStream) {
        String text = new Scanner(inputStream, "UTF-8").useDelimiter("\\Z").next();
        return text;
    }

    private static String getJsonFrom(String urlQueryString) {
        String json = null;
        try {
            URL url = new URL(urlQueryString);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setInstanceFollowRedirects(false);
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Content-Type", "application/json");
            connection.setRequestProperty("charset", "utf-8");
            connection.connect();
            InputStream inStream = connection.getInputStream();
            json = streamToString(inStream); // input stream to string
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        return json;
    }

    private static List<String> getValuesAtPath(JsonNode parentNode, String path){
        List<String> values = new ArrayList<>();
        Consumer<JsonNode> processData = (JsonNode node) -> values.add((node.at(path).asText()));
        parentNode.forEach(processData);
        return values;
    }

    private static String getValueAtPath(String urlString, String path) throws IOException {
        URL url = new URL(urlString);
        return new ObjectMapper().readTree(url).at(path).asText();
    }

    private static ArrayNode arrayNodeFromList(List<String> list){
        ArrayNode array = mapper.valueToTree(list);
        return array;
    }
}