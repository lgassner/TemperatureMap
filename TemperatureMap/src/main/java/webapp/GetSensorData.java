package webapp;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.FetchOptions;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;

@WebServlet(
	    name = "GetSensorData",
	    urlPatterns = {"/GetSensorData"}
	)

public class GetSensorData extends HttpServlet {
	
	DateTimeFormatter isoFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;
	
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		System.out.println(request.getParameter("ID"));
		
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		Query q = new Query("TempSensor");
		q.addFilter("SensorID", FilterOperator.EQUAL, Long.parseLong(request.getParameter("ID")));
		q.addSort("DateTime");
		PreparedQuery pq = ds.prepare(q);
		
		ArrayList<LocalDateTime> dateTimes = new ArrayList<>();
		ArrayList<Double> temps = new ArrayList<>();
		
		for(Entity result : pq.asIterable()) {
			 
			 LocalDateTime dt = isoFormatter.parse(((String)result.getProperty("DateTime")).substring(0, 23), LocalDateTime::from);
			 dateTimes.add(dt);
			 temps.add((double) result.getProperty("Temperature"));
		 }
		 
		
		
		String string = "{\"cols\":[";
		
		// generate cols of data table
		string += "{\"label\":\"date\", \"type\": \"datetime\"}";
		string += ",";
		string += "{\"label\":\"instant temp\", \"type\": \"number\"}";
		string += ",";
		string += "{\"label\":\"average temp hour\", \"type\": \"number\"}";
		string += ",";
		string += "{\"label\":\"average temp day\", \"type\": \"number\"}";
		string += ",";
		string += "{\"label\":\"average temp week\", \"type\": \"number\"}";
		
		string += "],";
		
		string += "\"rows\": [";

		int lastIndex = dateTimes.size()-1;
		
		// get aggregated values
		double averageWeek = temps.get(lastIndex);
		boolean weekDone = false;
		double averageDay= temps.get(lastIndex);
		boolean dayDone = false;
		double averageHour= temps.get(lastIndex);
		boolean hourDone = false;
		
		LocalDateTime currentTime = dateTimes.get(lastIndex);
		LocalDateTime checkDate;
		
		// debug
		System.out.println(currentTime.minusWeeks(1).isAfter(dateTimes.get(lastIndex-1)));
		
		// -debug
		
		int i = dateTimes.size()-2;
		while(i>=0) {
			checkDate = dateTimes.get(i);
			
			if(!hourDone) {
				if(currentTime.minusHours(1).isBefore(checkDate)) {
					averageHour += temps.get(i);
					System.out.println("add hour");
				} else {
					averageHour /= (lastIndex - i);
					hourDone = true;
				}
			}
			
			if(!dayDone) {
				if(currentTime.minusDays(1).isBefore(checkDate)) {
					averageDay += temps.get(i);
					System.out.println("add day");
				} else {
					averageDay /= (lastIndex - i);
					dayDone = true;
				}
			}
			
			if(!weekDone) {
				if(!weekDone && currentTime.minusWeeks(1).isBefore(checkDate)) {
					averageWeek += temps.get(i);
					System.out.println("add week");
				} else {
					averageWeek /= (lastIndex - i);
					weekDone = true;
				}
			}
			
			i--;
			
		}
		
		if(!hourDone) {
			averageHour /= lastIndex;
		}
		
		if(!dayDone) {
			averageDay /= lastIndex;
		}
		
		if(!weekDone) {
			averageWeek /= lastIndex;
		}
		
		// show only the last ten measurements
		i = temps.size() - 10;
		if(i < 0) {
			i = 0;
		}
		
		for(;i<dateTimes.size();i++) {
			
			String temp = temps.get(i).toString();
			
			string += "{\"c\":[";
			
			LocalDateTime dt = dateTimes.get(i);
			int year = dt.getYear();
			int month = dt.getMonthValue() - 1; // 0 - 11
			int day = dt.getDayOfMonth();
			int hour = dt.getHour();
			int minute = dt.getMinute();
			int second = dt.getSecond();

			string += "{\"v\": \"Date(" + year + "," + month + "," + day + "," + hour + "," + minute + "," + second + ")\"}";
			string += ",";
			string += "{\"v\":" + temp +"}";
			string += ",";
			string += "{\"v\":" + averageHour +"}";
			string += ",";
			string += "{\"v\":" + averageDay +"}";
			string += ",";
			string += "{\"v\":" + averageWeek +"}";
			
			string += "]}";
			
			if(i<dateTimes.size()-1) {
				string += ",";
			}
			
		}
		
		string += "]}";
		
		response.getWriter().print(string);
		System.out.println(string);


		 
	}

}
