package sensor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

public class DBConnection {

	final static private String localhost = "http://localhost:64000/PutSensorData";
	final static private String realURL = "http://temperature-map.appspot.com/PutSensorData";
	
	// start with latitude, longitude
	public static void main(String[] args) {
	
		String password = "0pen5esame";
		String latitude = args[0];
		String longitude = args[1];
		
		// TODO: use real sensor
		
		VirtualSensor sensor = new VirtualSensor(Double.parseDouble(latitude),Double.parseDouble(longitude),0);
		int i = 0;
		
		while(true) {
			
			if(sensor.getSimulated()) {
				i++;
				if(i > 1) {
					break;
				}
			} else {
				try {
					Thread.sleep(1000);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			String data = "Password=" + password;
			data += "&ID=" + Integer.toString(sensor.getID());
			data += "&Latitude=" + latitude;
			data += "&Longitude=" + longitude;
			data += "&Simulated=" + Boolean.toString(sensor.getSimulated());
			data += "&DateTime=" + sensor.getDateTime();
			data += "&Temperature=" + Double.toString(sensor.getTemp());

			URL url = null;
			try {
				url = new URL(localhost);
				//url = new URL(realURL);
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			HttpURLConnection con = null;
			try {
				con = (HttpURLConnection) url.openConnection();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

			try {
				con.setRequestMethod("POST");
			} catch (ProtocolException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			con.setDoOutput(true);
			try {
				con.connect();
				con.getOutputStream().write(data.getBytes("UTF-8"));
				BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
				String line;
				while ((line = reader.readLine()) != null) {
					System.out.println(line);
				}
				reader.close();
			} catch (UnsupportedEncodingException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	    
	    
		
		
	}
	
	
}
