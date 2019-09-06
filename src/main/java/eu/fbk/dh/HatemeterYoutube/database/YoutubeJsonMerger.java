package eu.fbk.dh.HatemeterYoutube.database;

import com.github.pemistahl.lingua.api.LanguageDetector;
import com.github.pemistahl.lingua.api.LanguageDetectorBuilder;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.stream.JsonReader;
import eu.fbk.dh.HatemeterYoutube.sentimentanalyzers.EnglishSentimentAnalyzer;
import eu.fbk.dh.HatemeterYoutube.sentimentanalyzers.FrenchSentimentAnalyzer;
import eu.fbk.dh.HatemeterYoutube.sentimentanalyzers.ItalianSentimentAnalyzer;

import java.io.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Properties;

public class YoutubeJsonMerger {
    private String lang;

    public YoutubeJsonMerger(String lang) {
        this.lang = lang;
    }

    public ArrayList<String> getKeywords() throws SQLException {
        Connection con = JDBCConnectionManager.getConnection(); //TODO bad practice because getting it from another project so think about another way
        ArrayList<String> keywords = new ArrayList<String>();
        try {
            PreparedStatement preparedStatement = con.prepareStatement("SELECT keyword from " + lang + "_youtube_keywords");
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                keywords.add(resultSet.getString("keyword"));
            }
            preparedStatement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        con.close();
        return keywords;
    }

    public void addAllDataJsonToDb() throws IOException, SQLException {
        Connection con = JDBCConnectionManager.getConnection();

        ArrayList<String> keywords = getKeywords();

        for (int i = 0; i < keywords.size(); i++) {
            String keyword = keywords.get(i);

            JsonObject allData = createAllDataJsonObject(keyword);

            System.out.println(keywords.get(i) + ":\n" + allData.toString() + "\n");

            PreparedStatement pstmt = con.prepareStatement("UPDATE " + lang + "_youtube_keywords SET allData=? WHERE " + lang + "_youtube_keywords.keyword=?");
            pstmt.setString(1, allData.toString());
            pstmt.setString(2, keywords.get(i));

            pstmt.execute();
            pstmt.close();
        }
        con.close();
    }

    public void addNeededDataJsonToDb() throws SQLException, IOException {
        Connection con = JDBCConnectionManager.getConnection();

        ArrayList<String> keywords = getKeywords();

        for (int i = 0; i < keywords.size(); i++) {
            String keyword = keywords.get(i);

            JsonArray neededData = createNeededDataJsonArray(keyword);

            PreparedStatement pstmt = con.prepareStatement("UPDATE " + lang + "_youtube_keywords SET neededData=? WHERE " + lang + "_youtube_keywords.keyword=?");
            pstmt.setString(1, neededData.toString());
            pstmt.setString(2, keyword);

            pstmt.execute();
            pstmt.close();
        }

        con.close();
    }


