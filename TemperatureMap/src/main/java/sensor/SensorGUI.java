package sensor;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;
import javax.swing.border.TitledBorder;

public class SensorGUI extends JFrame {
	
	private JLabel longitudeLabel = new JLabel(" Longitude: ");
	private JLabel latitudeLabel = new JLabel(" Latitude: ") ;
	private JLabel simulatedLabel = new JLabel(" Simulated: ");
	private JTextField latitudeTextField = new JTextField();
	private JTextField longitudeTextField = new JTextField();
	private JCheckBox simulatedCheckBox = new JCheckBox();
	private JLabel valueLabel = new JLabel(" last value: ");
	private JLabel timeLabel = new JLabel(" Date/Time of last value ");
	private JTextField valueTextField = new JTextField();
	private JTextField timeTextField = new JTextField();
	private JButton startStopButton = new JButton("Start Upload");
	private JTextArea replyTextArea = new JTextArea();
	private JScrollPane scrollPane = new JScrollPane (replyTextArea);
	
	private boolean started = false;
	
	private DBConnection connection;
	private Thread uploader;
	
	public SensorGUI() {
		super("Sensor Data Upload Tool");
		
		startStopButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                startStop();
            }
        });
		
		// Latitude Panel
		JPanel latitudePanel = new JPanel(new BorderLayout());
        latitudeLabel.setPreferredSize (new Dimension (150, 28));
        latitudeTextField.setPreferredSize (new Dimension (400, 28));
        latitudeTextField.setText ("47.1667");
        latitudePanel.add (latitudeLabel, BorderLayout.WEST);
        latitudePanel.add (latitudeTextField, BorderLayout.CENTER);
        latitudePanel.setBorder (new EmptyBorder (2, 2, 2, 2));
        
        // Longitude Panel
        JPanel longitudePanel = new JPanel(new BorderLayout());
        longitudeLabel.setPreferredSize (new Dimension (150, 28));
        longitudeTextField.setPreferredSize (new Dimension (400, 28));
        longitudeTextField.setText ("9.4667");
        longitudePanel.add (longitudeLabel, BorderLayout.WEST);
        longitudePanel.add (longitudeTextField, BorderLayout.CENTER);
        longitudePanel.setBorder (new EmptyBorder (2, 2, 2, 2));
        
        // Simulated Panel
        JPanel simulatedPanel = new JPanel(new BorderLayout());
        simulatedLabel.setPreferredSize (new Dimension (150, 28));
        simulatedPanel.add (simulatedLabel, BorderLayout.WEST);
        simulatedPanel.add (simulatedCheckBox, BorderLayout.CENTER);
        simulatedPanel.setBorder (new EmptyBorder (2, 2, 2, 2));
        
        // combine the tree above parts
        JPanel inputPanel = new JPanel (new GridLayout (3, 1));
        inputPanel.add (latitudePanel);
        inputPanel.add (longitudePanel);
        inputPanel.add(simulatedPanel);
        inputPanel.setBorder (new TitledBorder (" Input "));
        
        // current sensor Value
        JPanel valuePanel = new JPanel(new BorderLayout());
        valueLabel.setPreferredSize (new Dimension (150, 28));
        valueTextField.setPreferredSize (new Dimension (400, 28));
        valueTextField.setEnabled(false);
        valuePanel.add (valueLabel, BorderLayout.WEST);
        valuePanel.add (valueTextField, BorderLayout.CENTER);
        valuePanel.setBorder (new EmptyBorder (2, 2, 2, 2));
        
        // current Date & Time
        JPanel timePanel = new JPanel(new BorderLayout());
        timeLabel.setPreferredSize (new Dimension (150, 28));
        timeTextField.setPreferredSize (new Dimension (400, 28));
        timeTextField.setEnabled(false);
        timePanel.add (timeLabel, BorderLayout.WEST);
        timePanel.add (timeTextField, BorderLayout.CENTER);
        timePanel.setBorder (new EmptyBorder (2, 2, 2, 2));
        
        // combine both above parts
        JPanel currentPanel = new JPanel (new GridLayout (2, 1));
        currentPanel.add (valuePanel);
        currentPanel.add (timePanel);
        currentPanel.setBorder (new TitledBorder (" Last Info "));
	
        // add the main button
        JPanel buttonPanel = new JPanel(new FlowLayout());
        buttonPanel.add(startStopButton);
        
        // add the reply textArea
        JPanel replyPanel = new JPanel (new BorderLayout ());
        replyTextArea.setEditable(false);
        replyPanel.add (scrollPane, BorderLayout.CENTER);
        replyPanel.setBorder (new TitledBorder (" Last Reply from Server "));
	
        // combine upper parts
        JPanel upperPanel = new JPanel();
        upperPanel.setLayout(new BoxLayout(upperPanel,BoxLayout.Y_AXIS));
        upperPanel.add(inputPanel);
        upperPanel.add(currentPanel);
        upperPanel.add(buttonPanel);
        
        // put together
        JPanel contentPanel = new JPanel(new BorderLayout());
        contentPanel.add(upperPanel, BorderLayout.NORTH);
        contentPanel.add(replyPanel, BorderLayout.CENTER);
        
        setContentPane(contentPanel);
        setMinimumSize(new Dimension(400,400));
        setPreferredSize(new Dimension(400,400));
        
        pack();
        setVisible(true);
        
	}
	
	private void startStop() {
		if(started) {
			connection.setStop();
			try {
				uploader.join();
			} catch (InterruptedException e) {
				replyTextArea.setText(e.getMessage());
			}
			latitudeTextField.setEnabled(true);
			longitudeTextField.setEnabled(true);
			simulatedCheckBox.setEnabled(true);
			started = false;
			startStopButton.setText("Start Upload");
		} else {
			connection = new DBConnection(latitudeTextField.getText(),longitudeTextField.getText(),simulatedCheckBox.isSelected(),valueTextField,timeTextField,replyTextArea);
			uploader = new Thread(connection);
			uploader.start();
			latitudeTextField.setEnabled(false);
			longitudeTextField.setEnabled(false);
			simulatedCheckBox.setEnabled(false);
			started = true;
			startStopButton.setText("Stop Upload");
		}
	}
	
	public static void main (String argv[]) {
        new SensorGUI ();
    } 
	
	

}
