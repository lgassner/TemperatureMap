package sensor;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.ProtocolException;
import java.net.URL;

import javax.net.ssl.HttpsURLConnection;
import javax.swing.JTextArea;
import javax.swing.JTextField;

public class DBConnection implements Runnable {

	final private String localhost = "http://localhost:63000/PutSensorData";
	final private String realURL = "https://temperature-map.appspot.com/PutSensorData";
	final private String password = "0pen5esame";
	final private int uploadtime = 10000; // [ms]
	
	private String latitude;
	private String longitude;
	private boolean stop = false;
	private JTextField valueField;
	private JTextField timeField;
	private JTextArea textArea;
	
	private double temp;
	private String dateTime;
	
	private Sensor sensor;
	
	public DBConnection(String latitude, String longitude, boolean simulated,JTextField valueField, JTextField timeField, JTextArea textArea) {
		this.latitude = latitude;
		this.longitude = longitude;
		this.textArea = textArea;
		this.valueField = valueField;
		this.timeField = timeField;		
		
		if(simulated) {
			sensor = new VirtualSensor(Double.parseDouble(latitude),Double.parseDouble(longitude),0); 
		} else {
			sensor = new RealSensor(Double.parseDouble(latitude),Double.parseDouble(longitude));
		}
	}

	@Override
	public void run() {
			
		while(!stop) {
			
			try {
				Thread.sleep(uploadtime);
			} catch (InterruptedException e) {
				textArea.setText(e.getMessage());
			}
			
			dateTime = sensor.getDateTime();
			
			// no sensor value yet
			if(dateTime == null) {
				continue;
			}
			
			temp = sensor.getTemp();
			
			String data = "Password=" + password;
			data += "&ID=" + Integer.toString(sensor.getID());
			data += "&Latitude=" + latitude;
			data += "&Longitude=" + longitude;
			data += "&Simulated=" + Boolean.toString(sensor.getSimulated());
			data += "&DateTime=" + dateTime;
			data += "&Temperature=" + Double.toString(temp);
			
			valueField.setText(Double.toString(temp));
			timeField.setText(dateTime);

			URL url = null;
			try {
				// use for localhost
				// url = new URL(localhost);
				// use for real URL
				url = new URL(realURL);
			} catch (MalformedURLException e) {
				textArea.setText(e.getMessage());
			}
			
			// use for localhost
			//HttpURLConnection con = null;
			// use for real URL
			HttpsURLConnection con = null;
			try {
				// use for localhost
				// con = (HttpURLConnection) url.openConnection();
				// use for real URL
				con = (HttpsURLConnection) url.openConnection();
			} catch (IOException e) {
				textArea.setText(e.getMessage());
			}

			try {
				con.setRequestMethod("POST");
			} catch (ProtocolException e) {
				textArea.setText(e.getMessage());
			}
			con.setDoOutput(true);
			try {
				con.connect();
				con.getOutputStream().write(data.getBytes("UTF-8"));
				BufferedReader reader = new BufferedReader(new InputStreamReader(con.getInputStream()));
				textArea.setText("");
				String line;
				while ((line = reader.readLine()) != null) {
					textArea.append(line);
				}
				reader.close();
			} catch (UnsupportedEncodingException e) {
				textArea.setText(e.getMessage());
			} catch (IOException e) {
				textArea.setText(e.getMessage());
			}
		}
	}

	public void setStop() {
		stop = true;
		sensor.close();
	}
	
	
}
