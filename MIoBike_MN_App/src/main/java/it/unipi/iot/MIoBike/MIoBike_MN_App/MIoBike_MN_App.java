package it.unipi.iot.MIoBike.MIoBike_MN_App;

import static it.unipi.iot.MIoBike.MIoBike_MN_App.MN_Constants.*;

public class MIoBike_MN_App {
	public static void main( String[] args ) {
		if(args.length == 2) {
			String	MN_ADDRESS = args[0];
			String	BIKE_NAME = args[1];
			String	MN_URI = PROTOCOL + MN_ADDRESS+ ":" + MN_OM2M_PORT;
			String	MN_ID = "/" + BIKE_NAME + "-mn-cse";
			String	MN_NAME = "/" + BIKE_NAME + "-mn-name";

			MIoBike_MN_Manager MN_manager = new MIoBike_MN_Manager(MN_URI, MN_ID, MN_NAME, BIKE_NAME);
		} else {
			System.out.println("Please inert address and Name of the Bike");
		}
	}
}