package it.unipi.iot.MIoBike.MIoBike_IN_App;
import static it.unipi.iot.MIoBike.MIoBike_IN_App.IN_Constants.*;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import it.unipi.iot.MIoBike.MIoBike_common.MonitorThread;
import it.unipi.iot.MIoBike.MIoBike_common.om2m_Node_Manager;

/*questo deve tenere tutte le MN ovvero le bici come classi nodo e gestire tutto il IN come riprendi il dato, ridondanza
 * recupero del dato, sync, registrazione, notifica etc*/
public class MIoBike_IN_Manager extends om2m_Node_Manager{
	private Map<String, om2m_Node_Manager> Bikes;
	private MonitorThread NotificationManager;

	public MIoBike_IN_Manager(String uri, String Id, String Name) {
		super(uri, Id, Name);
		if(DEV_MODE_IN) {
			System.out.println("--------------Inizializing Infrastructure Node -----------------");			
		}

		Bikes = new HashMap<String, om2m_Node_Manager>();
		Create_NotificationManager();
		create_System_AE();
		Synch_with_registered_MN();
		Subscribe_for_MN_registration();

		if(DEV_MODE_IN) {
			System.out.println("--------------End Of Initialization -----------------");
		}
	}

	private void Create_NotificationManager() {
		String NotificationManager_name = IN_NM_NAME;
		NotificationManager = new MonitorThread(NotificationManager_name, IN_NM_PORT, this);
		NotificationManager.start();
	}

	public void create_System_AE() {
		create_AE("System_api", "System", "true", new String[0]);
		create_Container("System", "Root_Sub_Req", "", new String[0]);
		Subscribe_to_Container("System", "Root_Sub_Req", IN_NM_URL, IN_NM_NAME);
		create_Container("System", "Update_label_req", "", new String[0]);
		Subscribe_to_Container("System", "Update_label_req", IN_NM_URL, IN_NM_NAME);
	}
	
	/* this is to create a list of Bike Manager creating one for each MN already registered*/
	public void Synch_with_registered_MN() {
		JSONArray MNs_array = get_all_MN(new String[0]);
		for(int i=0; i < MNs_array.length(); i++) {
			JSONObject MN = MNs_array.getJSONObject(i);
			String uri_path = MN.getString("uri");
			if(uri_path != "") {
				String response = get_request_path(uri_path);
				JSONObject json_res = new JSONObject(response);
				JSONObject json_csr = json_res.getJSONObject("m2m:csr");
				String bike_id = json_csr.getString("csi");
				String bike_name = bike_id.replaceAll("cse", "name");

				om2m_Node_Manager Bike = Add_om2m_Bike_Manager(bike_id, bike_name);

				JSONArray ae_uris = Bike.get_all_AE(new String[0]);
				for(int ae_i=0; ae_i< ae_uris.length(); ae_i++) {
					String content = get_request_path(ae_uris.getJSONObject(ae_i).getString("uri"));
					JSONObject ae = new JSONObject(content).getJSONObject("m2m:ae");
					add_AE_to_MN(ae, Bike);
				}

				JSONArray cnt_uris = Bike.get_all_Container(new String[0]);
				for(int cnt_i=0; cnt_i<cnt_uris.length(); cnt_i++) {
					String content = get_request_path(cnt_uris.getJSONObject(cnt_i).getString("uri"));
					JSONObject cnt = new JSONObject(content).getJSONObject("m2m:cnt");
					add_Container_to_AE(cnt);
				}
			}
		}
	}

	public om2m_Node_Manager Add_om2m_Bike_Manager(String bike_id, String bike_name) {
		om2m_Node_Manager Bike = Bikes.get(bike_id);
		if(Bike == null) {
			Bike = new om2m_Node_Manager(Node_uri, bike_id, bike_name);
			Bikes.put(bike_id, Bike);
			Bike.Subscribe_to_Node_Root(IN_NM_URL, IN_NM_NAME);
			if(DEV_MODE_IN) {
				System.out.println("New Bike Manager id: " + bike_id + " , name: " + bike_name);
			}
		}
		return Bike;
	}
	
