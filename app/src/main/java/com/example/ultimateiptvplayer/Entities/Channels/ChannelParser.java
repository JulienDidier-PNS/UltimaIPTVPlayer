package com.example.ultimateiptvplayer.Entities.Channels;

import com.example.ultimateiptvplayer.Entities.Playlist.Playlist;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Objects;
import java.util.TreeMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChannelParser {

    public void updateChannels(Playlist playlist) {
        ArrayList<Channel> channels = new ArrayList<>();
        String playlistPath = playlist.getInternalPlaylistPath();

        //get the favorite path
        String favoriteFile = Paths.get(playlistPath).getParent().toString() + "/favorites.m3u8";

        //Read the file and extract the channels
        try (BufferedReader reader = new BufferedReader(new FileReader(playlistPath))) {
            String line;
            Channel channel = null;

            //Reference all channels
            while ((line = reader.readLine()) != null) {
                //we wanted ONLY channels
                Pattern pattern = Pattern.compile("movie|series", Pattern.CASE_INSENSITIVE);
                Matcher matcher = pattern.matcher(line);
                boolean contain_movie_or_series = matcher.find();

                if (line.startsWith("#EXTINF:")) {
                    String tvgId = extractAttribute(line, "tvg-id");
                    String tvgName = extractAttribute(line, "tvg-name");
                    String tvgLogo = extractAttribute(line, "tvg-logo");
                    String groupTitle = extractAttribute(line, "group-title");
                    String[] parts = line.split(",");
                    String channelName = parts.length > 1 ? parts[1] : "";

                    channel = new Channel(tvgId, tvgName, tvgLogo, groupTitle, channelName, "");
                } else if (line.startsWith("http://") || line.startsWith("https://")) {
                    //if the link does not contain movie or series
                    if(!contain_movie_or_series){
                        if (channel != null) {
                            channel.setUrl(line);
                            channels.add(channel);
                            channel = null;
                        }
                    }
                    else{break;}
                }
            }
            reader.close();
            //System.out.println("Channels: " + channels.size());
        } catch (IOException e) {e.printStackTrace();}

        //Read the favorite file if already exists
        if(new File(favoriteFile).exists()){
            try(BufferedReader reader = new BufferedReader(new FileReader(favoriteFile))) {
                String line;
                Channel channel = null;

                //Reference all channels
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("#EXTINF:")) {
                        String tvgId = extractAttribute(line, "tvg-id");
                        String tvgName = extractAttribute(line, "tvg-name");
                        String tvgLogo = extractAttribute(line, "tvg-logo");
                        String groupTitle = extractAttribute(line, "group-title");
                        String[] parts = line.split(",");
                        String channelName = parts.length > 1 ? parts[1] : "";

                        channel = new Channel(tvgId, tvgName, tvgLogo, groupTitle, channelName, "");
                    } else if (line.startsWith("http://") || line.startsWith("https://")) {
                        if (channel != null) {
                            channel.setUrl(line);
                            channels.add(channel);
                            channel = null;
                        }
                        else{break;}
                    }
                }
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        //Sort by group title in an Hashmap
        TreeMap<String, ArrayList<Channel>> categories = new TreeMap<>();
        //Add the favorites category
        categories.put("Favorites", new ArrayList<>());

        //Create the TreeMap with categories
        for (Channel c : channels) {
            if (!categories.containsKey(c.getGroupTitle())) {categories.put(c.getGroupTitle(), new ArrayList<>());}
            Objects.requireNonNull(categories.get(c.getGroupTitle())).add(c);
        }

        //Finally, set the channels for the playlist
        playlist.setChannels(categories);

    }


    private String extractAttribute(String line, String attribute) {
        String result = "";
        String search = attribute + "=\"";
        int start = line.indexOf(search);
        if (start != -1) {
            start += search.length();
            int end = line.indexOf("\"", start);
            if (end != -1) {
                result = line.substring(start, end);
            }
        }
        return result;
    }

    public String getM3U8Channel(Channel channel){
            return "#EXTINF:-1 tvg-id=\"" + channel.getTvgId() + "\" tvg-name=\"" + channel.getTvgName() + "\" tvg-logo=\"" + channel.getTvgLogo() + "\" group-title=\"" + channel.getGroupTitle() + "\"," + channel.getChannelName() + "\n" + channel.getUrl() + "\n";
    }
}
