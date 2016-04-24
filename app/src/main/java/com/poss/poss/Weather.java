package com.poss.poss;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import org.json.simple.*;

public class Weather {
	public static String fetchWeather(double latitude, double longitude) {
        if (latitude != 1000) {
            String api = "822044e4e41a50a8e4bcc246f07380a5";

            try {
                URL url = new URL("http://api.openweathermap.org/data/2.5/weather?lat="
                        + latitude + "&lon=" + longitude + "&APPID=" + api);

                URLConnection con = url.openConnection();
                InputStream is = con.getInputStream();

                BufferedReader br = new BufferedReader(new InputStreamReader(is));


                String line = null;

                while ((line = br.readLine()) != null) {
                    System.out.println(line);
                }

                //Object JSONValue.parse(line);

            } catch(IOException e) {
                System.out.println("URL Not Working");
            }

            return "";

        }
		return null;
	}
}