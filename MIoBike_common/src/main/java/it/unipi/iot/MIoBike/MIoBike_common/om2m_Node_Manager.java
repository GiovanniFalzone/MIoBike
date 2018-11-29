package it.unipi.iot.MIoBike.MIoBike_common;

import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.*;

import static it.unipi.iot.MIoBike.MIoBike_common.Constants.*;
import static it.unipi.iot.MIoBike.MIoBike_common.Constants_om2m.TAG_LBL;
import static it.unipi.iot.MIoBike.MIoBike_common.Constants_om2m.TAG_TY;
import static it.unipi.iot.MIoBike.MIoBike_common.Constants_om2m.TYPE_AE;
import static it.unipi.iot.MIoBike.MIoBike_common.Constants_om2m.TYPE_CONTAINER;
import static it.unipi.iot.MIoBike.MIoBike_common.Constants_om2m.TYPE_CONTENT;
import static it.unipi.iot.MIoBike.MIoBike_common.Constants_om2m.TYPE_MN;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.StringJoiner;
import java.util.TimeZone;

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
		if(DEV_MODE_1) {
			System.out.println("-----------get_from_Resource------------------");
		}
		String uri_path = Node_Id + Node_Name;
		if(!Res_Name.equals("")) {
			Res_Name = check_Path(Res_Name);
			uri_path += Res_Name;
		}
	
		if(!Res_Container_Name.equals("")) {
			Res_Container_Name = check_Path(Res_Container_Name);
			uri_path += Res_Container_Name;
		}
		if(!last_path.equals("")) {
			last_path = check_Path(last_path);
			uri_path += last_path;
		}
		String ret = get_request_path(uri_path);
		if(DEV_MODE_1) {
			System.out.println("-----------End of get_from_Resource------------------");
		}
		return ret;
	}

	public String get_request_path(String path) {
		if(DEV_MODE_1) {
			System.out.println("-----------get_request_path------------------");
		}

		path = check_Path(path);
		String uri = Node_uri + URI_M2M_BASE_PATH + path;
		String ret = get_request(uri, M2M_CREDENTIALS, new String [0]); 

		if(DEV_MODE_1) {
			System.out.println("-----------End of get_request_path------------------");
		}
		return ret;
	}	

	public String [] get_labels_from(String Res_Name, String Res_Container_Name, String ContentName) {
		if(DEV_MODE_1) {
			System.out.println("-----------get_labels_from------------------");
		}

		String content = get_from_Resource(Res_Name, Res_Container_Name, ContentName);
		JSONObject json_content = new JSONObject(content);
		JSONObject res_json = new JSONObject();
		if(json_content.has("m2m:csr")) {
			res_json = json_content.getJSONObject("m2m:csr");								
		} else if(json_content.has("m2m:ae")) {
			res_json = json_content.getJSONObject("m2m:ae");
		} else if(json_content.has("m2m:cnt")) {
			res_json = json_content.getJSONObject("m2m:cnt");
		} else if(json_content.has("m2m:cin")) {
			res_json = json_content.getJSONObject("m2m:cin");
		}
		JSONArray jarray = res_json.getJSONArray(TAG_LBL);
		String [] labels = convert_json_array_field_to_string_array(jarray);

		if(DEV_MODE_1) {
			System.out.println("-----------End of get_labels_from------------------");
		}

		return labels;
	}
	
	public String [] add_labels(String old_labels[], String new_labels []) {
		if(DEV_MODE_1) {
			System.out.println("-----------add_labels------------------");
		}
		String [] ret = new String[0];
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
			ret = labels;	
		} else {
			ret = old_labels;
		}
		if(DEV_MODE_1) {
			System.out.println("-----------End of add_labels------------------");
		}
		return ret;
	}

	public String combine_labels(String labels[]) {
		StringJoiner joiner = new StringJoiner(":");
		for(String label : labels) {
			joiner.add(label);
		}
		return joiner.toString();
	}

	public String [] retrieve_UserId_BikeName_from_lables(JSONArray labels) {
		String BikeName = "";
		String UserId = "";		
		for(int i=0; i<labels.length(); i++) {
			String label = labels.getString(i);
			if(label.contains(LABEL_PREFIX_USERID) && !label.equals(LABEL_PREFIX_USERID)) {
		    	UserId = label.split(LABEL_PREFIX_USERID)[1];
		    }
		    if(label.contains(LABEL_PREFIX_BIKENAME) && !label.equals(LABEL_PREFIX_BIKENAME)) {
		    	BikeName = label.split(LABEL_PREFIX_BIKENAME)[1];
		    }
		}
		return new String []{UserId, BikeName};
	}

	public String [] retrieve_UserId_BikeName_ResName_Type_from_lables(JSONArray labels) {
		String BikeName = "";
		String ResName = "";
		String Type = "";
		String UserId = "";
		for(int i=0; i<labels.length(); i++) {
			String label = labels.getString(i);
			if(label.contains(LABEL_PREFIX_BIKENAME) && !label.equals(LABEL_PREFIX_BIKENAME)) {
		    	BikeName = label.split(LABEL_PREFIX_BIKENAME)[1];
		    }
		    if(label.contains(LABEL_PREFIX_RESOURCENAME) && !label.equals(LABEL_PREFIX_RESOURCENAME)) {
		    	ResName = label.split(LABEL_PREFIX_RESOURCENAME)[1];
		    }
		    if(label.contains(LABEL_PREFIX_TYPE) && !label.equals(LABEL_PREFIX_TYPE)) {
		    	Type = label.split(LABEL_PREFIX_TYPE)[1];
		    }
		    if(label.contains(LABEL_PREFIX_USERID) && !label.equals(LABEL_PREFIX_USERID)) {
		    	UserId = label.split(LABEL_PREFIX_USERID)[1];
		    }
		}
		return new String []{UserId, BikeName, ResName, Type};
	}
	
	public String [] convert_json_array_field_to_string_array(JSONArray jarray) {
		String [] ret = new String[jarray.length()];
		for(int i=0; i<jarray.length(); i++) {
			ret[i] = jarray.getString(i);
		}
		return ret;
	}
	
	public String [] create_AE_labels(String AE_Name, String [] more_labels) {
		String [] labels = new String[0];
		if(AE_Name.contains("Bike")) {
			labels = new String[4];
			labels[0] = LABEL_PREFIX_BIKENAME + AE_Name.replace("/", "");
			labels[1] = LABEL_PREFIX_BIKESTATUS + LABEL_LOCKED;
			labels[2] = LABEL_PREFIX_USERID;
			labels[3] = "Bike";
			labels = add_labels(labels, more_labels);			
		} else if(AE_Name.contains("System")) {
			labels = new String[1];
			labels[0] = "System";
		}
		return labels;
	}

	public String [] create_AE_labels(String BikeName, String BikeStatus, String UserId, String [] more_labels) {
		String [] labels = new String[4];
		labels[0] = LABEL_PREFIX_BIKENAME + BikeName.replace("/", "");
		labels[1] = LABEL_PREFIX_BIKESTATUS + BikeStatus;
		labels[2] = LABEL_PREFIX_USERID + UserId;
		labels[3] = "Bike";
		labels = add_labels(labels, more_labels);			
		return labels;
	}
	
	public String [] create_Container_labels(String AE_Name, String ContainerName, String Type, String [] more_labels) {
		String [] labels = new String[4];
		labels[0] = LABEL_PREFIX_BIKENAME + AE_Name;
		labels[1] = LABEL_PREFIX_RESOURCENAME + ContainerName;
		labels[2] = LABEL_PREFIX_TYPE + Type;
		labels[3] = combine_labels(new String[]{ AE_Name, ContainerName });
		labels = add_labels(labels, more_labels);
		return labels;
	}

	public String [] create_Content_labels(String UserId, String AE_Name, String ContainerName, String Type, String [] more_labels) {
		String [] labels = new String[7];
		labels[0] = LABEL_PREFIX_BIKENAME + AE_Name;
		labels[1] = LABEL_PREFIX_RESOURCENAME + ContainerName;
		labels[2] = LABEL_PREFIX_TYPE + Type;
		labels[3] = ContainerName+":"+UserId;
		labels[4] = LABEL_PREFIX_USERID + UserId;
		labels[5] = combine_labels(new String[]{AE_Name, ContainerName, UserId});
		labels[6] = combine_labels(new String[]{ AE_Name, ContainerName });
		labels = add_labels(labels, more_labels);
		return labels;
	}
	
	public void create_AE(String api, String AE_Name, String ResReach, String [] more_labels) {
		if(DEV_MODE_1) {
			System.out.println("-----------create_AE------------------");
		}

		String [] labels = create_AE_labels(AE_Name, more_labels);
		String uri = Node_uri + URI_M2M_BASE_PATH + Node_Id + Node_Name;
		JSONObject json_res_info = new JSONObject();
		json_res_info.put("api", api);
		json_res_info.put("rn", AE_Name);
		json_res_info.put("rr", ResReach);
		json_res_info.put("lbl", labels);
		JSONObject json_root = new JSONObject();
		json_root.put("m2m:ae", json_res_info);
		create_AE(uri, json_root);

		if(DEV_MODE_1) {
			System.out.println("-----------End of create_AE------------------");
		}
	}
	
	public void create_Container(String AE_Name, String Container_Name, String Type,String [] more_labels) {
		if(DEV_MODE_1) {
			System.out.println("-----------create_Container------------------");
		}
		String [] labels = create_Container_labels(AE_Name, Container_Name, Type, more_labels);
		AE_Name = check_Path(AE_Name);
		String uri = Node_uri + URI_M2M_BASE_PATH + Node_Id + Node_Name + AE_Name;
		JSONObject json_res_info = new JSONObject();
		json_res_info.put("rn", Container_Name);
		json_res_info.put("lbl", labels);
		JSONObject json_root = new JSONObject();
		json_root.put("m2m:cnt", json_res_info);
		create_Container(uri, json_root);

		if(DEV_MODE_1) {
			System.out.println("-----------End of create_Container------------------");
		}
	}

	public void create_Content_Instance(String UserId, String AE_Name, String Container_Name, String Type, String cnf, String con, String [] more_labels) {
		if(DEV_MODE_1) {
			System.out.println("-----------create_Content_Instance------------------");
		}

		String [] labels = create_Content_labels(UserId, AE_Name, Container_Name, Type, more_labels);
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

		if(DEV_MODE_1) {
			System.out.println("-----------End of create_Content_Instance------------------");
		}
	}

	public String [] create_Root_Subscription_labels(String res_path, String [] more_labels) {
		String [] single_labels = res_path.substring(1).split("/");
		String [] labels = { single_labels[0] };
		return labels;
	}

	public String get_AE_name_from_res_path(String res_path) {
		res_path = check_Path(res_path);
		String [] single_labels = res_path.substring(1).split("/");
		String AE_Name = single_labels[0];
		return AE_Name;
	}

	public String get_Container_name_from_res_path(String res_path) {
		res_path = check_Path(res_path);
		String [] single_labels = res_path.substring(1).split("/");
		String AE_Name = single_labels[1];
		return AE_Name;
	}
	
	public String [] create_AE_Subscription_labels(String res_path, String [] more_labels) {
		String AE_Name = get_AE_name_from_res_path(res_path);
		String [] labels = new String[1];
		labels[0] = LABEL_PREFIX_BIKENAME + AE_Name.replace("/", "");
		labels = add_labels(labels, more_labels);
		return labels;
	}
	
	public String [] create_Container_Subscription_labels(String res_path, String [] more_labels) {
		String AE_Name = get_AE_name_from_res_path(res_path);
		String ContainerName = get_Container_name_from_res_path(res_path);
		String []labels= new String[0];
//		String [] labels = create_Container_labels(AE_Name, ContainerName, more_labels);
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
				if(DEV_MODE_1) {
					System.out.print("Subscription labels, ResPath error");					
				}
				break;
		}
		return labels;
	}
	
	public void Subscribe_to_uri(String uri, String notificationUrl, String Res_monitor_name, String [] more_labels) {
		if(DEV_MODE_1) {
			System.out.println("----------------Subscribe_to_uri------------------------");
		}
		String [] labels = create_Subscription_labels(uri, more_labels);
		uri = Node_uri + URI_M2M_BASE_PATH + uri;
		JSONObject json_res_info = new JSONObject();
		json_res_info.put("rn", Res_monitor_name);
		json_res_info.put("nu", notificationUrl);
		json_res_info.put("nct", 2);
		json_res_info.put("lbl", labels);
		JSONObject json_root = new JSONObject();
		json_root.put("m2m:sub", json_res_info);
		if(DEV_MODE_1) {
			System.out.println("Subscribing "+ Res_monitor_name + " NU:" + notificationUrl + " to: " + uri);
		}
		create_Subscription(uri, notificationUrl, Res_monitor_name, M2M_CREDENTIALS, json_root);
		if(DEV_MODE_1) {
			System.out.println("----------------End of Subscribe_to_uri------------------------");
		}
	}
	
	public JSONObject Discovery_request_tag(String tag, String [] Queries) {
		if(DEV_MODE_1) {
			System.out.println("-----------Discovery_request_tag------------------");
		}
		String uri = Node_uri + URI_M2M_BASE_PATH + Node_Id;
		JSONObject ret = Discovery_request(uri, Queries);
		JSONArray new_jarray = new JSONArray();
		if(ret.has("m2m:uril")) {
			JSONArray jarray = ret.getJSONArray("m2m:uril");
			String [] uris = convert_json_array_field_to_string_array(jarray);
			for (String elem : uris) {
				if(!elem.equals("")) {
					JSONObject jobj = new JSONObject();
					jobj.put("uri", elem);
					new_jarray.put(jobj);
				}
			}
			JSONObject json_root = new JSONObject();
			json_root.put("response_array", new_jarray);
			ret = json_root;
		}
		if(DEV_MODE_1) {
			System.out.println("---------------End of Discovery_request_tag-------------------");
		}
		return ret;
	}
	
	public JSONArray get_all_by_label(int Res_Type, String [] labels) {
		if(DEV_MODE_1) {
			System.out.println("-----------get_all_by_label------------------");
		}
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
			if(DEV_MODE_1) {
				System.out.println("Empty array");
			}
			ret = new JSONArray();
		}
		if(DEV_MODE_1) {
			System.out.println("-----------End of get_all_by_label------------------");
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
		if(DEV_MODE_1) {
			System.out.println("-----------Subscribe_to_each_element------------------");
		}
		for(int i=0; i< array_element.length(); i++) {
			JSONObject element = array_element.getJSONObject(i);
			String uri = element.getString("uri");
			String [] labels = new String[0];
			Subscribe_to_uri(uri, notificationUrl, Res_monitor_name, labels);
		}		
		if(DEV_MODE_1) {
			System.out.println("-----------End of Subscribe_to_each_element------------------");
		}
	}

	public void Subscribe_to_Node_Root(String notificationUrl, String Res_monitor_name) {
		if(DEV_MODE_1) {
			System.out.println("-----------Subscribe_to_Node_Root------------------");
		}
		String res_path = Node_Id;
		String uri = res_path;
		String [] labels = new String[0];
		Subscribe_to_uri(uri, notificationUrl, Res_monitor_name, labels);
		if(DEV_MODE_1) {
			System.out.println("-----------End of Subscribe_to_Node_Root------------------");
		}
	}
	
	public void Subscribe_to_Res_path(String res_path, String notificationUrl, String Res_monitor_name) {
		if(DEV_MODE_1) {
			System.out.println("-----------Subscribe_to_Res_path------------------");
		}
		String uri = Node_Id + Node_Name + res_path;
		String [] labels = new String[0];
		Subscribe_to_uri(uri, notificationUrl, Res_monitor_name, labels);
		if(DEV_MODE_1) {
			System.out.println("-----------End of Subscribe_to_Res_path------------------");
		}
	}	
	
	public void Subscribe_to_AE(String AE_name, String notificationUrl, String Res_monitor_name) {
		if(DEV_MODE_1) {
			System.out.println("-----------Subscribe_to_AE------------------");
		}
		AE_name = check_Path(AE_name);
		String res_path = AE_name;
		Subscribe_to_Res_path(res_path, notificationUrl, Res_monitor_name);
		if(DEV_MODE_1) {
			System.out.println("-----------End of Subscribe_to_AE------------------");
		}
	}

	public void Subscribe_to_Container(String AE_name, String ContainerName, String notificationUrl, String Res_monitor_name) {
		if(DEV_MODE_1) {
			System.out.println("-----------Subscribe_to_Container------------------");
		}
		AE_name = check_Path(AE_name);
		ContainerName = check_Path(ContainerName);
		String res_path = AE_name + ContainerName;
		Subscribe_to_Res_path(res_path, notificationUrl, Res_monitor_name);
		if(DEV_MODE_1) {
			System.out.println("-----------End of Subscribe_to_Container------------------");
		}
	}
	
	public boolean check_Node_Subscription(String ResPath, String NM_Name) {
		if(DEV_MODE_1) {
			System.out.println("-----------check_Node_Subscription------------------");
		}
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
			if(DEV_MODE_1) {
				System.out.print(".");
			}
		}
		if(DEV_MODE_1) {
			System.out.println("-----------End of check_Node_Subscription------------------");
		}

		return subscribed;
	}

	public void request_label_update(String BikeName, String BikeStatus, String UserId) {
		if(DEV_MODE_1) {
			System.out.println("-----------request_label_update------------------");
		}
		JSONObject Sys_req = new JSONObject();
		JSONObject sub_req = new JSONObject();
		JSONObject info = new JSONObject();
		info.put("BikeName", BikeName);
		info.put("BikeStatus", BikeStatus);
		info.put("UserId", UserId);
		sub_req.put("Update_label_req", info);
		Sys_req.put("System", sub_req);
		String con = Sys_req.toString();
		create_Content_Instance("", "System", "Update_label_req", LABEL_VIRTUAL, "JSON", con, new String[0]);
		if(DEV_MODE_1) {
			System.out.println("-----------End of request_label_update------------------");
		}
	}

	public void request_subscription(String NodeId, String NodeUri, String NodeName, String ResPath) {
		if(DEV_MODE_1) {
			System.out.println("-----------request_subscription------------------");
		}
		JSONObject Sys_req = new JSONObject();
		JSONObject sub_req = new JSONObject();
		JSONObject info = new JSONObject();
		info.put("NodeId", NodeId);
		info.put("NodeName", NodeName);
		info.put("ResPath", ResPath);
		sub_req.put("Root_Sub_Req", info);
		Sys_req.put("System", sub_req);
		String con = Sys_req.toString();
		create_Content_Instance("", "System", "Root_Sub_Req", LABEL_VIRTUAL, "JSON", con, new String[0]);
		if(DEV_MODE_1) {
			System.out.println("-----------End of request_subscription------------------");
		}
	}

