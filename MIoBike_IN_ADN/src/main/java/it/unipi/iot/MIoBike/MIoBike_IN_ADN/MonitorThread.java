package it.unipi.iot.MIoBike.MIoBike_IN_ADN;

import java.net.SocketException;

public class MonitorThread extends Thread{
	private String NotificationManager_name;
	public MonitorThread(String name) {
		NotificationManager_name = name;
	}
	public void run(){
		CoAPMonitor server;
		try {
			server = new CoAPMonitor(NotificationManager_name);
			server.addEndpoints();
	    	server.start();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}