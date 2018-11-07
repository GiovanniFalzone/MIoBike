package it.unipi.iot.MIoBike.MIoBike_Utils;

import org.eclipse.californium.core.CoapClient;

import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.coap.Option;
import org.eclipse.californium.core.coap.Request;
import org.json.JSONObject;
import org.eclipse.californium.core.coap.CoAP.Code;


public class Manager_om2m {
	String uri;
	String IN_Id;
	String IN_Name;
	String IN_base_path;
	
	public Manager_om2m(String IN_uri, String IN_id, String IN_name) {
		this.uri = IN_uri;
		this.IN_base_path = "/~/";
		this.IN_Id = IN_id;
		this.IN_Name = IN_name;
	}
	
	public void create_AE(String api, String ResName, String ResReach) {
		System.out.println( "Creation of AE in the IN" );

		JSONObject json_res_info = new JSONObject();
		json_res_info.put("api", api);
		json_res_info.put("rn", ResName);
		json_res_info.put("rr", ResReach);

		JSONObject json_root = new JSONObject();
		json_root.put("m2m:ae", json_res_info);

		String payload = json_root.toString();
		System.out.println("Payload: " + payload);
		Request req = new Request(Code.POST);
		req.getOptions().setAccept(MediaTypeRegistry.APPLICATION_JSON);
		req.getOptions().setContentFormat(MediaTypeRegistry.APPLICATION_JSON);
		req.getOptions().addOption(new Option(256, "admin:admin"));
		req.getOptions().addOption(new Option(267, 2));
		req.setPayload(payload);
		CoapClient client = new CoapClient(uri + IN_base_path + IN_Id);
		CoapResponse response = client.advanced(req);
		if (response!=null) {
			System.out.println(response.getCode());
			System.out.println(response.getOptions());
			System.out.println(response.getResponseText());
		} else {
			System.out.println("No response received.");
		}
	}
	
	public void create_Container(String Res_Name, String Res_Container_Name) {
		String cse_uri = uri + IN_base_path + IN_Id + "/" + IN_Name + "/" + Res_Name;
		System.out.println("CSE_uri: " + cse_uri);
		CoapClient client = new CoapClient(cse_uri);
		Request req = Request.newPost();
		req.getOptions().addOption(new Option(267, 3));
		req.getOptions().addOption(new Option(256, "admin:admin"));
		req.getOptions().setContentFormat(MediaTypeRegistry.APPLICATION_JSON);
		req.getOptions().setAccept(MediaTypeRegistry.APPLICATION_JSON);

		JSONObject json_res_info = new JSONObject();
		json_res_info.put("rn", Res_Container_Name);

		JSONObject json_root = new JSONObject();
		json_root.put("m2m:cnt", json_res_info);

		String payload = json_root.toString();
		req.setPayload(payload);
		System.out.println("Payload: " + payload);

		CoapResponse response = client.advanced(req);
		if (response!=null) {
			System.out.println(response.getCode());
			System.out.println(response.getOptions());
			System.out.println(response.getResponseText());
		} else {
			System.out.println("No response received.");
		}
	}

	public void create_Content_Instance(String Res_Name, String Res_Container_Name, String Instance_Name, String read_value) {
		String cse_uri = uri + IN_base_path + IN_Id + "/" + IN_Name + "/" + Res_Name + "/" + Res_Container_Name;
		System.out.println("CSE_uri: " + cse_uri);
		CoapClient client = new CoapClient(cse_uri);
		Request req = Request.newPost();
		req.getOptions().addOption(new Option(267, 4));
		req.getOptions().addOption(new Option(256, "admin:admin"));
		req.getOptions().setContentFormat(MediaTypeRegistry.APPLICATION_JSON);
		req.getOptions().setAccept(MediaTypeRegistry.APPLICATION_JSON);

		JSONObject json_res_info = new JSONObject();
		json_res_info.put("cnf", Instance_Name);
		json_res_info.put("con", read_value);

		JSONObject json_root = new JSONObject();
		json_root.put("m2m:cin", json_res_info);

		String payload = json_root.toString();
		req.setPayload(payload);
		System.out.println("Payload: " + payload);

		CoapResponse response = client.advanced(req);
//		if (response!=null) {
//			System.out.println(response.getCode());
//			System.out.println(response.getOptions());
//			System.out.println(response.getResponseText());
//		} else {
//			System.out.println("No response received.");
//		}
	}
	
	public void get_request(String Res_Name, String Res_Container_Name, String Instance_Name, String label) {
		Request req = new Request(Code.GET);
		req.getOptions().addOption(new Option(256, "admin:admin"));
		String cse_uri = uri + IN_base_path + IN_Id + "/" + IN_Name + "/" + Res_Name + "/" + Res_Container_Name + "/" + label;
		System.out.println("CSE_uri: " + cse_uri);
		CoapClient client = new CoapClient(cse_uri);
		CoapResponse response = client.advanced(req);
//		if (response!=null) {
//			System.out.println(response.getCode());
//			System.out.println(response.getOptions());
//			System.out.println(response.getResponseText());
//		} else {
//			System.out.println("No response received.");
//		}
	}

	public void put_request(String Res_Name, String Res_Container_Name, String Instance_Name) {
		Request req = new Request(Code.PUT);
		req.getOptions().addOption(new Option(256, "admin:admin"));
		String cse_uri = uri + IN_base_path + IN_Id + "/" + IN_Name + "/" + Res_Name + "/" + Res_Container_Name;
		System.out.println("CSE_uri: " + cse_uri);
		CoapClient client = new CoapClient(cse_uri);
		CoapResponse response = client.advanced(req);
//		if (response==null) {
//			System.out.println(response.getCode());
//			System.out.println(response.getOptions());
//			System.out.println(response.getResponseText());
//		} else {
//			System.out.println("No response received.");
//		}
	}

	public void discovery_request(String get_parameters) {
		Request req = new Request(Code.GET);
		req.getOptions().addOption(new Option(256, "admin:admin"));
		String cse_uri = uri + IN_base_path + IN_Id + "?" + get_parameters;
		System.out.println("req: " + cse_uri);
		CoapClient client = new CoapClient(cse_uri);
		CoapResponse response = client.advanced(req);

//		System.out.println("---------------------");
//		System.out.println(req.getOptions().toString());
//		System.out.println("---------------------");

//		if (response!=null) {
//			System.out.println(response.getCode());
//			System.out.println(response.getOptions());
			System.out.println(response.getResponseText());
//		} else {
//			System.out.println("No response received.");
//		}		
	}

	public void create_Subscription(String cse, String notificationUrl, String Res_monitor_name){
		System.out.println( "Creation of subscription" );
		CoapClient client = new CoapClient(cse);
		Request req = Request.newPost();
		req.getOptions().addOption(new Option(267, 23));
		req.getOptions().addOption(new Option(256, "admin:admin"));
		req.getOptions().setContentFormat(MediaTypeRegistry.APPLICATION_JSON);
		req.getOptions().setAccept(MediaTypeRegistry.APPLICATION_JSON);
		JSONObject content = new JSONObject();
		content.put("rn", Res_monitor_name);
		content.put("nu", notificationUrl);
		content.put("nct", 2);
		JSONObject root = new JSONObject();
		root.put("m2m:sub", content);
		String body = root.toString();
		req.setPayload(body);
		CoapResponse responseBody = client.advanced(req);
		String response = new String(responseBody.getPayload());
		System.out.println(response);
	}
}
