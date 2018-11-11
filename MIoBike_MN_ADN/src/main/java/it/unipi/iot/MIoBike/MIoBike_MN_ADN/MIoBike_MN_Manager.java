package it.unipi.iot.MIoBike.MIoBike_MN_ADN;

import it.unipi.iot.MIoBike.MIoBike_Utils.MonitorThread;
import it.unipi.iot.MIoBike.MIoBike_Utils.om2m_Node_Manager;

import static it.unipi.iot.MIoBike.MIoBike_MN_ADN.MN_Constants.*;
import static it.unipi.iot.MIoBike.MIoBike_Utils.Constants.IN_NM_NAME;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

public class MIoBike_MN_Manager extends om2m_Node_Manager {
	private om2m_Node_Manager IN_Manager;
	private MonitorThread NotificationManager;
	private Map<String, Resource_Manager> Resources;
	
	public MIoBike_MN_Manager(String uri, String id, String name) {
		super(uri, id, name);
		if(DEV_MODE) {
			System.out.println("-------------- Inizializing " + Bike_Name + "------------------");			
		}
		Create_NotificationManager();
		Create_om2m_IN_Manager();
		Subscribe_to_Node_Root(MN_NM_url, MN_NM_NAME);
		Create_AE_Bike();
		Create_Containers();
		
		if(DEV_MODE) {
			System.out.println("-------------- End of Inizialization --------------------------");
		}
	}
	
	private void Create_om2m_IN_Manager() {
		JSONArray INs_array = get_all_MN();
		JSONObject IN = INs_array.getJSONObject(0);
		String uri_path = IN.getString("uri");
		if(uri_path != "") {
			String response = get_request_path(uri_path);
			String IN_uri = get_label_from_XMLString("poa", response);
			IN_uri = Convert_HTTP_URI_To_CoAP_URI(IN_uri, OM2M_port);

			String IN_id = get_label_from_XMLString("csi", response);
			String IN_name = IN_id.replaceAll("cse", "name");
			IN_Manager = new om2m_Node_Manager(IN_uri, IN_id, IN_name);
			if(DEV_MODE) {
				System.out.println("New IN Manager, uri: " + IN_uri + " , id: " + IN_id + " , name: " + IN_name);
			}
			// controllo che IN abbia fatto la subscription sul mio nodo in root
			while(!check_Node_Subscription("", IN_NM_NAME)) {
				try {
						Thread.sleep(1000);
				} catch (InterruptedException ie) {
				    Thread.currentThread().interrupt();
				}
			}
		} else {
			if(DEV_MODE) {
				System.out.println("Error in IN Manager creation, empty url");
			}			
		}
	}
	
	private void Create_NotificationManager() {
		String NotificationManager_name = MN_NM_NAME;
		NotificationManager = new MonitorThread(NotificationManager_name, MN_NM_port, this);
		NotificationManager.start();
	}

	private void Create_AE_Bike() {
		String api = Bike_Name + "_api";
		String ResName = Bike_Name;
		String ResReach = "true";
		create_AE(api, ResName, ResReach);
		// controllo che IN abbia fatto la subscription sul mio nodo in root
		while(!check_Node_Subscription("/"+ResName, IN_NM_NAME)) {
				try {
					Thread.sleep(1000);
			} catch (InterruptedException ie) {
			    Thread.currentThread().interrupt();
			}
		}
	}
	
	private void Create_Containers(){
		System.out.println("Creating Resources for " + Bike_Name);
		Resources = new HashMap<String, Resource_Manager>();
		add_resource(GPS_uri, "GPS", 2000);
		add_resource(Lock_uri, "Lock", 2000);
		add_resource(Temperature_uri, "Temperature_uri", 2000);
		add_resource(Humidity_uri, "Humidity_uri", 2000);
		add_resource(Tire_pressure, "Tire_pressure", 2000);
		add_resource(Odometer, "Odometer", 2000);
		add_resource(Speedometer, "Speedometer", 2000);
	}		

	public void Subscribe_to_AE_on_IN() {
		
	}
	
