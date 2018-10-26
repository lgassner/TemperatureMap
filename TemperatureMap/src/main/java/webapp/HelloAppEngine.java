package webapp;

import java.io.IOException;

import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.Entity;
import com.google.appengine.api.datastore.PreparedQuery;
import com.google.appengine.api.datastore.Query;

import sensor.DBConnection;

@WebServlet(
    name = "HelloAppEngine",
    urlPatterns = {"/hello"}
)
public class HelloAppEngine extends HttpServlet {

  @Override
  public void doGet(HttpServletRequest request, HttpServletResponse response) 
      throws IOException {

    response.setContentType("text/plain");
    response.setCharacterEncoding("UTF-8");

    response.getWriter().print("Hello App Engine!\r\n");
    
    //put data from file in datastore
    DBConnection dbc = new DBConnection();
    
    DatastoreService ds = dbc.getDatastoreService();
    Query q = new Query("TempSensor");
    q.setFilter(new Query.FilterPredicate("Simulated", Query.FilterOperator.EQUAL, true));
    PreparedQuery pq = ds.prepare(q);
    
    for(Entity result : pq.asIterable()) {
    	response.getWriter().print(result.getProperty("Temperature") + "\r\n");
    }

  }
}