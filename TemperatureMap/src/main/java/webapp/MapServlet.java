package webapp;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.PropertyProjection;
import com.google.appengine.api.datastore.Query;

@WebServlet(
	    name = "Map",
	    urlPatterns = {"/map"}
	)

public class MapServlet extends HttpServlet {
	
	@Override
	  public void doGet(HttpServletRequest request, HttpServletResponse response) 
	      throws IOException {

		// get marker positions
		 DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		 Query q = new Query("TempSensor");
		 q.addProjection(new PropertyProjection("Latitude", Double.class));
		 q.addProjection(new PropertyProjection("Longitude", Double.class));
		 q.addProjection(new PropertyProjection("SensorID", Long.class));
		 q.setDistinct(true);
		 PreparedQuery pq = ds.prepare(q);
		 
		 ArrayList<String> locationvars = new ArrayList<>();
		 ArrayList<Double> latitudes = new ArrayList<>();
		 ArrayList<Double> longitudes = new ArrayList<>();
		 ArrayList<Long> ids = new ArrayList<>();
		 ArrayList<String> markervars = new ArrayList<>();
		 
		 int i = 0;
		 for(Entity result : pq.asIterable()) {
			 locationvars.add("location" + i);
			 latitudes.add((double) result.getProperty("Latitude"));
			 longitudes.add((double) result.getProperty("Longitude"));
			 ids.add((long)result.getProperty("SensorID"));
			 markervars.add("marker" + i++);
		 }
		    
	    response.setContentType("text/html");
	    response.setCharacterEncoding("UTF-8");

	    // generate map page from template
	    FileReader fr = new FileReader("map.html");
	    BufferedReader br = new BufferedReader(fr);
	    
	    String line;
	    String insertLine;
	    
	    // add markers & their listeners
	    while((line = br.readLine()) != null) {
	    	if(line.equals("//markerplace")) {
	    		 for(i = 0; i<locationvars.size(); i++) {
	    			 insertLine = "var ";
	    			 insertLine += locationvars.get(i);
	    			 insertLine += " = {lat: ";
	    			 insertLine += latitudes.get(i);
	    			 insertLine += ", lng: ";
	    			 insertLine += longitudes.get(i);
	    			 insertLine += "};";
	   
	    			 response.getWriter().println(insertLine);
	    		 }
	    	} else if(line.equals("//mapplace")) {
	    		insertLine = "var map = new google.maps.Map(document.getElementById('map'), {zoom: 4, center: ";
	    		insertLine += locationvars.get(0);
	    		insertLine += "});";
	    		
	    		response.getWriter().println(insertLine);
	    		
	    		for(i = 0; i<markervars.size(); i++) {
	    			insertLine = "var ";
	    			insertLine += markervars.get(i);
	    			insertLine += " = new google.maps.Marker({position: ";
	    			insertLine += locationvars.get(i);
	    			insertLine += ", map: map});";
	    			
	    			response.getWriter().println(insertLine);
	    			
	    			insertLine = markervars.get(i);
	    			insertLine += ".addListener('click',function(){infowindow.open(map, ";
	    			insertLine += markervars.get(i);
	    			insertLine += ");";
	    			
	    			response.getWriter().println(insertLine);
	    			insertLine = "drawChart(";
	    			insertLine += ids.get(i);
	    			insertLine += ");});";
	    			
	    			response.getWriter().println(insertLine);
	    		}
	    		
	    	} else {
	    		response.getWriter().println(line);
	    	}
	    }
	    
	    br.close();
	    fr.close();

	  }

}
