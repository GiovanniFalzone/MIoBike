package it.unipi.iot.MIoBike.MIoBike_Utils;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.*;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.StringJoiner;
import java.util.TimeZone;

import static it.unipi.iot.MIoBike.MIoBike_Utils.Constants.*;

public class om2m_Node_Manager extends om2m_basic {
	public String Node_Id;
	public String Node_uri;
	public String Node_Name;
	
	public om2m_Node_Manager(String uri, String id, String name) {
		this.Node_uri = uri;
		this.Node_Id = id;
		this.Node_Name = name;
	}
	
	private String check_Path(String path) {
		if(!path.equals("")) {
			if(!(path.getBytes()[0] == '/')) {
				path = "/" + path;
			}
		}
		return path;
	}

	private String get_from_Resource(String Res_Name, String Res_Container_Name, String last_path) {
		Res_Name = check_Path(Res_Name);
		Res_Container_Name = check_Path(Res_Container_Name);
		if(!last_path.equals("")) {
			last_path = check_Path(last_path);
		}		
		String uri_path = Node_Id + Node_Name + Res_Name + Res_Container_Name + last_path;
		String ret = get_request_path(uri_path);
		return ret;
	}

	public String get_request_path(String path) {
		path = check_Path(path);
		String uri = Node_uri + URI_M2M_BASE_PATH + path;
		return get_request(uri, M2M_CREDENTIALS, new String [0]);
	}	

	public String [] add_labels(String old_labels[], String new_labels []) {
		if(new_labels.length > 0) {
			int size = old_labels.length + new_labels.length;
			String [] labels = new String[size];
			for(int i=0; i<old_labels.length; i++) {
				labels[i] = old_labels[i];
			}
			for(int i=0; i<new_labels.length; i++) {
				int pos = i+old_labels.length;
				labels[pos] = new_labels[i];
			}
			return labels;	
		} else {
			return old_labels;
		}
	}

	public String combine_labels(String labels[]) {
		StringJoiner joiner = new StringJoiner(":");
		for(String label : labels) {
			joiner.add(label);
		}
		return joiner.toString();
	}
	
	public String [] create_AE_labels(String AE_Name, String [] more_labels) {
		String [] labels = new String[1];
		labels[0] = AE_Name.replace("/", "");
		labels = add_labels(labels, more_labels);
		return labels;
	}

	public String [] create_Container_labels(String AE_Name, String ContainerName, String [] more_labels) {
		String [] labels = new String[3];
		labels[0] = AE_Name.replace("/", "");
		labels[1] = ContainerName.replace("/", "");
		labels[2] = combine_labels(new String[]{ AE_Name, ContainerName });
		labels = add_labels(labels, more_labels);
		return labels;
	}

	public String [] create_Content_labels(String AE_Name, String ContainerName, String [] more_labels) {
		String [] labels = create_Container_labels(AE_Name, ContainerName, more_labels);
		return labels;
	}
	
	public void create_AE(String api, String AE_Name, String ResReach, String [] more_labels) {
		String [] labels = create_AE_labels(AE_Name, more_labels);
		String uri = Node_uri + URI_M2M_BASE_PATH + Node_Id;
		JSONObject json_res_info = new JSONObject();
		json_res_info.put("api", api);
		json_res_info.put("rn", AE_Name);
		json_res_info.put("rr", ResReach);
		json_res_info.put("lbl", labels);
		JSONObject json_root = new JSONObject();
		json_root.put("m2m:ae", json_res_info);
		create_AE(uri, json_root);
	}
	
	public void create_Container(String AE_Name, String Container_Name, String [] more_labels) {
		String [] labels = create_Container_labels(AE_Name, Container_Name, more_labels);
		AE_Name = check_Path(AE_Name);
		String uri = Node_uri + URI_M2M_BASE_PATH + Node_Id + Node_Name + AE_Name;
		JSONObject json_res_info = new JSONObject();
		json_res_info.put("rn", Container_Name);
		json_res_info.put("lbl", labels);
		JSONObject json_root = new JSONObject();
		json_root.put("m2m:cnt", json_res_info);
		create_Container(uri, json_root);
	}

