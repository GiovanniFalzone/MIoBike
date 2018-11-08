package it.unipi.iot.MIoBike.MIoBike_IN_ADN;

import org.json.JSONArray;
import org.json.JSONObject;

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
		IN_manager.print_all_containers_tree();
	}
}
