package it.unipi.iot.MIoBike.MIoBike_Utils;

import static it.unipi.iot.MIoBike.MIoBike_Utils.Constants.*;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import org.json.JSONArray;
import org.json.JSONObject;

public class Bike_Manager extends om2m_Node_Manager{
	private MonitorThread NotificationManager;
	private int vhp = VERY_HIGH_FREQ_PERIOD;
	private int hp = HIGH_FREQ_PERIOD;
	private int lp = LOW_FREQ_PERIOD;

	public Bike_Manager() {
		super(IN_URI, IN_ID, IN_NAME);
		if(DEV_MODE) {
			System.out.println("--------- Bike Manager Initialization ----------------");
		}
		Create_NotificationManager();
		Register_BikeManager_NM_to_Bikes_NFC();
		Subscribe_for_Bike_registration();
		if(DEV_MODE) {
			System.out.println("--------------------------------------------");
		}
	}

	public void set_periods(int very_high, int high, int low) {
		vhp = very_high;
		hp = high;
		lp = low;
	}
	
	public void Subscribe_for_Bike_registration() {
		Subscribe_to_Node_Root(BIKEMANAGER_NM_URL, BIKEMANAGER_NM_NAME);
	}
	
	private void Create_NotificationManager() {
		String NotificationManager_name = BIKEMANAGER_NM_NAME;
		NotificationManager = new MonitorThread(NotificationManager_name, BIKEMANAGER_NM_PORT, this);
		NotificationManager.start();
	}

	public JSONObject get_Bike_Data(String BikeName) {
		JSONObject ret = new JSONObject();
		String [] labels = { BikeName };
		JSONArray jarray = get_all_Container(labels);
		for(int i=0; i< jarray.length(); i++) {
			JSONObject element = jarray.getJSONObject(i);
			String uri = element.getString("uri");
			String ResourceName = get_ResourceName(uri);
			String LastRead = get_Last_Read(BikeName, ResourceName);
			JSONObject value = new JSONObject(LastRead);
			ret.put(ResourceName, value);
		}
		return ret;
	}
	
	public JSONArray get_Bikes_Data() {
		JSONArray jarray = get_all_AE(new String[0]);
		JSONArray ret = new JSONArray();
		for(int i=0; i< jarray.length(); i++) {
			JSONObject obj = jarray.getJSONObject(i);
			String uri = obj.getString("uri");
			String BikeName = get_BikeName(uri);
			JSONObject json_bike = get_Bike_Data(BikeName);
			ret.put(json_bike);
		}
		return ret;
	}
	
	public void set_Bike_Lock(String BikeName, boolean status) {
		set_content_to(BikeName, LOCK_NAME, String.valueOf(status));
	}
	
	public JSONArray get_all_Sensor_Data(String BikeName, String SensorName) {
		JSONArray uris = new JSONArray();
		uris = get_all_in_AE_Container(BikeName, SensorName, TYPE_CONTENT);
		JSONArray ret = retrieve_con_from_uri(uris);
		return ret;
	}

	public JSONObject retrieve_BikeData_from_uri(String uri) {
		String BikeName = get_BikeName(uri);
		JSONObject bike_json = new JSONObject();
		bike_json.put("BikeName", BikeName);
		JSONObject resources = get_Bike_Data(BikeName);
		bike_json.put("Resources", resources);
		return bike_json;
	}
	
	public JSONArray retrieve_BikeData_array_from_uri_array(JSONArray uris) {
		JSONArray ret = new JSONArray();	//convert_key(uris, "uri", "BikeName");
		for(int i=0; i<uris.length(); i++) {
			JSONObject obj = uris.getJSONObject(i);
			String uri = obj.getString("uri");
			JSONObject bike_json = retrieve_BikeData_from_uri(uri);
			ret.put(bike_json);
		}
		return ret;
	}
	
	public JSONArray get_unlocked_Bikes() {
		JSONArray uris = new JSONArray();
		String [] labels = {LABEL_UNLOCKED};
		uris = get_all_by_label(TYPE_AE, labels);
		JSONArray ret = retrieve_BikeData_array_from_uri_array(uris);
		return ret;
	}

	public String get_Bike_Lock_status(String BikeName) {
		return "";
	}
	
