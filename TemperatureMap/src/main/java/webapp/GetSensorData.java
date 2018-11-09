package webapp;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.api.client.http.HttpRequest;
import com.google.api.client.http.HttpResponse;
import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.PropertyProjection;
import com.google.appengine.api.datastore.Query;
import com.google.appengine.api.datastore.Query.FilterOperator;

@WebServlet(
	    name = "GetSensorData",
	    urlPatterns = {"/GetSensorData"}
	)

public class GetSensorData extends HttpServlet {
	
	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) {
		
		System.out.println(request.getParameter("ID"));
		
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		 Query q = new Query("TempSensor");
		 //q.addFilter("SensorID", FilterOperator.EQUAL, )
		 //PreparedQuery pq = ds.prepare(q);
	}

}
