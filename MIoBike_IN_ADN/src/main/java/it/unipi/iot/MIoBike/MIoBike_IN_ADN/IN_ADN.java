package it.unipi.iot.MIoBike.MIoBike_IN_ADN;

import org.json.JSONArray;
import org.json.JSONObject;
import it.unipi.iot.MIoBike.MIoBike_Utils.MIoBike_IN_Manager;

import static it.unipi.iot.MIoBike.MIoBike_Utils.Constants.IN_DEV_MODE;
import static it.unipi.iot.MIoBike.MIoBike_Utils.Constants.IN_Id;
import static it.unipi.iot.MIoBike.MIoBike_Utils.Constants.IN_Name;
import static it.unipi.iot.MIoBike.MIoBike_Utils.Constants.IN_uri;

public class IN_ADN {
	static String NotificationManager_name;
	static String Node_Name;
	static MIoBike_IN_Manager IN_manager;
	static MonitorThread NotificationManager;

	private static void create_om2m_manager() {
		System.out.println("Creating om2m manager");
		IN_manager = new MIoBike_IN_Manager(IN_uri, IN_Id, IN_Name);
	}

	private static void create_NotificationManager() {
		NotificationManager = new MonitorThread(NotificationManager_name);
		NotificationManager.start();
	}

	public static synchronized void handle_Notification(String content) {
		if(IN_DEV_MODE) {
			System.out.println("---------------------------------------------------------------");
			System.out.println("received notification");
			System.out.println("---------------------------------------------------------------");			
		}
	}	
	
	public static void main( String[] args ) {
		NotificationManager_name = Node_Name + "_NotificationManager";
		if(IN_DEV_MODE) {
			System.out.println("--------------Inizializing Infrastructure Node ADN-----------------");			
		}
		create_om2m_manager();
//		create_NotificationManager();
		if(IN_DEV_MODE) {
			System.out.println("--------------End Of Initialization -----------------");
		}
		JSONArray MNs_array = IN_manager.get_all_MN();
		for(int i=0; i< MNs_array.length(); i++) {
			JSONObject MN = MNs_array.getJSONObject(i);
			String uri_path = MN.getString("uri");

			if(IN_DEV_MODE) {
				System.out.println(uri_path);
			}

			String response = IN_manager.get_request_path(uri_path);
			String bike_cse_path = IN_manager.get_label_from_XMLString("csi", response);

			JSONArray Bikes_array = IN_manager.get_all_AE_uri(bike_cse_path);

			for(int j=0; j< Bikes_array.length(); j++) {
				JSONObject Bike = Bikes_array.getJSONObject(j);
				String Bike_uri_path = Bike.getString("uri");
				IN_manager.get_all_Container_uri(Bike_uri_path);
//				om2m_IN_Manager.create_Subscription("coap://127.0.0.1:5683/~" + res_path, "coap://127.0.0.1:5685/"+NotificationManager_name, NotificationManager_name);
			}
		}
	}
}
