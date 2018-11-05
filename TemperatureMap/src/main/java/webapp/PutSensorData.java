package webapp;

import java.io.IOException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.api.client.util.DateTime;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;

@WebServlet(
	    name = "PutSensorData",
	    urlPatterns = {"/PutSensorData"}
	)

public class PutSensorData extends HttpServlet {
	
	@Override
	  public void doPost(HttpServletRequest request, HttpServletResponse response) 
	      throws IOException {

		System.out.println("I'm in POST");
		
		response.setContentType("text/plain");
	    response.setCharacterEncoding("UTF-8");
		
		String password = request.getParameter("Password");
		if(password.equals("0pen5esame")) {

			int id = Integer.parseInt(request.getParameter("ID"));
			double latitude = Double.parseDouble(request.getParameter("Latitude"));
			double longitude = Double.parseDouble(request.getParameter("Longitude"));
			boolean simulated = Boolean.parseBoolean(request.getParameter("Simulated"));
			DateTime dateTime = new DateTime(request.getParameter("DateTime"));
			double temperature = Double.parseDouble(request.getParameter("Temperature"));
			
		    DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		    
		    Entity sensor = new Entity("TempSensor");
		    sensor.setProperty("SensorID", id);
		    sensor.setProperty("Latitude", latitude);
		    sensor.setProperty("Longitude", longitude);
		    sensor.setProperty("Simulated", simulated);
		    sensor.setProperty("DateTime", dateTime.toString()); 
		    sensor.setProperty("Temperature", temperature);
		    ds.put(sensor);
		    
		    response.getWriter().print("Put successful!\r\n");
			
		} else {
			response.getWriter().print("Access denied!\r\n");
		}
		
	    
	}
	
}
