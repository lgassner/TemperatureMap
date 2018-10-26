package sensor;

public abstract class Sensor {

	// Lat & Long in decimal notation
	double latitude;
	double longitude;
	int ID;
	boolean simulated;
	
	/**
	 * @param: Unique ID, Latitude, Longitude
	*/
	public Sensor(int uniqueID, double latitude, double longitude, boolean simulated) {
		ID = uniqueID;
		this.latitude = latitude;
		this.longitude = longitude;
		this.simulated = simulated;
	}
	
	public abstract double getTemp();
	
	public double getLatitude() {
		return latitude;
	}
	
	public double getlongitude() {
		return longitude;
	}
	
	public int getID() {
		return ID;
	}
	
	public boolean getSimulated() {
		return simulated;
	}
	
}
