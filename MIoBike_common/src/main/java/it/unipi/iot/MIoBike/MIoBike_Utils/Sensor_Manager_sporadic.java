package it.unipi.iot.MIoBike.MIoBike_Utils;

import static it.unipi.iot.MIoBike.MIoBike_Utils.Constants.*;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.CoapServer;
import org.eclipse.californium.core.network.CoapEndpoint;
import org.eclipse.californium.core.network.EndpointManager;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.eclipse.californium.core.server.resources.Resource;
import org.json.JSONObject;

public class Sensor_Manager_sporadic extends Resource_Manager{	
	private String NotificationManager_name;
	private int port;
	public Sensor_Manager_sporadic(String resource_uri, om2m_Node_Manager my_om2m_MN_manager,
			String my_Bike_Name, String my_Sensor_Name, int Resource_NM_port, String my_type) {
		super(resource_uri, my_om2m_MN_manager, 0, my_Bike_Name, my_Sensor_Name, my_type);
		port = Resource_NM_port;
		NotificationManager_name = ResourceName + NM_NAME_EXTENSION;
	}
	
	public class CoAPMonitor extends CoapServer {
		private int Coap_port;
		private String NotificationManager_name;
		Sensor_Manager_sporadic owner;

		public CoAPMonitor(String name, int port, Sensor_Manager_sporadic my_owner) throws SocketException {
			owner = my_owner;
			NotificationManager_name = name;
			Coap_port = port;
			add(new Resource[] { new Monitor() });
		}
		
		void addEndpoints() {
			for (InetAddress addr : EndpointManager.getEndpointManager().getNetworkInterfaces()) {
				if (((addr instanceof Inet4Address)) || (addr.isLoopbackAddress())) {
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
			 
			public void handleGET(CoapExchange exchange) {
				String format = exchange.getQueryParameter(JSON_KEY_FORMAT);
				String value = exchange.getQueryParameter(JSON_KEY_VALUE);
				JSONObject data_json = new JSONObject();
				data_json.put(JSON_KEY_FORMAT, format);
				data_json.put(JSON_KEY_VALUE, value);
				String data = data_json.toString();
				exchange.respond("forwarded to the system: "+ data);
				owner.execute_Job(data);
			}
		}
	}
	
	private void execute_Job(String data) {
		String date_str = "";
		SimpleDateFormat sdfLocal = new SimpleDateFormat("yyyy.MM.dd_HH:mm:ss");
		sdfLocal.setTimeZone(TimeZone.getTimeZone("GMT"));
	    date_str = sdfLocal.format(new Date());
		
		json_data.put(JSON_KEY_DATE, date_str);
		json_data.put(JSON_KEY_RESOURCEDATA, new JSONObject(data));
		
		String cnf = "JSON";
		String con = json_data.toString();
		String [] labels = new String[] { type };
		om2m_MN_manager.create_Content_Instance(BikeName, ResourceName, cnf, con, labels);

		if(DEV_MODE) {
			System.out.println(con.toString());
		}
	}

	public void run(){
		CoAPMonitor server;
		try {
			server = new CoAPMonitor(NotificationManager_name, port, this);
			server.addEndpoints();
	    	server.start();
		} catch (SocketException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