	/* this is to register the IN to receive notification of something appening in the IN itself*/
	public void Subscribe_for_MN_registration() {
		if(DEV_MODE_IN) {
			System.out.println("------------Subscribe for MN registration-------------");
		}
		Subscribe_to_Node_Root(IN_NM_URL, IN_NM_NAME);
		if(DEV_MODE_IN) {
			System.out.println("------------------------------------------------------------------------");
		}
	}

	public String add_AE_to_MN(JSONObject ae, om2m_Node_Manager Bike) {
		String api =  ae.getString(TAG_API);
		String ResName =  ae.getString(TAG_RN);
		String ResReach = String.valueOf(ae.getBoolean(TAG_RR));
		String [] labels = new String [0];
		if(ae.has(TAG_LBL)) {
			JSONArray json_arr = ae.getJSONArray(TAG_LBL);
			labels = new String[json_arr.length()];
			for(int i = 0; i < json_arr.length(); i++) {
			    labels[i] = json_arr.getString(i);
			}
		}
		create_AE(api, ResName, ResReach, labels);
		Bike.Subscribe_to_AE(ResName, IN_NM_URL, IN_NM_NAME);
		return ResName;
	}

	public String get_Bike_NM_uri(om2m_Node_Manager Bike) {
		String content = get_request_path("MIo-Bike-in-cse/MIo-Bike-in-name"+Bike.Node_Name);
		JSONObject content_json = new JSONObject(content);
		JSONObject csr_json = content_json.getJSONObject("m2m:csr");
		JSONArray poa_jarray = csr_json.getJSONArray(TAG_POA);
		String poa = convert_json_array_field_to_string_array(poa_jarray)[0];
		String NM_uri = Convert_HTTP_URI_To_CoAP_URI(poa, MN_NM_PORT);
		NM_uri += Bike.Node_Id.split("-")[0] +NM_NAME_EXTENSION;
		return NM_uri;
	}
	
	public String add_Container_to_AE(JSONObject cnt) {
		String bike_id = cnt.getString("ri").split("/cnt")[0];
		om2m_Node_Manager Bike = Bikes.get(bike_id);
		JSONArray jarray = cnt.getJSONArray(TAG_LBL);
		String [] labels = convert_json_array_field_to_string_array(jarray);

		String UI_BN_RN_TY [] = retrieve_UserId_BikeName_ResName_Type_from_lables(jarray);
		String BikeName = 		UI_BN_RN_TY[1];
		String Container_Name = UI_BN_RN_TY[2];
		String Type = 			UI_BN_RN_TY[3];
		create_Container(BikeName, Container_Name, Type, labels);
		if(Bike != null) {
			if(Type.equals(LABEL_SENSOR)) {
				Bike.Subscribe_to_Container(BikeName, Container_Name, IN_NM_URL, IN_NM_NAME);
			} else {
				String MN_NM_url = get_Bike_NM_uri(Bike);
				String MN_NM_name = BikeName + NM_NAME_EXTENSION;
				Subscribe_to_Container(BikeName, Container_Name, MN_NM_url, MN_NM_name);
				System.out.println(BikeName);
				System.out.println(Container_Name);
				System.out.println(MN_NM_name);
				System.out.println(MN_NM_url);

			}
		}	else {
				System.out.println("---------------------------------------------------------------");
				System.out.println("Container registration error, Bike doesn't exists " + bike_id);
				System.out.println("---------------------------------------------------------------");
		}
		return Container_Name;
	}

	public void Dispatch_System_Notification(JSONObject System_con_not) {
		if(DEV_MODE_IN) {
			System.out.println("Received a System request");
			System.out.println(System_con_not);
		}
		JSONObject Sys_req= System_con_not.getJSONObject("System");
		if(Sys_req.has("Root_Sub_Req")) {
			JSONObject req = Sys_req.getJSONObject("Root_Sub_Req");
			String Node_id = req.getString("NodeId");
			String NodeName = req.getString("NodeName");
			String ResPath = req.getString("ResPath");
			om2m_Node_Manager Bike = Add_om2m_Bike_Manager(Node_id, NodeName);
			if(ResPath.length() > 0) {
				Bike.Subscribe_to_Res_path(ResPath, IN_NM_URL, IN_NM_NAME);
			} else {
				Bike.Subscribe_to_Node_Root(IN_NM_URL, IN_NM_NAME);
			}
		} else if(Sys_req.has("Update_label_req")) {
			JSONObject req = Sys_req.getJSONObject("Update_label_req");
			String BikeName = req.getString("BikeName");
			String BikeStatus = req.getString("BikeStatus");
			String UserId = req.getString("UserId");
			String Node_id = "/"+BikeName+MN_NODE_ID_EXTENSION;
			om2m_Node_Manager Bike = Bikes.get(Node_id);
			Bike.update_labels_in_AE(BikeName, BikeStatus, UserId, new String [0]);
		}
	}
	
