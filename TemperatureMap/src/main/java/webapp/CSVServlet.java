package webapp;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStream;
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
	    name = "CSV Export",
	    urlPatterns = {"/csv"}
	)

public class CSVServlet extends HttpServlet {

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		DatastoreService ds = DatastoreServiceFactory.getDatastoreService();
		Query q = new Query("TempSensor");

		String IDString = request.getParameter("ID");
		// query datastore for ordered list of clicked sensor
		if(IDString != null) {
			q.addFilter("SensorID", FilterOperator.EQUAL, Long.parseLong(IDString));
			IDString = "_" + IDString;
		} else {
			IDString = "";
		}
		
		q.addSort("DateTime");
		
		PreparedQuery pq = ds.prepare(q);
		
		response.setContentType("text/comma-separated-values");
		String filename = "export" + IDString + ".csv";
		response.setHeader("Content-Disposition", "filename=\"" + filename + "\"");
		
		File fileCSV = File.createTempFile("export.csv", ".csv");
		 FileWriter fw = new FileWriter(fileCSV);
		 
		 fw.append("Date & Time;Sensor ID;Latitude;Longitude;Temperature;Simulated");

		 for(Entity result : pq.asIterable()) {

			 fw.append("\r\n");
			 fw.append(result.getProperty("DateTime").toString());
			 fw.append(";");
			 fw.append(result.getProperty("SensorID").toString());
			 fw.append(";");
			 fw.append(result.getProperty("Latitude").toString());
			 fw.append(";");
			 fw.append(result.getProperty("Longitude").toString());
			 fw.append(";");
			 fw.append(result.getProperty("Temperature").toString());
			 fw.append(";");
			 fw.append(result.getProperty("Simulated").toString());
		 
		 }
		 
		 fw.flush();
		 fw.close();
		 
		 BufferedInputStream bFis = new BufferedInputStream(new FileInputStream(fileCSV));
		 response.setContentLength((int) fileCSV.length());
		 OutputStream os = response.getOutputStream();

		 try {
			 int byteRead = 0;
			 while ((byteRead = bFis.read()) != -1) {
				 os.write(byteRead);
			 }
			 os.flush();
			 
		 } catch (Exception excp) {
			 // swallow
		 } finally {
			 os.close();
			 bFis.close();
		 }

	}
}
