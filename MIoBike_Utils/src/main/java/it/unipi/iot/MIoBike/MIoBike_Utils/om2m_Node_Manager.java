package it.unipi.iot.MIoBike.MIoBike_Utils;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.*;
import org.xml.sax.InputSource;
import javax.xml.parsers.*;
import java.io.*;
import java.util.Arrays;
import java.util.List;

import static it.unipi.iot.MIoBike.MIoBike_Utils.Constants.*;

public class om2m_Node_Manager extends om2m_general {
	protected String access_credentials;
	public String Node_base_path;
	public String Node_Id;
	public String Node_uri;
	public String Node_Name;
	
	public om2m_Node_Manager(String uri, String id, String name) {
		this.Node_base_path = URI_M2M_base_path;
		this.access_credentials = M2M_admin_credentials;
		this.Node_uri = uri;
		this.Node_Id = id;
		this.Node_Name = name;
	}

	public String get_request_path(String path) {
		String uri = Node_uri + Node_base_path + path;
		return get_request(uri, access_credentials);
	}

	public String get_from_Resource(String Res_Name, String Res_Container_Name, String Instance_Name) {
		String uri_path = Node_Id + "/" + Node_Name + "/" + Res_Name + "/" + Res_Container_Name;
		String ret = get_request_path(uri_path);
		return ret;
	}
	
	public void create_AE(String api, String ResName, String ResReach) {
		String uri = Node_uri + Node_base_path + Node_Id;

		JSONObject json_res_info = new JSONObject();
		json_res_info.put("api", api);
		json_res_info.put("rn", ResName);
		json_res_info.put("rr", ResReach);

		JSONObject json_root = new JSONObject();
		json_root.put("m2m:ae", json_res_info);

		post_request(uri, json_root, OPTION_TYPE, TYPE_AE, access_credentials);

		if(DEV_MODE) {
			System.out.println("Create AE call to " + uri);
		}
	}
	
	public void create_Container(String Res_Name, String Res_Container_Name) {
		String uri = Node_uri + Node_base_path + Node_Id + "/" + Node_Name + "/" + Res_Name;

		JSONObject json_res_info = new JSONObject();
		json_res_info.put("rn", Res_Container_Name);

		JSONObject json_root = new JSONObject();
		json_root.put("m2m:cnt", json_res_info);

		post_request(uri, json_root, OPTION_TYPE, TYPE_CONTAINER, access_credentials);

		if(DEV_MODE) {
			System.out.println("Create Container call to " + uri);
		}
	}

	public void create_Content_Instance(String Res_Name, String Res_Container_Name, String Instance_Name, String read_value) {
		String uri = Node_uri + Node_base_path + Node_Id + "/" + Node_Name + "/" + Res_Name + "/" + Res_Container_Name;

		JSONObject json_res_info = new JSONObject();
		json_res_info.put("cnf", Instance_Name);
		json_res_info.put("con", read_value);

		JSONObject json_root = new JSONObject();
		json_root.put("m2m:cin", json_res_info);

		post_request(uri, json_root, OPTION_TYPE, TYPE_CONTENT, access_credentials);
		if(DEV_MODE) {
			System.out.println("Create content instance call to " + uri);
		}
	}
	
	public JSONObject Discovery_request_uri(String tag, String uri_path , String query) {
		String uri = Node_uri + Node_base_path + uri_path + "?" + query;
		String response = get_request(uri, access_credentials);
		Document doc = null;
		try {
			doc = String_XML_to_XML_Document(response);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		NodeList nList = doc.getElementsByTagName(tag);
		JSONArray jarray = new JSONArray();
		Node nNode = nList.item(0);
		String name = "uri";
		String urils_str = nNode.getTextContent();
		List<String> items = Arrays.asList(urils_str.split("\\s"));
		if(items.isEmpty()) {	// se ho solo una risorsa
			JSONObject jobj = new JSONObject();
			jobj.put(name, urils_str);
			jarray.put(jobj);
		}else {	// se ho più risorse
			for (String uril : items) {
				JSONObject jobj = new JSONObject();
				jobj.put(name, uril);
				jarray.put(jobj);
			}			
		}

		JSONObject json_root = new JSONObject();
		json_root.put("response_array", jarray);

		if(DEV_MODE) {
			System.out.println("Discovery request call call to " + uri);
			System.out.println(urils_str);
			System.out.println(items.toString());
			System.out.println(json_root.toString());			
		}
		return json_root;
	}

	public JSONObject Discovery_request(String tag, String query) {
		String uri_path = Node_Id;
		JSONObject json_root = Discovery_request_uri(tag, uri_path , query);
		return json_root;
	}

	public JSONArray get_all(int Res_Type) {
		String query = "fu=1&rty=" + Res_Type;
		JSONObject Bikes_json = Discovery_request("m2m:uril", query);
		JSONArray ret = Bikes_json.getJSONArray("response_array");
		if(DEV_MODE) {
			System.out.println(Bikes_json.toString());
		}
		return ret;		
	}

	public JSONArray get_all_MN() {
		return get_all(TYPE_MN);
	}
	
	public JSONArray get_all_AE() {
		return get_all(TYPE_AE);
	}

	public JSONArray get_all_Container() {
		return get_all(TYPE_CONTAINER);
	}	

	public JSONArray get_all_Content() {
		return get_all(TYPE_CONTENT);
	}	
		
	public String get_label_from_path(String path, String label) {
		String response = get_request_path(path);
		String ret = get_label_from_XMLString(label, response);
		return ret;
	}
	
	public void Subscribe_to_each_Container(String notificationUrl, String Res_monitor_name) {
		JSONArray Containers = get_all_Container();
		for(int i=0; i< Containers.length(); i++) {
			JSONObject Container = Containers.getJSONObject(i);
			String res_path = Container.getString("uri");
			String uri = Node_uri + Node_base_path + res_path;
			create_Subscription(uri, notificationUrl, Res_monitor_name, access_credentials);
			if(DEV_MODE) {
				System.out.println("Subscribing "+ Res_monitor_name + " NU:" + notificationUrl + " to: " + uri);
			}
		}
	}

	public void Subscribe_to_each_MN(String notificationUrl, String Res_monitor_name) {
		JSONArray MNs = get_all_MN();
		for(int i=0; i< MNs.length(); i++) {
			JSONObject MN = MNs.getJSONObject(i);
			String res_path = MN.getString("uri");
			String uri = Node_uri + Node_base_path + res_path;
			create_Subscription(uri, notificationUrl, Res_monitor_name, access_credentials);
			if(DEV_MODE) {
				System.out.println("Subscribing "+ Res_monitor_name + " NU:" + notificationUrl + " to: " + uri);
			}
		}
	}	

	public void Subscribe_to_Itself(String notificationUrl, String Res_monitor_name) {
		String res_path = Node_Id;
		String uri = Node_uri + Node_base_path + res_path;
		create_Subscription(uri, notificationUrl, Res_monitor_name, access_credentials);
		if(DEV_MODE) {
			System.out.println("Subscribing "+ Res_monitor_name + " NU:" + notificationUrl + " to: " + uri);
		}
	}	

	public void Notification_Handler(String content) {
		if(DEV_MODE) {
			System.out.println("-------------Notification Handler------------------------");
			System.out.println(content);
		}
	}
	
	public String Get_Node_Name() {
		return Node_Name;
	}
}

