package lab10;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

import org.json.JSONObject;

public class Bot {
	private Date lastWeatherInfo;
	
	public Bot()
	{
		lastWeatherInfo = Calendar.getInstance().getTime();
	}
	
	public String respond(String msg)
	{
		if(msg.equals("Która godzina?"))
		{
			Calendar cal = Calendar.getInstance();
	        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
	        return sdf.format(cal.getTime());
		}
		else if(msg.equals("Jaki dziś dzień tygodnia?"))
		{
			Calendar cal = Calendar.getInstance();
			String wday;
			int day = cal.get(Calendar.DAY_OF_WEEK);
			switch (day){
			case 1: wday = "Niedziela";
			break;
			case 2: wday = "Poniedziałek";
			break;
			case 3: wday = "Wtorek";
			break;
			case 4: wday = "Środa";
			break;
			case 5: wday = "Czwartek";
			break;
			case 6: wday = "Piątek";
			break;
			case 7: wday = "Sobota";
			break;
			default: wday = "Coś poszło nie tak";
			}
			return wday;
		}
		else if(msg.equals("Jaka jest pogoda w Krakowie?"))
		{
			long diff = Calendar.getInstance().getTime().getTime() - lastWeatherInfo.getTime();
			long mins = diff / (60 * 1000) % 60;
			if(mins>10)
			{
				JSONParser.downloadWeather();
			}
			JSONObject weather = JSONParser.getWeather();
			String weatherstr = "" + weather.getJSONArray("weather").getJSONObject(0).getString("description") + 
					'\n' + "temperatura: " + (weather.getJSONObject("main").getDouble("temp") - 273.15) + "°C" + '\n' +
					"cisnienie: " + weather.getJSONObject("main").getInt("pressure") + "hPa" + '\n' +
					"wilgotność powietrza: " + weather.getJSONObject("main").getInt("humidity") + "%" + '\n';
			return weatherstr + "Dobra pogoda na Igrzyska Olimpijskie";
		}
		else
		{
			return "Niestety nie jestem w staie zrozumieć tego polecenia";
		}
	}
}
