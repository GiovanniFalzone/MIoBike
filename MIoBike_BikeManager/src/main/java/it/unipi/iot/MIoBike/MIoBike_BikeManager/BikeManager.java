package it.unipi.iot.MIoBike.MIoBike_BikeManager;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;
import org.json.JSONArray;
import org.json.JSONObject;

import static it.unipi.iot.MIoBike.MIoBike_common.Constants.*;
import static it.unipi.iot.MIoBike.MIoBike_common.Constants_om2m.IN_NM_NAME;
import static it.unipi.iot.MIoBike.MIoBike_common.Constants_om2m.IN_NM_URL;
import static it.unipi.iot.MIoBike.MIoBike_common.Constants_om2m.MN_OM2M_PORT;
import static it.unipi.iot.MIoBike.MIoBike_common.Constants_om2m.TAG_POA;
import static it.unipi.iot.MIoBike.MIoBike_common.Constants_om2m.TAG_RN;

import it.unipi.iot.MIoBike.MIoBike_common.MonitorThread;
import it.unipi.iot.MIoBike.MIoBike_common.om2m_Node_Manager;

public class BikeManager extends om2m_Node_Manager{
	protected int vhp = VERY_HIGH_FREQ_PERIOD;
	protected int hp = HIGH_FREQ_PERIOD;
	protected int lp = LOW_FREQ_PERIOD;
	protected int disabled_p = DISABLED_FREQ_PERIOD;

	public BikeManager() {
		super(IN_URI, IN_ID, IN_NAME);
	}
		
	public void set_periods(int very_high, int high, int low) {
		vhp = very_high;
		hp = high;
		lp = low;
	}

	public JSONObject get_Bike_Data(String BikeName) {
		JSONObject ret = new JSONObject();
		String [] labels = { LABEL_PREFIX_BIKENAME+BikeName };
		JSONArray jarray = get_all_Container(labels);
		for(int i=0; i< jarray.length(); i++) {
			JSONObject element = jarray.getJSONObject(i);
			String uri = element.getString("uri");
			String ResourceName = get_ResourceName(uri);
			JSONObject LastRead = get_Last_Read(BikeName, ResourceName);
			ret.put(ResourceName, LastRead);
		}
		return ret;
	}
	
	public JSONArray get_Bikes_Data() {
		JSONArray jarray = get_all_AE(new String[] {"Bike"});
		JSONArray ret = new JSONArray();
		for(int i=0; i< jarray.length(); i++) {
			JSONObject obj = jarray.getJSONObject(i);
			String uri = obj.getString("uri");
			String BikeName = get_BikeName(uri);
			JSONObject json_bike = new JSONObject();
			json_bike.put("BikeName", BikeName);
			json_bike.put("Resources", get_Bike_Data(BikeName));
			ret.put(json_bike);
		}
		return ret;
	}
	
	public void set_Bike_Lock(String BikeName, boolean status) {
		set_content_to(BikeName, LOCK_NAME, LABEL_ACTUATOR, String.valueOf(status));
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
		JSONArray ret = new JSONArray();
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
		String [] labels = { LABEL_PREFIX_BIKESTATUS+LABEL_UNLOCKED };
		uris = get_all_by_label(TYPE_AE, labels);
		JSONArray ret = retrieve_BikeData_array_from_uri_array(uris);
		return ret;
	}
	
	public JSONArray get_locked_Bikes() {
		JSONArray uris = new JSONArray();
		String [] labels = { LABEL_PREFIX_BIKESTATUS+LABEL_LOCKED };
		uris = get_all_by_label(TYPE_AE, labels);
		JSONArray ret = retrieve_BikeData_array_from_uri_array(uris);
		return ret;
	}	
	
	public JSONArray get_all_User_Content(String UserId, String Prefix) {
		if(!Prefix.endsWith(":")) {
			Prefix += ":";
		}
		JSONArray final_ret = new JSONArray();		
		JSONArray uris = new JSONArray();
		String [] labels = {Prefix+UserId};
		uris = get_all_by_label(TYPE_CONTENT, labels);
		final_ret = retrieve_con_from_uri(uris);
		return final_ret;
	}
	
	public JSONArray get_all_Usage(String UserId) {
		return get_all_User_Content(UserId, USER_RESOURCE_NAME);
	}

	public JSONArray get_all_Speed(String UserId) {
		return get_all_User_Content(UserId, SPEEDOMETER_NAME);
	}

	public JSONArray get_all_Distance(String UserId) {
		return get_all_User_Content(UserId, ODOMETER_NAME);
	}

	public JSONArray get_all_GPS(String UserId) {
		return get_all_User_Content(UserId, GPS_NAME);
	}

//--------------------------------------------------------------------------------	
	
	public void Lock_Bike(String BikeName) {
		set_Bike_Lock(BikeName, true);
		set_bike_period(BikeName, lp, lp, lp, disabled_p, hp, lp, disabled_p, lp);
	}

	public void UnLock_Bike(String BikeName) {
		set_Bike_Lock(BikeName, false);
		set_bike_period(BikeName, vhp, disabled_p, disabled_p, lp, lp, lp, vhp, lp);
	}
		
