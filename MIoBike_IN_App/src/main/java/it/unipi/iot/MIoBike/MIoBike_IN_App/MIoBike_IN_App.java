package it.unipi.iot.MIoBike.MIoBike_IN_App;

import static it.unipi.iot.MIoBike.MIoBike_IN_App.IN_Constants.*;

import it.unipi.iot.MIoBike.MIoBike_common.MonitorThread;

public class MIoBike_IN_App{
	static String Node_Name;
	static MIoBike_IN_Manager IN_manager;
	static MonitorThread NotificationManager;
	
	public static void main( String[] args ) {
		IN_manager = new MIoBike_IN_Manager(IN_URI, IN_ID, IN_NAME);
	}
}
