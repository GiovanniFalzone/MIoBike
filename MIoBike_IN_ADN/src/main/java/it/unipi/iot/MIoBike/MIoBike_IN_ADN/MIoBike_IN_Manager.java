package it.unipi.iot.MIoBike.MIoBike_IN_ADN;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import it.unipi.iot.MIoBike.MIoBike_Utils.MonitorThread;
import it.unipi.iot.MIoBike.MIoBike_Utils.om2m_Node_Manager;
import static it.unipi.iot.MIoBike.MIoBike_IN_ADN.IN_Constants.*;
import static it.unipi.iot.MIoBike.MIoBike_Utils.Constants.DEV_MODE;
import static it.unipi.iot.MIoBike.MIoBike_Utils.Constants.IN_NM_NAME;

/*questo deve tenere tutte le MN ovvero le bici come classi nodo e gestire tutto il IN come riprendi il dato, ridondanza
 * recupero del dato, sync, registrazione, notifica etc*/
public class MIoBike_IN_Manager extends om2m_Node_Manager{
	private Map<String, om2m_Node_Manager> Bikes;
	private MonitorThread NotificationManager;

	public MIoBike_IN_Manager(String uri, String Id, String Name) {
		super(uri, Id, Name);
		if(DEV_MODE) {
			System.out.println("--------------Inizializing Infrastructure Node -----------------");			
		}

		Create_NotificationManager();
		Create_om2m_Bikes();
		Subscribe_for_MN_registration();

		if(DEV_MODE) {
			System.out.println("--------------End Of Initialization -----------------");
		}
	}

	private void Create_NotificationManager() {
		String NotificationManager_name = IN_NM_NAME;
		NotificationManager = new MonitorThread(NotificationManager_name, IN_NM_port, this);
		NotificationManager.start();
	}
	
	/* this is to register the IN to receive notification of something appening in the IN itself*/
	public void Subscribe_for_MN_registration() {
		if(DEV_MODE) {
			System.out.println("------------Subscribe for MN registration-------------");
		}
		Subscribe_to_Node_Root(IN_NM_url, IN_NM_NAME);
		if(DEV_MODE) {
			System.out.println("------------------------------------------------------------------------");
		}
	}
	/* this is to create a list of Bike Manager creating one for each MN already registered*/
	public void Create_om2m_Bikes() {
		Bikes = new HashMap<String, om2m_Node_Manager>();
		JSONArray MNs_array = get_all_MN();
		for(int i=0; i< MNs_array.length(); i++) {
			JSONObject MN = MNs_array.getJSONObject(i);
			String uri_path = MN.getString("uri");
			if(uri_path != "") {
				String response = get_request_path(uri_path);
				String bike_uri = get_label_from_XMLString("poa", response);
				bike_uri = Convert_HTTP_URI_To_CoAP_URI(bike_uri, IN_om2m_port);
				String bike_id = get_label_from_XMLString("csi", response);
				String bike_name = bike_id.replaceAll("cse", "name");
				Add_om2m_Bike_Manager(bike_uri, bike_id, bike_name);				
			}
		}
	}
	
	public om2m_Node_Manager Add_om2m_Bike_Manager(String bike_uri, String bike_id, String bike_name) {
		om2m_Node_Manager Bike = Bikes.get(bike_id);
		if(Bike == null) {
			Bike = new om2m_Node_Manager(bike_uri, bike_id, bike_name);
			Bikes.put(bike_id, Bike);
//			Sync_with_Node(Bike);
			Subscribe_to_Node_Root(IN_NM_url, IN_NM_NAME);
			if(DEV_MODE) {
				System.out.println("New Bike Manager, uri: " + bike_uri + " , id: " + bike_id + " , name: " + bike_name);
			}
		}
		return Bike;
	}

	
	public void Handle_new_MN_Notification(JSONObject csr) {
		if(DEV_MODE) {
			System.out.println("---------------------------------------------------------------");
			System.out.println("MN registration");
			System.out.println("---------------------------------------------------------------");
		}
		String bike_uri = csr.getJSONArray("poa").getString(0);
		bike_uri = Convert_HTTP_URI_To_CoAP_URI(bike_uri, IN_om2m_port);
		String bike_id = csr.getString("csi");
		String bike_name = "/" + csr.getString("rn");
		om2m_Node_Manager Bike = Add_om2m_Bike_Manager(bike_uri, bike_id, bike_name);
		if(Bike != null) {
			Bike.Subscribe_to_Node_Root(IN_NM_url, IN_NM_NAME);
			if(DEV_MODE) {
				System.out.println("---------------------------------------------------------------");
				System.out.println("MN "+bike_id+" registered");
				System.out.println("---------------------------------------------------------------");
			}

		}	else {
			if(DEV_MODE) {
				System.out.println("---------------------------------------------------------------");
				System.out.println("MN registration error, Bike doesn't exists " + bike_id);
				System.out.println("---------------------------------------------------------------");
			}
		}
	}
	
