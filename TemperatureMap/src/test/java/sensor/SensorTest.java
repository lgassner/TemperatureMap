package sensor;

public class SensorTest {

	public static void main(String[] args) {

		// Coordinates of Buchs
		VirtualSensor sensorBuchs = new VirtualSensor(47.1667, 9.4667, 0);
		
		for(int i = 0; i<10; i++) {
			System.out.println(sensorBuchs.getDateTime());
			System.out.println(sensorBuchs.getTemp());
		}

	}

}
