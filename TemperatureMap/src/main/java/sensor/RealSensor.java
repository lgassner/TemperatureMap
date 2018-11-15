package sensor;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;

import jssc.SerialNativeInterface;
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

public class RealSensor {

	static SerialPort port = new SerialPort("COM8");
	
	public RealSensor() {
		
		
	}
	
	public static void main(String[] args) {
		
		try {
			port.openPort();
			port.setParams(SerialPort.BAUDRATE_9600, SerialPort.DATABITS_8, SerialPort.STOPBITS_1, SerialPort.PARITY_NONE);
			port.addEventListener(new PortReader(port), SerialPort.MASK_RXCHAR);
		} catch(Exception e) {
			System.out.println(e.getMessage());
		}
		
		while(true) {
			
		}
	}
}

class PortReader implements SerialPortEventListener {
	
	SerialPort port;
	String receivedData = "";
	StringBuffer sb = new StringBuffer();
	
	public PortReader(SerialPort port) {
		this.port = port;
	}
	
	@Override
    public void serialEvent(SerialPortEvent event) {
        if(event.isRXCHAR() && event.getEventValue() > 0) {
            try {
            	
            	// reply format: Temperature: xx.x°C\n\r
            	
            	sb.append(port.readString(event.getEventValue()));
                //System.out.println("Received response: " + receivedData);
                if(sb.toString().contains("\n\r")) {
                	int index = sb.indexOf("\n\r");
                	String first = sb.substring(0, index);
                	String second = sb.substring(index + 2);
                	System.out.println("Received response: " + first);
                	sb = new StringBuffer(second);
                	
                	//TODO: get temperature out of reply
                }
               
            }
            catch (SerialPortException ex) {
                System.out.println("Error in receiving string from COM-port: " + ex);
            }
        }
    }
}
