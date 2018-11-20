package it.unipi.iot.MIoBike.MIoBike_IN_ADN;
import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import it.unipi.iot.MIoBike.MIoBike_Utils.MonitorThread;
import it.unipi.iot.MIoBike.MIoBike_Utils.om2m_Node_Manager;
import static it.unipi.iot.MIoBike.MIoBike_IN_ADN.IN_Constants.*;

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
		NotificationManager = new MonitorThread(NotificationManager_name, IN_NM_PORT, this);
		NotificationManager.start();
	}
	
	/* this is to register the IN to receive notification of something appening in the IN itself*/
	public void Subscribe_for_MN_registration() {
		if(DEV_MODE) {
			System.out.println("------------Subscribe for MN registration-------------");
		}
		Subscribe_to_Node_Root(IN_NM_URL, IN_NM_NAME);
		if(DEV_MODE) {
			System.out.println("------------------------------------------------------------------------");
		}
	}
	/* this is to create a list of Bike Manager creating one for each MN already registered*/
	public void Create_om2m_Bikes() {
		Bikes = new HashMap<String, om2m_Node_Manager>();
		JSONArray MNs_array = get_all_MN(new String[0]);
		for(int i=0; i< MNs_array.length(); i++) {
			JSONObject MN = MNs_array.getJSONObject(i);
			String uri_path = MN.getString("uri");
			if(uri_path != "") {
				String response = get_request_path(uri_path);
				String bike_uri = get_label_from_XMLString(TAG_POA, response);
				bike_uri = Convert_HTTP_URI_To_CoAP_URI(bike_uri, IN_OM2M_PORT);
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
			Bike.Subscribe_to_Node_Root(IN_NM_URL, IN_NM_NAME);
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
		String bike_uri = csr.getJSONArray(TAG_POA).getString(0);
		bike_uri = Convert_HTTP_URI_To_CoAP_URI(bike_uri, MN_OM2M_PORT);
		String bike_id = csr.getString("csi");
		String bike_name = "/" + csr.getString(TAG_RN);
		Add_om2m_Bike_Manager(bike_uri, bike_id, bike_name);
		if(DEV_MODE) {
			System.out.println("---------------------------------------------------------------");
			System.out.println("MN "+bike_id+" registered");
			System.out.println("---------------------------------------------------------------");
		}
	}
	
	public void Handle_new_AE_Notification(JSONObject ae) {
		if(DEV_MODE) {
			System.out.println("---------------------------------------------------------------");
			System.out.println("AE registration");
			System.out.println("---------------------------------------------------------------");
		}				
		String bike_id = ae.getString(TAG_PI);
		if(!bike_id.equals(this.Node_Id)) {
			om2m_Node_Manager Bike = Bikes.get(bike_id);
			if(Bike != null) {
				String api =  ae.getString(TAG_API);
				String ResName =  ae.getString(TAG_RN);
				String ResReach = String.valueOf(ae.getBoolean(TAG_RR));

				JSONArray json_arr = ae.getJSONArray(TAG_LBL);
				String [] labels = new String[json_arr.length()];
				for(int i = 0; i < json_arr.length(); i++) {
				    labels[i] = json_arr.getString(i);
				}
				create_AE(api, ResName, ResReach, labels);
				Bike.Subscribe_to_AE(ResName, IN_NM_URL, IN_NM_NAME);

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
		JSONArray json_arr = cnt.getJSONArray(TAG_LBL);
		String [] labels = new String[json_arr.length()];
		for(int i = 0; i < json_arr.length(); i++) {
		    labels[i] = json_arr.getString(i);
		}
		String bike_id = cnt.getString("ri").split("/cnt")[0];
		om2m_Node_Manager Bike = Bikes.get(bike_id);
		String BikeName = labels[LABEL_BIKENAME_POS];
		String Container_Name = labels[LABEL_RESOURCENAME_POS];
		create_Container(BikeName, Container_Name, labels);
		if(Bike != null) {
			if(labels[LABEL_TYPE_POS].equals(LABEL_SENSOR)) {
				Bike.Subscribe_to_Container(BikeName, Container_Name, IN_NM_URL, IN_NM_NAME);
			} else {
				String MN_url = Bike.Node_uri.replaceAll(""+MN_OM2M_PORT, ""+MN_NM_PORT);
				String MN_NM_name = BikeName + NM_NAME_EXTENSION;
				String MN_NM_url = MN_url + "/" + MN_NM_name; 
				Subscribe_to_Container(BikeName, Container_Name, MN_NM_url, MN_NM_name);
			}

			if(DEV_MODE) {
				System.out.println("---------------------------------------------------------------");
				System.out.println("Container "+Container_Name+" registered");
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
		String con = cin.getString(TAG_CON);
		String cnf = cin.getString(TAG_CNF);		

		JSONArray json_arr = cin.getJSONArray(TAG_LBL);
		String [] labels = new String[json_arr.length()];
		for(int i = 0; i < json_arr.length(); i++) {
		    labels[i] = json_arr.getString(i);
		}
		String bike_name = labels[0];
		String container_name = labels[1];
		create_Content_Instance(bike_name, container_name, cnf, con, labels);
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
				int ty = json_notification_content.getInt(TAG_TY);
	
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