    public JsonObject getAllDataAsJsonFromKeyword(String keyword) throws SQLException {
        Connection con = JDBCConnectionManager.getConnection(); //TODO bad practice because getting it from another project so think about another way
        JsonObject allData = new JsonObject();
        try {
            PreparedStatement preparedStatement = con.prepareStatement("SELECT allData from " + lang + "_youtube_keywords WHERE " + lang + "_youtube_keywords.keyword=?"); //TODO change en to lang
            preparedStatement.setString(1, keyword);
            ResultSet resultSet = preparedStatement.executeQuery();
            while (resultSet.next()) {
                JsonReader jsonReader = new JsonReader(new StringReader(resultSet.getString("allData")));
                Gson gson = new Gson();
                allData = gson.fromJson(jsonReader, JsonObject.class);
            }
            preparedStatement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        con.close();
        return allData;
    }

    public JsonObject createAllDataJsonObject(String keyword) throws IOException {
        JsonObject keywordData = new JsonObject();
        JsonArray videoIds = new JsonArray();
        JsonArray metadata = new JsonArray();
        JsonArray comments = new JsonArray();

        BufferedReader br = new BufferedReader(new FileReader("/home/baalbaki/IdeaProjects/YoutubeCrawler/" + lang + "/" + keyword + ".txt"));
        String videoId = br.readLine();

        while (videoId != null) {
            videoIds.add(videoId);
            videoId = br.readLine();
        }
        // metadata and comments here
        for (int j = 0; j < videoIds.size(); j++) {
            Gson gson = new Gson();
            JsonReader metadataFileReader = new JsonReader(new FileReader("/home/baalbaki/IdeaProjects/YoutubeCrawler/" + lang + "_comments/" + keyword + "." + videoIds.get(j).toString().substring(1, videoIds.get(j).toString().length() - 1) + ".meta.json"));
            //TODO really bad thing to modify files here, modify python script later to return comments as a json array from the beginning
            Path path = Paths.get("/home/baalbaki/IdeaProjects/YoutubeCrawler/" + lang + "_comments/" + keyword + "." + videoIds.get(j).toString().substring(1, videoIds.get(j).toString().length() - 1) + ".comments.json");
            Charset charset = StandardCharsets.UTF_8;
            if (!(new String(Files.readAllBytes(path), charset)).isEmpty()) { //if the file is not empty
                //Todo uncomment this on a new crawl
                /*String commentsJson = new String(Files.readAllBytes(path), charset).trim();
                commentsJson = new StringBuilder(commentsJson).insert(0, '[').toString();
                commentsJson = new StringBuilder(commentsJson).insert(commentsJson.length(), ']').toString();
                commentsJson = commentsJson.replaceAll("\"}\\s+", "\"},"); //add comma after them
                commentsJson = new StringBuilder(commentsJson).deleteCharAt(commentsJson.length() - 2).toString(); //remove last comma
                commentsJson = new StringBuilder(commentsJson).insert(commentsJson.length() - 1, '}').toString(); //remove last comma
                Files.write(path, commentsJson.getBytes(charset));*/
                JsonReader commentsFileReader = new JsonReader(new FileReader("/home/baalbaki/IdeaProjects/YoutubeCrawler/" + lang + "_comments/" + keyword + "." + videoIds.get(j).toString().substring(1, videoIds.get(j).toString().length() - 1) + ".comments.json"));
                JsonArray commentsJsonArray = gson.fromJson(commentsFileReader, JsonArray.class);
                comments.add(commentsJsonArray);
            } else {
                JsonArray commentsJsonArray = new JsonArray();
                comments.add(commentsJsonArray); //add empty array
            }
            JsonObject metadataJsonObject = gson.fromJson(metadataFileReader, JsonObject.class);
            metadata.add(metadataJsonObject);
        }

        keywordData.add("videoIds", videoIds);
        keywordData.add("metadata", metadata);
        keywordData.add("comments", comments);

        return keywordData;
    }

    public JsonArray createNeededDataJsonArray(String keyword) throws SQLException, IOException {
        JsonObject allData = getAllDataAsJsonFromKeyword(keyword);
        JsonArray allVideoIds = allData.get("videoIds").getAsJsonArray();
        JsonArray allMetadata = allData.get("metadata").getAsJsonArray();
        JsonArray allComments = allData.get("comments").getAsJsonArray();
        JsonArray rootJsonArray = new JsonArray();
        EnglishSentimentAnalyzer englishSentimentAnalyzer = null;
        FrenchSentimentAnalyzer frenchSentimentAnalyzer = null;
        ItalianSentimentAnalyzer italianSentimentAnalyzer = null;
        JsonArray comments=null;
        JsonObject video=null;
        JsonObject channel=null;

        if (lang.equals("en")) {
            englishSentimentAnalyzer = new EnglishSentimentAnalyzer();
            englishSentimentAnalyzer.initializeCoreNLP();
        } else if (lang.equals("fr")) {
            Properties prop = new Properties();
            InputStream input = null;
            try {
                input = YoutubeJsonMerger.class.getClassLoader().getResourceAsStream("apicredentials.properties");
                prop.load(input);
                String frenchLemmasApiKey = prop.getProperty("frenchLemmasApiKey");
                frenchSentimentAnalyzer = new FrenchSentimentAnalyzer(frenchLemmasApiKey);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (lang.equals("it")) {
            italianSentimentAnalyzer = new ItalianSentimentAnalyzer();
        }

        LanguageDetector detector = LanguageDetectorBuilder.fromAllBuiltInLanguages().build();

        for (int j = 0; j < allVideoIds.size(); j++) {
            if (detector.detectLanguageOf(allMetadata.get(j).getAsJsonObject().get("title").getAsString()).getIsoCode().equals(lang)) { //title of the video
                video = new JsonObject();
                System.out.println("VIDEO " + allVideoIds.get(j).getAsString() + " entered");
                System.out.println("----------------------------------------------------");
                video.addProperty("videoId", allVideoIds.get(j).getAsString());
                video.addProperty("videoTitle", allMetadata.get(j).getAsJsonObject().get("title").getAsString());
                String uploadDate = new StringBuilder(allMetadata.get(j).getAsJsonObject().get("upload_date").getAsString()).insert(4, '-').toString();
                uploadDate = new StringBuilder(uploadDate).insert(7, '-').toString();
                video.addProperty("uploadDate", uploadDate);
                video.addProperty("videoUrl", allMetadata.get(j).getAsJsonObject().get("webpage_url").getAsString());
                video.addProperty("viewCount", allMetadata.get(j).getAsJsonObject().get("view_count").getAsInt());

                channel = new JsonObject();
                channel.addProperty("channelId", allMetadata.get(j).getAsJsonObject().get("channel_id").getAsString());
                channel.addProperty("channelName", allMetadata.get(j).getAsJsonObject().get("uploader").getAsString());
                channel.addProperty("channelUrl", allMetadata.get(j).getAsJsonObject().get("channel_url").getAsString());

                comments = new JsonArray();
                int negativeCommentLimit = 10;
                int counterNegativeComments = 0;
                if (allComments.get(j).getAsJsonArray().size() > 0) {
                    if (allComments.get(j).getAsJsonArray().size() >= 10) {
                        for (int k = 0; k < negativeCommentLimit; k++) {
                            int commentSentiment = 0;
                            System.out.println("index: "+k+", negative comment limit: "+negativeCommentLimit);
                            if (lang.equals("en") && detector.detectLanguageOf(allComments.get(j).getAsJsonArray().get(k).getAsJsonObject().get("text").getAsString()).getIsoCode().equals("en")) { //if it is specifically in english
                                //TODO ADD ISLAMOPHOBIA CATEGORY DETECTOR
                                commentSentiment = englishSentimentAnalyzer.getSentiment(allComments.get(j).getAsJsonArray().get(k).getAsJsonObject().get("text").getAsString());
                            } else if (lang.equals("fr") && detector.detectLanguageOf(allComments.get(j).getAsJsonArray().get(k).getAsJsonObject().get("text").getAsString()).getIsoCode().equals("fr")) {
                                commentSentiment = frenchSentimentAnalyzer.getSentiment(allComments.get(j).getAsJsonArray().get(k).getAsJsonObject().get("text").getAsString());
                            } else if (lang.equals("it") && detector.detectLanguageOf(allComments.get(j).getAsJsonArray().get(k).getAsJsonObject().get("text").getAsString()).getIsoCode().equals("it")) {
                                commentSentiment = italianSentimentAnalyzer.getSentiment(allComments.get(j).getAsJsonArray().get(k).getAsJsonObject().get("text").getAsString());
                            } else {
                                if (negativeCommentLimit == allComments.get(j).getAsJsonArray().size()) break;
                                negativeCommentLimit++;
                            }
                            if (commentSentiment < 0) {
                                JsonObject comment = new JsonObject();
                                comment.addProperty("commentId", allComments.get(j).getAsJsonArray().get(k).getAsJsonObject().get("cid").getAsString());
                                comment.addProperty("commentText", allComments.get(j).getAsJsonArray().get(k).getAsJsonObject().get("text").getAsString());
                                comment.addProperty("commentTime", allComments.get(j).getAsJsonArray().get(k).getAsJsonObject().get("time").getAsString());
                                comment.addProperty("commentAuthor", allComments.get(j).getAsJsonArray().get(k).getAsJsonObject().get("author").getAsString());
                                comments.add(comment);
                                counterNegativeComments++;
                                System.out.println(counterNegativeComments + " negative comments added");
                                System.out.println();
                                if(counterNegativeComments==10) break;
                            } else {
                                if (negativeCommentLimit == allComments.get(j).getAsJsonArray().size()) break;
                                negativeCommentLimit++;
                            }

                        }
                    } else {
                        int commentSentiment = 0;
                        for (int k = 0; k < allComments.get(j).getAsJsonArray().size(); k++) {
                            if (lang.equals("en") && detector.detectLanguageOf(allComments.get(j).getAsJsonArray().get(k).getAsJsonObject().get("text").getAsString()).getIsoCode().equals("en")) {
                                commentSentiment = englishSentimentAnalyzer.getSentiment(allComments.get(j).getAsJsonArray().get(k).getAsJsonObject().get("text").getAsString());
                            } else if (lang.equals("fr") && detector.detectLanguageOf(allComments.get(j).getAsJsonArray().get(k).getAsJsonObject().get("text").getAsString()).getIsoCode().equals("fr")) {
                                commentSentiment = frenchSentimentAnalyzer.getSentiment(allComments.get(j).getAsJsonArray().get(k).getAsJsonObject().get("text").getAsString());
                            } else if (lang.equals("it") && detector.detectLanguageOf(allComments.get(j).getAsJsonArray().get(k).getAsJsonObject().get("text").getAsString()).getIsoCode().equals("it")) {
                                commentSentiment = italianSentimentAnalyzer.getSentiment(allComments.get(j).getAsJsonArray().get(k).getAsJsonObject().get("text").getAsString());
                            }
                            else{
                                if (negativeCommentLimit == allComments.get(j).getAsJsonArray().size()) break;
                                negativeCommentLimit++;
                            }
                            if (commentSentiment < 0) {
                                JsonObject comment = new JsonObject();
                                comment.addProperty("commentId", allComments.get(j).getAsJsonArray().get(k).getAsJsonObject().get("cid").getAsString());
                                comment.addProperty("commentText", allComments.get(j).getAsJsonArray().get(k).getAsJsonObject().get("text").getAsString());
                                comment.addProperty("commentTime", allComments.get(j).getAsJsonArray().get(k).getAsJsonObject().get("time").getAsString());
                                comment.addProperty("commentAuthor", allComments.get(j).getAsJsonArray().get(k).getAsJsonObject().get("author").getAsString());
                                comments.add(comment);
                                counterNegativeComments++;
                                System.out.println(counterNegativeComments + " negative comments added");
                                System.out.println();
                                if(counterNegativeComments==10) break;
                            } else negativeCommentLimit++;
                            if (negativeCommentLimit == allComments.get(j).getAsJsonArray().size()) break;
                        }
                    }
                }
            } else {
                JsonObject comment = new JsonObject();
                comments.add(comment);
            }

            JsonObject composedJsonObject = new JsonObject();


                composedJsonObject.add("video", video);
                composedJsonObject.add("channel", channel);
                composedJsonObject.add("comments", comments);

                rootJsonArray.add(composedJsonObject);

        }
        return rootJsonArray;
    }
}
