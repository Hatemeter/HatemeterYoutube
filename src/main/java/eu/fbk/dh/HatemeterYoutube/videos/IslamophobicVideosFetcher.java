package eu.fbk.dh.HatemeterYoutube.videos;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import eu.fbk.dh.HatemeterYoutube.database.JDBCConnectionManager;
import eu.fbk.dh.HatemeterYoutube.database.YoutubeJsonMerger;
import eu.fbk.dh.HatemeterYoutube.models.IslamophobicVideo;

import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

public class IslamophobicVideosFetcher {
    private String lang;
    
    public IslamophobicVideosFetcher(String lang){
        this.lang=lang;
    }


    public LinkedHashMap<String, ArrayList<IslamophobicVideo>> showIslamophobicVideos() throws SQLException {
        System.out.println("Islamophobic Videos:");
        System.out.println("---------------------");
        Connection con = JDBCConnectionManager.getConnection();
        LinkedHashMap<String, ArrayList<IslamophobicVideo>> islamophobicVideosWithKeywords = new LinkedHashMap<>();
        ArrayList<String> keywords = new YoutubeJsonMerger("en").getKeywords();
        for (int i = 0; i < keywords.size(); i++) {
            String keyword = keywords.get(i);
            ArrayList<IslamophobicVideo> islamophobicVideos = new ArrayList<>();
            try {
                PreparedStatement getIslamophobicVideosQuery = con.prepareStatement("select neededData from "+lang+"_youtube_keywords where "+lang+"_youtube_keywords.keyword=?");
                getIslamophobicVideosQuery.setString(1, keyword);
                ResultSet islamophobicVideosResultSet = getIslamophobicVideosQuery.executeQuery();
                while (islamophobicVideosResultSet.next()) {
                    JsonReader jsonReader = new JsonReader(new StringReader(islamophobicVideosResultSet.getString("neededData")));
                    Gson gson = new Gson();
                    JsonArray neededDataJsonArray = gson.fromJson(jsonReader, JsonArray.class);
                    for (int j = 0; j < neededDataJsonArray.size(); j++) {
                        JsonObject islamophobicVideoJsonObject = neededDataJsonArray.get(j).getAsJsonObject().get("video").getAsJsonObject();
                        IslamophobicVideo islamophobicVideo = new IslamophobicVideo(islamophobicVideoJsonObject.get("videoId").getAsString()
                                , islamophobicVideoJsonObject.get("videoTitle").getAsString()
                                , islamophobicVideoJsonObject.get("uploadDate").getAsString()
                                , islamophobicVideoJsonObject.get("videoUrl").getAsString()
                                , islamophobicVideoJsonObject.get("viewCount").getAsInt());
                        islamophobicVideos.add(islamophobicVideo);
                        islamophobicVideosWithKeywords.put(keyword, islamophobicVideos);
                    }
                }
                /*else{
                    return null; //no islamophobic channels found
                }*/
                getIslamophobicVideosQuery.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        con.close();

        for (Map.Entry<String, ArrayList<IslamophobicVideo>> entry : islamophobicVideosWithKeywords.entrySet()) {
            String keyword = entry.getKey();
            ArrayList<IslamophobicVideo> islamophobicVideo= entry.getValue();
            System.out.println(keyword+": "+islamophobicVideo.toString());
            System.out.println();
        }

        return islamophobicVideosWithKeywords;
    }


}
