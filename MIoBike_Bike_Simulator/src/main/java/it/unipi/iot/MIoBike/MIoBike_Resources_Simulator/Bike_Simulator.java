package it.unipi.iot.MIoBike.MIoBike_Resources_Simulator;

import java.net.SocketException;

public class Bike_Simulator{
	public static void main( String[] args ) {
		BikeMonitor Bike;
//		NFC_Reader_Simulator User = new NFC_Reader_Simulator("coap://127.0.0.1:5691/NFC_NM", "Gionni");
//		User.start();
		try {
			Bike = new BikeMonitor(5690);
			Bike.addEndpoints();
	    	Bike.start();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
