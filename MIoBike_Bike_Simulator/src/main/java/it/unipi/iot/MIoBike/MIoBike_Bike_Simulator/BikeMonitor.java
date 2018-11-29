package it.unipi.iot.MIoBike.MIoBike_Bike_Simulator;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.network.CoapEndpoint;
import org.eclipse.californium.core.network.EndpointManager;
import org.eclipse.californium.core.server.resources.Resource;

public class BikeMonitor extends CoapServer {
	private int Coap_port;

	public BikeMonitor(int port) throws SocketException {
		Coap_port = port;
		add(new Resource[] { new AirQuality_Simulator() });
		add(new Resource[] { new GPS_Simulator() });
		add(new Resource[] { new Lock_Simulator() });
		add(new Resource[] { new Temperature_Simulator() });
		add(new Resource[] { new Humidity_Simulator() });
		add(new Resource[] { new TyrePressure_Simulator() });
		add(new Resource[] { new Odometer_Simulator() });
		add(new Resource[] { new Speed_Simulator() });
		add(new Resource[] { new Phy_Simulator(this) });
	}
	
	public void handler(boolean value) {
		System.out.println("Empty Handler");
	}
	
	public void addEndpoints() {
		for (InetAddress addr : EndpointManager.getEndpointManager().getNetworkInterfaces()) {
			if (((addr instanceof Inet4Address)) || (addr.isLoopbackAddress()))
			{
				InetSocketAddress bindToAddress = new InetSocketAddress(addr, Coap_port);
				addEndpoint(new CoapEndpoint(bindToAddress));
			}
		}
	}
	
}
