//package com.hubspot.jinjava;
//
//import com.fasterxml.jackson.core.type.TypeReference;
//import com.fasterxml.jackson.databind.ObjectMapper;
//
//import java.io.*;
//import java.util.Map;
//
//public class TalanMockTest {
//
//    public static void main(String args[]) {
//        try{
//        String jsonProfil = readFile("src/main/resources/TargetJsonFILLED.json");
//        } catch (Exception e){
//            e.printStackTrace();
//        }
//        System.out.println();
//    }
//
//    private static String readFile(String file) throws IOException {
//        BufferedReader reader = new BufferedReader(new FileReader(file));
//        String         line = null;
//        StringBuilder  stringBuilder = new StringBuilder();
//        String         ls = System.getProperty("line.separator");
//
//        try {
//            while((line = reader.readLine()) != null) {
//                stringBuilder.append(line);
//                //stringBuilder.append(ls);
//            }
//            return stringBuilder.toString();
//        } finally {
//            reader.close();
//            //System.out.println("stringbuilder closed");
//        }
//    }
//
//
//    private static Map<String, Object> mapperChampsAuxValeurs(String jsonProfil) {
//        ObjectMapper mapper = new ObjectMapper();
//
//        try {
//            Map<String, Object> context = mapper.readValue(jsonProfil, new TypeReference<Map<String, Object>>() {
//            });
//
//            context.put("Rajout", "ceci a été rajouté avec context.put()");
//
//            Jinjava jinjava = new Jinjava();
//            String renderedTemplate = jinjava.render(jsonProfil, context);
//
//            BufferedWriter bufferedWriter = new BufferedWriter(new FileWriter(new File("src/main/resources/output.json")));
//            bufferedWriter.write(renderedTemplate);
//            bufferedWriter.close();
//
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            return null;
//        } finally {
//            return context;
//        }
//
//
//    }
//}
//
//
//
//