	public void create_Content_Instance(String AE_Name, String Container_Name, String cnf, String con, String [] more_labels) {
		String [] labels = create_Container_labels(AE_Name, Container_Name, more_labels);
		AE_Name = check_Path(AE_Name);
		Container_Name = check_Path(Container_Name);
		String uri = Node_uri + URI_M2M_BASE_PATH + Node_Id + Node_Name + AE_Name + Container_Name;
		JSONObject json_res_info = new JSONObject();
		json_res_info.put("cnf", cnf);
		json_res_info.put("con", con);
		json_res_info.put("lbl", labels);
		JSONObject json_root = new JSONObject();
		json_root.put("m2m:cin", json_res_info);
		create_Content_Instance(uri, json_root);
	}

	public String [] create_Root_Subscription_labels(String res_path, String [] more_labels) {
		String [] single_labels = res_path.substring(1).split("/");
		String [] labels = { single_labels[0] };
		return labels;
	}

	public String [] create_AE_Subscription_labels(String res_path, String [] more_labels) {
		String [] single_labels = res_path.substring(1).split("/");
		String AE_Name = single_labels[LABEL_BIKENAME_POS];
		String [] labels = create_AE_labels(AE_Name, more_labels);
		return labels;
	}
	
	public String [] create_Container_Subscription_labels(String res_path, String [] more_labels) {
		String [] single_labels = res_path.substring(1).split("/");
		String AE_Name = single_labels[LABEL_BIKENAME_POS];
		String ContainerName = single_labels[LABEL_RESOURCENAME_POS];
		String [] labels = create_Container_labels(AE_Name, ContainerName, more_labels);
		return labels;
	}	
	
	public String [] create_Subscription_labels(String uri, String [] more_labels) {
		String [] labels = new String[0];
		String res_path = uri;
		int type = 0;
		if(uri.contains(Node_Name)) {
			res_path = uri.split(Node_Name)[1];
			type = res_path.substring(1).split("/").length;
		}
		switch(type) {
			case 0:
				labels = create_Root_Subscription_labels(res_path, more_labels);
				break;
			case 1:
				labels = create_AE_Subscription_labels(res_path, more_labels);
				break;
			case 2:
				labels = create_Container_Subscription_labels(res_path, more_labels);
				break;
			default:
				if(DEV_MODE) {
					System.out.print("Subscription labels, ResPath error");					
				}
				break;
		}
		return labels;
	}
	
	public void Subscribe_to_uri(String uri, String notificationUrl, String Res_monitor_name, String [] more_labels) {
		String [] labels = create_Subscription_labels(uri, more_labels);
		uri = Node_uri + URI_M2M_BASE_PATH + uri;
		JSONObject json_res_info = new JSONObject();
		json_res_info.put("rn", Res_monitor_name);
		json_res_info.put("nu", notificationUrl);
		json_res_info.put("nct", 2);
		json_res_info.put("lbl", labels);
		JSONObject json_root = new JSONObject();
		json_root.put("m2m:sub", json_res_info);
		if(DEV_MODE) {
			System.out.println("Subscribing "+ Res_monitor_name + " NU:" + notificationUrl + " to: " + uri);
		}
		create_Subscription(uri, notificationUrl, Res_monitor_name, M2M_CREDENTIALS, json_root);
	}
	
	public JSONObject Discovery_request_tag(String tag, String [] Queries) {
		String uri = Node_uri + URI_M2M_BASE_PATH + Node_Id;
		Document doc = Discovery_request(uri, Queries);
		NodeList nList = doc.getElementsByTagName(tag);
		JSONArray jarray = new JSONArray();
		Node nNode = nList.item(0);
		String name = "uri";
		String urils_str = nNode.getTextContent();
		List<String> items = Arrays.asList(urils_str.split("\\s"));
		for (String elem : items) {
			if(!elem.equals("")) {
				JSONObject jobj = new JSONObject();
				jobj.put(name, elem);
				jarray.put(jobj);
			}
		}

		JSONObject json_root = new JSONObject();
		json_root.put("response_array", jarray);
		JSONObject ret = json_root;
		if(DEV_MODE) {
			System.out.println("-----------Discovery_request_tag ret------------------");
			System.out.println(ret);
			System.out.println("--------------------------------------------");
		}
		return ret;
	}
	
