package it.unipi.iot.MIoBike.MIoBike_Utils;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.coap.Option;
import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.core.coap.CoAP.Code;
import org.json.JSONObject;
import org.w3c.dom.*;
import org.xml.sax.InputSource;
import javax.xml.parsers.*;
import java.io.*;
import static it.unipi.iot.MIoBike.MIoBike_Utils.Constants.*;

public class om2m_general {	

	public String get_request(String uri, String access_credentials) {
		if(DEV_MODE) {
			System.out.println("GET_Request to " + uri);
		}		
		Request req = new Request(Code.GET);
		req.getOptions().addOption(new Option(OPTION_ACCESS, access_credentials));
		CoapClient client = new CoapClient(uri);
		CoapResponse response = client.advanced(req);
		String ret = "No response";
		if(response != null) {
			ret = response.getResponseText();
		}

		if(DEV_MODE) {
			if(response == null) {
				System.out.println("No response received.");
			} else {
				System.out.println(response.getCode());
				System.out.println(response.getOptions());
				System.out.println(response.getResponseText());			
			}			
		}		
		return ret;
	}
		
	public void post_request(String uri, JSONObject json_payload, int opt_num, int opt_val, String access_credentials) {
		if(DEV_MODE) {
			System.out.println("POST_Request to " + uri);
		}
		String payload = json_payload.toString();
		Request req = new Request(Code.POST);
		req.getOptions().setAccept(MediaTypeRegistry.APPLICATION_JSON);
		req.getOptions().setContentFormat(MediaTypeRegistry.APPLICATION_JSON);
		req.getOptions().addOption(new Option(256, access_credentials));
		req.getOptions().addOption(new Option(opt_num, opt_val));
		req.setPayload(payload);
		CoapClient client = new CoapClient(uri);
		CoapResponse response = client.advanced(req);

		if(DEV_MODE) {
			System.out.println("Payload: " + payload);
			if (response==null) {
				System.out.println("No response received.");
			} else {
				System.out.println(response.getCode());
				System.out.println(response.getOptions());
				System.out.println(response.getResponseText());
			}
		}
	}

	public void create_Subscription(String uri, String notificationUrl, String Res_monitor_name, String access_credentials) {
		JSONObject content = new JSONObject();
		content.put("rn", Res_monitor_name);
		content.put("nu", notificationUrl);
		content.put("nct", 2);

		JSONObject json_root = new JSONObject();
		json_root.put("m2m:sub", content);

		post_request(uri, json_root, OPTION_TYPE, TYPE_SUBSCRIPTION, access_credentials);
		if(DEV_MODE) {
			System.out.println("create subscription call to " + uri);
		}
	}

	public Document String_XML_to_XML_Document(String str) throws Exception {
		Document doc;
		InputSource is = new InputSource(new StringReader(str));
		DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();
		DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
		doc = dBuilder.parse(is);
		doc.getDocumentElement().normalize();
		return doc;
	}
	
	public String get_label_from_XMLString(String tag, String xml_str) {
		if(DEV_MODE) {
			System.out.println("get label: "+tag+" from xml_str: " + xml_str);
		}
		Document doc = null;
		try {
			doc = String_XML_to_XML_Document(xml_str);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		NodeList nList = doc.getElementsByTagName(tag);
		Node nNode = nList.item(0);
		return nNode.getTextContent();
	}
	
	public String Convert_HTTP_URI_To_CoAP_URI(String uri, int port) {
		uri = uri.replaceAll("http", "coap");
		uri = uri.replaceAll("8080/", ""+port);
		uri = uri.replaceAll("8282/", ""+port);
		return uri;
	}
}

