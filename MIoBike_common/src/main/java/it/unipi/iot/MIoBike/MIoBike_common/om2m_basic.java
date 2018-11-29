package it.unipi.iot.MIoBike.MIoBike_common;
import static it.unipi.iot.MIoBike.MIoBike_common.Constants.*;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import org.apache.http.HttpHeaders;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.client.methods.RequestBuilder;
import org.apache.http.impl.client.HttpClients;


import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResponse;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.coap.Option;
import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.core.coap.CoAP.Code;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;


public class om2m_basic {
	protected String get_request_http(String uri, String [] Queries) throws ClientProtocolException, IOException {
		if(DEV_MODE_0) {
			System.out.println("------------------GET_Request to " + uri +"-------------------------------");
		}
		HttpClient client = HttpClients.custom().build();
		RequestBuilder req = RequestBuilder.get().setUri(uri);
		for(String query : Queries) {
			String parameter = query.split("=")[0];
			String value = query.split("=")[1];
			req.addParameter(parameter, value);
		}
		HttpUriRequest request = req
		  .setHeader(HttpHeaders.CONTENT_TYPE,"application/json")
		  .build();
		
		HttpResponse response = null;
		response = client.execute(request);
		BufferedReader rd = null;

		try {
			rd = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
		} catch (UnsupportedOperationException e1) {
			e1.printStackTrace();
		} catch (IOException e1) {
			e1.printStackTrace();
		}
		String line = "";
		String str_content = "";
		try {
			while ((line = rd.readLine()) != null) {
				str_content += line.trim();
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
		if(DEV_MODE_0) {
			System.out.println("Response Code : " + response.getStatusLine().getStatusCode());
			System.out.println("Response payload : " +str_content);			
		}
		return str_content;
	}
	
	protected String get_request(String uri, String access_credentials, String [] UriQueries) {
		if(DEV_MODE_0) {
			System.out.println("------------------GET_Request to " + uri + " Queries:"+Arrays.toString(UriQueries)+"-------------------------------");
		}		
		Request req = new Request(Code.GET);
		req.getOptions().setAccept(MediaTypeRegistry.APPLICATION_JSON);
		req.getOptions().addOption(new Option(OPTION_ACCESS, access_credentials));
		req.getOptions().addOption(new Option(MediaTypeRegistry.APPLICATION_JSON));
		for(String query: UriQueries) {
			req.getOptions().addUriQuery(query);
		}

		CoapClient client = new CoapClient(uri);
		client.setTimeout(REQUEST_TIMEOUT);
		CoapResponse response = client.advanced(req);
		String ret = "";
		if(response != null) {
			ret = response.getResponseText();
		}

		if(DEV_MODE_0) {
			if(response == null) {
				System.out.println("No response received.");
				System.out.println(response);
			} else {
				System.out.println(response.getCode());
				System.out.println(response.getOptions());
				System.out.println(response.getResponseText());							
			}
			System.out.println("-------------------------------------------------------------------");
		}		
		return ret;
	}	
	
	protected String post_request(String uri, JSONObject json_payload, int opt_num, int opt_val, String access_credentials) {
		if(DEV_MODE_0) {
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
		String ret = "";
		if(response != null) {
			ret = response.getResponseText();
		}
		if(DEV_MODE_0) {
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

	protected String put_request(String uri, JSONObject json_payload, int opt_num, int opt_val, String access_credentials) {
		if(DEV_MODE_0) {
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
		String ret = "";
		if(response != null) {
			ret = response.getResponseText();
		}
		if(DEV_MODE_0) {
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
	
	protected String get_type_str(int type) {
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
	
	protected void update_labels_in_type(int type, String uri, String [] labels) {
		String type_str = get_type_str(type);
		JSONObject json_con = new JSONObject();
		json_con.put("lbl", labels);
		JSONObject root = new JSONObject();
		root.put(type_str, json_con);
		put_request(uri, root, OPTION_TYPE, type, M2M_CREDENTIALS);
	}
	
	protected void create_AE(String uri, JSONObject content) {
		if(DEV_MODE_0) {
			System.out.println("Create AE call to " + uri);
		}
		post_request(uri, content, OPTION_TYPE, TYPE_AE, M2M_CREDENTIALS);
	}

	protected void create_Container(String uri, JSONObject content) {
		if(DEV_MODE_0) {
			System.out.println("Create Container call to " + uri);
		}
		post_request(uri, content, OPTION_TYPE, TYPE_CONTAINER, M2M_CREDENTIALS);
	}

	protected void create_Content_Instance(String uri, JSONObject content) {
		if(DEV_MODE_0) {
			System.out.println("Create content instance call to " + uri);
		}
		post_request(uri, content, OPTION_TYPE, TYPE_CONTENT, M2M_CREDENTIALS);
	}

	protected String create_Subscription(String uri, String notificationUrl, String Res_monitor_name, String access_credentials, JSONObject content) {
		String ret = post_request(uri, content, OPTION_TYPE, TYPE_SUBSCRIPTION, access_credentials);
		if(DEV_MODE_0) {
			System.out.println("create subscription call to " + uri);
		}
		return ret;
	}
	
	protected JSONObject Discovery_request(String uri, String [] Queries) {
		if(DEV_MODE_0) {
			System.out.println("Discovery request call call to " + uri + " Queries: ");
			for(String query : Queries) {
				System.out.print(query+" ");
			}
			System.out.println("");
		}
		String response = get_request(uri, M2M_CREDENTIALS, Queries);
		JSONObject ret = new JSONObject();
		if(response.length()>0) {
			ret = new JSONObject(response);
		}
		return ret;
	}

	public boolean Validate_JSONObject(String test) {
	    try {
	        new JSONObject(test);
	    } catch (JSONException exception_0) {
	        try {
	            new JSONArray(test);
	        } catch (JSONException exception_1) {
	            return false;
	        }
	    }
	    return true;
	}
	
	protected String Convert_HTTP_URI_To_CoAP_URI(String uri, int port) {
		uri = uri.replaceAll("http", "coap");
		uri = uri.replaceAll("8080/", ""+port);
		uri = uri.replaceAll("8282/", ""+port);
		return uri;
	}
	protected String Convert_CoAP_URI_To_HTTP_URI(String uri) {
		uri = uri.replaceAll("coap", "http");
		String port= uri.split(":")[2].split("/")[0];
		uri = uri.replaceAll(""+port, "8080");
		return uri;
	}
}

