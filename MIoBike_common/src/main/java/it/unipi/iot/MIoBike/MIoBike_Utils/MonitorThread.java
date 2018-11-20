package it.unipi.iot.MIoBike.MIoBike_Utils;

import java.net.SocketException;

public class MonitorThread extends Thread{
	private String NotificationManager_name;
	private int port;
	private om2m_Node_Manager owner;
	
	public MonitorThread(String name, int port, om2m_Node_Manager my_owner) {
		NotificationManager_name = name;
		this.port = port;
		owner = my_owner;
	}
	public void run(){
		CoAPMonitor server;
		try {
			server = new CoAPMonitor(NotificationManager_name, port, owner);
			server.addEndpoints();
	    	server.start();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}