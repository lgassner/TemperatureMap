package webapp;

import java.io.IOException;
import java.time.Duration;
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
		
		// debug
		// System.out.println(request.getParameter("ID"));
		
		// query datastore for ordered list of clicked sensor
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		Query q = new Query("TempSensor");
		q.addFilter("SensorID", FilterOperator.EQUAL, Long.parseLong(request.getParameter("ID")));
		q.addSort("DateTime");
		PreparedQuery pq = ds.prepare(q);
		
		// put results in Array Lists
		ArrayList<LocalDateTime> dateTimes = new ArrayList<>();
		ArrayList<Double> temps = new ArrayList<>();
		
		for(Entity result : pq.asIterable()) {
			 
			 LocalDateTime dt = isoFormatter.parse(((String)result.getProperty("DateTime")).substring(0, 23), LocalDateTime::from);
			 dateTimes.add(dt);
			 temps.add((double) result.getProperty("Temperature"));
		 }
		 
		// generate JSON response
		String string = "{\"cols\":[";
		
		// generate cols of data table
		string += "{\"label\":\"date\", \"type\": \"datetime\"}";
		string += ",";
		string += "{\"label\":\"instant temp\", \"type\": \"number\"}";
		string += ",";
		string += "{\"label\":\"avg temp hour\", \"type\": \"number\"}";
		string += ",";
		string += "{\"label\":\"avg temp day\", \"type\": \"number\"}";
		string += ",";
		string += "{\"label\":\"avg temp week\", \"type\": \"number\"}";
		
		string += "],";
		
		string += "\"rows\": [";

		int lastIndex = dateTimes.size()-1;
		
		// get aggregated values
		LocalDateTime currentTime = dateTimes.get(lastIndex);
		
		// initialize values
		double averageWeek = 0;
		boolean weekDone = false;
		double averageDay = 0;
		boolean dayDone = false;
		double averageHour = 0;
		boolean hourDone = false;
		
		LocalDateTime checkDate;
		
		// debug
		// System.out.println(currentTime.minusWeeks(1).isAfter(dateTimes.get(lastIndex-1)));
		
		// calculate average values via discrete integral (trapez approximation)
		int i = dateTimes.size()-2;
		double area;
		Duration duration;
		while(i>=0) {
			checkDate = dateTimes.get(i);
			duration = Duration.between(checkDate, dateTimes.get(i+1));
			area = (temps.get(i) + temps.get(i+1))/2 * (duration.getSeconds() + duration.getNano() * Math.pow(10,-9));
			duration = Duration.between(checkDate, currentTime);
			
			if(!hourDone) {
				averageHour += area;
				if(currentTime.minusHours(1).isAfter(checkDate)) {
					averageHour /= (duration.getSeconds() + duration.getNano() * Math.pow(10, -9));
					hourDone = true;
				}
			}
			
			if(!dayDone) {
				averageDay += area;
				if(currentTime.minusDays(1).isAfter(checkDate)) {
					averageDay /= (duration.getSeconds() + duration.getNano() * Math.pow(10, -9));
					dayDone = true;
				}
			}
			
			if(!weekDone) {
				averageWeek += area;
				if(currentTime.minusWeeks(1).isAfter(checkDate)) {
					averageWeek /= (duration.getSeconds() + duration.getNano() * Math.pow(10, -9));
					weekDone = true;
				}
			}
			
			if(hourDone && dayDone && weekDone) {
				break;
			}
			
			i--;
			
		}
		
		// use full range if measurement period wasn't long enough
		duration = Duration.between(dateTimes.get(0), currentTime);
		double fullTime = duration.getSeconds() + duration.getNano() * Math.pow(10, -9);
		if(!hourDone) {	
			averageHour /= fullTime;
		}
		
		if(!dayDone) {
			averageDay /= fullTime;
		}
		
		if(!weekDone) {
			averageWeek /= fullTime;
		}
		
		// show only the last 24 measurements
		i = temps.size() - 24;
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
		// debug
		// System.out.println(string);

		 
	}

}
