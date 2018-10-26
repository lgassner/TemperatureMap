package sensor;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * 
 * @author lgassner
 *This class imports a CSV file with temperature data and delivers it to any asking program.
 *
 */

public class VirtualSensor extends Sensor {
	
	final boolean simulated = true;
	String csvFile = "2017_DataExport1h.csv";
	String csvSplitBy = ";";
	String line;
	BufferedReader br;
	
	ArrayList<Double> temps;
	ArrayList<LocalDateTime> dates;
	int i = 0;
	
	/**
	 * 
	 * @param uniqueID
	 * @param latitude
	 * @param longitude
	 * @param offset
	 */
	public VirtualSensor(int uniqueID, double latitude, double longitude, double offset){
		super(uniqueID, latitude, longitude, true);
		
		temps = new ArrayList<>();
		dates = new ArrayList<>();
		
		try {
			br = new BufferedReader(new FileReader(csvFile));
			br.readLine(); // skip header line
			
			while((line = br.readLine()) != null) {
				String[] data = line.split(csvSplitBy);
				temps.add(new Double(data[3])+offset);
				DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss", Locale.ENGLISH);
				dates.add(LocalDateTime.parse(data[0], formatter));
			}
			
		} catch (FileNotFoundException e) {
			System.err.println(e.getMessage());
		} catch (IOException e) {
			System.err.println(e.getMessage());
		} finally {
			if(br != null) {
				try {
					br.close();
				} catch(IOException e) {
					System.err.println(e.getMessage());
				}
			}
		}
		
	}

	@Override
	public double getTemp() {
		if(i >= temps.size()) {
			i = 0;
		}
		return temps.get(i++);
	}
	
	// Attention: run this method always together with getTemp (and before it) for consistency
	public LocalDateTime getDateTime() {
		return dates.get(i);
	}
}
