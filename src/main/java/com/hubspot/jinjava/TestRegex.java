package com.hubspot.jinjava;

public class TestRegex {

    public static void main(String[] args) {

    String nomTemplate = "_..";
    //String nomTemplate = "_-abcd-efùlgh.i/j`k*l..";
    String nomBackEnd = nomTemplate.replaceAll("[^A-Za-z0-9._\\-]","");
    //System.out.println(nomBackEnd);

    // pas de leading ou trailing dans les noms . _ (et espace)
    while (nomBackEnd.startsWith(".") | nomBackEnd.startsWith("-") | nomBackEnd.startsWith("_")) nomBackEnd = nomBackEnd.substring(1);
    while (nomBackEnd.endsWith(".") | nomBackEnd.endsWith("-") | nomBackEnd.endsWith("_")) nomBackEnd = nomBackEnd.substring(0,nomBackEnd.length()-1);
    if (nomBackEnd.length() == 0 ) System.out.println("nooo");
    System.out.println(nomBackEnd);
    }



}
