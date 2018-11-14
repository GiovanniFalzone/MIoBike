package it.unipi.iot.MIoBike.MIoBike_Utils;

import static it.unipi.iot.MIoBike.MIoBike_Utils.Constants.*;

import java.util.Arrays;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Node;

public class Bike_Manager {
	om2m_Node_Manager IN_Manager;
	
	public Bike_Manager() {
		IN_Manager = new om2m_Node_Manager(IN_uri, IN_Id, IN_Name);
	}

	public JSONArray get_Bikes_Data() {
		JSONArray jarray = IN_Manager.get_all_AE();
		JSONArray ret = new JSONArray();
		for(int i=0; i< jarray.length(); i++) {
			JSONObject element = jarray.getJSONObject(i);
			String bike_res_path = element.getString("uri");
			String str_split[] = bike_res_path.split("/");
			String BikeName = str_split[str_split.length-1];
			JSONObject json_bike = get_Bike_Data(BikeName);
			System.out.println(json_bike.toString());
			ret.put(json_bike);
		}
		System.out.println(ret.toString());
		return ret;
	}

	public JSONObject get_Bike_Data(String BikeName) {
		JSONObject ret = new JSONObject();
		JSONArray jarray = IN_Manager.get_all_Container();
		for(int i=0; i< jarray.length(); i++) {
			JSONObject element = jarray.getJSONObject(i);
			String res_path = element.getString("uri");
			String str_split[] = res_path.split("/");
			String SensorName = str_split[str_split.length-1];
			String value = get_Last_Read(BikeName, SensorName);
			ret.put(SensorName, value);
		}
		return ret;
	}

	public String get_Last_Read(String BikeName, String SensorName) {
		String value = "";
		String la = IN_Manager.get_from_Resource("/"+BikeName, SensorName, "la");
		System.out.println(la);
		if(!la.equals("Resource not found")) {
			value = IN_Manager.get_label_from_XMLString("con", la);			
		}
		return value;
	}

	public boolean set_Bike_Lock(boolean status) {
		boolean ret = false;
		while(!ret) {
			ret = set_Resource_Value("Bike1", "Lock", "LockStatus", ""+status);
		}
		return ret;
	}

	public boolean set_Resource_Value(String BikeName, String Resource, String ContentName, String value) {
		boolean ret = false;
		IN_Manager.create_Content_Instance(BikeName, Resource, ContentName, value);
		ret = (get_Last_Read(BikeName, Resource).equals(value));
		System.out.println(ret);
		return ret;
	}

	public JSONArray get_all_Sensor_Data(String BikeName, String SensorName) {
		String ResPath = BikeName+"/"+SensorName;
		JSONArray ret = new JSONArray();
		JSONArray uris = new JSONArray();
		uris = IN_Manager.get_all_in_Res(ResPath, TYPE_CONTENT);
		for(int i=0; i< uris.length(); i++) {
			JSONObject element = uris.getJSONObject(i);
			String obj_res_path = element.getString("uri");
			String response = IN_Manager.get_request_path(obj_res_path);
			String key = IN_Manager.get_label_from_XMLString("cnf", response);
			String value = IN_Manager.get_label_from_XMLString("con", response);
			JSONObject ret_elem = new JSONObject();
			ret_elem.put(key, value);
			ret.put(ret_elem);
		}

		System.out.println("---------------------------------------");
		System.out.println(ret);
		System.out.println("---------------------------------------");
		return ret;
	}
	
}