	public void Handle_new_AE_Notification(JSONObject ae) {
		if(DEV_MODE) {
			System.out.println("---------------------------------------------------------------");
			System.out.println("AE registration");
			System.out.println("---------------------------------------------------------------");
		}				
		String bike_id = ae.getString("pi");
		if(!bike_id.equals(this.Node_Id)) {
			String api =  ae.getString("api");
			String ResName =  ae.getString("rn");
			String ResReach = String.valueOf(ae.getBoolean("rr"));
			create_AE(api, ResName, ResReach);
			om2m_Node_Manager Bike = Bikes.get(bike_id);
			String res_path = "/" + ResName;
			System.out.println("----------------->" + res_path);
			if(Bike != null) {
				Bike.Subscribe_to_Res_path(res_path, IN_NM_url, IN_NM_NAME);			
//				Bike.Subscribe_to_each_Container(IN_NM_url, IN_NM_NAME);
				if(DEV_MODE) {
					System.out.println("---------------------------------------------------------------");
					System.out.println("AE "+ResName+" registered");
					System.out.println("---------------------------------------------------------------");
				}
			}	else {
				if(DEV_MODE) {
					System.out.println("---------------------------------------------------------------");
					System.out.println("AE registration error, Bike doesn't exists " + bike_id);
					System.out.println("---------------------------------------------------------------");
				}
			}
		}
	}

	public void Handle_new_Container_Notification(JSONObject cnt) {
		if(DEV_MODE) {
			System.out.println("---------------------------------------------------------------");
			System.out.println("Container registration");
			System.out.println("---------------------------------------------------------------");
		}

		// mi sottoscrivo a questo container
		String bike_id = cnt.getString("ri").split("/cnt")[0];
		String Res_Container_Name = cnt.getString("rn");
		String ResName = bike_id.split("-mn-cse")[0].split("/")[1];
		create_Container(ResName, Res_Container_Name);
		om2m_Node_Manager Bike = Bikes.get(bike_id);
		String res_path = "/" + ResName + "/" + Res_Container_Name;
		System.out.println("-----------------> " + res_path);
		if(Bike != null) {
			Bike.Subscribe_to_Res_path(res_path, IN_NM_url, IN_NM_NAME);			
			if(DEV_MODE) {
				System.out.println("---------------------------------------------------------------");
				System.out.println("Container "+Res_Container_Name+" registered");
				System.out.println("---------------------------------------------------------------");
			}				
		}	else {
			if(DEV_MODE) {
				System.out.println("---------------------------------------------------------------");
				System.out.println("Container registration error, Bike doesn't exists " + bike_id);
				System.out.println("---------------------------------------------------------------");
			}				
		}
	}
			
	public void Handle_Content_Notification(JSONObject cin) {
		if(DEV_MODE) {
			System.out.println("---------------------------------------------------------------");
			System.out.println("ContentInstance Registration");
			System.out.println("---------------------------------------------------------------");
		}
		String con = cin.getString("con");
		String cnf = cin.getString("cnf");		
		String Res_Name = cnf.split("_")[0].split("/")[0];
		String Res_Container_Name = cnf.split("_")[0].split("/")[1];
		create_Content_Instance(Res_Name, Res_Container_Name, cnf, con);
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
				}
				int ty = json_notification_content.getInt("ty");
	
				switch(ty) {
					case TYPE_MN:
						Handle_new_MN_Notification(json_notification_content);
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
