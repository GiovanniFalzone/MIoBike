package it.unipi.iot.MIoBike.MIoBike_Bike_Simulator;

import org.eclipse.californium.core.CoapClient;
import org.eclipse.californium.core.CoapResource;
import org.eclipse.californium.core.coap.MediaTypeRegistry;
import org.eclipse.californium.core.coap.Option;
import org.eclipse.californium.core.coap.Request;
import org.eclipse.californium.core.coap.CoAP.Code;
import org.eclipse.californium.core.server.resources.CoapExchange;
import org.json.JSONObject;
import static it.unipi.iot.MIoBike.MIoBike_common.Constants.BIKE_JAVA_SIMULATOR_PORT;

class Lock_Simulator extends CoapResource {
	private String lock_status; 
	public Lock_Simulator() {
		super("Lock");	
		lock_status = "true";
		getAttributes().setTitle("Lock");
		setObservable(true);
	}

	public void handleGET(CoapExchange exchange) {
		String status = exchange.getQueryParameter("value");
		lock_status = status;
		System.out.println("New Lock status: "+ status);
		JSONObject response = new JSONObject();
		response.put("format", "boolean");
		response.put("value",  String.valueOf(lock_status));
		exchange.respond(response.toString());
		System.out.println("New Lock Status: "+lock_status);

		String uri = "coap://127.0.0.1:"+BIKE_JAVA_SIMULATOR_PORT+"/PhySim?value="+lock_status;
		Request req = new Request(Code.GET);
		req.getOptions().setAccept(MediaTypeRegistry.APPLICATION_JSON);
		req.getOptions().addOption(new Option(MediaTypeRegistry.APPLICATION_JSON));
		CoapClient client = new CoapClient(uri);
		client.advanced(req);
	}

}