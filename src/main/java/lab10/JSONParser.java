package lab10;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import org.json.JSONObject;

public class JSONParser {
	public static void downloadWeather() {
		try {
			URL website = new URL(
					"http://api.openweathermap.org/data/2.5/weather?id=3094802&APPID=f81e91fadddb0efff5d31ac9b997c440&lang=-pl");
			try (InputStream in = website.openStream()) {
				File file = new File("weather.json");
				Files.copy(in, file.toPath(), StandardCopyOption.REPLACE_EXISTING);
			}
		} catch (MalformedURLException ex) {
			System.out.println(ex);
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	public static JSONObject getWeather()
	{
		try (BufferedReader br = new BufferedReader(new FileReader("weather.json"))) {
			StringBuilder sb = new StringBuilder();
			String line = new String();
			line = br.readLine();
			while (line != null) {
				sb.append(line);
				line = br.readLine();
			}
			line = sb.toString();
			return new JSONObject(line);
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return null;
	}
}
