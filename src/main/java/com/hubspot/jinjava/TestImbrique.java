//package com.hubspot.jinjava;
//
//import com.fasterxml.jackson.databind.ObjectMapper;
//import com.google.common.collect.Maps;
//import com.hubspot.jinjava.util.ForLoop;
//import com.hubspot.jinjava.util.ObjectIterator;
//
//import java.io.IOException;
//import java.util.HashMap;
//import java.util.Map;
//
//public class TestImbrique {
//    public static void main(String[] args) throws IOException {
//
//        String JSON_SOURCE = "{\n" +
//                "    \"menu\": {\n" +
//                "        \"id\": \"file\",\n" +
//                "        \"value\": \"File\",\n" +
//                "        \"popup\": {\n" +
//                "            \"menuitem\": [\n" +
//                "                { \"value\": \"New\", \"onclick\": \"CreateNewDoc()\" },\n" +
//                "                { \"value\": \"Open\", \"onclick\": \"OpenDoc()\" },\n" +
//                "                { \"value\": \"Close\", \"onclick\": \"CloseDoc()\" }\n" +
//                "            ]\n" +
//                "        }\n" +
//                "    }\n" +
//                "}";
//
//        HashMap<String,Object> result =
//                new ObjectMapper().readValue(JSON_SOURCE, HashMap.class);
//
//        String resultString =
//
//        ForLoop cours = ObjectIterator.getLoop(result);
//
//        Jinjava jinjava = new Jinjava();
//        Map<String, Object> context = Maps.newHashMap();
//        context.put("nomEleve", "Jordan");
//
//
//        String renderedTemplate = jinjava.render(result, context);
//    }
//}
