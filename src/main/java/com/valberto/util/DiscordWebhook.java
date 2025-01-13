package com.valberto.util;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DiscordWebhook {
    private static final String WEBHOOK_URL = "aqui url webhook";

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