	public JSONArray get_all_by_label(int Res_Type, String [] labels) {
		String [] queries  = new String[labels.length+2];
		queries[0] = "fu=1";
		queries[1] = "rty="+Res_Type;
		if(labels.length > 0) {
			for(int i = 0; i<labels.length ; i++) {
				queries[i+2] = "lbl="+labels[i];
			}
		}
		JSONObject json_obj = Discovery_request_tag("m2m:uril", queries);
		JSONArray ret;
		if(json_obj.has("response_array")) {
			ret = json_obj.getJSONArray("response_array");
		} else {
			if(DEV_MODE) {
				System.out.println("Empty array");
			}
			ret = new JSONArray();
		}
		if(DEV_MODE) {
			System.out.println("-----------get_all_by_label ret------------------");
			System.out.println(ret);
			System.out.println("--------------------------------------------");
		}
		return ret;	
	}	
	
	public JSONArray get_all_MN(String [] labels) {
		return get_all_by_label(TYPE_MN, labels);
	}
	
	public JSONArray get_all_Subscribed(String [] labels) {
		return get_all_by_label(TYPE_SUBSCRIPTION, labels);
	}
	
	public JSONArray get_all_AE(String [] labels) {
		return get_all_by_label(TYPE_AE, labels);
	}

	public JSONArray get_all_Container(String [] labels) {
		return get_all_by_label(TYPE_CONTAINER, labels);
	}	

	public JSONArray get_all_Content(String [] labels) {
		return get_all_by_label(TYPE_CONTENT, labels);
	}
	
	public JSONArray get_all_in_AE_Container(String AE_name, String Container_name, int Res_Type) {
		String [] labels = new String[] { combine_labels(new String[] { AE_name, Container_name}) };
		JSONArray ret = get_all_by_label(Res_Type, labels);
		if(DEV_MODE) {
			System.out.println("-----------get_all_in_AE_Container ret------------------");
			System.out.println(ret);
			System.out.println("--------------------------------------------");
		}
		return ret;
	}
		
	public void Subscribe_to_each_Container(String notificationUrl, String Res_monitor_name, String [] labels) {
		JSONArray Containers = get_all_Container(labels);
		Subscribe_to_each_element(Containers, notificationUrl, Res_monitor_name);
	}

	public void Subscribe_to_each_AE(String notificationUrl, String Res_monitor_name, String [] labels) {
		JSONArray AEs = get_all_AE(labels);
		Subscribe_to_each_element(AEs, notificationUrl, Res_monitor_name);
	}
	
	public void Subscribe_to_each_MN(String notificationUrl, String Res_monitor_name) {
		JSONArray MNs = get_all_MN(new String[0]);
		Subscribe_to_each_element(MNs, notificationUrl, Res_monitor_name);
	}	

	public void Subscribe_to_each_element(JSONArray array_element, String notificationUrl, String Res_monitor_name) {
		for(int i=0; i< array_element.length(); i++) {
			JSONObject element = array_element.getJSONObject(i);
			String uri = element.getString("uri");
			String [] labels = new String[0];
			Subscribe_to_uri(uri, notificationUrl, Res_monitor_name, labels);
		}		
	}

	public void Subscribe_to_Node_Root(String notificationUrl, String Res_monitor_name) {
		String res_path = Node_Id;
		String uri = res_path;
		String [] labels = new String[0];
		Subscribe_to_uri(uri, notificationUrl, Res_monitor_name, labels);
	}
	
	public void Subscribe_to_Res_path(String res_path, String notificationUrl, String Res_monitor_name) {
		String uri = Node_Id + Node_Name + res_path;
		String [] labels = new String[0];
		Subscribe_to_uri(uri, notificationUrl, Res_monitor_name, labels);
	}	
	
	public void Subscribe_to_AE(String AE_name, String notificationUrl, String Res_monitor_name) {
		AE_name = check_Path(AE_name);
		String res_path = AE_name;
		Subscribe_to_Res_path(res_path, notificationUrl, Res_monitor_name);
	}

	public void Subscribe_to_Container(String AE_name, String ContainerName, String notificationUrl, String Res_monitor_name) {
		AE_name = check_Path(AE_name);
		ContainerName = check_Path(ContainerName);
		String res_path = AE_name + ContainerName;
		Subscribe_to_Res_path(res_path, notificationUrl, Res_monitor_name);
	}
	
