package it.unipi.iot.MIoBike.MIoBike_IN_ADN;

import static it.unipi.iot.MIoBike.MIoBike_IN_ADN.IN_Constants.*;

import it.unipi.iot.MIoBike.MIoBike_Utils.MonitorThread;

public class IN_ADN {
	static String Node_Name;
	static MIoBike_IN_Manager IN_manager;
	static MonitorThread NotificationManager;
	
	public static void main( String[] args ) {
		IN_manager = new MIoBike_IN_Manager(IN_uri, IN_Id, IN_Name);
	}
}
