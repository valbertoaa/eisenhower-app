package com.valberto.util;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DiscordWebhook {
    private static final String WEBHOOK_URL = "https://discordapp.com/api/webhooks/1325872025822040137/wd0WvVVUv3mfZj1VCWMVL_fyHO8UygdVvJakdgmaYEC5szofEo_I3dhWQMZfQJ9t1rXD";

    @SuppressWarnings("deprecation")
    
	public static void sendMessage(String message) {
        try {
            URL url = new URL(WEBHOOK_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoOutput(true);
            connection.setRequestMethod("POST");
            connection.setRequestProperty("Content-Type", "application/json");

            String payload = String.format("{\"content\": \"%s\"}", message);
            OutputStream outputStream = connection.getOutputStream();
            outputStream.write(payload.getBytes());
            outputStream.flush();
            outputStream.close();
            
            connection.getResponseCode();

            //int responseCode = connection.getResponseCode();            
            //System.out.println("Response Code: " + responseCode);
            
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
 
}