//--------------------------------------------------------	
	public void Notification_Handler(String content) {
		if(DEV_MODE_1) {
			System.out.println("-------------Notification Handler------------------------");
			System.out.println(content);
		}
	}
//--------------------------------------------------------	
	
	public JSONObject get_Last_Read(String AEName, String ResName) {
		if(DEV_MODE_1) {
			System.out.println("-----------get_Last_Read------------------");
		}

		JSONObject ret = new JSONObject();
		String content = get_from_Resource(AEName, ResName, "la");
		if(Validate_JSONObject(content)) {
			JSONObject json_content = new JSONObject(content);
			if(json_content.has("m2m:cin")) {
				JSONObject json_cin = json_content.getJSONObject("m2m:cin");
				ret = new JSONObject(json_cin.getString("con"));				
			}
		}
		if(DEV_MODE_1) {
			System.out.println("-----------End of get_Last_Read------------------");
		}
		return ret;
	}	
	
	public void update_labels_in_AE(String AE_Name, String [] more_labels) {
		if(DEV_MODE_1) {
			System.out.println("-----------update_labels_in_AE------------------");
		}
		AE_Name = check_Path(AE_Name);
		String uri = Node_uri + URI_M2M_BASE_PATH + Node_Id + Node_Name + AE_Name;
		String [] labels = create_AE_labels(AE_Name, more_labels);
		update_labels_in_type(TYPE_AE, uri, labels);
		if(DEV_MODE_1) {
			System.out.println("-----------End of update_labels_in_AE------------------");
		}
	}

	public void update_labels_in_AE(String BikeName, String BikeStatus, String UserId, String [] more_labels) {
		if(DEV_MODE_1) {
			System.out.println("-----------update_labels_in_AE------------------");
		}

		String [] labels = create_AE_labels(BikeName, BikeStatus, UserId, more_labels);
		BikeName = check_Path(BikeName);
		String uri = Node_uri + URI_M2M_BASE_PATH + Node_Id + Node_Name + BikeName;
		update_labels_in_type(TYPE_AE, uri, labels);
		if(DEV_MODE_1) {
			System.out.println("-----------End of update_labels_in_AE------------------");
		}
	}
	
	public void update_labels_in_Container(String AE_Name, String ContainerName, String Type, String [] more_labels) {
		if(DEV_MODE_1) {
			System.out.println("-----------update_labels_in_Container------------------");
		}
		AE_Name = check_Path(AE_Name);
		ContainerName = check_Path(ContainerName);
		String uri = Node_uri + URI_M2M_BASE_PATH + Node_Id + Node_Name + AE_Name + ContainerName;
		String [] labels = create_Container_labels(AE_Name, ContainerName, Type, more_labels);
		update_labels_in_type(TYPE_CONTAINER, uri, labels);
		if(DEV_MODE_1) {
			System.out.println("-----------End of update_labels_in_Container------------------");
		}
	}

	
	public JSONArray retrieve_con_from_uri(JSONArray uris) {
		if(DEV_MODE_1) {
			System.out.println("-----------retrieve_con_from_uri------------------");
		}
		JSONArray ret = new JSONArray();
		for(int i=0; i< uris.length(); i++) {
			JSONObject element = uris.getJSONObject(i);
			String obj_res_path = element.getString("uri");
			String response = get_request_path(obj_res_path);
			JSONObject json_res = new JSONObject(response);
			if(json_res.has("m2m:cin")) {
				JSONObject json_cin = json_res.getJSONObject("m2m:cin");
				String con = json_cin.getString("con");
				JSONObject json_value = new JSONObject(con);
				ret.put(json_value);				
			}
		}
		if(DEV_MODE_1) {
			System.out.println("-----------End of retrieve_con_from_uri------------------");
		}
		return ret;
	}

	public void set_content_to(String AE_Name, String Container_Name, String Type, String value) {
		if(DEV_MODE_1) {
			System.out.println("-----------set_content_to------------------");
		}
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
		con_json.put(JSON_KEY_BIKENAME, AE_Name);

		String str_value = con_json.toString();

		String [] labels = { };
		create_Content_Instance("System", AE_Name, Container_Name, Type, "JSON", str_value, labels);
		boolean ret = (get_Last_Read(AE_Name, Container_Name).equals(str_value));
		if(DEV_MODE_1) {
			System.out.println("-----------End of set_content_to------------------");
		}

	}

}