	public void Handle_new_MN_Notification(JSONObject csr) {
		if(DEV_MODE_IN) {
			System.out.println("---------------------------------------------------------------");
			System.out.println("MN registration");
			System.out.println("---------------------------------------------------------------");
		}

		String bike_id = csr.getString("csi");
		String bike_name = "/" + csr.getString(TAG_RN);
		Add_om2m_Bike_Manager(bike_id, bike_name);

		if(DEV_MODE_IN) {
			System.out.println("---------------------------------------------------------------");
			System.out.println("MN "+bike_id+" registered");
			System.out.println("---------------------------------------------------------------");
		}
	}
	
	public void Handle_new_AE_Notification(JSONObject ae) {
		if(DEV_MODE_IN) {
			System.out.println("---------------------------------------------------------------");
			System.out.println("AE registration");
			System.out.println("---------------------------------------------------------------");
		}				
		String bike_id = ae.getString(TAG_PI);
		if(!bike_id.equals(this.Node_Id)) {
			om2m_Node_Manager Bike = Bikes.get(bike_id);
			if(Bike != null) {
				String ResName = add_AE_to_MN(ae, Bike);
				if(DEV_MODE_IN) {
					System.out.println("---------------------------------------------------------------");
					System.out.println("AE "+ResName+" registered");
					System.out.println("---------------------------------------------------------------");
				}
			}	else {
				if(DEV_MODE_IN) {
					System.out.println("---------------------------------------------------------------");
					System.out.println("AE registration error, Bike doesn't exists " + bike_id);
					System.out.println("---------------------------------------------------------------");
				}
			}
		}
	}

	public void Handle_new_Container_Notification(JSONObject cnt) {
		if(DEV_MODE_IN) {
			System.out.println("---------------------------------------------------------------");
			System.out.println("Container registration");
			System.out.println("---------------------------------------------------------------");
		}

		String Container_Name = add_Container_to_AE(cnt);

		if(DEV_MODE_IN) {
			System.out.println("---------------------------------------------------------------");
			System.out.println("Container "+Container_Name+" registered");
			System.out.println("---------------------------------------------------------------");
		}				
	}
			
	public void Handle_Content_Notification(JSONObject cin) {
		if(DEV_MODE_IN) {
			System.out.println("---------------------------------------------------------------");
			System.out.println("ContentInstance Registration");
			System.out.println("---------------------------------------------------------------");
		}
		String cnf = cin.getString(TAG_CNF);
		String con = cin.getString(TAG_CON);

		JSONObject json_con = new JSONObject(con);
		if(json_con.has("System")) {
			Dispatch_System_Notification(json_con);
		} else {
			JSONArray jarray = cin.getJSONArray(TAG_LBL);
			String [] labels = convert_json_array_field_to_string_array(jarray);
			String [] ret = retrieve_UserId_BikeName_ResName_Type_from_lables(jarray);
			String UserId = ret[0];
			String bike_name = ret[1];
			String container_name = ret[2];
			String Type = ret[3];
			create_Content_Instance(UserId, bike_name, container_name, Type, cnf, con, labels);
			if(DEV_MODE_IN) {
				System.out.println("---------------------------------------------------------------");
				System.out.println("ContentInstance Registered");
				System.out.println("---------------------------------------------------------------");
			}
		}

	}

	public void Notification_Handler(String content) {
		if(DEV_MODE_IN) {
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
			if(DEV_MODE_IN) {
				System.out.println("--------------End of notification Handler-----------------");
			}
		}
	}
}
