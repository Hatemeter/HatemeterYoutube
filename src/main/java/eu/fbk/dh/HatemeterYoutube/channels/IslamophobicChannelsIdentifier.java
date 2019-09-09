package eu.fbk.dh.HatemeterYoutube.channels;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import eu.fbk.dh.HatemeterYoutube.database.JDBCConnectionManager;
import eu.fbk.dh.HatemeterYoutube.database.YoutubeJsonMerger;
import eu.fbk.dh.HatemeterYoutube.models.IslamophobicChannel;

import java.io.StringReader;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;

public class IslamophobicChannelsIdentifier {
    private String lang;
    
    public IslamophobicChannelsIdentifier(String lang){
        this.lang=lang;
    }
    
    public void addIslamophobicChannelsToDb() throws SQLException {
        Connection con = JDBCConnectionManager.getConnection();
        try {
            ArrayList<String> keywords = new YoutubeJsonMerger(lang).getKeywords();
            for (int i = 0; i < keywords.size(); i++) {
                String keyword = keywords.get(i);
                PreparedStatement getNeededDataFromKeywordQuery = con.prepareStatement("SELECT neededData from "+lang+"_youtube_keywords where keyword=?");
                getNeededDataFromKeywordQuery.setString(1, keyword);
                ResultSet neededDataFromKeywordResultSet = getNeededDataFromKeywordQuery.executeQuery();
                JsonArray neededDataJsonArray = new JsonArray();
                while (neededDataFromKeywordResultSet.next()) {
                    JsonReader jsonReader = new JsonReader(new StringReader(neededDataFromKeywordResultSet.getString("neededData")));
                    Gson gson = new Gson();
                    neededDataJsonArray = gson.fromJson(jsonReader, JsonArray.class);
                    for (int j = 0; j < neededDataJsonArray.size(); j++) {
                        String videoId = neededDataJsonArray.get(j).getAsJsonObject().get("video").getAsJsonObject().get("videoId").getAsString();
                        String videoTitle = neededDataJsonArray.get(j).getAsJsonObject().get("video").getAsJsonObject().get("videoTitle").getAsString();
                        String uploadDate = neededDataJsonArray.get(j).getAsJsonObject().get("video").getAsJsonObject().get("uploadDate").getAsString();
                        String videoUrl = neededDataJsonArray.get(j).getAsJsonObject().get("video").getAsJsonObject().get("videoUrl").getAsString();
                        int viewCount = neededDataJsonArray.get(j).getAsJsonObject().get("video").getAsJsonObject().get("viewCount").getAsInt();

                        String channelId = neededDataJsonArray.get(j).getAsJsonObject().get("channel").getAsJsonObject().get("channelId").getAsString();
                        String channelName = neededDataJsonArray.get(j).getAsJsonObject().get("channel").getAsJsonObject().get("channelName").getAsString();
                        String channelUrl = neededDataJsonArray.get(j).getAsJsonObject().get("channel").getAsJsonObject().get("channelUrl").getAsString();

                        PreparedStatement checkPreparedStatement = con.prepareStatement("SELECT 1 from "+lang+"_youtube_islamophobic_channels where channelId=?");
                        checkPreparedStatement.setString(1, channelId);
                        ResultSet resultSet = checkPreparedStatement.executeQuery();

                        if (!resultSet.next()) { //if the result set is empty => channelId is not in db
                            System.out.println(channelId + " was seen for the first time");
                            PreparedStatement insertPreparedStatement = con.prepareStatement("INSERT into "+lang+"_youtube_islamophobic_channels(channelId,channelName,channelUrl,islamophobicVideos,nbOfIslamophobicVideos) VALUES(?,?,?,?,?)");
                            insertPreparedStatement.setString(1, channelId);
                            insertPreparedStatement.setString(2, channelName);
                            System.out.println("Channel name: " + channelName);
                            insertPreparedStatement.setString(3, channelUrl);
                            System.out.println("Channel url: " + channelUrl);
                            JsonArray islamophobicVideosJsonArray = new JsonArray();
                            JsonObject islamophobicVideoJsonObject = new JsonObject();
                            islamophobicVideoJsonObject.addProperty("videoId", videoId);
                            islamophobicVideoJsonObject.addProperty("videoTitle", videoTitle);
                            islamophobicVideoJsonObject.addProperty("uploadDate", uploadDate);
                            islamophobicVideoJsonObject.addProperty("videoUrl", videoUrl);
                            islamophobicVideoJsonObject.addProperty("viewCount", viewCount);
                            islamophobicVideosJsonArray.add(islamophobicVideoJsonObject);
                            insertPreparedStatement.setString(4, islamophobicVideosJsonArray.toString());
                            System.out.println("Islamophobic Videos: " + islamophobicVideosJsonArray.toString());
                            System.out.println("DONE!");
                            System.out.println();
                            insertPreparedStatement.setInt(5, 1);
                            insertPreparedStatement.execute();
                            insertPreparedStatement.close();
                        } else {
                            System.out.println(channelId + " was seen before");
                            //check if video was already added
                            boolean videoAlreadyAdded = false;
                            JsonArray islamophobicVideosJsonArray = new JsonArray();
                            PreparedStatement getIslamophobicVideosQuery = con.prepareStatement("SELECT islamophobicVideos from "+lang+"_youtube_islamophobic_channels where channelId=?");
                            getIslamophobicVideosQuery.setString(1, channelId);
                            ResultSet islamophobicVideosResultSet = getIslamophobicVideosQuery.executeQuery();
                            while (islamophobicVideosResultSet.next()) {
                                jsonReader = new JsonReader(new StringReader(islamophobicVideosResultSet.getString("islamophobicVideos")));
                                islamophobicVideosJsonArray = gson.fromJson(jsonReader, JsonArray.class);
                            }
                            for (int k = 0; k < islamophobicVideosJsonArray.size(); k++) {
                                if ((islamophobicVideosJsonArray.get(k).getAsJsonObject().get("videoId").getAsString()).equals(videoId)) {
                                    videoAlreadyAdded = true;
                                    System.out.println("Video " + videoId + " already seen");
                                    System.out.println("DONE!");
                                    System.out.println();
                                    break;
                                }
                            }

                            if (videoAlreadyAdded == false) { //if this false
                                PreparedStatement replacePreparedStatement = con.prepareStatement("UPDATE "+lang+"_youtube_islamophobic_channels SET islamophobicVideos=?, nbOfIslamophobicVideos=? where channelId=?"); //these are the only things that will change
                                JsonObject islamophobicVideoJsonObject = new JsonObject();
                                islamophobicVideoJsonObject.addProperty("videoId", videoId);
                                islamophobicVideoJsonObject.addProperty("videoTitle", videoTitle);
                                islamophobicVideoJsonObject.addProperty("uploadDate", uploadDate);
                                islamophobicVideoJsonObject.addProperty("videoUrl", videoUrl);
                                islamophobicVideoJsonObject.addProperty("viewCount", viewCount);
                                islamophobicVideosJsonArray.add(islamophobicVideoJsonObject);
                                replacePreparedStatement.setString(1, islamophobicVideosJsonArray.toString());
                                System.out.println("Replaced Islamophobic Videos: " + islamophobicVideosJsonArray.toString());
                                PreparedStatement getNbOfIslamophobicVideosQuery = con.prepareStatement("SELECT nbOfIslamophobicVideos from "+lang+"_youtube_islamophobic_channels WHERE channelId=?");
                                getNbOfIslamophobicVideosQuery.setString(1, channelId);
                                ResultSet nbOfIslamophobicVideosResultSet = getNbOfIslamophobicVideosQuery.executeQuery();
                                while (nbOfIslamophobicVideosResultSet.next()) {
                                    replacePreparedStatement.setInt(2, nbOfIslamophobicVideosResultSet.getInt("nbOfIslamophobicVideos") + 1);
                                    System.out.println("Replaced Nb of Isl: " + (nbOfIslamophobicVideosResultSet.getInt("nbOfIslamophobicVideos") + 1));
                                    System.out.println("DONE!");
                                    System.out.println();
                                }
                                replacePreparedStatement.setString(3,channelId);
                                replacePreparedStatement.execute();
                                replacePreparedStatement.close();
                            }
                        }
                        checkPreparedStatement.close();

                    }
                }
                getNeededDataFromKeywordQuery.close();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        con.close();
    }

    public ArrayList<IslamophobicChannel> showIslamophobicChannels() throws SQLException {
        System.out.println("Islamophobic Channels:");
        System.out.println("---------------------");
            Connection con=JDBCConnectionManager.getConnection();
            ArrayList<IslamophobicChannel> islamophobicChannels=new ArrayList<>();
            try{
                PreparedStatement getIslamophobicChannelsQuery=con.prepareStatement("select * from "+lang+"_youtube_islamophobic_channels where "+lang+"_youtube_islamophobic_channels.nbOfIslamophobicVideos>=?");
                getIslamophobicChannelsQuery.setInt(1,3);
                ResultSet getIslamophobicChannelsResultSet=getIslamophobicChannelsQuery.executeQuery();
                    while (getIslamophobicChannelsResultSet.next()) {
                        IslamophobicChannel islamophobicChannel = new IslamophobicChannel(getIslamophobicChannelsResultSet.getString("channelId")
                                , getIslamophobicChannelsResultSet.getString("channelName")
                                , getIslamophobicChannelsResultSet.getString("channelUrl")
                                ,getIslamophobicChannelsResultSet.getString("islamophobicVideos")
                                , getIslamophobicChannelsResultSet.getInt("nbOfIslamophobicVideos"));
                        islamophobicChannels.add(islamophobicChannel);
                }
                /*else{ //TODO
                    return null; //no islamophobic channels found
                }*/
                getIslamophobicChannelsQuery.close();
            }
            catch (Exception e){
                e.printStackTrace();
            }
            con.close();

            return islamophobicChannels;
    }

}
