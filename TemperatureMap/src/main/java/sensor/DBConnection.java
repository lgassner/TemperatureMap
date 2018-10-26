package sensor;

import com.google.appengine.api.datastore.DatastoreService;
import com.google.appengine.api.datastore.DatastoreServiceFactory;
import com.google.appengine.api.datastore.Entity;

public class DBConnection {

	DatastoreService ds;
	
	public DBConnection() {
		
		// get datastore service
		ds = DatastoreServiceFactory.getDatastoreService();
		
		// Coordinates of Buchs
		VirtualSensor sensorBuchs = new VirtualSensor(1, 47.1667, 9.4667, 0);
		
		for(int i = 0; i<24; i++) {
			Entity sensor = new Entity("TempSensor");
			sensor.setProperty("ID", sensorBuchs.getID());
			sensor.setProperty("Latitude", sensorBuchs.getLatitude());
			sensor.setProperty("Longitude", sensorBuchs.getlongitude());
			sensor.setProperty("Simulated", sensorBuchs.getSimulated());
			// TODO: unsupported Property Type
			//sensor.setProperty("Date & Time", sensorBuchs.getDateTime()); 
			sensor.setProperty("Temperature", sensorBuchs.getTemp());
			ds.put(sensor);
		}
	}
	
	public DatastoreService getDatastoreService() {
		return ds;
	}
	
	
}