	public JSONArray get_locked_Bikes() {
		JSONArray uris = new JSONArray();
		String [] labels = {LABEL_LOCKED};
		uris = get_all_by_label(TYPE_AE, labels);
		JSONArray ret = retrieve_BikeData_array_from_uri_array(uris);
		return ret;
	}	
	
	public void Lock_Bike(String BikeName) {
		set_Bike_Lock(BikeName, true);
		set_bike_period(BikeName, vhp, lp, lp, lp, lp, hp, lp, lp);
	}

	public void UnLock_Bike(String BikeName) {
		set_Bike_Lock(BikeName, false);
		set_bike_period(BikeName, lp, hp, lp, lp, lp, lp, lp, vhp);
	}
		
	public void set_bike_period(String BikeName, int NFC_p, int GPS_p, int Temp_p, int Hum_p, int Odo_p, int Lock_p, int Tyre_p, int Speed_p) {
		JSONObject json_con = new JSONObject();
		json_con.put(JSON_KEY_RESOURCENAME, SAMPLING_PERIOD);
		JSONArray root = new JSONArray();
		JSONObject GPS = get_period_obj(GPS_NAME, GPS_p);
		root.put(GPS);
		JSONObject Temperature = get_period_obj(TEMPERATURE_NAME, Temp_p);
		root.put(Temperature);
		JSONObject Humidity = get_period_obj(HUMIDITY_NAME, Hum_p);
		root.put(Humidity);
		JSONObject Odometer = get_period_obj(ODOMETER_NAME, Odo_p);
		root.put(Odometer);
		JSONObject Lock = get_period_obj(LOCK_NAME, Lock_p);
		root.put(Lock);
		JSONObject Tyre_pressure = get_period_obj(TYRE_PRESSURE_NAME, Tyre_p);
		root.put(Tyre_pressure);
		JSONObject Speedometer = get_period_obj(SPEEDOMETER_NAME, Speed_p);
		root.put(Speedometer);
		json_con.put(JSON_KEY_RESOURCEDATA, root);
		String con = json_con.toString();
		String cnf = "JSON";

		String ResName = SAMPLING_PERIOD;
		String [] labels = {BikeName.replace("/", ""), ResName.replace("/", ""), "virtual"};
		create_Content_Instance(BikeName, ResName, cnf, con, labels);
	}

	public String get_BikeName(String uri) {
		String [] str_split = uri.split("/");
		int len = str_split.length;
		int offset = len - 3;
		return str_split[str_split.length-offset];
	}
	
	public String get_ResourceName(String uri) {
		String [] str_split = uri.split("/");
		int len = str_split.length;
		int offset = len - 4;
		return str_split[str_split.length-offset];
	}
		
	public JSONObject get_period_obj(String name, int period) {
		JSONObject res = new JSONObject();
		res.put(JSON_KEY_RESOURCENAME, name);
		res.put(JSON_KEY_PERIOD, ""+period);
		return res;
	}

	public JSONArray convert_key(JSONArray jarray, String old_key, String new_key) {
		JSONArray ret = new JSONArray();
		for(int i=0; i<jarray.length(); i++) {
			JSONObject obj = jarray.getJSONObject(i);
			JSONObject new_obj = new JSONObject();
			new_obj.put(new_key, get_BikeName(obj.getString(old_key)));
			ret.put(new_obj);
		}
		return ret;
	}

	public void Register_BikeManager_NM_to_Bikes_NFC() {
		Subscribe_to_each_Container(BIKEMANAGER_NM_URL, BIKEMANAGER_NM_NAME, new String[]{ NFC_READER_NAME });
	}
	
