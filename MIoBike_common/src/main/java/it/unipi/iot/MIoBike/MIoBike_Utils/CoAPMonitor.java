package it.unipi.iot.MIoBike.MIoBike_Utils;

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
	private int Coap_port;
	private String NotificationManager_name;
	om2m_Node_Manager owner;

	public CoAPMonitor(String name, int port, om2m_Node_Manager my_owner) throws SocketException {
		NotificationManager_name = name;
		Coap_port = port;
		owner = my_owner;
		add(new Resource[] { new Monitor() });
	}
	
	void addEndpoints() {
		for (InetAddress addr : EndpointManager.getEndpointManager().getNetworkInterfaces()) {
			if (((addr instanceof Inet4Address)) || (addr.isLoopbackAddress()))
			{
				InetSocketAddress bindToAddress = new InetSocketAddress(addr, Coap_port);
				addEndpoint(new CoapEndpoint(bindToAddress));
			}
		}
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
			owner.Notification_Handler(contentStr);
		}
	}
}
