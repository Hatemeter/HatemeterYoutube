package eu.fbk.dh.HatemeterYoutube.main;

import eu.fbk.dh.HatemeterYoutube.channels.IslamophobicChannelsIdentifier;
import eu.fbk.dh.HatemeterYoutube.database.YoutubeJsonMerger;
import eu.fbk.dh.HatemeterYoutube.videos.IslamophobicVideosFetcher;

import java.io.IOException;
import java.util.ArrayList;

public class HatemeterYoutube {
    public static void main(String[] args) {
        //todo: crawl comments here
        for(int i=0;i<args.length;i++) {
            String lang=args[i];
            try {
                YoutubeJsonMerger youtubeJsonMerger = new YoutubeJsonMerger(lang);
                youtubeJsonMerger.addAllDataJsonToDb();
                youtubeJsonMerger.addNeededDataJsonToDb();

                IslamophobicVideosFetcher islamophobicVideosFetcher = new IslamophobicVideosFetcher(lang);
                System.out.println(islamophobicVideosFetcher.showIslamophobicVideos());
                System.out.println();

                IslamophobicChannelsIdentifier islamophobicChannelsIdentifier = new IslamophobicChannelsIdentifier(lang);
                islamophobicChannelsIdentifier.addIslamophobicChannelsToDb();
                System.out.println(islamophobicChannelsIdentifier.showIslamophobicChannels().toString());

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}