package it.unipi.iot.MIoBike.MIoBike_MN_ADN;

import org.eclipse.californium.core.coap.CoAP.ResponseCode;
import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.network.CoapEndpoint;
import org.eclipse.californium.core.network.EndpointManager;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.eclipse.californium.core.server.resources.Resource;

public class CoAPMonitor extends CoapServer {
	private static final int COAP_PORT = 5685;
	private static String NotificationManager_name;
	void addEndpoints() {
		for (InetAddress addr : EndpointManager.getEndpointManager().getNetworkInterfaces()) {
			if (((addr instanceof Inet4Address)) || (addr.isLoopbackAddress()))
			{
				InetSocketAddress bindToAddress = new InetSocketAddress(addr, COAP_PORT);
				addEndpoint(new CoapEndpoint(bindToAddress));
			}
		}
	}
	
	public CoAPMonitor(String name) throws SocketException {
		NotificationManager_name = name;
		add(new Resource[] { new Monitor() });
	}
	
	class Monitor extends CoapResource {
		public Monitor() {
			super(NotificationManager_name);	
			getAttributes().setTitle(NotificationManager_name);
		}
		 
		public void handlePOST(CoapExchange exchange) {
			exchange.respond(ResponseCode.CREATED);
			byte[] content = exchange.getRequestPayload();
			String contentStr = new String(content);
			Bike_Manager.handle_Notification(contentStr);
			System.out.println("---------------Notification Handler---------------");
			System.out.println(contentStr);
			System.out.println("--------------------------------------------------");
		}
	}
}