	public void set_bike_period(String BikeName, int GPS_p, int Temp_p, int Hum_p, int Odo_p, int Lock_p, int Tyre_p, int Speed_p, int AQ_p) {
		JSONObject json_con = new JSONObject();
		json_con.put(JSON_KEY_RESOURCENAME, SAMPLING_PERIOD);
		JSONArray root = new JSONArray();
		root.put(get_period_obj(GPS_NAME, GPS_p));
		root.put(get_period_obj(TEMPERATURE_NAME, Temp_p));
		root.put(get_period_obj(HUMIDITY_NAME, Hum_p));
		root.put(get_period_obj(ODOMETER_NAME, Odo_p));
		root.put(get_period_obj(LOCK_NAME, Lock_p));
		root.put(get_period_obj(TYRE_PRESSURE_NAME, Tyre_p));
		root.put(get_period_obj(SPEEDOMETER_NAME, Speed_p));
		root.put(get_period_obj(AIRQUALITY_NAME, AQ_p));
		json_con.put(JSON_KEY_RESOURCEDATA, root);
		String con = json_con.toString();
		String cnf = "JSON";

		String ResName = SAMPLING_PERIOD;
		String [] labels = { };
		create_Content_Instance("System", BikeName, ResName, LABEL_VIRTUAL, cnf, con, labels);
	}

	public String get_BikeName(String uri) {
		String [] str_split = uri.split("/");
		int len = str_split.length;
		int offset = len - 3;
		return str_split[str_split.length-offset];
	}
	
	public String get_ResourceName(String uri) {
		if(DEV_MODE) {
			System.out.println(uri);			
		}
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
	
	public String create_Content_User(String UserId) {
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
		String con = json_data.toString();		
		return con;
	}
	
	private void clear_to_unlock(String UserId, String BikeName) {
		String combined_bike_lbl = combine_labels(new String[]{BikeName, LABEL_UNLOCKED});
		String [] labels = { combined_bike_lbl };
		update_labels_in_AE(BikeName,LABEL_UNLOCKED, UserId, labels);
		request_label_update(BikeName, LABEL_UNLOCKED, UserId);
		String cnf = "JSON";
		String con = create_Content_User(UserId);
		create_Content_Instance(UserId, BikeName, USER_RESOURCE_NAME, LABEL_VIRTUAL, cnf, con, new String[0]);
		UnLock_Bike(BikeName);
		System.out.println("The user: "+UserId+" is using the bike: " + BikeName);
	}
	
	public boolean User_Unlock_Request(String UserId, String BikeName) {
		boolean ret = false;
		JSONArray res = get_all_AE(new String[] { LABEL_PREFIX_USERID+UserId });
		if(res.length() == 0) {														// verifico che l'user non abbia già una bici
			String combined_bike_lbl = combine_labels(new String[]{BikeName, LABEL_UNLOCKED});
			res = get_all_AE(new String[] { combined_bike_lbl });
			if(res.length() == 0) {													// verifico che la bici non sia già in uso da un altro user
				clear_to_unlock(UserId, BikeName);
				ret = true;
			} else {
				System.out.println("The Bike: "+BikeName+" is in use by another user");
			}
		} else if(res.length() > 1) {
			System.out.println("The user: "+ UserId + " is using more then one Bike: " + res);
		} else {
			System.out.println("The user: "+ UserId + " is already using the Bike: " + res);
		}
		return ret;
	}
	
	public boolean User_Release_Bike(String BikeName) {
		String UserId = retrieve_UserName_by_BikeName(BikeName);
		String combined_bike = combine_labels(new String[]{BikeName, LABEL_LOCKED});
		String [] labels = { combined_bike };
		update_labels_in_AE(BikeName, LABEL_LOCKED, "", labels);
		request_label_update(BikeName, LABEL_LOCKED, "");
		Lock_Bike(BikeName);
		if(UserId.length() > 0) {
			System.out.println("The user: "+UserId+" released the bike: " + BikeName);			
		}
		return true;
	}
	
	public String retrieve_UserName_by_BikeName(String BikeName){
		JSONArray jarray = get_all_AE(new String[] { LABEL_PREFIX_BIKENAME+BikeName });
		String res_path = jarray.getJSONObject(0).getString("uri");
		String content = get_request_path(res_path);
		JSONObject json_content = new JSONObject(content);
		JSONObject json_ae = json_content.getJSONObject("m2m:ae");
		jarray = json_ae.getJSONArray(TAG_LBL);
		String UserId = retrieve_UserId_BikeName_from_lables(jarray)[0];
		return UserId;
	}

	public String retrieve_BikeName_by_UserName(String userName) {
		String BikeName = null;
		JSONArray jarray = get_all_AE(new String[] {LABEL_PREFIX_USERID+userName});
		if(jarray.length() > 0) {
			String res_path = jarray.getJSONObject(0).getString("uri");
	    	BikeName = get_BikeName(res_path);			
		}
	    return BikeName;
	}
	
}