	private void add_resource(String res_uri, String res_name, int period) {
		Resource_Manager res = new Resource_Manager(res_uri, this, period, Bike_Name, res_name);
		Resources.put(res_name, res);
		// controllo che IN abbia fatto la subscription sul mio nodo per la nuova risorsa
		while(!check_Node_Subscription("/"+Bike_Name+"/"+res_name, IN_NM_NAME)) {
			try {
					Thread.sleep(1000);
			} catch (InterruptedException ie) {
			    Thread.currentThread().interrupt();
			}
		}
		res.start();
	}

	public void Handle_Subscription(JSONObject sub) {
		if(DEV_MODE) {
			System.out.println("---------------------------------------------------------------");
			System.out.println("Subscription registration");
			System.out.println("---------------------------------------------------------------");
		}
		if(DEV_MODE) {
			System.out.println("---------------------------------------------------------------");
			System.out.println("Subscription registered");
			System.out.println("---------------------------------------------------------------");
		}		
	}
	
	public void Handle_new_AE_Notification(JSONObject ae) {
		if(DEV_MODE) {
			System.out.println("---------------------------------------------------------------");
			System.out.println("AE registration");
			System.out.println("---------------------------------------------------------------");
		}				

		if(DEV_MODE) {
			System.out.println("---------------------------------------------------------------");
			System.out.println("AE registered");
			System.out.println("---------------------------------------------------------------");
		}
	}

	public void Handle_new_Container_Notification(JSONObject cnt) {
		if(DEV_MODE) {
			System.out.println("---------------------------------------------------------------");
			System.out.println("Container registration");
			System.out.println("---------------------------------------------------------------");
		}

		if(DEV_MODE) {
			System.out.println("---------------------------------------------------------------");
			System.out.println("Container registered");
			System.out.println("---------------------------------------------------------------");
		}				
	}
			
	public void Handle_Content_Notification(JSONObject cin) {
		if(DEV_MODE) {
			System.out.println("---------------------------------------------------------------");
			System.out.println("ContentInstance Registration");
			System.out.println("---------------------------------------------------------------");
		}

		if(DEV_MODE) {
			System.out.println("---------------------------------------------------------------");
			System.out.println("ContentInstance Registered");
			System.out.println("---------------------------------------------------------------");
		}
	}

	
	public void Notification_Handler(String content) {
		if(DEV_MODE) {
			System.out.println("-------------Notification Handler------------------------");
			System.out.println(content);
		}
		JSONObject json_content = new JSONObject(content);
		JSONObject json_sgn = json_content.getJSONObject("m2m:sgn");
		if(json_sgn.has("m2m:sur")) {
			if(json_sgn.has("m2m:nev")) {
				JSONObject json_nev = json_sgn.getJSONObject("m2m:nev");
				JSONObject json_rep = json_nev.getJSONObject("m2m:rep");
				JSONObject json_notification_content = new JSONObject();
				if(json_rep.has("m2m:csr")) {
					json_notification_content = json_rep.getJSONObject("m2m:csr");								
				} else if(json_rep.has("m2m:ae")) {
					json_notification_content = json_rep.getJSONObject("m2m:ae");
				} else if(json_rep.has("m2m:cnt")) {
					json_notification_content = json_rep.getJSONObject("m2m:cnt");
				} else if(json_rep.has("m2m:cin")) {
					json_notification_content = json_rep.getJSONObject("m2m:cin");
				} else if(json_rep.has("m2m:sub")) {
					json_notification_content = json_rep.getJSONObject("m2m:sub");
				}
				int ty = json_notification_content.getInt("ty");
	
				switch(ty) {
					case TYPE_MN:
						break;
					case TYPE_AE:
						Handle_new_AE_Notification(json_notification_content);
						break;
					case TYPE_CONTAINER:
						Handle_new_Container_Notification(json_notification_content);
						break;
					case TYPE_CONTENT:
						Handle_Content_Notification(json_notification_content);
						break;
					case TYPE_SUBSCRIPTION:
						Handle_Subscription(json_notification_content);
						break;
					default:
						System.out.println("----------------Received bad Notification--------------");
						System.out.println(content);
						System.out.println("-------------------------------------------------------");
						break;
				}			
			}
			if(DEV_MODE) {
				System.out.println("--------------End of notification Handler-----------------");
			}
		}
	}

}
