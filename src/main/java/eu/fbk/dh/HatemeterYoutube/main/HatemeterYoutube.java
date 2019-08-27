package eu.fbk.dh.HatemeterYoutube.main;

import eu.fbk.dh.HatemeterYoutube.channels.IslamophobicChannelsIdentifier;
import eu.fbk.dh.HatemeterYoutube.database.YoutubeJsonMerger;
import eu.fbk.dh.HatemeterYoutube.videos.IslamophobicVideosFetcher;

import java.io.IOException;

public class HatemeterYoutube {
    public static void main(String[] args) throws IOException {
        try {

            YoutubeJsonMerger youtubeJsonMerger=new YoutubeJsonMerger();
            youtubeJsonMerger.addAllDataJsonToDb();
            youtubeJsonMerger.addNeededDataJsonToDb();

            IslamophobicVideosFetcher islamophobicVideosFetcher=new IslamophobicVideosFetcher();
            System.out.println(islamophobicVideosFetcher.showIslamophobicVideos());
            System.out.println();

            IslamophobicChannelsIdentifier islamophobicChannelsIdentifier=new IslamophobicChannelsIdentifier();
            islamophobicChannelsIdentifier.addIslamophobicChannelsToDb();
            System.out.println(islamophobicChannelsIdentifier.showIslamophobicChannels().toString());

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}