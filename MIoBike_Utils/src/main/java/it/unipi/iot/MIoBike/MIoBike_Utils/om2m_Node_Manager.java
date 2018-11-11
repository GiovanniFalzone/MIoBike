package it.unipi.iot.MIoBike.MIoBike_Utils;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.*;
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
		String uri_path = Node_Id + Node_Name + Res_Name + "/" + Res_Container_Name;
		String ret = get_request_path(uri_path);
		return ret;
	}
	
	public void create_AE(String api, String ResName, String ResReach) {
		String uri = Node_uri + Node_base_path + Node_Id;
		if(DEV_MODE) {
			System.out.println("Create AE call to " + uri);
		}
		JSONObject json_res_info = new JSONObject();
		json_res_info.put("api", api);
		json_res_info.put("rn", ResName);
		json_res_info.put("rr", ResReach);

		JSONObject json_root = new JSONObject();
		json_root.put("m2m:ae", json_res_info);

		post_request(uri, json_root, OPTION_TYPE, TYPE_AE, access_credentials);
	}
	
	public void create_Container(String Res_Name, String Res_Container_Name) {
		String uri = Node_uri + Node_base_path + Node_Id + Node_Name + "/" + Res_Name;

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
		String uri = Node_uri + Node_base_path + Node_Id + Node_Name + "/" + Res_Name + "/" + Res_Container_Name;

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
		String uri = Node_uri + Node_base_path + uri_path + "/?" + query;
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
		JSONObject json_obj = Discovery_request("m2m:uril", query);
		JSONArray ret = json_obj.getJSONArray("response_array");
		if(DEV_MODE) {
			System.out.println(json_obj.toString());
		}
		return ret;		
	}

	public JSONArray get_all_in_Res(String Res_path, int Res_Type) {
		String query = "fu=1&rty=" + Res_Type;
		Res_path = Node_Id + Res_path;
		JSONObject json_obj = Discovery_request_uri("m2m:uril", Res_path, query);
		JSONArray ret = json_obj.getJSONArray("response_array");
		if(DEV_MODE) {
			System.out.println(json_obj.toString());
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

	public JSONArray get_all_Subscribed() {
		return get_all(TYPE_SUBSCRIPTION);
	}
	
	public String get_label_from_path(String path, String label) {
		String response = get_request_path(path);
		String ret = get_label_from_XMLString(label, response);
		return ret;
	}
	
	public void Subscribe_to_Res_path(String res_path, String notificationUrl, String Res_monitor_name) {
		String uri = Node_uri + Node_base_path + Node_Id + Node_Name +res_path;
		if(DEV_MODE) {
			System.out.println("Subscribing "+ Res_monitor_name + " NU:" + notificationUrl + " to: " + uri);
		}
		create_Subscription(uri, notificationUrl, Res_monitor_name, access_credentials);
	}
	
	public void Subscribe_to_each_element(JSONArray array_element, String notificationUrl, String Res_monitor_name) {
		for(int i=0; i< array_element.length(); i++) {
			JSONObject element = array_element.getJSONObject(i);
			String res_path = element.getString("uri");
			Subscribe_to_Res_path(res_path, notificationUrl, Res_monitor_name);
		}		
	}
	
	public void Subscribe_to_each_Container(String notificationUrl, String Res_monitor_name) {
		JSONArray Containers = get_all_Container();
		Subscribe_to_each_element(Containers, notificationUrl, Res_monitor_name);
	}

	public void Subscribe_to_each_AE(String notificationUrl, String Res_monitor_name) {
		JSONArray AEs = get_all_AE();
		Subscribe_to_each_element(AEs, notificationUrl, Res_monitor_name);

	}
	
	public void Subscribe_to_each_MN(String notificationUrl, String Res_monitor_name) {
		JSONArray MNs = get_all_MN();
		Subscribe_to_each_element(MNs, notificationUrl, Res_monitor_name);
	}	

	public void Subscribe_to_Node_Root(String notificationUrl, String Res_monitor_name) {
		String res_path = Node_Id;
		String uri = Node_uri + Node_base_path + res_path;
		create_Subscription(uri, notificationUrl, Res_monitor_name, access_credentials);
		if(DEV_MODE) {
			System.out.println("Subscribing "+ Res_monitor_name + " NU:" + notificationUrl + " to: " + uri);
		}
	}	
	
	public boolean check_Node_Subscription(String ResPath, String NM_Name) {
		boolean subscribed = false;
		JSONArray Subscribed_array = get_all_Subscribed();
		for(int i = 0; i<Subscribed_array.length(); i++) {
			JSONObject Subscribed = Subscribed_array.getJSONObject(i);
			String sub_uri_path = Subscribed.getString("uri");
			String check_str = Node_Id + Node_Name + ResPath +"/" + NM_Name;
			subscribed = sub_uri_path.equals(check_str);
			if(subscribed == true) {
				return subscribed;
			}
		}
		if(!subscribed) {
			if(DEV_MODE) {
				System.out.println("-------------- Subscription to "+ResPath+" not found --------------------------");
				System.out.println("-------------------------------------------------------------------------------");
			}
		}
		return subscribed;
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

	public void Sync_with_Node(om2m_Node_Manager Node) {
		if(DEV_MODE) {
			System.out.println("------------Synch with Node " + Node.Get_Node_Name() + "-------------");
		}
		JSONArray AEs = Node.get_all_AE();
		for(int i=0; i< AEs.length(); i++) {
			String BikeName = Node.Node_Name.replace("/", "").split("-")[0];
			String api = BikeName + "_api";
			String ResReach = "true";
			create_AE(api, BikeName, ResReach);
			JSONArray Containers = Node.get_all_Container();
			System.out.println("------------------------------------all containers "+ Containers.length() + "-------------------");
			for(int cont_id=0; cont_id< Containers.length(); cont_id++) {
				JSONObject Container = Containers.getJSONObject(cont_id);
				String res_path = Container.getString("uri");
				if(res_path != "") {
					String ContainerName = res_path.split("/")[4];
					create_Container(BikeName, ContainerName);
					JSONArray Contents = Node.get_all_Content();
					System.out.println("------------------------------------all contents "+ Contents.length() + "-------------------");
					for(int continst_id=0; continst_id< Contents.length(); continst_id++) {
						JSONObject Content = Contents.getJSONObject(continst_id);
						String content_path = Content.getString("uri");
						if(content_path != "") {
							String response = Node.get_request_path(content_path);
							String read_value = Node.get_label_from_XMLString("con", response);
							String ContentName = Node.get_label_from_XMLString("cnf", response);
							create_Content_Instance(BikeName, ContainerName, ContentName, read_value);
						}
					}
				}
			}
		}
		if(DEV_MODE) {
			System.out.println("------------------------------------------------------------------------");
		}
	}


}