	public void User_Unlock_Request(String UserId, String BikeName) {
		JSONArray res = get_all_AE(new String[] { UserId });
		if(res.length() == 0) {			// verifico che l'user non abbia già una bici
			String combined_bike_lbl = combine_labels(new String[]{BikeName, LABEL_UNLOCKED});
			res = get_all_AE(new String[] { combined_bike_lbl });
			if(res.length() == 0) {		// verifico che la bici non sia già in uso da un altro user
				String [] labels = { UserId, LABEL_UNLOCKED, combined_bike_lbl };
				update_labels_in_AE(BikeName, labels);
				System.out.println("Udate labels: " + combined_bike_lbl);
				String date_str = "";
				SimpleDateFormat sdfLocal = new SimpleDateFormat("yyyy.MM.dd_HH:mm:ss");
				JSONObject ResData = new JSONObject();
				ResData.put(JSON_KEY_FORMAT, "String");
				ResData.put(JSON_KEY_VALUE, UserId);
				sdfLocal.setTimeZone(TimeZone.getTimeZone("GMT"));
			    date_str = sdfLocal.format(new Date());
			    JSONObject json_data= new JSONObject();
			    json_data.put(JSON_KEY_DATE, date_str);
				json_data.put(JSON_KEY_RESOURCEDATA, ResData);
				String cnf = "JSON";
				String con = json_data.toString();
				create_Content_Instance(BikeName, USER_RESOURCE_NAME, cnf, con, new String[0]);
				UnLock_Bike(BikeName);
				System.out.println("The user: "+UserId+" is using the bike: " + BikeName);
			} else {
				System.out.println("The Bike: "+BikeName+" is in use by another user");
			}
		} else {
			System.out.println("The user: "+ UserId + " is already using the Bike: " + res);
		}
	}

	public void User_Release_Bike(String UserId) {
		JSONArray res = get_all_AE(new String[] { UserId });
		if(res.length() > 0) {							// verifico che l'user stia usando una bici
			JSONObject uri = res.getJSONObject(0);		// prendo la prima bici in uso dall'utente
			String BikeName = get_BikeName(uri.getString("uri"));
			String combined_bike = combine_labels(new String[]{BikeName, LABEL_LOCKED});
			String [] labels = { LABEL_LOCKED , combined_bike };
			update_labels_in_AE(BikeName, labels);
			Lock_Bike(BikeName);
		} else {
			System.out.println("The user: "+ UserId + " isn't using a Bike");
		}
		if(res.length() > 1) {
			System.out.println("The user: "+ UserId + " is using more then one Bike: " + res);
		}
	}
	
	public void Handle_new_Bike_Notification(JSONObject ae) {
		if(DEV_MODE) {
			System.out.println("---------------------------------------------------------------");
			System.out.println("Bike registration");
			System.out.println("---------------------------------------------------------------");
		}
		JSONArray json_arr = ae.getJSONArray(TAG_LBL);
		String [] labels = new String[json_arr.length()];
		for(int i = 0; i < json_arr.length(); i++) {
		    labels[i] = json_arr.getString(i);
		}
		String BikeName = labels[LABEL_BIKENAME_POS];
		Subscribe_to_AE(BikeName, BIKEMANAGER_NM_URL, BIKEMANAGER_NM_NAME);
	}

	public void Handle_new_Resource_Notification(JSONObject cnt) {
		if(DEV_MODE) {
			System.out.println("---------------------------------------------------------------");
			System.out.println("Resource registration");
			System.out.println("---------------------------------------------------------------");
		}
		JSONArray json_arr = cnt.getJSONArray(TAG_LBL);
		String [] labels = new String[json_arr.length()];
		for(int i = 0; i < json_arr.length(); i++) {
		    labels[i] = json_arr.getString(i);
		}
		String BikeName = labels[LABEL_BIKENAME_POS];
		String ResName = labels[LABEL_RESOURCENAME_POS];
		if(ResName.equals(NFC_READER_NAME)) {
			Subscribe_to_Container(BikeName, ResName, BIKEMANAGER_NM_URL, BIKEMANAGER_NM_NAME);			
		}
	}
	
	public void Handle_Content_Notification(JSONObject cin) {
		String con = cin.getString("con");
		JSONObject json_con = new JSONObject(con);
		String BikeName = json_con.getString(JSON_KEY_BIKENAME);
		if(json_con.getString(JSON_KEY_RESOURCENAME).equals(NFC_READER_NAME)) {
			JSONObject ResData = json_con.getJSONObject(JSON_KEY_RESOURCEDATA);
			String UserId = ResData.getString(JSON_KEY_VALUE);
			User_Unlock_Request(UserId, BikeName);
		}
	}
	
	public void Notification_Handler(String content) {
		if(DEV_MODE) {
			System.out.println("-------------System Notification-----------");
			System.out.println(content);
			System.out.println("-----------------------------------------");
			
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
						break;
					case TYPE_AE:
						Handle_new_Bike_Notification(json_notification_content);
						break;
					case TYPE_CONTAINER:
						Handle_new_Resource_Notification(json_notification_content);
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
		}
	}

}
