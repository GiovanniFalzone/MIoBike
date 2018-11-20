package it.unipi.iot.MIoBike.MIoBike_Utils;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.coap.Option;
import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.core.coap.CoAP.Code;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.*;
import org.xml.sax.InputSource;
import javax.xml.parsers.*;
import java.io.*;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;

import static it.unipi.iot.MIoBike.MIoBike_Utils.Constants.*;

public class om2m_basic {	
	public String get_request(String uri, String access_credentials, String [] UriQueries) {
		if(DEV_MODE) {
			System.out.println("------------------GET_Request to " + uri + " Queries:"+Arrays.toString(UriQueries)+"-------------------------------");
		}		
		Request req = new Request(Code.GET);
		req.getOptions().addOption(new Option(OPTION_ACCESS, access_credentials));
		req.getOptions().addOption(new Option(MediaTypeRegistry.APPLICATION_JSON));
		for(String query: UriQueries) {
			req.getOptions().addUriQuery(query);
		}

		CoapClient client = new CoapClient(uri);
		client.setTimeout(REQUEST_TIMEOUT);
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
			System.out.println("-------------------------------------------------------------------");
		}		
		return ret;
	}
		
	public String post_request(String uri, JSONObject json_payload, int opt_num, int opt_val, String access_credentials) {
		if(DEV_MODE) {
			System.out.println("----------------------POST_Request to " + uri +"--------------------------------------");
		}
		String payload = json_payload.toString();
		Request req = new Request(Code.POST);
		req.getOptions().setAccept(MediaTypeRegistry.APPLICATION_JSON);
		req.getOptions().setContentFormat(MediaTypeRegistry.APPLICATION_JSON);
		req.getOptions().addOption(new Option(OPTION_ACCESS, access_credentials));
		req.getOptions().addOption(new Option(opt_num, opt_val));
		req.setPayload(payload);
		CoapClient client = new CoapClient(uri);
		client.setTimeout(REQUEST_TIMEOUT);
		CoapResponse response = client.advanced(req);
		String ret = "No response";
		if(response != null) {
			ret = response.getResponseText();
		}
		if(DEV_MODE) {
			System.out.println("Payload: " + payload);
			if (response==null) {
				System.out.println("No response received.");
			} else {
				System.out.println(response.getCode());
				System.out.println(response.getOptions());
				System.out.println(response.getResponseText());
			}
			System.out.println("-------------------------------------------------------------------");
		}
		return ret;
	}

	public String put_request(String uri, JSONObject json_payload, int opt_num, int opt_val, String access_credentials) {
		if(DEV_MODE) {
			System.out.println("----------------------PUT_Request to " + uri +"--------------------------------------");
		}
		String payload = json_payload.toString();
		Request req = new Request(Code.PUT);
		req.getOptions().setAccept(MediaTypeRegistry.APPLICATION_JSON);
		req.getOptions().setContentFormat(MediaTypeRegistry.APPLICATION_JSON);
		req.getOptions().addOption(new Option(OPTION_ACCESS, access_credentials));
		req.getOptions().addOption(new Option(opt_num, opt_val));
		req.setPayload(payload);
		CoapClient client = new CoapClient(uri);
		client.setTimeout(REQUEST_TIMEOUT);
		CoapResponse response = client.advanced(req);
		String ret = "No response";
		if(response != null) {
			ret = response.getResponseText();
		}
		if(DEV_MODE) {
			System.out.println("Payload: " + payload);
			if (response==null) {
				System.out.println("No response received.");
			} else {
				System.out.println(response.getCode());
				System.out.println(response.getOptions());
				System.out.println(response.getResponseText());
			}
			System.out.println("-------------------------------------------------------------------");
		}
		return ret;
	}
	
	public String get_type_str(int type) {
		String ret = "m2m:";
		switch(type) {
			case TYPE_AE:
				ret = ret + "ae";
				break;
			case TYPE_CONTAINER:
				ret = ret + "cnt";
				break;
			case TYPE_CONTENT:
				ret = ret + "cin";
				break;
		}
		return ret;
	}
	
	public void update_labels_in_type(int type, String uri, String [] labels) {
		String type_str = get_type_str(type);
		JSONObject json_con = new JSONObject();
		json_con.put("lbl", labels);
		JSONObject root = new JSONObject();
		root.put(type_str, json_con);
		put_request(uri, root, OPTION_TYPE, type, M2M_CREDENTIALS);
	}
	
	public void create_AE(String uri, JSONObject content) {
		if(DEV_MODE) {
			System.out.println("Create AE call to " + uri);
		}
		post_request(uri, content, OPTION_TYPE, TYPE_AE, M2M_CREDENTIALS);
	}

	public void create_Container(String uri, JSONObject content) {
		if(DEV_MODE) {
			System.out.println("Create Container call to " + uri);
		}
		post_request(uri, content, OPTION_TYPE, TYPE_CONTAINER, M2M_CREDENTIALS);
	}

	public void create_Content_Instance(String uri, JSONObject content) {
		if(DEV_MODE) {
			System.out.println("Create content instance call to " + uri);
		}
		post_request(uri, content, OPTION_TYPE, TYPE_CONTENT, M2M_CREDENTIALS);
	}

	public String create_Subscription(String uri, String notificationUrl, String Res_monitor_name, String access_credentials, JSONObject content) {
		String ret = post_request(uri, content, OPTION_TYPE, TYPE_SUBSCRIPTION, access_credentials);
		if(DEV_MODE) {
			System.out.println("create subscription call to " + uri);
		}
		return ret;
	}	
	
	public Document Discovery_request(String uri, String [] Queries) {
		if(DEV_MODE) {
			StringJoiner joiner = new StringJoiner(" ");
			for(String query : Queries) {
				joiner.add(query);
			}
			System.out.println("Discovery request call call to " + uri + " Queries: " + joiner.toString());
		}
		String response = get_request(uri, M2M_CREDENTIALS, Queries);
		Document doc = null;
		try {
			doc = String_XML_to_XML_Document(response);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return doc;
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

