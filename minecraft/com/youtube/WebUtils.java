package com.youtube;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import com.mentalfrostbyte.jello.music.music.Player;

public class WebUtils {
	public static String agent1 = "User-Agent";
    public static String agent2 = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/72.0.3626.121 Safari/537.36";
	
	public static String visitSiteThreaded(final String urly){
		final List<String> lines = new ArrayList<String>();
		String stuff = "";
		    (new Thread(new Runnable()
            {
                public void run()
                {
		URL url;
        try {
            String line;
            url = new URL(urly);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.addRequestProperty(agent1, agent2);
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            while ((line = in.readLine()) != null) {
            	lines.add(line);
            }
        }
        catch (Exception e) {
        }
                }
            })).start();
        for(String s : lines){
        	stuff += s;
        }
		return stuff;
		
	}
	
	public static void playMusicLink(final String urly) {
		new Thread() {
			@Override
			public void run() {
				String[] a = (visitSite("https://www.yt-download.org/api/button/mp3/"+urly).split("<a href="));
				try {
					Player.play(a[4].substring(1, a[3].indexOf(" ")-1));
					//TOODO Mania.instance.music.nextPlay = true;
				}catch(Error e){
					System.out.println("ERror");
				}
			}
		}.start();
	}
	
	public static List<String> visitSiteThreadedFriends(final String urly){
		final List<String> lines = new ArrayList<String>();
		try
        {	
            (new Thread(new Runnable()
            {
                public void run()
                {
		URL url;
        try {
            String line;
            url = new URL(urly);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.addRequestProperty(agent1, agent2);
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            while ((line = in.readLine()) != null) {
            		if(!line.isEmpty() && !line.equals(" ") && !line.equals("   ")) {
            		lines.add(line.contains(" ") ? line.replace(" ", "") : line);
            	}
            }
        }
        catch (Exception e) {
        }
                }
            })).start();
        }
        catch (RuntimeException runtimeexception){
        	
        }
		return lines;
	}
	
	public static String visitSite(String urly){
		ArrayList<String> lines = new ArrayList<String>();
		String stuff = "";
		URL url;
        try {
            String line;
            url = new URL(urly);
            HttpURLConnection connection = (HttpURLConnection)url.openConnection();
            connection.addRequestProperty(agent1, agent2);
            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream(), "UTF-8"));
            while ((line = in.readLine()) != null) {
                lines.add(line);
            }
        }
        catch (Exception e) {
        }
        for(String s : lines){
        	stuff += s;
        }
		return stuff;
	}
	
}
