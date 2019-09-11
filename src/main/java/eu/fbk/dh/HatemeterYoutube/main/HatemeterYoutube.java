package eu.fbk.dh.HatemeterYoutube.main;

import eu.fbk.dh.HatemeterYoutube.channels.IslamophobicChannelsIdentifier;
import eu.fbk.dh.HatemeterYoutube.database.JDBCConnectionManager;
import eu.fbk.dh.HatemeterYoutube.database.YoutubeJsonMerger;
import eu.fbk.dh.HatemeterYoutube.videos.IslamophobicVideosFetcher;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

public class HatemeterYoutube {
    public static void main(String[] args) {
        try {
            InputStream input = HatemeterYoutube.class.getClassLoader().getResourceAsStream("config.properties");
            Properties prop = new Properties();
            prop.load(input);
            String pythonFilePath = prop.getProperty("pythonFilePath");
            String commentsFilePath= prop.getProperty("commentsFilePath");

            for (String lang : args) {
                System.out.println("Processing language: " + lang);
                System.out.println();
                Process process = new ProcessBuilder(
                        pythonFilePath,
                        "src/bin/down-all.py",
                        "--languages", lang,
                        "--folder", commentsFilePath
                ).start();
                InputStream is = process.getInputStream();
                InputStreamReader isr = new InputStreamReader(is);
                BufferedReader br = new BufferedReader(isr);
                String line;
                while ((line = br.readLine()) != null) {
                    System.out.println(line);
                }

                YoutubeJsonMerger youtubeJsonMerger = new YoutubeJsonMerger(lang);
                youtubeJsonMerger.addAllDataJsonToDb(); //all all crawled data to database
                youtubeJsonMerger.addNeededDataJsonToDb(); //create, curate and add the data we will use to the database

                IslamophobicVideosFetcher islamophobicVideosFetcher = new IslamophobicVideosFetcher(lang);
                System.out.println(islamophobicVideosFetcher.showIslamophobicVideos());
                System.out.println();

                IslamophobicChannelsIdentifier islamophobicChannelsIdentifier = new IslamophobicChannelsIdentifier(lang);
                islamophobicChannelsIdentifier.addIslamophobicChannelsToDb();
                System.out.println(islamophobicChannelsIdentifier.showIslamophobicChannels().toString());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}