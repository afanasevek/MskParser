import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.gson.Gson;
import com.sun.source.tree.IfTree;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {
    public static void main(String[] args) {
        try {
            Set<String> stations = new HashSet<>();
            JSONObject obj = new JSONObject();
            JSONObject obj1 = new JSONObject();
            JSONObject objFinal = new JSONObject();
            HashMap<String, MskMetro> map = new HashMap<>();
            HashMap<String, MskMetro> connections = new HashMap<>();
            String url = "https://ru.wikipedia.org/wiki/%D0%A1%D0%BF%D0%B8%D1%81%D0%BE%D0%BA_%D1%81%D1%82%D0%B0%D0%BD%D1%86%D0%B8%D0%B9_%D0%9C%D0%BE%D1%81%D0%BA%D0%BE%D0%B2%D1%81%D0%BA%D0%BE%D0%B3%D0%BE_%D0%BC%D0%B5%D1%82%D1%80%D0%BE%D0%BF%D0%BE%D0%BB%D0%B8%D1%82%D0%B5%D0%BD%D0%B0";
            Document doc = Jsoup.parse(new URL(url), 30000);
            Element table = doc.select("table.standard.sortable").first();
            Element tbody = table.selectFirst("tbody");
            Elements tr = tbody.select("tr");
            tr.stream().forEach(element -> {
                Elements td = element.select("td");
                for (int o = 0;o<td.size();){
                    Elements span = td.get(0).select("span[title]");
                    span.forEach(spanStream ->{
                        String statinURL = spanStream.absUrl("title");
                        String[] strings = statinURL.split("/+");
                        String line = strings[3];
                        Elements span1 = td.get(1).select("span");
                            span1.forEach(test ->{
                                String station = test.select("a[title]").text();
                                stations.add(station);
                                if (!map.containsKey(line)) {
                                    map.put(line, new MskMetro());
                                    map.get(line).setStations(station);
                                } else {
                                    map.get(line).setStations(station);
                                }
                                Elements elements2 = td.get(3).select("span[title]");
                                elements2.stream().forEach(element2 -> {
                                    String statinURL1 = element2.absUrl("title");
                                    String[] stringsconnect = statinURL1.split("/+");
                                    String connect = stringsconnect[3];
                                    if (!connections.containsKey(connect)) {
                                        connections.put(connect, new MskMetro());
                                        connections.get(connect).setStations(station);
                                    } else {
                                        connections.get(connect).setStations(station);
                                    }
                                });
                            });
                    });
                    break;
                }
            });
            map.entrySet().stream().forEach(map1->obj.put(map1.getKey(), map.get(map1.getKey()).stations));
            connections.entrySet().stream().forEach(connect -> {
                stations.forEach(s -> {
                    if (connect.getKey().toString().contains(s)) {
                        connections.get(connect.getKey()).setStations(s);
                    }
                });
            });
            connections.entrySet().stream().forEach(map1->obj1.put(map1.getKey(), connections.get(map1.getKey()).stations));
            objFinal.put("Stations", obj);
            objFinal.put("Connections", obj1);
            ObjectMapper objectMapper = new ObjectMapper();
            objectMapper.enable(SerializationFeature.INDENT_OUTPUT);
            objectMapper.writeValue(new File("/home/evgeny/Skillbox/java_basics/09_FilesAndNetwork/homework9.14/data/test1.json"), objFinal);

            FileWriter writer = new FileWriter("/home/evgeny/Skillbox/java_basics/09_FilesAndNetwork/homework9.14/data/test.json");
            writer.write(objFinal.toJSONString());
            writer.flush();
            writer.close();
            StringBuilder builder = new StringBuilder();
            List<String> lines = Files.readAllLines(Paths.get("/home/evgeny/Skillbox/java_basics/09_FilesAndNetwork/homework9.14/data/test.json"));
            lines.forEach(line1 -> builder.append(line1));
            JSONParser parser = new JSONParser();
            JSONObject jsonData = (JSONObject) parser.parse(builder.toString());
            JSONObject jsonObjectConnections =(JSONObject) jsonData.get("Connections");
            JSONObject jsonObjectStations =(JSONObject) jsonData.get("Stations");
            jsonObjectConnections.keySet().forEach(connections1 ->{
                String connectJson = connections1.toString();
                System.out.println(connectJson);
                JSONArray stationsArray = (JSONArray) jsonObjectConnections.get(connections1);
                stationsArray.forEach(System.out::println);
                System.out.println();
            });
            jsonObjectStations.keySet().forEach(stations1 ->{
                String stationJson = stations1.toString();
                System.out.println(stationJson);
                JSONArray stationsArray = (JSONArray) jsonObjectStations.get(stations1);
                stationsArray.forEach(System.out::println);
                System.out.println("Количество станций на линии : "+stationsArray.size());
                System.out.println();
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
