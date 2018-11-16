package sensor;

import java.time.Instant;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Calendar;

import jssc.SerialPort;
import jssc.SerialPortEvent;
import jssc.SerialPortEventListener;
import jssc.SerialPortException;

/**
 * 
 * @author lgassner
 * This class connects to a temperature sensor and delivers its current temperature
 *
 */

public class RealSensor extends Sensor {

	double temp;
	String dateTime;
	SerialPort port;
	PortReader pr;
	
	public RealSensor(double latitude, double longitude) {
		super(latitude, longitude, false);
		
		port = new SerialPort("COM4");
		
		try {
			port.openPort();
			port.setParams(SerialPort.BAUDRATE_9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
			pr = new PortReader(port, this);
			port.addEventListener(pr, SerialPort.MASK_RXCHAR);
		} catch(Exception e) {
			System.out.println(e.getMessage());
		}
		
	}
	
	protected synchronized void setTemp(double temp) {
		this.temp = temp;
	}
	
	protected synchronized void setDateTime(String dateTime) {
		this.dateTime = dateTime;
	}
	
	public synchronized double getTemp() {
		return temp;
	}
	
	public synchronized String getDateTime() {
		return dateTime;
	}
	
	@Override
	public void close() {
		try {
			port.closePort();
		} catch (SerialPortException e) {
			// swallow
		} finally {
			pr = null;
		}
	}
	
}

class PortReader implements SerialPortEventListener {
	
	SerialPort port;
	RealSensor sensor;
	String receivedData = "";
	StringBuffer sb = new StringBuffer();
	
	public PortReader(SerialPort port, RealSensor sensor) {
		this.port = port;
		this.sensor = sensor;
	}
	
	@Override
    public void serialEvent(SerialPortEvent event) {
        if(event.isRXCHAR() && event.getEventValue() > 0) {
            try {
            	
            	// reply format: "Temperature: xx.x°C\n\r"
            	
            	sb.append(port.readString(event.getEventValue()));
                if(sb.toString().contains("\n\r")) {
                	int index = sb.indexOf("\n\r");
                	String first = sb.substring(0, index);
                	String second = sb.substring(index + 2);
                	sb = new StringBuffer(second);
                	
                	// get temperature out of reply
                	index = first.indexOf(" ");
                	String temp = first.substring(index + 1, first.length()-2);
                	// set temperature and it's time
                	sensor.setTemp(Double.parseDouble(temp));
                	sensor.setDateTime(Instant.now().toString());
                }
               
            }
            catch (SerialPortException ex) {
                System.out.println("Error in receiving string from COM-port: " + ex);
            }
        }
    }
}