	public boolean check_Node_Subscription(String ResPath, String NM_Name) {
		boolean subscribed = false;
		String [] labels = {};
		if(ResPath.length()>0) {
			labels = new String[]{ResPath.substring(1).replaceAll("/", ":")};
		}
		JSONArray Subscribed_array = get_all_Subscribed(new String[0]);
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
				System.out.print(".");
			}
		}
		return subscribed;
	}

	public void active_wait_for_subscription(String ResPath, String NM_Name) {
		if(DEV_MODE) {
			System.out.println("Checking Subscription for: "+NM_Name+" to: " + ResPath);
		}
		while(!check_Node_Subscription(ResPath, NM_Name)) {
			try {
					Thread.sleep(1000);
			} catch (InterruptedException ie) {
			    Thread.currentThread().interrupt();
			}
		}
	}
	
	public void Notification_Handler(String content) {
		if(DEV_MODE) {
			System.out.println("-------------Notification Handler------------------------");
			System.out.println(content);
		}
	}
	
	public String get_Last_Read(String AEName, String ResName) {
		String value = "";
		String la = get_from_Resource(AEName, ResName, "la");
		if(!la.equals("Resource not found")) {
			value = get_label_from_XMLString("con", la);
		} else {
			value = new JSONObject().toString();
		}
		return value;
	}	
	
	public void update_labels_in_AE(String AE_Name, String [] more_labels) {
		AE_Name = check_Path(AE_Name);
		String uri = Node_uri + URI_M2M_BASE_PATH + Node_Id + Node_Name + AE_Name;
		String [] labels = create_AE_labels(AE_Name, more_labels);
		update_labels_in_type(TYPE_AE, uri, labels);
	}

	public void update_labels_in_Container(String AE_Name, String ContainerName, String [] more_labels) {
		AE_Name = check_Path(AE_Name);
		ContainerName = check_Path(ContainerName);
		String uri = Node_uri + URI_M2M_BASE_PATH + Node_Id + Node_Name + AE_Name + ContainerName;
		String [] labels = create_Container_labels(AE_Name, ContainerName, more_labels);
		update_labels_in_type(TYPE_CONTAINER, uri, labels);
	}

	
	public JSONArray retrieve_con_from_uri(JSONArray uris) {
		JSONArray ret = new JSONArray();
		for(int i=0; i< uris.length(); i++) {
			JSONObject element = uris.getJSONObject(i);
			String obj_res_path = element.getString("uri");
			String response = get_request_path(obj_res_path);

			String str_value = get_label_from_XMLString("con", response);
			JSONObject json_value = new JSONObject(str_value);
			ret.put(json_value);
		}
		if(DEV_MODE) {
			System.out.println("---------------------------------------");
			System.out.println(ret);
			System.out.println("---------------------------------------");
		}
		return ret;
	}

	public void set_content_to(String AE_Name, String Container_Name, String value) {
		String date_str = "";
		SimpleDateFormat sdfLocal = new SimpleDateFormat("yyyy.MM.dd_HH:mm:ss");
		sdfLocal.setTimeZone(TimeZone.getTimeZone("GMT"));
	    date_str = sdfLocal.format(new Date());

	    JSONObject sensor_read = new JSONObject();
	    sensor_read.put(JSON_KEY_FORMAT, "boolean");
	    sensor_read.put(JSON_KEY_VALUE, value);
	    
		JSONObject con_json = new JSONObject();
		con_json.put(JSON_KEY_RESOURCEDATA, sensor_read);
		con_json.put(JSON_KEY_DATE, date_str);
		con_json.put(JSON_KEY_RESOURCENAME, Container_Name);
		con_json.put(JSON_KEY_BIKENAME, Container_Name);

		String str_value = con_json.toString();
		System.out.println(str_value);

		String [] labels = {AE_Name.replace("/", ""), Container_Name.replace("/", ""), LABEL_ACTUATOR};
		create_Content_Instance(AE_Name, Container_Name, "JSON", str_value, labels);
		boolean ret = (get_Last_Read(AE_Name, Container_Name).equals(str_value));
		System.out.println(ret);		
	}

}

